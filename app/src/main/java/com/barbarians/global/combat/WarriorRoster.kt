package com.barbarians.global.combat

import com.barbarians.global.model.Warrior
import com.barbarians.global.model.WarriorClass

object WarriorRoster {

    fun getDefaultDuelists(playerWarrior: Warrior): List<Combatant> {
        val playerAsCombatant = Combatant(
            id = "combatant_player",
            name = "${playerWarrior.name} (Player)",
            warriorClass = playerWarrior.warriorClass,
            level = playerWarrior.level,
            maxHp = playerWarrior.maxHp,
            currentHp = playerWarrior.maxHp,
            baseAttack = playerWarrior.attack,
            baseDefense = playerWarrior.defense,
            speed = 18,
            weaponName = "Equipped Weapon",
            armorName = "Equipped Armor",
            fury = 10,
            avatarEmoji = "👑"
        )

        val presetDuelists = listOf(
            Combatant(
                id = "warrior_ragnar",
                name = "Ragnar Ironjaw",
                warriorClass = WarriorClass.BERSERKER,
                level = 3,
                maxHp = 150,
                currentHp = 150,
                baseAttack = 32,
                baseDefense = 14,
                speed = 16,
                weaponName = "Bloodcleaver Greatsword",
                armorName = "Boar-hide Harness",
                fury = 15,
                avatarEmoji = "🪓"
            ),
            Combatant(
                id = "warrior_valerius",
                name = "Valerius the Shield",
                warriorClass = WarriorClass.GLADIATOR,
                level = 3,
                maxHp = 170,
                currentHp = 170,
                baseAttack = 26,
                baseDefense = 24,
                speed = 13,
                weaponName = "Skullcrusher Mace",
                armorName = "Bloodforged Cuirass",
                fury = 10,
                avatarEmoji = "🛡️"
            ),
            Combatant(
                id = "warrior_vorok",
                name = "Vorok the Chieftain",
                warriorClass = WarriorClass.WARLORD,
                level = 4,
                maxHp = 195,
                currentHp = 195,
                baseAttack = 28,
                baseDefense = 18,
                speed = 14,
                weaponName = "Crude Iron Greataxe",
                armorName = "Gladiator Chainmail",
                fury = 20,
                avatarEmoji = "🦁"
            ),
            Combatant(
                id = "warrior_brynhild",
                name = "Brynhild Spear-Maiden",
                warriorClass = WarriorClass.VALKYRIE,
                level = 3,
                maxHp = 140,
                currentHp = 140,
                baseAttack = 34,
                baseDefense = 16,
                speed = 22,
                weaponName = "Stormbringer Spear",
                armorName = "Winged Leather Harness",
                fury = 15,
                avatarEmoji = "⚡"
            ),
            Combatant(
                id = "warrior_grom",
                name = "Grom Thunderfist",
                warriorClass = WarriorClass.BERSERKER,
                level = 4,
                maxHp = 165,
                currentHp = 165,
                baseAttack = 36,
                baseDefense = 12,
                speed = 15,
                weaponName = "Dual Warcleavers",
                armorName = "Spiked Pauldrons",
                fury = 25,
                avatarEmoji = "💥"
            ),
            Combatant(
                id = "warrior_crixus",
                name = "Crixus the Colosseum Tyrant",
                warriorClass = WarriorClass.GLADIATOR,
                level = 5,
                maxHp = 210,
                currentHp = 210,
                baseAttack = 38,
                baseDefense = 26,
                speed = 16,
                weaponName = "Champion Gladius",
                armorName = "Imperial Segmentata",
                fury = 15,
                avatarEmoji = "🏆"
            )
        )

        return listOf(playerAsCombatant) + presetDuelists
    }
}
