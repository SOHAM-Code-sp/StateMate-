package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "statmate_prefs")

class UserPreferencesRepository(private val context: Context) {

  private object Keys {
    val FAVORITE_TOOLS = stringSetPreferencesKey("favorite_tools")
    val DECIMAL_PRECISION = intPreferencesKey("decimal_precision")
  }

  val favoriteToolsFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
    preferences[Keys.FAVORITE_TOOLS] ?: setOf("mean", "median", "sample_std", "linear_regression")
  }

  val decimalPrecisionFlow: Flow<Int> = context.dataStore.data.map { preferences ->
    preferences[Keys.DECIMAL_PRECISION] ?: 4
  }

  suspend fun toggleFavoriteTool(toolId: String) {
    context.dataStore.edit { preferences ->
      val current = preferences[Keys.FAVORITE_TOOLS]?.toMutableSet()
        ?: mutableSetOf("mean", "median", "sample_std", "linear_regression")
      if (current.contains(toolId)) {
        current.remove(toolId)
      } else {
        current.add(toolId)
      }
      preferences[Keys.FAVORITE_TOOLS] = current
    }
  }

  suspend fun setDecimalPrecision(precision: Int) {
    context.dataStore.edit { preferences ->
      preferences[Keys.DECIMAL_PRECISION] = precision.coerceIn(1, 8)
    }
  }
}
