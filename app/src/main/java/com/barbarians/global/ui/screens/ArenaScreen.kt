package com.barbarians.global.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barbarians.global.model.*
import com.barbarians.global.ui.theme.*
import com.barbarians.global.viewmodel.ArenaUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaScreen(
    state: ArenaUiState,
    onStartBattle: (Opponent) -> Unit,
    onExecuteAction: (CombatActionType) -> Unit,
    onReturnToMenu: () -> Unit
) {
    Scaffold(
        containerColor = ArenaBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.activeOpponent != null) "THE COLOSSEUM" else "CHOOSE YOUR OPPONENT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onReturnToMenu,
                        modifier = Modifier.testTag("back_to_menu_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Return to Menu",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.activeOpponent == null) {
                // Opponent Selection List
                OpponentSelectionList(
                    opponents = state.opponents,
                    onSelect = onStartBattle
                )
            } else {
                // Active Combat View
                ActiveCombatView(
                    state = state,
                    onExecuteAction = onExecuteAction,
                    onReturnToMenu = onReturnToMenu
                )
            }
        }
    }
}

@Composable
private fun OpponentSelectionList(
    opponents: List<Opponent>,
    onSelect: (Opponent) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Select a challenger to fight for gold, blood, and glory:",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        items(opponents) { opp ->
            val diffColor = when (opp.difficulty) {
                "EASY" -> HealthGreen
                "MEDIUM" -> BarbarianGold
                "HARD" -> FuryOrange
                else -> BarbarianCrimson
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, ArenaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = opp.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = opp.title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary
                                )
                            )
                        }

                        Surface(
                            color = diffColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, diffColor.copy(alpha = 0.7f))
                        ) {
                            Text(
                                text = opp.difficulty,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = diffColor,
                                    fontWeight = FontWeight.Black
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "❤️ HP: ${opp.maxHp}",
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthGreen, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "⚔️ ATK: ${opp.attack}",
                            style = MaterialTheme.typography.bodySmall.copy(color = BarbarianCrimson, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "🛡️ DEF: ${opp.defense}",
                            style = MaterialTheme.typography.bodySmall.copy(color = DefenseBlue, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "🪙 +${opp.goldReward} Gold",
                                style = MaterialTheme.typography.labelSmall.copy(color = BarbarianGold)
                            )
                            Text(
                                text = "🏆 +${opp.gloryReward} Glory",
                                style = MaterialTheme.typography.labelSmall.copy(color = BarbarianCrimson)
                            )
                        }

                        Button(
                            onClick = { onSelect(opp) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BarbarianCrimson,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("fight_button_${opp.id}")
                        ) {
                            Text("FIGHT", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveCombatView(
    state: ArenaUiState,
    onExecuteAction: (CombatActionType) -> Unit,
    onReturnToMenu: () -> Unit
) {
    val opponent = state.activeOpponent ?: return
    val warrior = state.warrior
    val logListState = rememberLazyListState()

    LaunchedEffect(state.combatLogs.size) {
        if (state.combatLogs.isNotEmpty()) {
            logListState.animateScrollToItem(state.combatLogs.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. OPPONENT STATUS CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, BarbarianCrimson.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = opponent.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = opponent.title,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                    Text(
                        text = "${opponent.currentHp} / ${opponent.maxHp} HP",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthGreen
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Opponent HP Bar
                val oppProgress = (opponent.currentHp.toFloat() / opponent.maxHp).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { oppProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = if (oppProgress > 0.3f) HealthGreen else BarbarianCrimson,
                    trackColor = ArenaBlack
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. COMBAT LOG (Center Arena Action)
        Card(
            colors = CardDefaults.cardColors(containerColor = ArenaDarkGray),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, ArenaBorder),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(
                    text = "BATTLE ACTION LOG",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = BarbarianGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )

                HorizontalDivider(
                    color = ArenaBorder,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                LazyColumn(
                    state = logListState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(state.combatLogs) { log ->
                        val logColor = when {
                            log.isCritical -> BarbarianGold
                            log.isHeal -> HealthGreen
                            log.isPlayerAction -> TextPrimary
                            else -> FuryOrange
                        }

                        Text(
                            text = "• ${log.text}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = logColor,
                                fontWeight = if (log.isCritical) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. PLAYER COMBAT BARS
        Card(
            colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, ArenaBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = warrior.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "${warrior.currentHp} / ${warrior.maxHp} HP",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthGreen
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val playerHpProgress = (warrior.currentHp.toFloat() / warrior.maxHp).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { playerHpProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = HealthGreen,
                    trackColor = ArenaBlack
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Fury Meter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "FURY RAGE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FuryOrange
                        )
                    )
                    Text(
                        text = "${warrior.fury} / ${warrior.maxFury}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FuryOrange
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val furyProgress = (warrior.fury.toFloat() / warrior.maxFury).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { furyProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = FuryOrange,
                    trackColor = ArenaBlack
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. COMBAT ACTION BUTTONS
        if (state.battleStatus == BattleStatus.IN_PROGRESS) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onExecuteAction(CombatActionType.SLASH) },
                        enabled = !state.isExecutingTurn,
                        colors = ButtonDefaults.buttonColors(containerColor = BarbarianCrimson),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_slash")
                    ) {
                        Text("QUICK STRIKE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onExecuteAction(CombatActionType.HEAVY_CLEAVE) },
                        enabled = !state.isExecutingTurn,
                        colors = ButtonDefaults.buttonColors(containerColor = BarbarianDeepRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_heavy_cleave")
                    ) {
                        Text("HEAVY CLEAVE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onExecuteAction(CombatActionType.SHIELD_BASH) },
                        enabled = !state.isExecutingTurn && warrior.fury >= 10,
                        colors = ButtonDefaults.buttonColors(containerColor = DefenseBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_shield_bash")
                    ) {
                        Text("SHIELD SLAM (10)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onExecuteAction(CombatActionType.WAR_CRY) },
                        enabled = !state.isExecutingTurn && warrior.fury >= 30,
                        colors = ButtonDefaults.buttonColors(containerColor = HealthGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_war_cry")
                    ) {
                        Text("BLOOD ROAR (30)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Ultimate Berserk Rage
                val rageReady = warrior.fury >= 50
                Button(
                    onClick = { onExecuteAction(CombatActionType.BERSERK_RAGE) },
                    enabled = !state.isExecutingTurn && rageReady,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (rageReady) FuryOrange else ArenaCardBg,
                        contentColor = if (rageReady) TextPrimary else TextMuted
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("action_berserk_rage")
                ) {
                    Text(
                        text = if (rageReady) "⚡ UNLEASH BERSERK RAGE (50 FURY) ⚡" else "BERSERK RAGE (NEEDS 50 FURY)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            // Battle Ended - Victory / Defeat Display
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (state.battleStatus == BattleStatus.VICTORY) BarbarianDeepRed else ArenaDarkGray
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(2.dp, if (state.battleStatus == BattleStatus.VICTORY) BarbarianGold else BarbarianCrimson),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (state.battleStatus == BattleStatus.VICTORY) "VICTORY ACHIEVED!" else "FALLEN IN BATTLE",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = if (state.battleStatus == BattleStatus.VICTORY) BarbarianGold else BarbarianCrimson
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.battleStatus == BattleStatus.VICTORY) {
                        Text(
                            text = "Rewards: +${state.lastRewardGold} Gold • +${state.lastRewardGlory} Glory",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                            fontWeight = FontWeight.Bold
                        )
                        if (state.lastLeveledUp) {
                            Text(
                                text = "⭐ LEVEL UP! Warrior attributes increased!",
                                style = MaterialTheme.typography.bodySmall.copy(color = BarbarianGold),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            text = "Your wounds are tended to. Rise and fight again!",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onReturnToMenu,
                        colors = ButtonDefaults.buttonColors(containerColor = BarbarianGold, contentColor = ArenaBlack),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("battle_finish_button")
                    ) {
                        Text("CONTINUE TO MAIN MENU", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
