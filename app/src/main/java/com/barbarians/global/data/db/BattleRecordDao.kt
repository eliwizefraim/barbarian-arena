package com.barbarians.global.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BattleRecordDao {
    @Query("SELECT * FROM battle_records ORDER BY timestamp DESC")
    fun getAllBattleRecords(): Flow<List<BattleRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBattleRecord(record: BattleRecordEntity)

    @Query("DELETE FROM battle_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM battle_records")
    suspend fun clearAllRecords()
}
