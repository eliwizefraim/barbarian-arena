package com.barbarians.global.combat

import com.barbarians.global.model.WarriorClass
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

enum class StatusEffectType(val displayName: String, val turnsDuration: Int) {
    SHIELD_WALL("Shield Wall", 1),
    ENRAGED("Enraged", 2),
    STUNNED("Stunned", 1),
    BLEEDING("Bleeding", 2)
}

data class ActiveStatus(
    val type: StatusEffectType,
    var turnsLeft: Int
)

data class Combatant(
    val id: String,
    val name: String,
    val warriorClass: WarriorClass,
    val level: Int = 1,
    val maxHp: Int,
    var currentHp: Int,
    val baseAttack: Int,
    val baseDefense: Int,
    val speed: Int = 15,
    val weaponName: String,
    val armorName: String,
    var fury: Int = 0,
    val maxFury: Int = 100,
    val activeStatuses: MutableList<ActiveStatus> = mutableListOf(),
    val avatarEmoji: String = "⚔️"
) {
    fun copyCombatant(): Combatant {
        return copy(
            currentHp = currentHp,
            fury = fury,
            activeStatuses = activeStatuses.map { it.copy() }.toMutableList()
        )
    }

    val isAlive: Boolean get() = currentHp > 0

    val effectiveAttack: Int
        get() {
            var atk = baseAttack
            if (activeStatuses.any { it.type == StatusEffectType.ENRAGED }) {
                atk = (atk * 1.3f).toInt()
            }
            return atk
        }

    val effectiveDefense: Int
        get() {
            var def = baseDefense
            if (activeStatuses.any { it.type == StatusEffectType.SHIELD_WALL }) {
                def = (def * 1.5f).toInt()
            }
            if (activeStatuses.any { it.type == StatusEffectType.ENRAGED }) {
                def = (def * 0.85f).toInt()
            }
            return def
        }
}

enum class DuelAction(val title: String, val furyCost: Int, val description: String) {
    QUICK_STRIKE("Quick Strike", 0, "Swift standard attack (+15 Fury)"),
    HEAVY_CLEAVE("Heavy Cleave", 0, "Brutal slash with high damage variance (+25 Fury)"),
    SHIELD_BASH("Shield Bash", 10, "Bludgeon foe; chance to stun & grants Shield Wall"),
    BERSERK_RAGE("Berserk Rage", 50, "Devastating strike consuming 50 Fury; grants Enraged!"),
    WAR_ROAR("War Roar", 30, "Heals 30 HP and increases combat resolve (+5 Fury)")
}

data class TurnResult(
    val roundNumber: Int,
    val turnNumber: Int,
    val attackerName: String,
    val defenderName: String,
    val action: DuelAction,
    val damageDealt: Int,
    val healedAmount: Int,
    val isCritical: Boolean,
    val isDodged: Boolean,
    val isStunnedTurn: Boolean,
    val statusApplied: StatusEffectType?,
    val attackerHpRemaining: Int,
    val defenderHpRemaining: Int,
    val attackerFuryRemaining: Int,
    val defenderFuryRemaining: Int,
    val logMessage: String
)

object CombatEngine {

