package com.barbarians.global.model

enum class CombatActionType(
    val title: String,
    val furyCost: Int,
    val description: String
) {
    SLASH("Quick Strike", 0, "Swift attack with standard damage (+15 Fury)"),
    HEAVY_CLEAVE("Heavy Cleave", 0, "Brutal strike with high damage variance (+25 Fury)"),
    SHIELD_BASH("Shield Slam", 10, "Bludgeon foe, reducing their incoming counterattack"),
    BERSERK_RAGE("Berserk Rage", 50, "Devastating lethal blow consuming boiling rage!"),
    WAR_CRY("Blood Roar", 30, "Heal 25 HP and empower next turn's offensive power")
}

data class Opponent(
    val id: String,
    val name: String,
    val title: String,
    val maxHp: Int,
    var currentHp: Int,
    val attack: Int,
    val defense: Int,
    val goldReward: Int,
    val gloryReward: Int,
    val avatarTag: String,
    val difficulty: String
)

data class CombatLogEntry(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isPlayerAction: Boolean = true,
    val isCritical: Boolean = false,
    val damageDealt: Int = 0,
    val isHeal: Boolean = false
)

enum class BattleStatus {
    IN_PROGRESS,
    VICTORY,
    DEFEAT
}

object OpponentCatalog {
    fun createOpponents(): List<Opponent> = listOf(
        Opponent(
            id = "opp_pit_brawler",
            name = "Gorok the Thug",
            title = "Novice Pit Brawler",
            maxHp = 80,
            currentHp = 80,
            attack = 16,
            defense = 6,
            goldReward = 45,
            gloryReward = 15,
            avatarTag = "BRAWLER",
            difficulty = "EASY"
        ),
        Opponent(
            id = "opp_iron_gladiator",
            name = "Valerius Steelhand",
            title = "Chainmail Veteran",
            maxHp = 130,
            currentHp = 130,
            attack = 24,
            defense = 14,
            goldReward = 85,
            gloryReward = 30,
            avatarTag = "GLADIATOR",
            difficulty = "MEDIUM"
        ),
        Opponent(
            id = "opp_bloodfang_beast",
            name = "Bloodfang Direwolf",
            title = "Savage Beast of the Steppes",
            maxHp = 160,
            currentHp = 160,
            attack = 32,
            defense = 12,
            goldReward = 120,
            gloryReward = 45,
            avatarTag = "BEAST",
            difficulty = "HARD"
        ),
        Opponent(
            id = "opp_arena_champion",
            name = "Malakor the Undefeated",
            title = "Grand Colosseum Tyrant",
            maxHp = 230,
            currentHp = 230,
            attack = 42,
            defense = 22,
            goldReward = 250,
            gloryReward = 100,
            avatarTag = "CHAMPION",
            difficulty = "EXTREME"
        )
    )
}
