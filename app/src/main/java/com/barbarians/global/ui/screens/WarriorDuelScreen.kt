package com.barbarians.global.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barbarians.global.combat.Combatant
import com.barbarians.global.combat.StatusEffectType
import com.barbarians.global.combat.TurnResult
import com.barbarians.global.ui.theme.*
import com.barbarians.global.viewmodel.ArenaUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarriorDuelScreen(
    state: ArenaUiState,
    onSelectFighter1: (Combatant) -> Unit,
    onSelectFighter2: (Combatant) -> Unit,
    onStartDuel: () -> Unit,
    onStepTurn: () -> Unit,
    onToggleAuto: () -> Unit,
    onSetSpeed: (Float) -> Unit,
    onResetDuel: () -> Unit,
    onReturnToMenu: () -> Unit
) {
    val isSwahili = state.isSwahili

    Scaffold(
        containerColor = ArenaBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isDuelActive) {
                            if (isSwahili) "MAPAMBANO YA UWANJANI" else "WARRIOR DUEL ARENA"
                        } else {
                            if (isSwahili) "CHAGUA WAPIGANAJI WAWILI" else "SELECT 2 WARRIORS TO DUEL"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onReturnToMenu,
                        modifier = Modifier.testTag("duel_back_button")
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!state.isDuelActive) {
                // Setup & Select 2 Warriors
                DuelMatchupSetupView(
                    state = state,
                    isSwahili = isSwahili,
                    onSelectFighter1 = onSelectFighter1,
                    onSelectFighter2 = onSelectFighter2,
                    onStartDuel = onStartDuel
                )
            } else {
                // Live Turn-Based Battle View
                LiveDuelArenaView(
                    state = state,
                    isSwahili = isSwahili,
                    onStepTurn = onStepTurn,
                    onToggleAuto = onToggleAuto,
                    onSetSpeed = onSetSpeed,
                    onResetDuel = onResetDuel
                )
            }
        }
    }
}