    fun executeTurn(
        round: Int,
        turn: Int,
        attacker: Combatant,
        defender: Combatant,
        action: DuelAction
    ): TurnResult {
        // 1. Check if attacker is stunned
        val stunnedStatus = attacker.activeStatuses.find { it.type == StatusEffectType.STUNNED }
        if (stunnedStatus != null) {
            attacker.activeStatuses.remove(stunnedStatus)
            return TurnResult(
                roundNumber = round,
                turnNumber = turn,
                attackerName = attacker.name,
                defenderName = defender.name,
                action = action,
                damageDealt = 0,
                healedAmount = 0,
                isCritical = false,
                isDodged = false,
                isStunnedTurn = true,
                statusApplied = null,
                attackerHpRemaining = attacker.currentHp,
                defenderHpRemaining = defender.currentHp,
                attackerFuryRemaining = attacker.fury,
                defenderFuryRemaining = defender.fury,
                logMessage = "[Round $round • Turn $turn] 💫 ${attacker.name} is STUNNED and cannot act this turn!"
            )
        }

        // Apply bleed on attacker if present
        val bleedStatus = attacker.activeStatuses.find { it.type == StatusEffectType.BLEEDING }
        if (bleedStatus != null) {
            val bleedDmg = 8
            attacker.currentHp = (attacker.currentHp - bleedDmg).coerceAtLeast(0)
            bleedStatus.turnsLeft -= 1
            if (bleedStatus.turnsLeft <= 0) {
                attacker.activeStatuses.remove(bleedStatus)
            }
        }

        // 2. Dodge check
        val dodgeChance = when (defender.warriorClass) {
            WarriorClass.VALKYRIE -> 18
            WarriorClass.GLADIATOR -> 10
            else -> 6
        }
        val isDodged = Random.nextInt(100) < dodgeChance && action != DuelAction.WAR_ROAR

        if (isDodged) {
            attacker.fury = min(attacker.maxFury, attacker.fury + 5)
            defender.fury = min(defender.maxFury, defender.fury + 10)
            return TurnResult(
                roundNumber = round,
                turnNumber = turn,
                attackerName = attacker.name,
                defenderName = defender.name,
                action = action,
                damageDealt = 0,
                healedAmount = 0,
                isCritical = false,
                isDodged = true,
                isStunnedTurn = false,
                statusApplied = null,
                attackerHpRemaining = attacker.currentHp,
                defenderHpRemaining = defender.currentHp,
                attackerFuryRemaining = attacker.fury,
                defenderFuryRemaining = defender.fury,
                logMessage = "[Round $round • Turn $turn] 💨 ${defender.name} swiftly DODGED ${attacker.name}'s ${action.title}!"
            )
        }

        // 3. Crit check
        val critBonus = when (attacker.warriorClass) {
            WarriorClass.BERSERKER -> 28
            WarriorClass.VALKYRIE -> 22
            else -> 12
        }
        val isCrit = Random.nextInt(100) < critBonus && action != DuelAction.WAR_ROAR
        val critMultiplier = if (isCrit) 1.65f else 1.0f

        var damage = 0
        var heal = 0
        var statusToApply: StatusEffectType? = null
        var customDesc = ""

        when (action) {
            DuelAction.QUICK_STRIKE -> {
                val raw = (attacker.effectiveAttack * (0.95f + Random.nextFloat() * 0.25f) * critMultiplier).toInt()
                val defMitigation = (defender.effectiveDefense * 0.45f).toInt()
                damage = max(7, raw - defMitigation)
                attacker.fury = min(attacker.maxFury, attacker.fury + 15)
                defender.fury = min(defender.maxFury, defender.fury + 8)
                customDesc = "${attacker.name} strikes with ${attacker.weaponName} for $damage damage"
            }
            DuelAction.HEAVY_CLEAVE -> {
                val raw = (attacker.effectiveAttack * 1.55f * (0.85f + Random.nextFloat() * 0.45f) * critMultiplier).toInt()
                val defMitigation = (defender.effectiveDefense * 0.4f).toInt()
                damage = max(12, raw - defMitigation)
                attacker.fury = min(attacker.maxFury, attacker.fury + 25)
                defender.fury = min(defender.maxFury, defender.fury + 12)

                // 25% chance to cause Bleeding
                if (Random.nextInt(100) < 25) {
                    statusToApply = StatusEffectType.BLEEDING
                }
                customDesc = "${attacker.name} swings a crushing Heavy Cleave dealing $damage damage"
            }
            DuelAction.SHIELD_BASH -> {
                attacker.fury = max(0, attacker.fury - 10)
                val raw = (attacker.effectiveAttack * 0.75f * critMultiplier).toInt()
                val defMitigation = (defender.effectiveDefense * 0.3f).toInt()
                damage = max(5, raw - defMitigation)
                attacker.activeStatuses.add(ActiveStatus(StatusEffectType.SHIELD_WALL, 1))

                // 35% chance to stun
                if (Random.nextInt(100) < 35) {
                    statusToApply = StatusEffectType.STUNNED
                    defender.activeStatuses.add(ActiveStatus(StatusEffectType.STUNNED, 1))
                }
                customDesc = "${attacker.name} bashes ${defender.name} with shield for $damage damage & raises Shield Wall"
            }
            DuelAction.BERSERK_RAGE -> {
                attacker.fury = max(0, attacker.fury - 50)
                val raw = (attacker.effectiveAttack * 2.4f * (1.0f + Random.nextFloat() * 0.5f) * critMultiplier).toInt()
                val defMitigation = (defender.effectiveDefense * 0.25f).toInt()
                damage = max(32, raw - defMitigation)
                attacker.activeStatuses.add(ActiveStatus(StatusEffectType.ENRAGED, 2))
                statusToApply = StatusEffectType.ENRAGED
                customDesc = "🔥 BERSERK RAGE! ${attacker.name} ravages ${defender.name} for $damage lethal damage"
            }
            DuelAction.WAR_ROAR -> {
                attacker.fury = max(0, attacker.fury - 30)
                heal = 30
                attacker.currentHp = min(attacker.maxHp, attacker.currentHp + heal)
                attacker.fury = min(attacker.maxFury, attacker.fury + 5)
                customDesc = "📣 ${attacker.name} bellows an ancient War Roar, recovering $heal HP"
            }
        }

        if (statusToApply != null && statusToApply == StatusEffectType.BLEEDING) {
            defender.activeStatuses.add(ActiveStatus(StatusEffectType.BLEEDING, 2))
        }

        // Apply damage to defender
        defender.currentHp = max(0, defender.currentHp - damage)

        // Tick down status durations on attacker
        val statusIterator = attacker.activeStatuses.iterator()
        while (statusIterator.hasNext()) {
            val status = statusIterator.next()
            if (status.type != StatusEffectType.STUNNED) {
                status.turnsLeft -= 1
                if (status.turnsLeft <= 0) {
                    statusIterator.remove()
                }
            }
        }

        val critText = if (isCrit) " 💥 [CRITICAL HIT!]" else ""
        val statusText = when (statusToApply) {
            StatusEffectType.STUNNED -> " [STUNNED!]"
            StatusEffectType.BLEEDING -> " [BLEEDING APPLIED!]"
            StatusEffectType.ENRAGED -> " [ENRAGED!]"
            else -> ""
        }

        val log = "[Round $round • Turn $turn] $customDesc$critText$statusText. (${defender.name} HP: ${defender.currentHp}/${defender.maxHp})"

        return TurnResult(
            roundNumber = round,
            turnNumber = turn,
            attackerName = attacker.name,
            defenderName = defender.name,
            action = action,
            damageDealt = damage,
            healedAmount = heal,
            isCritical = isCrit,
            isDodged = false,
            isStunnedTurn = false,
            statusApplied = statusToApply,
            attackerHpRemaining = attacker.currentHp,
            defenderHpRemaining = defender.currentHp,
            attackerFuryRemaining = attacker.fury,
            defenderFuryRemaining = defender.fury,
            logMessage = log
        )
    }

    fun chooseAiAction(actor: Combatant, opponent: Combatant): DuelAction {
        val fury = actor.fury
        val hpPercent = (actor.currentHp.toFloat() / actor.maxHp)

        // 1. If fury is high and enemy is vulnerable or actor is low -> Berserk Rage
        if (fury >= 50 && (hpPercent < 0.6f || opponent.currentHp < 50)) {
            return DuelAction.BERSERK_RAGE
        }

        // 2. If low health and can afford heal -> War Roar
        if (hpPercent < 0.35f && fury >= 30) {
            return DuelAction.WAR_ROAR
        }

        // 3. If opponent is enraged and can afford shield bash -> Shield Bash
        if (fury >= 10 && opponent.activeStatuses.any { it.type == StatusEffectType.ENRAGED }) {
            return DuelAction.SHIELD_BASH
        }

        // 4. Random strategic choice
        val rand = Random.nextInt(100)
        return when {
            fury >= 10 && rand < 25 -> DuelAction.SHIELD_BASH
            rand < 65 -> DuelAction.HEAVY_CLEAVE
            else -> DuelAction.QUICK_STRIKE
        }
    }
}
