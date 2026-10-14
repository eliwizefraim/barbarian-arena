package com.barbarians.global.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.barbarians.global.combat.CombatEngine
import com.barbarians.global.combat.Combatant
import com.barbarians.global.combat.TurnResult
import com.barbarians.global.combat.WarriorRoster
import com.barbarians.global.data.ArenaStorage
import com.barbarians.global.data.db.ArenaRoomDatabase
import com.barbarians.global.data.db.BattleRecordEntity
import com.barbarians.global.data.db.BattleRecordRepository
import com.barbarians.global.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Screen {
    SPLASH,
    MAIN_MENU,
    ARENA_BATTLE,
    WARRIOR_DUEL,
    PARTICIPANT_SELECTION,
    BATTLE_HISTORY,
    BARRACKS,
    ARMORY,
    CHAMPIONS,
    DEV_GUIDE
}

enum class UpgradeableStat {
    HEALTH,
    ATTACK,
    DEFENSE
}

data class ArenaUiState(
    val currentScreen: Screen = Screen.SPLASH,
    val warrior: Warrior = Warrior(),
    val armoryItems: List<EquipmentItem> = ArmoryCatalog.items,
    val opponents: List<Opponent> = OpponentCatalog.createOpponents(),
    val activeOpponent: Opponent? = null,
    val battleStatus: BattleStatus = BattleStatus.IN_PROGRESS,
    val combatLogs: List<CombatLogEntry> = emptyList(),
    val isExecutingTurn: Boolean = false,
    val lastRewardGold: Int = 0,
    val lastRewardGlory: Int = 0,
    val lastLeveledUp: Boolean = false,
    val damageNotification: String? = null,
    val isSwahili: Boolean = false,
    val dailyBountyClaimed: Boolean = false,
    // Room DB History Records
    val battleHistoryRecords: List<BattleRecordEntity> = emptyList(),
    // Participant Roster State
    val warriorEntities: List<WarriorEntity> = emptyList(),
    val selectedEntitySlot1: WarriorEntity? = null,
    val selectedEntitySlot2: WarriorEntity? = null,
    // Duel Matchup State
    val duelAvailableRoster: List<Combatant> = emptyList(),
    val duelFighter1: Combatant? = null,
    val duelFighter2: Combatant? = null,
    val isDuelActive: Boolean = false,
    val isDuelAutoRunning: Boolean = false,
    val duelCurrentRound: Int = 1,
    val duelCurrentTurn: Int = 1,
    val duelLogs: List<TurnResult> = emptyList(),
    val duelWinner: Combatant? = null,
    val duelSpeedMultiplier: Float = 1.0f
)

class ArenaViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = ArenaStorage(application)
    private val roomDatabase = ArenaRoomDatabase.getDatabase(application)
    private val battleRepository = BattleRecordRepository(roomDatabase.battleRecordDao())

    private val _uiState = MutableStateFlow(ArenaUiState())
    val uiState: StateFlow<ArenaUiState> = _uiState.asStateFlow()

    init {
        loadPersistedData()
        observeBattleHistory()
    }

    private fun observeBattleHistory() {
        viewModelScope.launch {
            battleRepository.allRecords.collect { records ->
                _uiState.update { it.copy(battleHistoryRecords = records) }
            }
        }
    }

    private fun loadPersistedData() {
        val savedWarrior = storage.loadWarrior()
        val ownedItemIds = storage.loadOwnedItemIds()
        val isSwahili = storage.loadLanguage()

        val updatedItems = ArmoryCatalog.items.map {
            it.copy(isOwned = ownedItemIds.contains(it.id))
        }

        val roster = WarriorRoster.getDefaultDuelists(savedWarrior)
        val entities = listOf(WarriorEntityRepository.createFromPlayer(savedWarrior)) + WarriorEntityRepository.getPresetWarriors()

        _uiState.update {
            it.copy(
                warrior = savedWarrior,
                armoryItems = updatedItems,
                isSwahili = isSwahili,
                duelAvailableRoster = roster,
                duelFighter1 = roster.firstOrNull(),
                duelFighter2 = roster.getOrNull(1),
                warriorEntities = entities,
                selectedEntitySlot1 = entities.firstOrNull(),
                selectedEntitySlot2 = entities.getOrNull(1)
            )
        }
        recalculateTotalStats()
    }

    fun selectEntitySlot1(entity: WarriorEntity) {
        _uiState.update { it.copy(selectedEntitySlot1 = entity) }
    }

    fun selectEntitySlot2(entity: WarriorEntity) {
        _uiState.update { it.copy(selectedEntitySlot2 = entity) }
    }

    fun addNewWarriorEntity(entity: WarriorEntity) {
        _uiState.update {
            val updated = it.warriorEntities + entity
            val updatedRoster = it.duelAvailableRoster + entity.toCombatant()
            it.copy(
                warriorEntities = updated,
                duelAvailableRoster = updatedRoster,
                selectedEntitySlot2 = entity
            )
        }
    }

    fun launchDuelFromParticipants() {
        val e1 = _uiState.value.selectedEntitySlot1 ?: return
        val e2 = _uiState.value.selectedEntitySlot2 ?: return

        val c1 = e1.toCombatant()
        val c2 = e2.toCombatant()

        _uiState.update {
            it.copy(
                currentScreen = Screen.WARRIOR_DUEL,
                duelFighter1 = c1,
                duelFighter2 = c2,
                isDuelActive = true,
                duelLogs = emptyList(),
                duelCurrentRound = 1,
                duelCurrentTurn = 1,
                duelWinner = null,
                isDuelAutoRunning = false
            )
        }
    }

    private fun persistData() {
        val state = _uiState.value
        storage.saveWarrior(state.warrior)
        val ownedIds = state.armoryItems.filter { it.isOwned }.map { it.id }.toSet()
        storage.saveOwnedItemIds(ownedIds)
        storage.saveLanguage(state.isSwahili)
    }

    fun toggleLanguage() {
        _uiState.update { it.copy(isSwahili = !it.isSwahili) }
        persistData()
    }

    fun claimDailyBounty() {
        if (_uiState.value.dailyBountyClaimed) return
        _uiState.update { state ->
            val updated = state.warrior.copy(
                gold = state.warrior.gold + 75,
                glory = state.warrior.glory + 25
            )
            state.copy(
                warrior = updated,
                dailyBountyClaimed = true
            )
        }
        persistData()
    }

    fun navigateTo(screen: Screen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun selectWarriorClass(warriorClass: WarriorClass) {
        _uiState.update { state ->
            val updated = state.warrior.copy(warriorClass = warriorClass)
            state.copy(warrior = updated)
        }
        recalculateTotalStats()
        persistData()
        refreshDuelRoster()
    }

    private fun refreshDuelRoster() {
        val roster = WarriorRoster.getDefaultDuelists(_uiState.value.warrior)
        _uiState.update {
            it.copy(
                duelAvailableRoster = roster,
                duelFighter1 = roster.find { f -> f.id == it.duelFighter1?.id } ?: roster.firstOrNull(),
                duelFighter2 = roster.find { f -> f.id == it.duelFighter2?.id } ?: roster.getOrNull(1)
            )
        }
    }

    // --- DUEL MATCHUP ARENA METHODS ---

    fun selectDuelFighter1(fighter: Combatant) {
        _uiState.update { it.copy(duelFighter1 = fighter.copyCombatant()) }
    }

    fun selectDuelFighter2(fighter: Combatant) {
        _uiState.update { it.copy(duelFighter2 = fighter.copyCombatant()) }
    }

    fun startDuelBattle() {
        val f1 = _uiState.value.duelFighter1?.copyCombatant() ?: return
        val f2 = _uiState.value.duelFighter2?.copyCombatant() ?: return

        f1.currentHp = f1.maxHp
        f1.fury = 10
        f1.activeStatuses.clear()

        f2.currentHp = f2.maxHp
        f2.fury = 10
        f2.activeStatuses.clear()

        _uiState.update {
            it.copy(
                isDuelActive = true,
                duelFighter1 = f1,
                duelFighter2 = f2,
                duelLogs = emptyList(),
                duelCurrentRound = 1,
                duelCurrentTurn = 1,
                duelWinner = null,
                isDuelAutoRunning = false
            )
        }
    }

    fun stepDuelTurn() {
        val state = _uiState.value
        val f1 = state.duelFighter1?.copyCombatant() ?: return
        val f2 = state.duelFighter2?.copyCombatant() ?: return

        if (!f1.isAlive || !f2.isAlive) return

        val currentTurn = state.duelCurrentTurn
        val currentRound = state.duelCurrentRound

        // Alternating turn: Odd turns -> Fighter 1 attacks, Even turns -> Fighter 2 attacks
        val isF1Attacker = currentTurn % 2 == 1
        val attacker = if (isF1Attacker) f1 else f2
        val defender = if (isF1Attacker) f2 else f1

        val action = CombatEngine.chooseAiAction(attacker, defender)
        val result = CombatEngine.executeTurn(
            round = currentRound,
            turn = currentTurn,
            attacker = attacker,
            defender = defender,
            action = action
        )

        val winner = when {
            !defender.isAlive -> attacker
            !attacker.isAlive -> defender
            else -> null
        }

        val nextRound = if (currentTurn % 2 == 0) currentRound + 1 else currentRound
        val nextTurn = currentTurn + 1

        val updatedF1 = if (isF1Attacker) attacker else defender
        val updatedF2 = if (isF1Attacker) defender else attacker

        _uiState.update {
            it.copy(
                duelFighter1 = updatedF1,
                duelFighter2 = updatedF2,
                duelLogs = it.duelLogs + result,
                duelCurrentRound = nextRound,
                duelCurrentTurn = nextTurn,
                duelWinner = winner,
                isDuelAutoRunning = if (winner != null) false else it.isDuelAutoRunning
            )
        }

        if (winner != null) {
            viewModelScope.launch {
                battleRepository.insertRecord(
                    BattleRecordEntity(
                        fighter1Name = f1.name,
                        fighter1Class = f1.warriorClass.displayName,
                        fighter1Emoji = f1.avatarEmoji,
                        fighter2Name = f2.name,
                        fighter2Class = f2.warriorClass.displayName,
                        fighter2Emoji = f2.avatarEmoji,
                        winnerName = winner.name,
                        winnerEmoji = winner.avatarEmoji,
                        totalRounds = currentRound,
                        totalTurns = nextTurn - 1,
                        battleSummary = "Duel decided in round $currentRound: ${winner.name} defeated the rival fighter."
                    )
                )
            }
        }
    }

    fun deleteBattleRecord(id: Long) {
        viewModelScope.launch {
            battleRepository.deleteRecord(id)
        }
    }

    fun clearAllBattleHistory() {
        viewModelScope.launch {
            battleRepository.clearHistory()
        }
    }

    fun toggleAutoDuel() {
        val willRun = !_uiState.value.isDuelAutoRunning
        _uiState.update { it.copy(isDuelAutoRunning = willRun) }

        if (willRun) {
            viewModelScope.launch {
                while (_uiState.value.isDuelAutoRunning && _uiState.value.duelWinner == null) {
                    stepDuelTurn()
                    val delayMs = when (_uiState.value.duelSpeedMultiplier) {
                        0.5f -> 1200L
                        2.0f -> 400L
                        else -> 750L
                    }
                    delay(delayMs)
                }
                _uiState.update { it.copy(isDuelAutoRunning = false) }
            }
        }
    }

    fun setDuelSpeed(multiplier: Float) {
        _uiState.update { it.copy(duelSpeedMultiplier = multiplier) }
    }

    fun resetDuelBattle() {
        _uiState.update {
            it.copy(
                isDuelActive = false,
                isDuelAutoRunning = false,
                duelWinner = null,
                duelLogs = emptyList()
            )
        }
    }

    // --- STANDARD OPPONENT BATTLE METHODS ---

    fun startBattle(opponent: Opponent) {
        val freshOpponent = opponent.copy(currentHp = opponent.maxHp)
        val introText = if (_uiState.value.isSwahili) {
            "Pembe za vita zinalia! Unaingia kwenye mchanga wa damu kupambana na ${freshOpponent.name}!"
        } else {
            "War horns sound! You step onto the blood-soaked sand facing ${freshOpponent.name}!"
        }

        val initialLogs = listOf(
            CombatLogEntry(
                text = introText,
                isPlayerAction = true
            )
        )
        _uiState.update { state ->
            val readyWarrior = state.warrior.copy(
                currentHp = state.warrior.maxHp,
                fury = 10
            )
            state.copy(
                currentScreen = Screen.ARENA_BATTLE,
                activeOpponent = freshOpponent,
                warrior = readyWarrior,
                battleStatus = BattleStatus.IN_PROGRESS,
                combatLogs = initialLogs,
                isExecutingTurn = false,
                lastRewardGold = 0,
                lastRewardGlory = 0,
                damageNotification = null
            )
        }
    }

    fun executeCombatAction(action: CombatActionType) {
        val currentState = _uiState.value
        val opponent = currentState.activeOpponent ?: return
        val warrior = currentState.warrior

        if (currentState.isExecutingTurn || currentState.battleStatus != BattleStatus.IN_PROGRESS) return
        if (warrior.fury < action.furyCost) return

        viewModelScope.launch {
            _uiState.update { it.copy(isExecutingTurn = true) }

            val baseAtk = warrior.attack
            val critChance = if (warrior.warriorClass == WarriorClass.BERSERKER) 30 else 15
            val isCrit = Random.nextInt(100) < critChance
            val critMultiplier = if (isCrit) 1.65f else 1.0f

            var damageToEnemy = 0
            var newFury = warrior.fury - action.furyCost
            var healAmount = 0
            var actionLog = ""

            val isSwahili = currentState.isSwahili

            when (action) {
                CombatActionType.SLASH -> {
                    val raw = (baseAtk * (0.9f + Random.nextFloat() * 0.3f) * critMultiplier).toInt()
                    damageToEnemy = (raw - opponent.defense / 2).coerceAtLeast(8)
                    newFury = (newFury + 15).coerceAtMost(warrior.maxFury)
                    actionLog = if (isSwahili) {
                        "Umempiga ${opponent.name} kwa shambulio la haraka na kusababisha jeraha la $damageToEnemy!"
                    } else {
                        "You strike ${opponent.name} with Quick Strike for $damageToEnemy damage!"
                    }
                }
                CombatActionType.HEAVY_CLEAVE -> {
                    val raw = (baseAtk * 1.5f * (0.8f + Random.nextFloat() * 0.5f) * critMultiplier).toInt()
                    damageToEnemy = (raw - opponent.defense / 2).coerceAtLeast(14)
                    newFury = (newFury + 25).coerceAtMost(warrior.maxFury)
                    actionLog = if (isSwahili) {
                        "Umerusha panga zito kwa nguvu dhidi ya ${opponent.name} likileta madhara makubwa ya $damageToEnemy!"
                    } else {
                        "You unleash a Heavy Cleave smashing into ${opponent.name} for $damageToEnemy massive damage!"
                    }
                }
                CombatActionType.SHIELD_BASH -> {
                    val raw = (baseAtk * 0.75f * critMultiplier).toInt()
                    damageToEnemy = (raw - opponent.defense / 3).coerceAtLeast(6)
                    newFury = (newFury + 10).coerceAtMost(warrior.maxFury)
                    actionLog = if (isSwahili) {
                        "Umemgonga ${opponent.name} na ngao kwa $damageToEnemy damage na kumzingua!"
                    } else {
                        "You bash ${opponent.name} with your shield for $damageToEnemy damage, dazing them!"
                    }
                }
                CombatActionType.BERSERK_RAGE -> {
                    val raw = (baseAtk * 2.5f * (1.1f + Random.nextFloat() * 0.4f) * critMultiplier).toInt()
                    damageToEnemy = (raw - opponent.defense / 4).coerceAtLeast(35)
                    actionLog = if (isSwahili) {
                        "HASIRA KALI YA JANGWANI! Umepasua ulinzi wa ${opponent.name} na kumletea $damageToEnemy DAMAGE YA KUU!"
                    } else {
                        "BOILING RAGE! You unleash a savage flurry tearing through ${opponent.name} for $damageToEnemy CRITICAL damage!"
                    }
                }
                CombatActionType.WAR_CRY -> {
                    healAmount = 30
                    newFury = (newFury + 5).coerceAtMost(warrior.maxFury)
                    actionLog = if (isSwahili) {
                        "Ulipaza sauti ya kivita ya kutisha na kurudisha afya ya $healAmount HP!"
                    } else {
                        "You let out a terrifying war cry, recovering $healAmount HP!"
                    }
                }
            }

            if (isCrit && action != CombatActionType.WAR_CRY) {
                actionLog += if (isSwahili) " [PIGO LA HATARI!]" else " [CRITICAL HIT!]"
            }

            val newOpponentHp = (opponent.currentHp - damageToEnemy).coerceAtLeast(0)
            val newPlayerHp = (warrior.currentHp + healAmount).coerceAtMost(warrior.maxHp)
            val updatedOpponent = opponent.copy(currentHp = newOpponentHp)

            val playerLog = CombatLogEntry(
                text = actionLog,
                isPlayerAction = true,
                isCritical = isCrit,
                damageDealt = damageToEnemy,
                isHeal = healAmount > 0
            )

            _uiState.update { state ->
                state.copy(
                    activeOpponent = updatedOpponent,
                    warrior = state.warrior.copy(
                        currentHp = newPlayerHp,
                        fury = newFury
                    ),
                    combatLogs = state.combatLogs + playerLog
                )
            }

            if (newOpponentHp <= 0) {
                handleVictory(updatedOpponent)
                _uiState.update { it.copy(isExecutingTurn = false) }
                return@launch
            }

            delay(750)

            // Enemy Counter-Attack
            val enemyAtkBase = opponent.attack
            val playerDef = warrior.defense + if (action == CombatActionType.SHIELD_BASH) 8 else 0
            val enemyRaw = (enemyAtkBase * (0.85f + Random.nextFloat() * 0.35f)).toInt()
            val enemyDamage = (enemyRaw - playerDef / 2).coerceAtLeast(5)

            val remainingPlayerHp = (newPlayerHp - enemyDamage).coerceAtLeast(0)
            val enemyFuryGain = 10
            val finalPlayerFury = (newFury + enemyFuryGain).coerceAtMost(warrior.maxFury)

            val enemyLog = CombatLogEntry(
                text = if (isSwahili) {
                    "${opponent.name} alirudisha shambulio kali na kukupiga kwa $enemyDamage damage!"
                } else {
                    "${opponent.name} counters fiercely, dealing $enemyDamage damage to you!"
                },
                isPlayerAction = false,
                damageDealt = enemyDamage
            )

            val isDefeated = remainingPlayerHp <= 0

            _uiState.update { state ->
                val updatedWarrior = state.warrior.copy(
                    currentHp = remainingPlayerHp,
                    fury = finalPlayerFury
                )
                state.copy(
                    warrior = updatedWarrior,
                    combatLogs = state.combatLogs + enemyLog,
                    battleStatus = if (isDefeated) BattleStatus.DEFEAT else BattleStatus.IN_PROGRESS,
                    isExecutingTurn = false
                )
            }

            if (isDefeated) {
                handleDefeat()
            }
        }
    }

    private fun handleVictory(opponent: Opponent) {
        val warrior = _uiState.value.warrior
        val goldReward = opponent.goldReward
        val gloryReward = opponent.gloryReward
        val xpGain = 45

        val newXp = warrior.currentXp + xpGain
        val leveledUp = newXp >= warrior.maxXp
        val finalLevel = if (leveledUp) warrior.level + 1 else warrior.level
        val finalXp = if (leveledUp) (newXp - warrior.maxXp) else newXp
        val newStreak = warrior.streak + 1
        val newHighest = maxOf(newStreak, warrior.highestStreak)

        val updatedWarrior = warrior.copy(
            gold = warrior.gold + goldReward,
            glory = warrior.glory + gloryReward,
            wins = warrior.wins + 1,
            streak = newStreak,
            highestStreak = newHighest,
            level = finalLevel,
            currentXp = finalXp,
            maxHp = warrior.maxHp + (if (leveledUp) 15 else 0),
            attack = warrior.attack + (if (leveledUp) 3 else 0),
            defense = warrior.defense + (if (leveledUp) 2 else 0)
        )

        val isSwahili = _uiState.value.isSwahili
        val victoryLog = CombatLogEntry(
            text = if (isSwahili) {
                "USHINDI MKUU! ${opponent.name} ameanguka mbele yako! Umepata dhahabu $goldReward na utukufu $gloryReward!"
            } else {
                "VICTORY! ${opponent.name} falls before you! Earned $goldReward Gold and $gloryReward Glory!"
            },
            isPlayerAction = true,
            isCritical = true
        )

        _uiState.update { state ->
            state.copy(
                warrior = updatedWarrior,
                battleStatus = BattleStatus.VICTORY,
                combatLogs = state.combatLogs + victoryLog,
                lastRewardGold = goldReward,
                lastRewardGlory = gloryReward,
                lastLeveledUp = leveledUp
            )
        }
        persistData()

        viewModelScope.launch {
            battleRepository.insertRecord(
                BattleRecordEntity(
                    fighter1Name = warrior.name,
                    fighter1Class = warrior.warriorClass.displayName,
                    fighter1Emoji = "👑",
                    fighter2Name = opponent.name,
                    fighter2Class = opponent.difficulty,
                    fighter2Emoji = "💀",
                    winnerName = warrior.name,
                    winnerEmoji = "👑",
                    totalRounds = 1,
                    totalTurns = _uiState.value.combatLogs.size,
                    battleSummary = "Victory against ${opponent.name}. Earned $goldReward Gold & $gloryReward Glory."
                )
            )
        }
    }

    private fun handleDefeat() {
        val warrior = _uiState.value.warrior
        val isSwahili = _uiState.value.isSwahili
        val opponent = _uiState.value.activeOpponent
        val defeatLog = CombatLogEntry(
            text = if (isSwahili) {
                "UMESHINDWA! Ulianguka kwenye mchanga wa uwanja... Mfululizo wako wa ushindi umekatika."
            } else {
                "DEFEAT! You collapse in the arena sands... Your winning streak ends."
            },
            isPlayerAction = false
        )

        _uiState.update { state ->
            state.copy(
                warrior = warrior.copy(
                    losses = warrior.losses + 1,
                    streak = 0
                ),
                battleStatus = BattleStatus.DEFEAT,
                combatLogs = state.combatLogs + defeatLog
            )
        }
        persistData()

        viewModelScope.launch {
            battleRepository.insertRecord(
                BattleRecordEntity(
                    fighter1Name = warrior.name,
                    fighter1Class = warrior.warriorClass.displayName,
                    fighter1Emoji = "👑",
                    fighter2Name = opponent?.name ?: "Arena Opponent",
                    fighter2Class = opponent?.difficulty ?: "Arena",
                    fighter2Emoji = "💀",
                    winnerName = opponent?.name ?: "Arena Opponent",
                    winnerEmoji = "💀",
                    totalRounds = 1,
                    totalTurns = _uiState.value.combatLogs.size,
                    battleSummary = "Defeated in the arena sands by ${opponent?.name ?: "Opponent"}."
                )
            )
        }
    }

    fun upgradeStat(stat: UpgradeableStat) {
        val warrior = _uiState.value.warrior
        val cost = when (stat) {
            UpgradeableStat.HEALTH -> 40 + (warrior.maxHp - 120) / 2
            UpgradeableStat.ATTACK -> 50 + (warrior.attack - 24) * 3
            UpgradeableStat.DEFENSE -> 45 + (warrior.defense - 14) * 3
        }

        if (warrior.gold < cost) return

        val updatedWarrior = when (stat) {
            UpgradeableStat.HEALTH -> warrior.copy(
                gold = warrior.gold - cost,
                maxHp = warrior.maxHp + 20,
                currentHp = warrior.maxHp + 20
            )
            UpgradeableStat.ATTACK -> warrior.copy(
                gold = warrior.gold - cost,
                attack = warrior.attack + 3
            )
            UpgradeableStat.DEFENSE -> warrior.copy(
                gold = warrior.gold - cost,
                defense = warrior.defense + 2
            )
        }

        _uiState.update { it.copy(warrior = updatedWarrior) }
        persistData()
        refreshDuelRoster()
    }

    fun buyItem(item: EquipmentItem) {
        val warrior = _uiState.value.warrior
        if (warrior.gold < item.priceGold || warrior.glory < item.priceGlory) return

        val updatedItems = _uiState.value.armoryItems.map {
            if (it.id == item.id) it.copy(isOwned = true) else it
        }

        val updatedWarrior = warrior.copy(
            gold = warrior.gold - item.priceGold,
            glory = warrior.glory - item.priceGlory
        )

        _uiState.update {
            it.copy(
                warrior = updatedWarrior,
                armoryItems = updatedItems
            )
        }

        equipItem(item)
        persistData()
    }

    fun equipItem(item: EquipmentItem) {
        val warrior = _uiState.value.warrior
        val updatedWarrior = when (item.type) {
            EquipmentType.WEAPON -> warrior.copy(equippedWeaponId = item.id)
            EquipmentType.ARMOR -> warrior.copy(equippedArmorId = item.id)
            EquipmentType.TALISMAN -> warrior
        }
        _uiState.update { it.copy(warrior = updatedWarrior) }
        recalculateTotalStats()
        persistData()
        refreshDuelRoster()
    }

    private fun recalculateTotalStats() {
        val state = _uiState.value
        val weapon = state.armoryItems.find { it.id == state.warrior.equippedWeaponId }
        val armor = state.armoryItems.find { it.id == state.warrior.equippedArmorId }

        val weaponAtk = weapon?.attackBonus ?: 0
        val armorDef = armor?.defenseBonus ?: 0
        val armorHp = armor?.hpBonus ?: 0

        val baseHp = 120 + (state.warrior.level - 1) * 15
        val baseAtk = 24 + (state.warrior.level - 1) * 3
        val baseDef = 14 + (state.warrior.level - 1) * 2

        val newMaxHp = baseHp + armorHp
        val newAtk = baseAtk + weaponAtk
        val newDef = baseDef + armorDef

        _uiState.update {
            it.copy(
                warrior = it.warrior.copy(
                    maxHp = newMaxHp,
                    attack = newAtk,
                    defense = newDef
                )
            )
        }
    }
}
