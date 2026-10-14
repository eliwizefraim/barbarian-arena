package com.barbarians.global.model

enum class EquipmentType {
    WEAPON,
    ARMOR,
    TALISMAN
}

data class EquipmentItem(
    val id: String,
    val name: String,
    val type: EquipmentType,
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val hpBonus: Int = 0,
    val furyBonus: Int = 0,
    val priceGold: Int = 0,
    val priceGlory: Int = 0,
    val description: String,
    val isOwned: Boolean = false
)

object ArmoryCatalog {
    val items = listOf(
        // Weapons
        EquipmentItem(
            id = "wpn_iron_axe",
            name = "Crude Iron Axe",
            type = EquipmentType.WEAPON,
            attackBonus = 5,
            priceGold = 0,
            description = "A notched battleaxe forged from bog iron.",
            isOwned = true
        ),
        EquipmentItem(
            id = "wpn_spiked_mace",
            name = "Skullcrusher Mace",
            type = EquipmentType.WEAPON,
            attackBonus = 12,
            priceGold = 80,
            description = "Heavy iron flail with serrated spikes.",
            isOwned = false
        ),
        EquipmentItem(
            id = "wpn_blood_cleaver",
            name = "Bloodcleaver Greatsword",
            type = EquipmentType.WEAPON,
            attackBonus = 22,
            furyBonus = 10,
            priceGold = 180,
            description = "Two-handed blade quenched in dragon blood.",
            isOwned = false
        ),
        EquipmentItem(
            id = "wpn_valhalla_axe",
            name = "Stormbringer Greataxe",
            type = EquipmentType.WEAPON,
            attackBonus = 35,
            furyBonus = 25,
            priceGold = 350,
            priceGlory = 50,
            description = "Legendary axe pulsing with thunder and fury.",
            isOwned = false
        ),
        // Armors
        EquipmentItem(
            id = "arm_leather_harness",
            name = "Boar-hide Harness",
            type = EquipmentType.ARMOR,
            defenseBonus = 4,
            hpBonus = 20,
            priceGold = 0,
            description = "Stitched animal hide providing basic protection.",
            isOwned = true
        ),
        EquipmentItem(
            id = "arm_gladiator_chain",
            name = "Gladiator Chainmail",
            type = EquipmentType.ARMOR,
            defenseBonus = 10,
            hpBonus = 45,
            priceGold = 100,
            description = "Riveted iron rings worn by arena champions.",
            isOwned = false
        ),
        EquipmentItem(
            id = "arm_spiked_plate",
            name = "Bloodforged Spiked Cuirass",
            type = EquipmentType.ARMOR,
            defenseBonus = 20,
            hpBonus = 90,
            priceGold = 240,
            description = "Heavy blackened steel with outwards-facing razor spikes.",
            isOwned = false
        ),
        // Talismans
        EquipmentItem(
            id = "tal_wolf_tooth",
            name = "Direwolf Tooth",
            type = EquipmentType.TALISMAN,
            attackBonus = 6,
            furyBonus = 15,
            priceGold = 120,
            description = "Infuses the bearer with savage predatory instinct.",
            isOwned = false
        ),
        EquipmentItem(
            id = "tal_warlord_banner",
            name = "Signet of the Chieftain",
            type = EquipmentType.TALISMAN,
            hpBonus = 60,
            defenseBonus = 8,
            priceGold = 220,
            priceGlory = 30,
            description = "A carved bone talisman giving the blessing of ancestors.",
            isOwned = false
        )
    )
}
