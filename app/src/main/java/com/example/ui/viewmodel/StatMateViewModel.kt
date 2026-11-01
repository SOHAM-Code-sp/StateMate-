package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.CalculationRecord
import com.example.data.repository.StatMateRepository
import com.example.domain.calculator.StatisticalCalculator
import com.example.domain.model.CalculationResult
import com.example.domain.model.InputType
import com.example.domain.model.StatCategory
import com.example.domain.model.StatOperation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val testTag: String) {
  HOME("Home", "nav_home"),
  CALCULATOR("Calculator", "nav_calculator"),
  HISTORY("History", "nav_history"),
  SETTINGS("Setting", "nav_settings")
}

data class CalculatorUiState(
  val selectedCategory: StatCategory = StatCategory.DESCRIPTIVE,
  val selectedOperation: StatOperation = StatOperation.MEAN,
  val inputX: String = "",
  val inputY: String = "",
  val percentileP: Double = 50.0,
  val observationCountX: Int = 0,
  val observationCountY: Int = 0,
  val calculationResult: CalculationResult? = null,
  val errorMessage: String? = null,
  val isSaved: Boolean = false,
  val isCalculating: Boolean = false
)

class StatMateViewModel(
  private val repository: StatMateRepository
) : ViewModel() {

  private val _currentTab = MutableStateFlow(AppTab.HOME)
  val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

  private val _calcState = MutableStateFlow(CalculatorUiState())
  val calcState: StateFlow<CalculatorUiState> = _calcState.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _historyCategoryFilter = MutableStateFlow("All")
  val historyCategoryFilter: StateFlow<String> = _historyCategoryFilter.asStateFlow()

  val favoriteToolIds: StateFlow<Set<String>> = repository.favoriteTools
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("mean", "median", "sample_std", "linear_regression"))

  val decimalPrecision: StateFlow<Int> = repository.decimalPrecision
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 4)

  val recentCalculations: StateFlow<List<CalculationRecord>> = repository.recentHistory
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allHistoryCount: StateFlow<Int> = repository.allHistory
    .map { it.size }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val filteredHistory: StateFlow<List<CalculationRecord>> = combine(
    repository.allHistory,
    _searchQuery,
    _historyCategoryFilter
  ) { history, query, filter ->
    history.filter { record ->
      val matchesQuery = query.isBlank() ||
        record.operationName.contains(query, ignoreCase = true) ||
        record.categoryName.contains(query, ignoreCase = true) ||
        record.datasetX.contains(query, ignoreCase = true) ||
        record.primaryResult.contains(query, ignoreCase = true)

      val matchesFilter = when (filter) {
        "All" -> true
        "Favorites" -> record.isFavorite
        else -> record.categoryName.equals(filter, ignoreCase = true) ||
          record.categoryId.equals(filter, ignoreCase = true)
      }

      matchesQuery && matchesFilter
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setTab(tab: AppTab) {
    _currentTab.value = tab
  }

  fun selectOperation(operation: StatOperation) {
    _calcState.update { current ->
      current.copy(
        selectedCategory = operation.category,
        selectedOperation = operation,
        errorMessage = null,
        isSaved = false
      )
    }
    _currentTab.value = AppTab.CALCULATOR
  }

  fun selectCategory(category: StatCategory) {
    val firstOp = StatOperation.getByCategory(category).firstOrNull() ?: StatOperation.MEAN
    _calcState.update { current ->
      current.copy(
        selectedCategory = category,
        selectedOperation = firstOp,
        errorMessage = null,
        isSaved = false
      )
    }
  }

  fun onInputXChanged(input: String) {
    val count = countObservations(input)
    _calcState.update { current ->
      current.copy(
        inputX = input,
        observationCountX = count,
        errorMessage = null,
        isSaved = false
      )
    }
  }

  fun onInputYChanged(input: String) {
    val count = countObservations(input)
    _calcState.update { current ->
      current.copy(
        inputY = input,
        observationCountY = count,
        errorMessage = null,
        isSaved = false
      )
    }
  }

  fun onPercentileChanged(p: Double) {
    _calcState.update { it.copy(percentileP = p.coerceIn(0.0, 100.0), isSaved = false) }
  }

  fun clearInputs() {
    _calcState.update {
      it.copy(
        inputX = "",
        inputY = "",
        observationCountX = 0,
        observationCountY = 0,
        calculationResult = null,
        errorMessage = null,
        isSaved = false
      )
    }
  }

  fun loadSampleData() {
    val op = _calcState.value.selectedOperation
    when (op.inputType) {
      InputType.SINGLE_DATASET, InputType.SINGLE_DATASET_WITH_P -> {
        val sample = "14.2, 18.5, 12.0, 24.8, 19.3, 15.7, 22.1, 18.5, 31.0"
        onInputXChanged(sample)
      }
      InputType.DUAL_DATASET -> {
        val sampleX = "2, 3, 5, 7, 8, 10, 12"
        val sampleY = "4.5, 7.0, 11.2, 15.0, 17.8, 21.0, 25.4"
        onInputXChanged(sampleX)
        onInputYChanged(sampleY)
      }
      InputType.PROBABILITY_EVENTS -> {
        onInputXChanged("3")
        onInputYChanged("10")
      }
      InputType.COMBINATORICS -> {
        onInputXChanged("10")
        onInputYChanged("3")
      }
    }
  }

  fun calculate() {
    val state = _calcState.value
    val op = state.selectedOperation
    val decimals = decimalPrecision.value

    val result = StatisticalCalculator.calculate(
      operation = op,
      inputX = state.inputX,
      inputY = state.inputY,
      extraParam = state.percentileP,
      decimals = decimals
    )

    result.onSuccess { calcResult ->
      _calcState.update {
        it.copy(
          calculationResult = calcResult,
          errorMessage = null,
          isSaved = false
        )
      }
    }.onFailure { ex ->
      _calcState.update {
        it.copy(
          errorMessage = ex.message ?: "An unexpected calculation error occurred.",
          calculationResult = null
        )
      }
    }
  }

  fun saveCurrentResult() {
    val result = _calcState.value.calculationResult ?: return
    viewModelScope.launch {
      val record = CalculationRecord(
        operationId = result.operation.id,
        operationName = result.operation.title,
        categoryId = result.operation.category.id,
        categoryName = result.operation.category.title,
        datasetX = result.formattedDataset,
        datasetY = result.formattedDatasetY,
        primaryResult = result.mainResult,
        formula = result.formula,
        interpretation = result.interpretation,
        stepsRaw = result.steps.joinToString("\n"),
        observationCount = result.observationCount,
        isFavorite = false,
        timestamp = System.currentTimeMillis()
      )
      repository.saveCalculation(record)
      _calcState.update { it.copy(isSaved = true) }
    }
  }

  fun reopenRecord(record: CalculationRecord) {
    val op = StatOperation.fromId(record.operationId)
    // Clean raw dataset prefixes if any (e.g. "X: " -> "")
    val cleanX = record.datasetX.removePrefix("X: ").removePrefix("n = ").substringBefore(", r = ")
    val cleanY = record.datasetY?.removePrefix("Y: ") ?: if (record.datasetX.contains("r = ")) {
      record.datasetX.substringAfter("r = ")
    } else ""

    _calcState.update {
      it.copy(
        selectedCategory = op.category,
        selectedOperation = op,
        inputX = cleanX,
        inputY = cleanY,
        observationCountX = countObservations(cleanX),
        observationCountY = countObservations(cleanY),
        errorMessage = null,
        isSaved = true
      )
    }
    // Auto calculate to show fresh results
    calculate()
    _currentTab.value = AppTab.CALCULATOR
  }

  fun toggleFavoriteRecord(record: CalculationRecord) {
    viewModelScope.launch {
      repository.toggleFavoriteRecord(record.id, !record.isFavorite)
    }
  }

  fun deleteRecord(id: Long) {
    viewModelScope.launch {
      repository.deleteRecord(id)
    }
  }

  fun clearAllHistory() {
    viewModelScope.launch {
      repository.clearHistory()
    }
  }

  fun onSearchQueryChanged(query: String) {
    _searchQuery.value = query
  }

  fun onHistoryFilterChanged(filter: String) {
    _historyCategoryFilter.value = filter
  }

  fun toggleFavoriteTool(toolId: String) {
    viewModelScope.launch {
      repository.toggleFavoriteTool(toolId)
    }
  }

  fun setDecimalPrecision(precision: Int) {
    viewModelScope.launch {
      repository.setDecimalPrecision(precision)
      // recalculate if we have a current calculation
      if (_calcState.value.calculationResult != null) {
        calculate()
      }
    }
  }

  private fun countObservations(input: String): Int {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return 0
    return trimmed.split(Regex("[,;\\s]+")).count { it.isNotBlank() }
  }

  class Factory(private val repository: StatMateRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(StatMateViewModel::class.java)) {
        return StatMateViewModel(repository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
