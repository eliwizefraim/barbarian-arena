package com.barbarians.global.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.barbarians.global.ui.theme.*

data class ChampionEntry(
    val rank: Int,
    val name: String,
    val title: String,
    val wins: Int,
    val winRate: String,
    val league: String,
    val isPlayer: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionsScreen(
    warrior: Warrior,
    onReturnToMenu: () -> Unit
) {
    val totalMatches = warrior.wins + warrior.losses
    val playerWinRate = if (totalMatches > 0) "${(warrior.wins * 100 / totalMatches)}%" else "100%"

    val leaderboard = listOf(
        ChampionEntry(1, "Malakor the Undefeated", "Grand Arena Tyrant", 142, "94%", "VALHALLA"),
        ChampionEntry(2, "Spartacus of Thrace", "Legendary Rebel", 98, "88%", "VALHALLA"),
        ChampionEntry(3, "Flamma the Syrian", "Four-Time Wooden Sword", 67, "82%", "BLOOD ARENA"),
        ChampionEntry(4, warrior.name, "${warrior.warriorClass.displayName} (YOU)", warrior.wins, playerWinRate, "SILVER PIT", isPlayer = true),
        ChampionEntry(5, "Boris Bloodaxe", "Northland Marauder", 29, "65%", "BRONZE PIT"),
        ChampionEntry(6, "Crixus the Gallic Champion", "Arena Veteran", 24, "59%", "BRONZE PIT")
    )

    Scaffold(
        containerColor = ArenaBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HALL OF CHAMPIONS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onReturnToMenu,
                        modifier = Modifier.testTag("champions_back_button")
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BarbarianDeepRed.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BarbarianGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "COLOSSEUM LEAGUE STANDINGS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = BarbarianGold,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Top gladiators who conquered the sands of death. Defeat higher champions to claim immortal Valhalla glory.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "GLADIATOR LEADERBOARD",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = BarbarianGold,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            items(leaderboard.size) { index ->
                val entry = leaderboard[index]
                val rankColor = when (entry.rank) {
                    1 -> BarbarianGold
                    2 -> Color(0xFFC0C0C0)
                    3 -> Color(0xFFCD7F32)
                    else -> TextSecondary
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (entry.isPlayer) BarbarianDeepRed.copy(alpha = 0.45f) else ArenaCardBg
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        if (entry.isPlayer) BarbarianGold else ArenaBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = rankColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, rankColor),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "#${entry.rank}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = rankColor,
                                        fontWeight = FontWeight.Black
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = entry.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (entry.isPlayer) BarbarianGold else TextPrimary
                                    )
                                )
                                if (entry.isPlayer) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = BarbarianGold,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "YOU",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ArenaBlack,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 9.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = entry.title,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${entry.wins} Wins",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HealthGreen
                                )
                            )
                            Text(
                                text = entry.winRate,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
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