@Composable
private fun DuelMatchupSetupView(
    state: ArenaUiState,
    isSwahili: Boolean,
    onSelectFighter1: (Combatant) -> Unit,
    onSelectFighter2: (Combatant) -> Unit,
    onStartDuel: () -> Unit
) {
    val roster = state.duelAvailableRoster
    val fighter1 = state.duelFighter1
    val fighter2 = state.duelFighter2

    var choosingForFighterSlot by remember { mutableStateOf<Int?>(null) } // 1 or 2

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BarbarianDeepRed.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BarbarianGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isSwahili) "⚔️ INJINI YA VITA: MPAMBANO WA MASHUJAA WAWILI" else "⚔️ COMBAT ENGINE: TWO WARRIORS DUEL",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = BarbarianGold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isSwahili) {
                            "Chagua shujaa wa 1 na shujaa wa 2 kutoka orodha ya wapiganaji. Tazama vita vikiendelea hatua kwa hatua kwa mfumo kamili wa mahesabu ya nguvu, hasira (fury), na ulinzi!"
                        } else {
                            "Select Warrior 1 and Warrior 2 from the arena roster. Watch them battle live turn-by-turn with simulated damage, critical strikes, fury buildup, and status effects!"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // MATCHUP PREVIEW (VS LAYOUT)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fighter 1 Card
                Box(modifier = Modifier.weight(1f)) {
                    FighterSlotCard(
                        slotNumber = 1,
                        combatant = fighter1,
                        isSwahili = isSwahili,
                        accentColor = DefenseBlue,
                        onClick = { choosingForFighterSlot = 1 }
                    )
                }

                // VS Badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(BarbarianCrimson, CircleShape)
                            .border(1.5.dp, BarbarianGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        )
                    }
                }

                // Fighter 2 Card
                Box(modifier = Modifier.weight(1f)) {
                    FighterSlotCard(
                        slotNumber = 2,
                        combatant = fighter2,
                        isSwahili = isSwahili,
                        accentColor = BarbarianCrimson,
                        onClick = { choosingForFighterSlot = 2 }
                    )
                }
            }
        }

        // START BATTLE BUTTON
        item {
            val canStart = fighter1 != null && fighter2 != null && fighter1.id != fighter2.id
            Button(
                onClick = onStartDuel,
                enabled = canStart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BarbarianGold,
                    contentColor = ArenaBlack
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_duel_button")
            ) {
                Text(
                    text = if (isSwahili) "INGIA UWANJANI NA UANZE VITA ⚔️" else "ENTER ARENA & START DUEL ⚔️",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                )
            }

            if (fighter1 != null && fighter2 != null && fighter1.id == fighter2.id) {
                Text(
                    text = if (isSwahili) "⚠️ Chagua wapiganaji wawili tofauti." else "⚠️ Please select two different warriors for the matchup.",
                    style = MaterialTheme.typography.bodySmall.copy(color = BarbarianGold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }

        // ROSTER SELECTION LIST
        item {
            Text(
                text = if (isSwahili) "ORODHA YA MASHUJAA WA UWANJANI (BONYEZA KUTEUA)" else "ARENA WARRIOR ROSTER (TAP TO ASSIGN)",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = BarbarianGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(roster) { warrior ->
            val isAssignedF1 = fighter1?.id == warrior.id
            val isAssignedF2 = fighter2?.id == warrior.id

            Card(
                colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.dp,
                    when {
                        isAssignedF1 -> DefenseBlue
                        isAssignedF2 -> BarbarianCrimson
                        else -> ArenaBorder
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = warrior.avatarEmoji,
                            fontSize = 28.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = warrior.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = BarbarianGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = warrior.warriorClass.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = BarbarianGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "HP: ${warrior.maxHp} • ATK: ${warrior.baseAttack} • DEF: ${warrior.baseDefense} • ${warrior.weaponName}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { onSelectFighter1(warrior) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAssignedF1) DefenseBlue else ArenaDarkGray,
                                contentColor = if (isAssignedF1) TextPrimary else TextSecondary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("P1", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onSelectFighter2(warrior) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAssignedF2) BarbarianCrimson else ArenaDarkGray,
                                contentColor = if (isAssignedF2) TextPrimary else TextSecondary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("P2", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FighterSlotCard(
    slotNumber: Int,
    combatant: Combatant?,
    isSwahili: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, accentColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = accentColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = if (slotNumber == 1) "FIGHTER 1" else "FIGHTER 2",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = accentColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = combatant?.avatarEmoji ?: "👤",
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = combatant?.name ?: if (isSwahili) "Chagua" else "Select",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = combatant?.warriorClass?.displayName ?: "",
                style = MaterialTheme.typography.bodySmall.copy(color = BarbarianGold),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (combatant != null) {
                Text(
                    text = "HP: ${combatant.maxHp} | ATK: ${combatant.baseAttack}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun LiveDuelArenaView(
    state: ArenaUiState,
    isSwahili: Boolean,
    onStepTurn: () -> Unit,
    onToggleAuto: () -> Unit,
    onSetSpeed: (Float) -> Unit,
    onResetDuel: () -> Unit
) {
    val f1 = state.duelFighter1 ?: return
    val f2 = state.duelFighter2 ?: return
    val logs = state.duelLogs
    val logListState = rememberLazyListState()

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            logListState.animateScrollToItem(logs.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP MATCH STATUS BAR & SPEED SELECTOR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = ArenaCardBg,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BarbarianGold)
            ) {
                Text(
                    text = "ROUND ${state.duelCurrentRound} • TURN ${state.duelCurrentTurn}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = BarbarianGold,
                        fontWeight = FontWeight.Black
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Speed Multiplier Chips
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(0.5f, 1.0f, 2.0f).forEach { speed ->
                    val isSelected = state.duelSpeedMultiplier == speed
                    Surface(
                        color = if (isSelected) BarbarianGold else ArenaCardBg,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.clickable { onSetSpeed(speed) }
                    ) {
                        Text(
                            text = "${speed}x",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) ArenaBlack else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // DUAL FIGHTER HEALTH & FURY BARS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Fighter 1 Card
            Box(modifier = Modifier.weight(1f)) {
                ActiveDuelistCard(
                    combatant = f1,
                    isAttackingTurn = state.duelCurrentTurn % 2 == 1,
                    accentColor = DefenseBlue
                )
            }

            // Fighter 2 Card
            Box(modifier = Modifier.weight(1f)) {
                ActiveDuelistCard(
                    combatant = f2,
                    isAttackingTurn = state.duelCurrentTurn % 2 == 0,
                    accentColor = BarbarianCrimson
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // LIVE COMBAT LOG VIEW
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSwahili) "REKODI YA VITA MOJA KWA MOJA (LIVE LOG)" else "LIVE TURN-BASED COMBAT LOG",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BarbarianGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "${logs.size} Moves",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }

                HorizontalDivider(
                    color = ArenaBorder,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                if (logs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSwahili) "Bonyeza 'NEXT TURN' au 'AUTO DUEL' kuanza vita!" else "Press 'NEXT TURN' or 'AUTO DUEL' to commence combat!",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        state = logListState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(logs) { turn ->
                            val textColor = when {
                                turn.isCritical -> BarbarianGold
                                turn.isDodged -> DefenseBlue
                                turn.isStunnedTurn -> FuryOrange
                                turn.healedAmount > 0 -> HealthGreen
                                turn.statusApplied != null -> Color(0xFFCE93D8)
                                else -> TextPrimary
                            }

                            Text(
                                text = turn.logMessage,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = textColor,
                                    fontWeight = if (turn.isCritical) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // DUEL CONTROLS OR WINNER BANNER
        if (state.duelWinner == null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onStepTurn,
                    enabled = !state.isDuelAutoRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = BarbarianCrimson),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("step_turn_button")
                ) {
                    Text(
                        text = if (isSwahili) "NEXT TURN ⚔️" else "NEXT TURN ⚔️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onToggleAuto,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isDuelAutoRunning) BarbarianAmber else DefenseBlue
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("auto_duel_button")
                ) {
                    Text(
                        text = if (state.isDuelAutoRunning) {
                            if (isSwahili) "SIMAMISHA (PAUSE)" else "PAUSE AUTO"
                        } else {
                            if (isSwahili) "AUTO DUEL ⚡" else "AUTO DUEL ⚡"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onResetDuel,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, ArenaBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Exit")
                }
            }
        } else {
            // WINNER BANNER
            val winner = state.duelWinner
            Card(
                colors = CardDefaults.cardColors(containerColor = BarbarianDeepRed),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, BarbarianGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isSwahili) "👑 MSHINDI WA UWANJANI!" else "👑 ARENA DUEL CHAMPION!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = BarbarianGold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${winner.name} (${winner.warriorClass.displayName})",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = if (isSwahili) "Ameshinda baada ya raundi ${state.duelCurrentRound}!" else "Claimed victory after ${state.duelCurrentRound} intense rounds!",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onResetDuel,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BarbarianGold,
                                contentColor = ArenaBlack
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isSwahili) "MPAMBANO MPYA (NEW MATCH)" else "NEW MATCHUP",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveDuelistCard(
    combatant: Combatant,
    isAttackingTurn: Boolean,
    accentColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            if (isAttackingTurn) 2.dp else 1.dp,
            if (isAttackingTurn) BarbarianGold else ArenaBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = combatant.avatarEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = combatant.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 12.sp
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = combatant.warriorClass.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BarbarianGold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                if (isAttackingTurn) {
                    Surface(
                        color = BarbarianGold,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "TURN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ArenaBlack,
                                fontWeight = FontWeight.Black,
                                fontSize = 8.sp
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // HP Bar
            val hpProgress = (combatant.currentHp.toFloat() / combatant.maxHp).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "HP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = HealthGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${combatant.currentHp}/${combatant.maxHp}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = HealthGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            LinearProgressIndicator(
                progress = { hpProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (hpProgress > 0.3f) HealthGreen else BarbarianCrimson,
                trackColor = ArenaBlack
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Fury Bar
            val furyProgress = (combatant.fury.toFloat() / combatant.maxFury).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "FURY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = FuryOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${combatant.fury}/${combatant.maxFury}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = FuryOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            LinearProgressIndicator(
                progress = { furyProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = FuryOrange,
                trackColor = ArenaBlack
            )

            // Active Statuses Badges
            if (combatant.activeStatuses.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    combatant.activeStatuses.forEach { status ->
                        Surface(
                            color = when (status.type) {
                                StatusEffectType.ENRAGED -> FuryOrange.copy(alpha = 0.3f)
                                StatusEffectType.SHIELD_WALL -> DefenseBlue.copy(alpha = 0.3f)
                                StatusEffectType.STUNNED -> Color(0xFFCE93D8).copy(alpha = 0.3f)
                                StatusEffectType.BLEEDING -> BarbarianCrimson.copy(alpha = 0.3f)
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${status.type.displayName} (${status.turnsLeft})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
