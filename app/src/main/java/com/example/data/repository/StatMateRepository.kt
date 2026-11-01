package com.example.data.repository

import com.example.data.local.CalculationDao
import com.example.data.local.CalculationRecord
import com.example.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class StatMateRepository(
  private val dao: CalculationDao,
  private val preferences: UserPreferencesRepository
) {

  val allHistory: Flow<List<CalculationRecord>> = dao.getAllRecords()
  val recentHistory: Flow<List<CalculationRecord>> = dao.getRecentRecords(limit = 3)
  val favoriteHistory: Flow<List<CalculationRecord>> = dao.getFavoriteRecords()
  val favoriteTools: Flow<Set<String>> = preferences.favoriteToolsFlow
  val decimalPrecision: Flow<Int> = preferences.decimalPrecisionFlow

  suspend fun saveCalculation(record: CalculationRecord): Long {
    return dao.insertRecord(record)
  }

  suspend fun toggleFavoriteRecord(id: Long, isFavorite: Boolean) {
    dao.updateFavorite(id, isFavorite)
  }

  suspend fun deleteRecord(id: Long) {
    dao.deleteRecordById(id)
  }

  suspend fun clearHistory() {
    dao.clearAllRecords()
  }

  suspend fun toggleFavoriteTool(toolId: String) {
    preferences.toggleFavoriteTool(toolId)
  }

  suspend fun setDecimalPrecision(precision: Int) {
    preferences.setDecimalPrecision(precision)
  }
}
