package com.barbarians.global.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BattleRecordEntity::class], version = 1, exportSchema = false)
abstract class ArenaRoomDatabase : RoomDatabase() {
    abstract fun battleRecordDao(): BattleRecordDao

    companion object {
        @Volatile
        private var INSTANCE: ArenaRoomDatabase? = null

        fun getDatabase(context: Context): ArenaRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArenaRoomDatabase::class.java,
                    "barbarian_arena_room.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
