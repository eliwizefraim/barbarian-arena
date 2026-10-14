package com.barbarians.global.model

enum class WarriorClass(val displayName: String, val description: String, val passiveBonus: String) {
    BERSERKER("Berserker", "Relentless savage driven by boiling blood fury.", "+25% Fury generation and critical strike chance"),
    GLADIATOR("Gladiator", "Disciplined arena veteran skilled in blade and shield.", "+20% Defense and counters enemy attacks"),
    WARLORD("Warlord", "Tribal chieftain whose battle cry strikes terror.", "+30% Health and empowered battle cries"),
    VALKYRIE("Valkyrie", "Swift winged spear maiden delivering deadly thrusts.", "+20% Attack speed and dodge chance")
}

data class Warrior(
    val id: String = "player_1",
    val name: String = "Ragnar Ironjaw",
    val warriorClass: WarriorClass = WarriorClass.BERSERKER,
    val level: Int = 1,
    val currentXp: Int = 0,
    val maxXp: Int = 100,
    val currentHp: Int = 120,
    val maxHp: Int = 120,
    val attack: Int = 24,
    val defense: Int = 14,
    val fury: Int = 0,
    val maxFury: Int = 100,
    val gold: Int = 150,
    val glory: Int = 40,
    val wins: Int = 0,
    val losses: Int = 0,
    val streak: Int = 0,
    val highestStreak: Int = 0,
    val equippedWeaponId: String = "wpn_iron_axe",
    val equippedArmorId: String = "arm_leather_harness"
)
