package com.barbarians.global.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barbarians.global.model.Warrior
import com.barbarians.global.model.WarriorClass
import com.barbarians.global.ui.theme.*
import com.barbarians.global.viewmodel.UpgradeableStat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarracksScreen(
    warrior: Warrior,
    onUpgradeStat: (UpgradeableStat) -> Unit,
    onSelectClass: (WarriorClass) -> Unit,
    onReturnToMenu: () -> Unit
) {
    Scaffold(
        containerColor = ArenaBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "WARRIOR BARRACKS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onReturnToMenu,
                        modifier = Modifier.testTag("barracks_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Return",
                            tint = BarbarianGold
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile & EXP
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, ArenaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = warrior.name,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "Level ${warrior.level} ${warrior.warriorClass.displayName}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = BarbarianGold)
                                )
                            }

                            Surface(
                                color = BarbarianGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, BarbarianGold)
                            ) {
                                Text(
                                    text = "${warrior.gold} GOLD",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = BarbarianGold,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // EXP Progress
                        Text(
                            text = "EXPERIENCE: ${warrior.currentXp} / ${warrior.maxXp} XP",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val xpProgress = (warrior.currentXp.toFloat() / warrior.maxXp).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { xpProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = BarbarianGold,
                            trackColor = ArenaBlack
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🏆 Victories: ${warrior.wins}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                            )
                            Text(
                                text = "💀 Defeats: ${warrior.losses}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                            Text(
                                text = "🔥 Streak: ${warrior.streak}",
                                style = MaterialTheme.typography.bodySmall.copy(color = FuryOrange)
                            )
                        }
                    }
                }
            }

            // Stat Upgrades Section
            item {
                Text(
                    text = "ATTRIBUTE TRAINING (SPEND GOLD)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = BarbarianGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            item {
                StatUpgradeCard(
                    statName = "HEALTH POINTS (HP)",
                    currentValue = "${warrior.maxHp} HP",
                    bonusInfo = "+20 Max Health",
                    cost = 40 + (warrior.maxHp - 120) / 2,
                    playerGold = warrior.gold,
                    accentColor = HealthGreen,
                    onUpgrade = { onUpgradeStat(UpgradeableStat.HEALTH) }
                )
            }

            item {
                StatUpgradeCard(
                    statName = "ATTACK POWER (ATK)",
                    currentValue = "${warrior.attack} ATK",
                    bonusInfo = "+3 Physical Attack",
                    cost = 50 + (warrior.attack - 24) * 3,
                    playerGold = warrior.gold,
                    accentColor = BarbarianCrimson,
                    onUpgrade = { onUpgradeStat(UpgradeableStat.ATTACK) }
                )
            }

            item {
                StatUpgradeCard(
                    statName = "ARMOR DEFENSE (DEF)",
                    currentValue = "${warrior.defense} DEF",
                    bonusInfo = "+2 Damage Reduction",
                    cost = 45 + (warrior.defense - 14) * 3,
                    playerGold = warrior.gold,
                    accentColor = DefenseBlue,
                    onUpgrade = { onUpgradeStat(UpgradeableStat.DEFENSE) }
                )
            }

            // Warrior Class Selection
            item {
                Text(
                    text = "SELECT WARRIOR CLASS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = BarbarianGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            items(WarriorClass.entries.size) { index ->
                val cls = WarriorClass.entries[index]
                val isSelected = warrior.warriorClass == cls

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) BarbarianDeepRed.copy(alpha = 0.4f) else ArenaCardBg
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) BarbarianGold else ArenaBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectClass(cls) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cls.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) BarbarianGold else TextPrimary
                                )
                            )
                            if (isSelected) {
                                Surface(
                                    color = BarbarianGold,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ArenaBlack,
                                            fontWeight = FontWeight.Black
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cls.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Passive: ${cls.passiveBonus}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BarbarianGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatUpgradeCard(
    statName: String,
    currentValue: String,
    bonusInfo: String,
    cost: Int,
    playerGold: Int,
    accentColor: Color,
    onUpgrade: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ArenaBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = statName,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = currentValue,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = accentColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = bonusInfo,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }

            val canAfford = playerGold >= cost
            Button(
                onClick = onUpgrade,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) BarbarianGold else ArenaBorder,
                    contentColor = if (canAfford) ArenaBlack else TextMuted
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Upgrade", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$cost G",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
