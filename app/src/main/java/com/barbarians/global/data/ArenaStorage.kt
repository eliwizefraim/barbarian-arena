package com.barbarians.global.data

import android.content.Context
import android.content.SharedPreferences
import com.barbarians.global.model.EquipmentItem
import com.barbarians.global.model.Warrior
import com.barbarians.global.model.WarriorClass

class ArenaStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("barbarian_arena_prefs", Context.MODE_PRIVATE)

    fun saveWarrior(warrior: Warrior) {
        prefs.edit().apply {
            putString("warrior_name", warrior.name)
            putString("warrior_class", warrior.warriorClass.name)
            putInt("warrior_level", warrior.level)
            putInt("warrior_xp", warrior.currentXp)
            putInt("warrior_max_xp", warrior.maxXp)
            putInt("warrior_hp", warrior.currentHp)
            putInt("warrior_max_hp", warrior.maxHp)
            putInt("warrior_attack", warrior.attack)
            putInt("warrior_defense", warrior.defense)
            putInt("warrior_fury", warrior.fury)
            putInt("warrior_gold", warrior.gold)
            putInt("warrior_glory", warrior.glory)
            putInt("warrior_wins", warrior.wins)
            putInt("warrior_losses", warrior.losses)
            putInt("warrior_streak", warrior.streak)
            putInt("warrior_highest_streak", warrior.highestStreak)
            putString("equipped_weapon", warrior.equippedWeaponId)
            putString("equipped_armor", warrior.equippedArmorId)
            apply()
        }
    }

    fun loadWarrior(): Warrior {
        val name = prefs.getString("warrior_name", "Ragnar Ironjaw") ?: "Ragnar Ironjaw"
        val className = prefs.getString("warrior_class", WarriorClass.BERSERKER.name) ?: WarriorClass.BERSERKER.name
        val warriorClass = try {
            WarriorClass.valueOf(className)
        } catch (e: Exception) {
            WarriorClass.BERSERKER
        }

        return Warrior(
            name = name,
            warriorClass = warriorClass,
            level = prefs.getInt("warrior_level", 1),
            currentXp = prefs.getInt("warrior_xp", 0),
            maxXp = prefs.getInt("warrior_max_xp", 100),
            currentHp = prefs.getInt("warrior_hp", 120),
            maxHp = prefs.getInt("warrior_max_hp", 120),
            attack = prefs.getInt("warrior_attack", 24),
            defense = prefs.getInt("warrior_defense", 14),
            fury = prefs.getInt("warrior_fury", 10),
            gold = prefs.getInt("warrior_gold", 150),
            glory = prefs.getInt("warrior_glory", 40),
            wins = prefs.getInt("warrior_wins", 0),
            losses = prefs.getInt("warrior_losses", 0),
            streak = prefs.getInt("warrior_streak", 0),
            highestStreak = prefs.getInt("warrior_highest_streak", 0),
            equippedWeaponId = prefs.getString("equipped_weapon", "wpn_iron_axe") ?: "wpn_iron_axe",
            equippedArmorId = prefs.getString("equipped_armor", "arm_leather_harness") ?: "arm_leather_harness"
        )
    }

    fun saveOwnedItemIds(ownedIds: Set<String>) {
        prefs.edit().putStringSet("owned_items", ownedIds).apply()
    }

    fun loadOwnedItemIds(): Set<String> {
        return prefs.getStringSet("owned_items", setOf("wpn_iron_axe", "arm_leather_harness"))
            ?: setOf("wpn_iron_axe", "arm_leather_harness")
    }

    fun saveLanguage(isSwahili: Boolean) {
        prefs.edit().putBoolean("is_swahili", isSwahili).apply()
    }

    fun loadLanguage(): Boolean {
        return prefs.getBoolean("is_swahili", false)
    }
}
