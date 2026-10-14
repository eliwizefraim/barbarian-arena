package com.barbarians.global.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battle_records")
data class BattleRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fighter1Name: String,
    val fighter1Class: String,
    val fighter1Emoji: String,
    val fighter2Name: String,
    val fighter2Class: String,
    val fighter2Emoji: String,
    val winnerName: String,
    val winnerEmoji: String,
    val totalRounds: Int,
    val totalTurns: Int,
    val battleSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)
