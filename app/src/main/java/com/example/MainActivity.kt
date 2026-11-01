package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppDatabase
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.StatMateRepository
import com.example.ui.components.StatMateTopBar
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Navy900
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPrimary
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.StatMateViewModel

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getDatabase(applicationContext)
    val preferences = UserPreferencesRepository(applicationContext)
    val repository = StatMateRepository(database.calculationDao(), preferences)
    val viewModelFactory = StatMateViewModel.Factory(repository)
    val viewModel by viewModels<StatMateViewModel> { viewModelFactory }

    setContent {
      MyApplicationTheme {
        StatMateApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun StatMateApp(viewModel: StatMateViewModel) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: AppTab.HOME.name.lowercase()

  val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
  val calcState by viewModel.calcState.collectAsStateWithLifecycle()
  val recentCalculations by viewModel.recentCalculations.collectAsStateWithLifecycle()
  val favoriteToolIds by viewModel.favoriteToolIds.collectAsStateWithLifecycle()
  val filteredHistory by viewModel.filteredHistory.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val historyFilter by viewModel.historyCategoryFilter.collectAsStateWithLifecycle()
  val decimalPrecision by viewModel.decimalPrecision.collectAsStateWithLifecycle()
  val allHistoryCount by viewModel.allHistoryCount.collectAsStateWithLifecycle()

  // Sync tab state when changed from ViewModel (e.g. clicking tool on Home)
  LaunchedEffect(currentTab) {
    val targetRoute = currentTab.name.lowercase()
    if (currentRoute != targetRoute) {
      navController.navigate(targetRoute) {
        popUpTo(navController.graph.findStartDestination().id) {
          saveState = true
        }
        launchSingleTop = true
        restoreState = true
      }
    }
  }

  // Handle back button on sub-screens
  BackHandler(enabled = currentRoute != AppTab.HOME.name.lowercase()) {
    viewModel.setTab(AppTab.HOME)
  }

  Scaffold(
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      StatMateTopBar(
        currentPrecision = decimalPrecision,
        onPrecisionSelected = { viewModel.setDecimalPrecision(it) }
      )
    },
    bottomBar = {
      // Fixed bottom navigation bar with accent color and distinct active icon state
      NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        // Tab 1: Home
        val isHomeSelected = currentRoute == AppTab.HOME.name.lowercase()
        NavigationBarItem(
          selected = isHomeSelected,
          onClick = { viewModel.setTab(AppTab.HOME) },
          icon = {
            Icon(
              imageVector = if (isHomeSelected) Icons.Default.Home else Icons.Outlined.Home,
              contentDescription = "Home"
            )
          },
          label = {
            Text(
              text = "Home",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isHomeSelected) FontWeight.Bold else FontWeight.Medium
              )
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = VioletPrimary,
            indicatorColor = VioletPrimary,
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextMuted
          ),
          modifier = Modifier.testTag(AppTab.HOME.testTag)
        )

        // Tab 2: Calculator
        val isCalcSelected = currentRoute == AppTab.CALCULATOR.name.lowercase()
        NavigationBarItem(
          selected = isCalcSelected,
          onClick = { viewModel.setTab(AppTab.CALCULATOR) },
          icon = {
            Icon(
              imageVector = if (isCalcSelected) Icons.Default.Calculate else Icons.Outlined.Calculate,
              contentDescription = "Calculator"
            )
          },
          label = {
            Text(
              text = "Calculator",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isCalcSelected) FontWeight.Bold else FontWeight.Medium
              )
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = VioletPrimary,
            indicatorColor = VioletPrimary,
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextMuted
          ),
          modifier = Modifier.testTag(AppTab.CALCULATOR.testTag)
        )

        // Tab 3: History
        val isHistorySelected = currentRoute == AppTab.HISTORY.name.lowercase()
        NavigationBarItem(
          selected = isHistorySelected,
          onClick = { viewModel.setTab(AppTab.HISTORY) },
          icon = {
            Icon(
              imageVector = if (isHistorySelected) Icons.Default.History else Icons.Outlined.History,
              contentDescription = "History"
            )
          },
          label = {
            Text(
              text = "History",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isHistorySelected) FontWeight.Bold else FontWeight.Medium
              )
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = VioletPrimary,
            indicatorColor = VioletPrimary,
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextMuted
          ),
          modifier = Modifier.testTag(AppTab.HISTORY.testTag)
        )

        // Tab 4: Setting
        val isSettingSelected = currentRoute == AppTab.SETTINGS.name.lowercase()
        NavigationBarItem(
          selected = isSettingSelected,
          onClick = { viewModel.setTab(AppTab.SETTINGS) },
          icon = {
            Icon(
              imageVector = if (isSettingSelected) Icons.Default.Settings else Icons.Outlined.Settings,
              contentDescription = "Setting"
            )
          },
          label = {
            Text(
              text = "Setting",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSettingSelected) FontWeight.Bold else FontWeight.Medium
              )
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = VioletPrimary,
            indicatorColor = VioletPrimary,
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextMuted
          ),
          modifier = Modifier.testTag(AppTab.SETTINGS.testTag)
        )
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = AppTab.HOME.name.lowercase(),
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      composable(AppTab.HOME.name.lowercase()) {
        HomeScreen(
          onStartCalculatingClicked = { viewModel.setTab(AppTab.CALCULATOR) },
          onToolSelected = { op -> viewModel.selectOperation(op) },
          onCategorySelected = { cat -> viewModel.selectCategory(cat) },
          recentCalculations = recentCalculations,
          favoriteToolIds = favoriteToolIds,
          onReopenRecord = { record -> viewModel.reopenRecord(record) },
          onToggleFavoriteTool = { toolId -> viewModel.toggleFavoriteTool(toolId) }
        )
      }

      composable(AppTab.CALCULATOR.name.lowercase()) {
        CalculatorScreen(
          state = calcState,
          onCategorySelected = { cat -> viewModel.selectCategory(cat) },
          onOperationSelected = { op -> viewModel.selectOperation(op) },
          onInputXChanged = { viewModel.onInputXChanged(it) },
          onInputYChanged = { viewModel.onInputYChanged(it) },
          onPercentileChanged = { viewModel.onPercentileChanged(it) },
          onClearClicked = { viewModel.clearInputs() },
          onLoadSampleClicked = { viewModel.loadSampleData() },
          onCalculateClicked = { viewModel.calculate() },
          onSaveClicked = { viewModel.saveCurrentResult() }
        )
      }

      composable(AppTab.HISTORY.name.lowercase()) {
        HistoryScreen(
          records = filteredHistory,
          searchQuery = searchQuery,
          selectedFilter = historyFilter,
          onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
          onFilterChanged = { viewModel.onHistoryFilterChanged(it) },
          onReopenRecord = { record -> viewModel.reopenRecord(record) },
          onToggleFavorite = { record -> viewModel.toggleFavoriteRecord(record) },
          onDeleteRecord = { id -> viewModel.deleteRecord(id) },
          onClearAllHistory = { viewModel.clearAllHistory() }
        )
      }

      composable(AppTab.SETTINGS.name.lowercase()) {
        SettingsScreen(
          currentPrecision = decimalPrecision,
          totalSavedCalculations = allHistoryCount,
          onPrecisionSelected = { viewModel.setDecimalPrecision(it) },
          onClearHistoryClicked = { viewModel.clearAllHistory() }
        )
      }
    }
  }
}
