package com.barbarians.global.model

import com.barbarians.global.combat.Combatant

/**
 * Data class representing a Warrior entity with core RPG attributes:
 * - Strength (STR): Dictates physical striking power and critical force
 * - Agility (AGI): Dictates initiative speed, dodge chance, and reflex evasion
 * - Health (HP): Total vitality and endurance in the arena
 */
data class WarriorEntity(
    val id: String,
    val name: String,
    val title: String,
    val warriorClass: WarriorClass,
    val health: Int,
    val maxHealth: Int = health,
    val strength: Int,
    val agility: Int,
    val armor: Int = 12,
    val weaponName: String = "Broadsword",
    val armorName: String = "Gladiator Chain",
    val avatarEmoji: String = "⚔️",
    val description: String = ""
) {
    // Derived combat mechanics calculated from primary stats
    val attackPower: Int get() = strength * 2 + 10
    val dodgeChancePercent: Int get() = (agility * 0.8).toInt().coerceIn(5, 35)
    val critChancePercent: Int get() = (agility * 0.5 + strength * 0.3).toInt().coerceIn(6, 42)
    val battlePowerRating: Int get() = strength * 3 + agility * 2 + health / 2

    fun toCombatant(): Combatant = Combatant(
        id = id,
        name = name,
        warriorClass = warriorClass,
        level = ((strength + agility + health / 10) / 10).coerceAtLeast(1),
        maxHp = maxHealth,
        currentHp = health,
        baseAttack = attackPower,
        baseDefense = armor,
        speed = agility,
        weaponName = weaponName,
        armorName = armorName,
        fury = 10,
        avatarEmoji = avatarEmoji
    )
}

object WarriorEntityRepository {
    fun getPresetWarriors(): List<WarriorEntity> = listOf(
        WarriorEntity(
            id = "entity_ragnar",
            name = "Ragnar Ironjaw",
            title = "The Savage Executioner",
            warriorClass = WarriorClass.BERSERKER,
            health = 160,
            strength = 28,
            agility = 18,
            armor = 14,
            weaponName = "Bloodcleaver Greatsword",
            armorName = "Boar-hide Harness",
            avatarEmoji = "🪓",
            description = "High Strength juggernaut who strikes with crushing brute force."
        ),
        WarriorEntity(
            id = "entity_valerius",
            name = "Valerius Steelhand",
            title = "The Wall of Capua",
            warriorClass = WarriorClass.GLADIATOR,
            health = 185,
            strength = 22,
            agility = 14,
            armor = 26,
            weaponName = "Skullcrusher Mace",
            armorName = "Bloodforged Plate",
            avatarEmoji = "🛡️",
            description = "High Health and Armor stalwart who outlasts opponents."
        ),
        WarriorEntity(
            id = "entity_brynhild",
            name = "Brynhild Spear-Maiden",
            title = "Valkyrie of the Highlands",
            warriorClass = WarriorClass.VALKYRIE,
            health = 145,
            strength = 24,
            agility = 32,
            armor = 15,
            weaponName = "Stormbringer Spear",
            armorName = "Winged Leather Harness",
            avatarEmoji = "⚡",
            description = "Maximum Agility duelist who evades attacks and lands rapid strikes."
        ),
        WarriorEntity(
            id = "entity_vorok",
            name = "Vorok Bloodaxe",
            title = "Chieftain of the North",
            warriorClass = WarriorClass.WARLORD,
            health = 200,
            strength = 26,
            agility = 16,
            armor = 18,
            weaponName = "Crude Iron Greataxe",
            armorName = "Gladiator Chainmail",
            avatarEmoji = "🦁",
            description = "Balanced titan boasting mammoth Health and devastating strikes."
        ),
        WarriorEntity(
            id = "entity_grom",
            name = "Grom Thunderfist",
            title = "Mountain Marauder",
            warriorClass = WarriorClass.BERSERKER,
            health = 170,
            strength = 32,
            agility = 15,
            armor = 12,
            weaponName = "Dual Warcleavers",
            armorName = "Spiked Pauldrons",
            avatarEmoji = "💥",
            description = "Raw Strength powerhouse capable of annihilating opponents in seconds."
        ),
        WarriorEntity(
            id = "entity_crixus",
            name = "Crixus the Gallic Undefeated",
            title = "Colosseum Supreme",
            warriorClass = WarriorClass.GLADIATOR,
            health = 220,
            strength = 30,
            agility = 20,
            armor = 24,
            weaponName = "Champion Gladius",
            armorName = "Imperial Segmentata",
            avatarEmoji = "🏆",
            description = "Legendary arena champion with elite Strength, Agility, and Health."
        )
    )

    fun createFromPlayer(playerWarrior: Warrior): WarriorEntity = WarriorEntity(
        id = "entity_player",
        name = "${playerWarrior.name} (Player)",
        title = "Arena Hero",
        warriorClass = playerWarrior.warriorClass,
        health = playerWarrior.maxHp,
        strength = (playerWarrior.attack - 10) / 2,
        agility = 20,
        armor = playerWarrior.defense,
        weaponName = "Equipped Weapon",
        armorName = "Equipped Armor",
        avatarEmoji = "👑",
        description = "Your personal custom warrior trained in the barracks."
    )
}
