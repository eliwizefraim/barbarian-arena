package com.barbarians.global

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.barbarians.global.ui.screens.*
import com.barbarians.global.ui.theme.ArenaBlack
import com.barbarians.global.ui.theme.BarbarianArenaTheme
import com.barbarians.global.viewmodel.ArenaViewModel
import com.barbarians.global.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: ArenaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BarbarianArenaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ArenaBlack
                ) {
                    BarbarianArenaApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun BarbarianArenaApp(viewModel: ArenaViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle back button for custom state switching
    if (uiState.currentScreen != Screen.MAIN_MENU && uiState.currentScreen != Screen.SPLASH) {
        BackHandler {
            viewModel.navigateTo(Screen.MAIN_MENU)
        }
    }

    when (uiState.currentScreen) {
        Screen.SPLASH -> {
            SplashScreen(
                onEnterArena = { viewModel.navigateTo(Screen.MAIN_MENU) }
            )
        }
        Screen.MAIN_MENU -> {
            MainMenuScreen(
                warrior = uiState.warrior,
                isSwahili = uiState.isSwahili,
                dailyBountyClaimed = uiState.dailyBountyClaimed,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onClaimDailyBounty = { viewModel.claimDailyBounty() },
                onNavigateToArena = { viewModel.navigateTo(Screen.ARENA_BATTLE) },
                onNavigateToDuel = { viewModel.navigateTo(Screen.WARRIOR_DUEL) },
                onNavigateToParticipants = { viewModel.navigateTo(Screen.PARTICIPANT_SELECTION) },
                onNavigateToHistory = { viewModel.navigateTo(Screen.BATTLE_HISTORY) },
                onNavigateToBarracks = { viewModel.navigateTo(Screen.BARRACKS) },
                onNavigateToArmory = { viewModel.navigateTo(Screen.ARMORY) },
                onNavigateToChampions = { viewModel.navigateTo(Screen.CHAMPIONS) },
                onNavigateToDevGuide = { viewModel.navigateTo(Screen.DEV_GUIDE) }
            )
        }
        Screen.BATTLE_HISTORY -> {
            BattleHistoryScreen(
                records = uiState.battleHistoryRecords,
                isSwahili = uiState.isSwahili,
                onDeleteRecord = { id -> viewModel.deleteBattleRecord(id) },
                onClearAll = { viewModel.clearAllBattleHistory() },
                onReturnToMenu = { viewModel.navigateTo(Screen.MAIN_MENU) }
            )
        }
        Screen.PARTICIPANT_SELECTION -> {
            ParticipantSelectionScreen(
                warriors = uiState.warriorEntities,
                selectedFighter1 = uiState.selectedEntitySlot1,
                selectedFighter2 = uiState.selectedEntitySlot2,
                isSwahili = uiState.isSwahili,
                onSelectFighter1 = { entity -> viewModel.selectEntitySlot1(entity) },
                onSelectFighter2 = { entity -> viewModel.selectEntitySlot2(entity) },
                onStartDuel = { viewModel.launchDuelFromParticipants() },
                onAddNewWarrior = { newEntity -> viewModel.addNewWarriorEntity(newEntity) },
                onReturnToMenu = { viewModel.navigateTo(Screen.MAIN_MENU) }
            )
        }
        Screen.WARRIOR_DUEL -> {
            WarriorDuelScreen(
                state = uiState,
                onSelectFighter1 = { fighter -> viewModel.selectDuelFighter1(fighter) },
                onSelectFighter2 = { fighter -> viewModel.selectDuelFighter2(fighter) },
                onStartDuel = { viewModel.startDuelBattle() },
                onStepTurn = { viewModel.stepDuelTurn() },
                onToggleAuto = { viewModel.toggleAutoDuel() },
                onSetSpeed = { speed -> viewModel.setDuelSpeed(speed) },
                onResetDuel = { viewModel.resetDuelBattle() },
                onReturnToMenu = { viewModel.navigateTo(Screen.MAIN_MENU) }
            )
        }
        Screen.ARENA_BATTLE -> {
            ArenaScreen(
                state = uiState,
                onStartBattle = { opponent -> viewModel.startBattle(opponent) },
                onExecuteAction = { action -> viewModel.executeCombatAction(action) },
                onReturnToMenu = { viewModel.navigateTo(Screen.MAIN_MENU) }
            )
        }
        Screen.BARRACKS -> {
            BarracksScreen(
                warrior = uiState.warrior,
                onUpgradeStat = { stat -> viewModel.upgradeStat(stat) },
                onSelectClass = { warriorClass -> viewModel.selectWarriorClass(warriorClass) },
                onReturnToMenu = { viewModel.navigateTo(Screen.MAIN_MENU) }
            )
        }
        Screen.ARMORY -> {
            ArmoryScreen(
                warrior = uiState.warrior,
                items = uiState.armoryItems,
                onBuyItem = { item -> viewModel.buyItem(item) },
                onEquipItem = { item -> viewModel.equipItem(item) },
                onReturnToMenu = { viewModel.navigateTo(Screen.MAIN_MENU) }
            )
        }
        Screen.CHAMPIONS -> {
            ChampionsScreen(
                warrior = uiState.warrior,
                onReturnToMenu = { viewModel.navigateTo(Screen.MAIN_MENU) }
            )
        }
        Screen.DEV_GUIDE -> {
            DeveloperGuideScreen(
                isSwahili = uiState.isSwahili,
                onReturnToMenu = { viewModel.navigateTo(Screen.MAIN_MENU) }
            )
        }
    }
}
