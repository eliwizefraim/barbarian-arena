package com.barbarians.global.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barbarians.global.model.WarriorClass
import com.barbarians.global.model.WarriorEntity
import com.barbarians.global.ui.theme.*

enum class StatSortCriterion(val displayName: String, val shortLabel: String) {
    BATTLE_POWER("Power Rating", "PWR"),
    STRENGTH("Strength", "STR"),
    AGILITY("Agility", "AGI"),
    HEALTH("Health", "HP")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantSelectionScreen(
    warriors: List<WarriorEntity>,
    selectedFighter1: WarriorEntity?,
    selectedFighter2: WarriorEntity?,
    isSwahili: Boolean,
    onSelectFighter1: (WarriorEntity) -> Unit,
    onSelectFighter2: (WarriorEntity) -> Unit,
    onStartDuel: () -> Unit,
    onAddNewWarrior: (WarriorEntity) -> Unit,
    onReturnToMenu: () -> Unit
) {
    var selectedSort by remember { mutableStateOf(StatSortCriterion.BATTLE_POWER) }
    var selectedClassFilter by remember { mutableStateOf<WarriorClass?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val filteredAndSortedWarriors = remember(warriors, selectedSort, selectedClassFilter) {
        val list = if (selectedClassFilter == null) warriors else warriors.filter { it.warriorClass == selectedClassFilter }
        when (selectedSort) {
            StatSortCriterion.BATTLE_POWER -> list.sortedByDescending { it.battlePowerRating }
            StatSortCriterion.STRENGTH -> list.sortedByDescending { it.strength }
            StatSortCriterion.AGILITY -> list.sortedByDescending { it.agility }
            StatSortCriterion.HEALTH -> list.sortedByDescending { it.health }
        }
    }

    Scaffold(
        containerColor = ArenaBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isSwahili) "CHAGUA WASHIRIKI WA UWANJA" else "SELECT ARENA PARTICIPANTS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onReturnToMenu,
                        modifier = Modifier.testTag("participants_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Return",
                            tint = BarbarianGold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.testTag("create_warrior_button")
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Create Warrior",
                            tint = BarbarianGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ArenaDarkGray,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            // MATCHUP DOCKED FOOTER
            Surface(
                color = ArenaDarkGray,
                border = BorderStroke(1.dp, ArenaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Slot 1
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SLOT 1 (BLUE)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DefenseBlue,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = selectedFighter1?.name ?: if (isSwahili) "Bado haijachaguliwa" else "None selected",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                maxLines = 1
                            )
                        }

                        Text(
                            text = "⚔️ VS ⚔️",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        // Slot 2
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "SLOT 2 (RED)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BarbarianCrimson,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = selectedFighter2?.name ?: if (isSwahili) "Bado haijachaguliwa" else "None selected",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val canLaunch = selectedFighter1 != null && selectedFighter2 != null && selectedFighter1.id != selectedFighter2.id
                    Button(
                        onClick = onStartDuel,
                        enabled = canLaunch,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BarbarianGold,
                            contentColor = ArenaBlack
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("launch_arena_duel_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSwahili) "ANZA MAPAMBANO YA UWANJA" else "LAUNCH ARENA COMBAT",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Sort & Filter Controls
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isSwahili) "PANGA KWA SIFA (SORT BY STATS):" else "SORT BY WARRIOR STATS:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BarbarianGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatSortCriterion.entries.forEach { criterion ->
                            val isSelected = selectedSort == criterion
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedSort = criterion },
                                label = { Text(criterion.displayName, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BarbarianCrimson,
                                    selectedLabelColor = TextPrimary
                                )
                            )
                        }
                    }

                    // Class filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedClassFilter == null,
                            onClick = { selectedClassFilter = null },
                            label = { Text("ALL", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BarbarianGold,
                                selectedLabelColor = ArenaBlack
                            )
                        )
                        WarriorClass.entries.forEach { cls ->
                            val isSelected = selectedClassFilter == cls
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedClassFilter = cls },
                                label = { Text(cls.displayName.uppercase(), fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BarbarianGold,
                                    selectedLabelColor = ArenaBlack
                                )
                            )
                        }
                    }
                }
            }

            // WARRIOR ENTITY CARDS LIST
            items(filteredAndSortedWarriors) { entity ->
                val isSlot1 = selectedFighter1?.id == entity.id
                val isSlot2 = selectedFighter2?.id == entity.id

                WarriorEntityCard(
                    warrior = entity,
                    isSlot1 = isSlot1,
                    isSlot2 = isSlot2,
                    isSwahili = isSwahili,
                    onAssignSlot1 = { onSelectFighter1(entity) },
                    onAssignSlot2 = { onSelectFighter2(entity) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showCreateDialog) {
        CreateWarriorEntityDialog(
            isSwahili = isSwahili,
            onDismiss = { showCreateDialog = false },
            onConfirm = { newWarrior ->
                onAddNewWarrior(newWarrior)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun WarriorEntityCard(
    warrior: WarriorEntity,
    isSlot1: Boolean,
    isSlot2: Boolean,
    isSwahili: Boolean,
    onAssignSlot1: () -> Unit,
    onAssignSlot2: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.5.dp,
            when {
                isSlot1 -> DefenseBlue
                isSlot2 -> BarbarianCrimson
                else -> ArenaBorder
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Avatar, Name, Title, Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(ArenaDarkGray, CircleShape)
                            .border(1.dp, BarbarianGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = warrior.avatarEmoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = warrior.name,
                                style = MaterialTheme.typography.titleMedium.copy(
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
                        Text(
                            text = warrior.title,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }

                // Battle Power Tag
                Surface(
                    color = ArenaDarkGray,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ArenaBorder)
                ) {
                    Text(
                        text = "PWR ${warrior.battlePowerRating}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = BarbarianGold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // PRIMARY STAT GAUGES: STRENGTH, AGILITY, HEALTH
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Strength
                StatBarRow(
                    label = "STRENGTH (STR)",
                    value = warrior.strength,
                    subtext = "Attack: ${warrior.attackPower} dmg",
                    maxValue = 40,
                    color = BarbarianCrimson
                )

                // Agility
                StatBarRow(
                    label = "AGILITY (AGI)",
                    value = warrior.agility,
                    subtext = "Dodge: ${warrior.dodgeChancePercent}%",
                    maxValue = 40,
                    color = FuryOrange
                )

                // Health
                StatBarRow(
                    label = "HEALTH (HP)",
                    value = warrior.health,
                    subtext = "Max Vitality",
                    maxValue = 250,
                    color = HealthGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Weapon & Armor Gear
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⚔️ ${warrior.weaponName}",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
                Text(
                    text = "🛡️ ${warrior.armorName} (${warrior.armor} DEF)",
                    style = MaterialTheme.typography.labelSmall.copy(color = DefenseBlue)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SELECTION BUTTONS FOR ARENA SLOTS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAssignSlot1,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSlot1) DefenseBlue else ArenaDarkGray,
                        contentColor = if (isSlot1) TextPrimary else DefenseBlue
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DefenseBlue),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSlot1) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = if (isSlot1) "SLOT 1 ACTIVE" else "ASSIGN TO SLOT 1",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onAssignSlot2,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSlot2) BarbarianCrimson else ArenaDarkGray,
                        contentColor = if (isSlot2) TextPrimary else BarbarianCrimson
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BarbarianCrimson),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSlot2) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = if (isSlot2) "SLOT 2 ACTIVE" else "ASSIGN TO SLOT 2",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBarRow(
    label: String,
    value: Int,
    subtext: String,
    maxValue: Int,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$label: $value",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 10.sp
                )
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        val progress = (value.toFloat() / maxValue).coerceIn(0f, 1f)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp),
            color = color,
            trackColor = ArenaBlack
        )
    }
}

@Composable
private fun CreateWarriorEntityDialog(
    isSwahili: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (WarriorEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf(WarriorClass.BERSERKER) }
    var strength by remember { mutableIntStateOf(25) }
    var agility by remember { mutableIntStateOf(20) }
    var health by remember { mutableIntStateOf(160) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArenaCardBg,
        title = {
            Text(
                text = if (isSwahili) "Unda Shujaa Mpya" else "Forge New Warrior Entity",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = BarbarianGold
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Warrior Name") },
                    placeholder = { Text("e.g. Alaric the Bold") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BarbarianGold,
                        unfocusedBorderColor = ArenaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Select Class: ${selectedClass.displayName}",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    WarriorClass.entries.forEach { cls ->
                        Surface(
                            color = if (selectedClass == cls) BarbarianGold else ArenaDarkGray,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedClass = cls }
                        ) {
                            Text(
                                text = cls.displayName.take(3).uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (selectedClass == cls) ArenaBlack else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Strength Slider
                Text(text = "Strength: $strength (Atk: ${strength * 2 + 10})", style = MaterialTheme.typography.labelSmall.copy(color = BarbarianCrimson))
                Slider(
                    value = strength.toFloat(),
                    onValueChange = { strength = it.toInt() },
                    valueRange = 15f..40f,
                    colors = SliderDefaults.colors(thumbColor = BarbarianCrimson, activeTrackColor = BarbarianCrimson)
                )

                // Agility Slider
                Text(text = "Agility: $agility (Dodge: ${(agility * 0.8).toInt()}%)", style = MaterialTheme.typography.labelSmall.copy(color = FuryOrange))
                Slider(
                    value = agility.toFloat(),
                    onValueChange = { agility = it.toInt() },
                    valueRange = 10f..40f,
                    colors = SliderDefaults.colors(thumbColor = FuryOrange, activeTrackColor = FuryOrange)
                )

                // Health Slider
                Text(text = "Health: $health HP", style = MaterialTheme.typography.labelSmall.copy(color = HealthGreen))
                Slider(
                    value = health.toFloat(),
                    onValueChange = { health = it.toInt() },
                    valueRange = 120f..250f,
                    colors = SliderDefaults.colors(thumbColor = HealthGreen, activeTrackColor = HealthGreen)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (name.isBlank()) "Gladiator #${(100..999).random()}" else name.trim()
                    val newEntity = WarriorEntity(
                        id = "custom_${System.currentTimeMillis()}",
                        name = finalName,
                        title = "Custom Arena Challenger",
                        warriorClass = selectedClass,
                        health = health,
                        maxHealth = health,
                        strength = strength,
                        agility = agility,
                        armor = 16,
                        weaponName = "Forged Arena Blade",
                        armorName = "Heavy Spiked Armor",
                        avatarEmoji = when (selectedClass) {
                            WarriorClass.BERSERKER -> "🪓"
                            WarriorClass.GLADIATOR -> "🛡️"
                            WarriorClass.WARLORD -> "🦁"
                            WarriorClass.VALKYRIE -> "⚡"
                        },
                        description = "Custom-forged gladiator entity."
                    )
                    onConfirm(newEntity)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BarbarianGold, contentColor = ArenaBlack)
            ) {
                Text("FORGE WARRIOR", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        }
    )
}
