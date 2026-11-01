package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
  @Query("SELECT * FROM calculation_records ORDER BY timestamp DESC")
  fun getAllRecords(): Flow<List<CalculationRecord>>

  @Query("SELECT * FROM calculation_records ORDER BY timestamp DESC LIMIT :limit")
  fun getRecentRecords(limit: Int = 3): Flow<List<CalculationRecord>>

  @Query("SELECT * FROM calculation_records WHERE isFavorite = 1 ORDER BY timestamp DESC")
  fun getFavoriteRecords(): Flow<List<CalculationRecord>>

  @Query("SELECT * FROM calculation_records WHERE id = :id LIMIT 1")
  suspend fun getRecordById(id: Long): CalculationRecord?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecord(record: CalculationRecord): Long

  @Query("UPDATE calculation_records SET isFavorite = :isFavorite WHERE id = :id")
  suspend fun updateFavorite(id: Long, isFavorite: Boolean)

  @Query("DELETE FROM calculation_records WHERE id = :id")
  suspend fun deleteRecordById(id: Long)

  @Query("DELETE FROM calculation_records")
  suspend fun clearAllRecords()
}
