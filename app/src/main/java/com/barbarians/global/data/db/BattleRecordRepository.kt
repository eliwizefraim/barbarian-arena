package com.barbarians.global.data.db

import kotlinx.coroutines.flow.Flow

class BattleRecordRepository(private val dao: BattleRecordDao) {
    val allRecords: Flow<List<BattleRecordEntity>> = dao.getAllBattleRecords()

    suspend fun insertRecord(record: BattleRecordEntity) {
        dao.insertBattleRecord(record)
    }

    suspend fun deleteRecord(id: Long) {
        dao.deleteRecordById(id)
    }

    suspend fun clearHistory() {
        dao.clearAllRecords()
    }
}
