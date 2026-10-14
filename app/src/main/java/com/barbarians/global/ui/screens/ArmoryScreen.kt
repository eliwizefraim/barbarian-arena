package com.barbarians.global.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barbarians.global.model.EquipmentItem
import com.barbarians.global.model.EquipmentType
import com.barbarians.global.model.Warrior
import com.barbarians.global.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArmoryScreen(
    warrior: Warrior,
    items: List<EquipmentItem>,
    onBuyItem: (EquipmentItem) -> Unit,
    onEquipItem: (EquipmentItem) -> Unit,
    onReturnToMenu: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<EquipmentType?>(null) }

    val filteredItems = remember(selectedCategory, items) {
        if (selectedCategory == null) items else items.filter { it.type == selectedCategory }
    }

    Scaffold(
        containerColor = ArenaBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "THE ARMORY",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onReturnToMenu,
                        modifier = Modifier.testTag("armory_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Return",
                            tint = BarbarianGold
                        )
                    }
                },
                actions = {
                    Surface(
                        color = ArenaCardBg,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BarbarianGold),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "${warrior.gold} G",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = BarbarianGold,
                                fontWeight = FontWeight.Black
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ArenaDarkGray,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("ALL") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BarbarianCrimson,
                        selectedLabelColor = TextPrimary
                    )
                )
                FilterChip(
                    selected = selectedCategory == EquipmentType.WEAPON,
                    onClick = { selectedCategory = EquipmentType.WEAPON },
                    label = { Text("WEAPONS") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BarbarianCrimson,
                        selectedLabelColor = TextPrimary
                    )
                )
                FilterChip(
                    selected = selectedCategory == EquipmentType.ARMOR,
                    onClick = { selectedCategory = EquipmentType.ARMOR },
                    label = { Text("ARMOR") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BarbarianCrimson,
                        selectedLabelColor = TextPrimary
                    )
                )
                FilterChip(
                    selected = selectedCategory == EquipmentType.TALISMAN,
                    onClick = { selectedCategory = EquipmentType.TALISMAN },
                    label = { Text("TALISMANS") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BarbarianCrimson,
                        selectedLabelColor = TextPrimary
                    )
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredItems) { item ->
                    val isEquipped = item.id == warrior.equippedWeaponId || item.id == warrior.equippedArmorId
                    val canAfford = warrior.gold >= item.priceGold && warrior.glory >= item.priceGlory

                    Card(
                        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isEquipped) BarbarianGold else ArenaBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                    )
                                }

                                if (isEquipped) {
                                    Surface(
                                        color = BarbarianGold,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "EQUIPPED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ArenaBlack,
                                                fontWeight = FontWeight.Black
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Stat Buffs
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                if (item.attackBonus > 0) {
                                    Text(
                                        text = "+${item.attackBonus} ATK",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = BarbarianCrimson,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                if (item.defenseBonus > 0) {
                                    Text(
                                        text = "+${item.defenseBonus} DEF",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = DefenseBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                if (item.hpBonus > 0) {
                                    Text(
                                        text = "+${item.hpBonus} HP",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = HealthGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                if (item.furyBonus > 0) {
                                    Text(
                                        text = "+${item.furyBonus} FURY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = FuryOrange,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!item.isOwned) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "🪙 ${item.priceGold} Gold",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = BarbarianGold,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        if (item.priceGlory > 0) {
                                            Text(
                                                text = "🏆 ${item.priceGlory} Glory",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = BarbarianCrimson,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { onBuyItem(item) },
                                        enabled = canAfford,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (canAfford) BarbarianGold else ArenaBorder,
                                            contentColor = if (canAfford) ArenaBlack else TextMuted
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("PURCHASE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                } else if (!isEquipped) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Button(
                                        onClick = { onEquipItem(item) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = BarbarianCrimson,
                                            contentColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("EQUIP", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
