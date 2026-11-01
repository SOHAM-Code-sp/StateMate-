package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.Navy100
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoseError
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletLight
import com.example.ui.theme.VioletPrimary

@Composable
fun SettingsScreen(
  currentPrecision: Int,
  totalSavedCalculations: Int,
  onPrecisionSelected: (Int) -> Unit,
  onClearHistoryClicked: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showClearConfirmDialog by remember { mutableStateOf(false) }
  var cheatSheetExpanded by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("settings_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp)
  ) {
    // 1. Header
    item {
      Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(
          text = "Settings & Preferences",
          style = MaterialTheme.typography.titleLarge.copy(
            color = Navy900,
            fontWeight = FontWeight.Bold
          )
        )
        Text(
          text = "Configure calculation precision and manage offline storage",
          style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )
      }
    }

    // 2. Decimal Precision Settings Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
          .testTag("precision_settings_card")
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = VioletLight,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Tune,
                  contentDescription = null,
                  tint = VioletPrimary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Decimal Precision",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Navy900
                )
              )
              Text(
                text = "Active formatting: $currentPrecision decimal places",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          val options = listOf(2, 3, 4, 6, 8)
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            options.forEach { precision ->
              val isSelected = currentPrecision == precision
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) VioletLight.copy(alpha = 0.5f) else Color.Transparent)
                  .clickable {
                    onPrecisionSelected(precision)
                    Toast.makeText(context, "Precision set to $precision decimals", Toast.LENGTH_SHORT).show()
                  }
                  .padding(horizontal = 8.dp, vertical = 6.dp)
              ) {
                RadioButton(
                  selected = isSelected,
                  onClick = {
                    onPrecisionSelected(precision)
                    Toast.makeText(context, "Precision set to $precision decimals", Toast.LENGTH_SHORT).show()
                  },
                  colors = RadioButtonDefaults.colors(selectedColor = VioletPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "$precision decimals",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = Navy900
                  )
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                  text = "π ≈ ${String.format(java.util.Locale.US, "%.${precision}f", Math.PI)}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = if (isSelected) VioletPrimary else TextMuted
                  )
                )
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // 3. Local Storage & History Management
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = CyanLight,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Storage,
                  contentDescription = null,
                  tint = CyanAccent,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Local Storage (Room Database)",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Navy900
                )
              )
              Text(
                text = "$totalSavedCalculations saved calculation(s) stored on device",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "Calculation History",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = Navy900
                )
              )
              Text(
                text = "Erase all saved records from local SQLite",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
              )
            }

            TextButton(
              onClick = { showClearConfirmDialog = true },
              colors = ButtonDefaults.textButtonColors(contentColor = RoseError),
              modifier = Modifier.testTag("clear_history_settings_btn")
            ) {
              Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Clear All", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // 4. Formula Cheat Sheet Accordion
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { cheatSheetExpanded = !cheatSheetExpanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFDCFCE7),
                modifier = Modifier.size(36.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.Functions,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Statistical Formulas Reference",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                  )
                )
                Text(
                  text = "Mathematical conventions & formulas",
                  style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
              }
            }

            Icon(
              imageVector = if (cheatSheetExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null,
              tint = TextSecondary
            )
          }

          AnimatedVisibility(
            visible = cheatSheetExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              FormulaItem(
                name = "Sample Variance (s²)",
                formula = "s² = ∑(x - x̄)² / (N - 1)",
                note = "Bessel's correction yields unbiased variance estimation."
              )
              FormulaItem(
                name = "Population Variance (σ²)",
                formula = "σ² = ∑(x - μ)² / N",
                note = "Exact variance of complete population."
              )
              FormulaItem(
                name = "Pearson Correlation (r)",
                formula = "r = Cov(X,Y) / (sₓ · sᵧ)",
                note = "Measures linear co-movement between -1 and +1."
              )
              FormulaItem(
                name = "Linear Regression",
                formula = "y = mx + b | m = Cov/Var(X), b = ȳ - mx̄",
                note = "Ordinary least-squares linear trendline."
              )
              FormulaItem(
                name = "Quartiles (Q1, Q2, Q3)",
                formula = "Tukey split-half median convention",
                note = "Median divides data into halves; medians of halves yield Q1 and Q3."
              )
              FormulaItem(
                name = "Combinations & Permutations",
                formula = "ⁿPᵣ = n!/(n-r)! | ⁿCᵣ = n!/[r!(n-r)!]",
                note = "Order matters in permutations; order does not matter in combinations."
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // 5. Offline & Privacy Guarantee Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Navy100,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Security,
                  contentDescription = null,
                  tint = Navy800,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Privacy & Offline Architecture",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Navy900
                )
              )
              Text(
                text = "100% On-Device Calculation",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "• No remote backend or cloud servers\n• No authentication, tracking, or telemetry\n• All computations execute locally on device CPU\n• Datasets remain securely in private device storage",
            style = MaterialTheme.typography.bodySmall.copy(
              color = TextSecondary,
              lineHeight = 20.sp
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "StatMate Version 1.0 (Build 1)",
            style = MaterialTheme.typography.labelSmall.copy(
              color = TextMuted,
              fontWeight = FontWeight.Medium
            )
          )
        }
      }
    }
  }

  // Clear confirmation dialog
  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      title = {
        Text(
          text = "Clear All Calculations?",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Text(
          text = "This will permanently remove all saved calculations from the local database.",
          style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            onClearHistoryClicked()
            showClearConfirmDialog = false
            Toast.makeText(context, "Calculation history cleared", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.textButtonColors(contentColor = RoseError)
        ) {
          Text("Clear All", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirmDialog = false }) {
          Text("Cancel", color = Navy900)
        }
      }
    )
  }
}

@Composable
private fun FormulaItem(name: String, formula: String, note: String) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = Color(0xFFF8FAFC),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(
        text = name,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Navy900
        )
      )
      Text(
        text = formula,
        style = MaterialTheme.typography.bodySmall.copy(
          fontFamily = FontFamily.Monospace,
          color = VioletPrimary,
          fontWeight = FontWeight.SemiBold
        )
      )
      Text(
        text = note,
        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
      )
    }
  }
}
