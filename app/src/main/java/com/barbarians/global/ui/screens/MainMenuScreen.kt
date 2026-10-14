package com.barbarians.global.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barbarians.global.model.Warrior
import com.barbarians.global.ui.theme.*

@Composable
fun MainMenuScreen(
    warrior: Warrior,
    isSwahili: Boolean,
    dailyBountyClaimed: Boolean,
    onToggleLanguage: () -> Unit,
    onClaimDailyBounty: () -> Unit,
    onNavigateToArena: () -> Unit,
    onNavigateToDuel: () -> Unit,
    onNavigateToParticipants: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToBarracks: () -> Unit,
    onNavigateToArmory: () -> Unit,
    onNavigateToChampions: () -> Unit,
    onNavigateToDevGuide: () -> Unit
) {
    Scaffold(
        containerColor = ArenaBlack,
        topBar = {
            TopAppBarHero(
                warrior = warrior,
                isSwahili = isSwahili,
                onToggleLanguage = onToggleLanguage
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                WarriorBannerCard(
                    warrior = warrior,
                    isSwahili = isSwahili,
                    onEditWarrior = onNavigateToBarracks
                )
            }

            // Daily Bounty Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (!dailyBountyClaimed) BarbarianGold else ArenaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isSwahili) "🎁 Zawadi ya Kila Siku (Daily Bounty)" else "🎁 Daily Arena Bounty",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BarbarianGold
                                )
                            )
                            Text(
                                text = if (isSwahili) "+75 Dhahabu & +25 Utukufu bure" else "+75 Gold & +25 Glory free gift",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }

                        Button(
                            onClick = onClaimDailyBounty,
                            enabled = !dailyBountyClaimed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!dailyBountyClaimed) BarbarianGold else ArenaBorder,
                                contentColor = if (!dailyBountyClaimed) ArenaBlack else TextMuted
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("claim_bounty_button")
                        ) {
                            Text(
                                text = if (dailyBountyClaimed) (if (isSwahili) "IMEPATIKANA" else "CLAIMED") else (if (isSwahili) "POKEA" else "CLAIM"),
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = if (isSwahili) "SHUGHULI ZA UWANJANI" else "ARENA ACTIVITIES",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = BarbarianGold,
                        letterSpacing = 1.2.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            item {
                MenuFeatureCard(
                    title = if (isSwahili) "UWANYA WA VITA (PVE ARENA)" else "BATTLE ARENA (VS FOES)",
                    subtitle = if (isSwahili) "Ingia kwenye uwanja wa mchanga. Pambana na wapinzani na wanyama wakali." else "Step into the fighting pit. Slay rival champions and beasts.",
                    badge = if (isSwahili) "PIGANA SASA" else "FIGHT NOW",
                    badgeColor = BarbarianCrimson,
                    icon = Icons.Default.PlayArrow,
                    accentColor = BarbarianCrimson,
                    testTag = "menu_battle_arena",
                    onClick = onNavigateToArena
                )
            }

            item {
                MenuFeatureCard(
                    title = if (isSwahili) "MPAMBANO WA MASHUJAA (DUEL ENGINE)" else "WARRIOR DUEL (2 FIGHTERS)",
                    subtitle = if (isSwahili) "Chagua mashujaa wawili na uangalie mapambano ya moja kwa moja ya live turn-by-turn logs!" else "Select 2 warriors from the roster and watch live turn-based combat with detailed battle logs!",
                    badge = "2 WARRIORS",
                    badgeColor = BarbarianGold,
                    icon = Icons.Default.PlayArrow,
                    accentColor = BarbarianGold,
                    testTag = "menu_warrior_duel",
                    onClick = onNavigateToDuel
                )
            }

            item {
                MenuFeatureCard(
                    title = if (isSwahili) "WASHIRIKI WA UWANJA (STR, AGI, HP)" else "ARENA PARTICIPANTS (STATS)",
                    subtitle = if (isSwahili) "Angalia orodha kamili ya mashujaa kulingana na Strength, Agility, na Health, na teua wapiganaji!" else "View warrior roster with Strength, Agility, and Health stats, filter attributes, and assign combatants!",
                    badge = "STR • AGI • HP",
                    badgeColor = DefenseBlue,
                    icon = Icons.Default.Person,
                    accentColor = DefenseBlue,
                    testTag = "menu_participants",
                    onClick = onNavigateToParticipants
                )
            }

            item {
                MenuFeatureCard(
                    title = if (isSwahili) "KAMBI YA MASHUJAA (BARRACKS)" else "WARRIOR BARRACKS",
                    subtitle = if (isSwahili) "Pandisha sifa (HP, Attack, Defense), chagua aina ya shujaa wako." else "Level up attributes, change combat class, view combat record.",
                    badge = "LVL ${warrior.level}",
                    badgeColor = BarbarianGold,
                    icon = Icons.Default.Person,
                    accentColor = BarbarianGold,
                    testTag = "menu_barracks",
                    onClick = onNavigateToBarracks
                )
            }

            item {
                MenuFeatureCard(
                    title = if (isSwahili) "GALA YA SILAHA (ARMORY)" else "THE ARMORY",
                    subtitle = if (isSwahili) "Nunua na vaa mapanga makali, mashoka, ngao zenye miiba na hirizi za vita." else "Equip brutal battleaxes, spiked shields, and ancient war runes.",
                    badge = "${warrior.gold} G",
                    badgeColor = BarbarianAmber,
                    icon = Icons.Default.Build,
                    accentColor = BarbarianAmber,
                    testTag = "menu_armory",
                    onClick = onNavigateToArmory
                )
            }

            item {
                MenuFeatureCard(
                    title = if (isSwahili) "UKUMBI WA MABINGWA (CHAMPIONS)" else "HALL OF CHAMPIONS",
                    subtitle = if (isSwahili) "Orodha ya washindi, viwango vya ligi na utukufu wa kudumu." else "Gladiator leaderboard, arena leagues, and legendary prestige.",
                    badge = "${warrior.wins} " + if (isSwahili) "USHINDI" else "WINS",
                    badgeColor = HealthGreen,
                    icon = Icons.Default.Star,
                    accentColor = HealthGreen,
                    testTag = "menu_champions",
                    onClick = onNavigateToChampions
                )
            }

            item {
                MenuFeatureCard(
                    title = if (isSwahili) "HISTORIA YA VITA (BATTLE LOGS)" else "BATTLE LOG HISTORY",
                    subtitle = if (isSwahili) "Kanzidata ya Room: Tazama matokeo, idadi ya raundi, na washindi wa vita vilivyopita!" else "Local Room DB: Review past arena outcomes, rounds lasted, and duel summaries!",
                    badge = "ROOM DB",
                    badgeColor = BarbarianGold,
                    icon = Icons.Default.Info,
                    accentColor = BarbarianGold,
                    testTag = "menu_battle_history",
                    onClick = onNavigateToHistory
                )
            }

            item {
                MenuFeatureCard(
                    title = if (isSwahili) "PAKUA & MWONGOZO WA USANIKISHAJI" else "DOWNLOAD & INSTALL GUIDE",
                    subtitle = if (isSwahili) "Jinsi ya kupakua APK, kusanikisha kwenye simu, na leseni ya MIT." else "How to download APK, install on Android phones, and MIT License.",
                    badge = "APK / DOCS",
                    badgeColor = DefenseBlue,
                    icon = Icons.Default.Info,
                    accentColor = DefenseBlue,
                    testTag = "menu_dev_guide",
                    onClick = onNavigateToDevGuide
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TopAppBarHero(
    warrior: Warrior,
    isSwahili: Boolean,
    onToggleLanguage: () -> Unit
) {
    Surface(
        color = ArenaDarkGray,
        border = BorderStroke(1.dp, ArenaBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(BarbarianCrimson, CircleShape)
                        .border(1.dp, BarbarianGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚔",
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "BARBARIAN ARENA",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = warrior.warriorClass.displayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BarbarianGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Language Switcher Button
                Surface(
                    color = ArenaCardBg,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BarbarianGold.copy(alpha = 0.6f)),
                    modifier = Modifier.clickable { onToggleLanguage() }
                ) {
                    Text(
                        text = if (isSwahili) "🇹🇿 SW" else "🇬🇧 EN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BarbarianGold,
                            fontWeight = FontWeight.Black
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                ResourcePill(
                    icon = "🪙",
                    value = "${warrior.gold}",
                    color = BarbarianGold
                )
                ResourcePill(
                    icon = "🏆",
                    value = "${warrior.glory}",
                    color = BarbarianCrimson
                )
            }
        }
    }
}

@Composable
private fun ResourcePill(icon: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .background(ArenaCardBg, RoundedCornerShape(16.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
    }
}

@Composable
private fun WarriorBannerCard(
    warrior: Warrior,
    isSwahili: Boolean,
    onEditWarrior: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BarbarianCrimson.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditWarrior)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            BarbarianDeepRed.copy(alpha = 0.35f),
                            ArenaCardBg
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = warrior.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = if (isSwahili) {
                        "Kiwango ${warrior.level} ${warrior.warriorClass.displayName} • Ushindi ${warrior.wins} / Hasara ${warrior.losses}"
                    } else {
                        "Level ${warrior.level} ${warrior.warriorClass.displayName} • ${warrior.wins} W / ${warrior.losses} L"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "HP: ${warrior.maxHp}",
                        style = MaterialTheme.typography.labelSmall.copy(color = HealthGreen, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "ATK: ${warrior.attack}",
                        style = MaterialTheme.typography.labelSmall.copy(color = BarbarianCrimson, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "DEF: ${warrior.defense}",
                        style = MaterialTheme.typography.labelSmall.copy(color = DefenseBlue, fontWeight = FontWeight.Bold)
                    )
                }
            }

            IconButton(
                onClick = onEditWarrior,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = BarbarianCrimson.copy(alpha = 0.2f),
                    contentColor = BarbarianGold
                )
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Warrior")
            }
        }
    }
}

@Composable
private fun MenuFeatureCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, ArenaBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(accentColor.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Surface(
                        color = badgeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = badgeColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary
                    )
                )
            }
        }
    }
}
