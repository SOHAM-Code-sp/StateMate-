package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.InputType
import com.example.domain.model.StatCategory
import com.example.domain.model.StatOperation
import com.example.ui.components.CalculationResultSection
import com.example.ui.components.ObservationBadge
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
import com.example.ui.viewmodel.CalculatorUiState

@Composable
fun CalculatorScreen(
  state: CalculatorUiState,
  onCategorySelected: (StatCategory) -> Unit,
  onOperationSelected: (StatOperation) -> Unit,
  onInputXChanged: (String) -> Unit,
  onInputYChanged: (String) -> Unit,
  onPercentileChanged: (Double) -> Unit,
  onClearClicked: () -> Unit,
  onLoadSampleClicked: () -> Unit,
  onCalculateClicked: () -> Unit,
  onSaveClicked: () -> Unit,
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("calculator_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp)
  ) {
    // 1. Category Selector Chips
    item {
      Text(
        text = "Select Category",
        style = MaterialTheme.typography.labelMedium.copy(
          color = TextSecondary,
          fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(bottom = 6.dp)
      )
      CategoryTabsRow(
        selectedCategory = state.selectedCategory,
        onCategorySelected = onCategorySelected
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    // 2. Specific Operations in Category
    item {
      Text(
        text = "Operation",
        style = MaterialTheme.typography.labelMedium.copy(
          color = TextSecondary,
          fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(bottom = 6.dp)
      )
      OperationChipsRow(
        category = state.selectedCategory,
        selectedOperation = state.selectedOperation,
        onOperationSelected = onOperationSelected
      )
      Spacer(modifier = Modifier.height(14.dp))
    }

    // 3. Operation Info Header
    item {
      OperationInfoCard(operation = state.selectedOperation)
      Spacer(modifier = Modifier.height(16.dp))
    }

    // 4. Input Fields based on InputType
    item {
      when (state.selectedOperation.inputType) {
        InputType.SINGLE_DATASET -> {
          SingleDatasetInputs(
            input = state.inputX,
            count = state.observationCountX,
            minRequired = state.selectedOperation.minObservations,
            onInputChanged = onInputXChanged,
            onClearClicked = onClearClicked,
            onLoadSampleClicked = onLoadSampleClicked
          )
        }
        InputType.SINGLE_DATASET_WITH_P -> {
          PercentileInputs(
            input = state.inputX,
            count = state.observationCountX,
            minRequired = state.selectedOperation.minObservations,
            percentileP = state.percentileP,
            onInputChanged = onInputXChanged,
            onPercentileChanged = onPercentileChanged,
            onClearClicked = onClearClicked,
            onLoadSampleClicked = onLoadSampleClicked
          )
        }
        InputType.DUAL_DATASET -> {
          DualDatasetInputs(
            inputX = state.inputX,
            inputY = state.inputY,
            countX = state.observationCountX,
            countY = state.observationCountY,
            minRequired = state.selectedOperation.minObservations,
            onInputXChanged = onInputXChanged,
            onInputYChanged = onInputYChanged,
            onClearClicked = onClearClicked,
            onLoadSampleClicked = onLoadSampleClicked
          )
        }
        InputType.PROBABILITY_EVENTS -> {
          ProbabilityEventInputs(
            nE = state.inputX,
            nS = state.inputY,
            onNEChanged = onInputXChanged,
            onNSChanged = onInputYChanged,
            onClearClicked = onClearClicked,
            onLoadSampleClicked = onLoadSampleClicked
          )
        }
        InputType.COMBINATORICS -> {
          CombinatoricsInputs(
            n = state.inputX,
            r = state.inputY,
            operationTitle = state.selectedOperation.title,
            onNChanged = onInputXChanged,
            onRChanged = onInputYChanged,
            onClearClicked = onClearClicked,
            onLoadSampleClicked = onLoadSampleClicked
          )
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // 5. Error Banner (if any)
    item {
      AnimatedVisibility(
        visible = state.errorMessage != null,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        state.errorMessage?.let { error ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFE4E6),
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, Color(0xFFFDA4AF), RoundedCornerShape(12.dp))
              .padding(bottom = 12.dp)
              .testTag("error_banner")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(14.dp)
            ) {
              Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = RoseError,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = RoseError,
                  fontWeight = FontWeight.Medium
                )
              )
            }
          }
        }
      }
    }

    // 6. Action Button: Calculate
    item {
      Button(
        onClick = {
          focusManager.clearFocus()
          onCalculateClicked()
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = Navy800,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("calculate_button")
      ) {
        Icon(
          imageVector = Icons.Default.Calculate,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Calculate ${state.selectedOperation.title}",
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        )
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // 7. Results Section
    item {
      AnimatedVisibility(
        visible = state.calculationResult != null,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        state.calculationResult?.let { result ->
          CalculationResultSection(
            result = result,
            isSaved = state.isSaved,
            onSaveClicked = onSaveClicked
          )
        }
      }
    }
  }
}

@Composable
private fun CategoryTabsRow(
  selectedCategory: StatCategory,
  onCategorySelected: (StatCategory) -> Unit
) {
  LazyRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    items(StatCategory.entries) { category ->
      val isSelected = category == selectedCategory
      FilterChip(
        selected = isSelected,
        onClick = { onCategorySelected(category) },
        label = {
          Text(
            text = category.title,
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          )
        },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = Navy800,
          selectedLabelColor = Color.White,
          containerColor = Color.White,
          labelColor = Navy900
        ),
        border = FilterChipDefaults.filterChipBorder(
          enabled = true,
          selected = isSelected,
          borderColor = BorderMedium,
          selectedBorderColor = Navy800
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("category_tab_${category.id}")
      )
    }
  }
}

@Composable
private fun OperationChipsRow(
  category: StatCategory,
  selectedOperation: StatOperation,
  onOperationSelected: (StatOperation) -> Unit
) {
  val operations = StatOperation.getByCategory(category)
  LazyRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    items(operations) { op ->
      val isSelected = op == selectedOperation
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) VioletPrimary else Color.White,
        border = androidx.compose.foundation.BorderStroke(
          width = 1.dp,
          color = if (isSelected) VioletPrimary else BorderMedium
        ),
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .clickable { onOperationSelected(op) }
          .testTag("operation_chip_${op.id}")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Text(
            text = op.symbol,
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isSelected) Color.White else VioletPrimary,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = op.title,
            style = MaterialTheme.typography.labelMedium.copy(
              color = if (isSelected) Color.White else Navy900,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          )
        }
      }
    }
  }
}

@Composable
private fun OperationInfoCard(operation: StatOperation) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = VioletLight,
        modifier = Modifier.size(38.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = operation.symbol,
            style = MaterialTheme.typography.titleMedium.copy(
              color = VioletPrimary,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = operation.title,
          style = MaterialTheme.typography.titleSmall.copy(
            color = Navy900,
            fontWeight = FontWeight.Bold
          )
        )
        Text(
          text = operation.description,
          style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )
      }
    }
  }
}

@Composable
private fun SingleDatasetInputs(
  input: String,
  count: Int,
  minRequired: Int,
  onInputChanged: (String) -> Unit,
  onClearClicked: () -> Unit,
  onLoadSampleClicked: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Dataset Values",
          style = MaterialTheme.typography.titleSmall.copy(
            color = Navy900,
            fontWeight = FontWeight.Bold
          )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          OutlinedButton(
            onClick = onLoadSampleClicked,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.height(30.dp).testTag("load_sample_button")
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              modifier = Modifier.size(12.dp),
              tint = VioletPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Sample",
              style = MaterialTheme.typography.labelSmall.copy(
                color = VioletPrimary,
                fontWeight = FontWeight.Bold
              )
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          if (input.isNotEmpty()) {
            IconButton(
              onClick = onClearClicked,
              modifier = Modifier.size(28.dp).testTag("clear_input_button")
            ) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear input",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Enter numbers separated by commas (decimals & negative values allowed):",
        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
      )

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = input,
        onValueChange = onInputChanged,
        placeholder = { Text("e.g. 14.2, 18.5, -4.0, 24.8, 19.3", color = TextMuted) },
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Done
        ),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = VioletPrimary,
          unfocusedBorderColor = BorderMedium,
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("dataset_input_x")
      )

      Spacer(modifier = Modifier.height(10.dp))

      ObservationBadge(count = count, minRequired = minRequired)
    }
  }
}

@Composable
private fun PercentileInputs(
  input: String,
  count: Int,
  minRequired: Int,
  percentileP: Double,
  onInputChanged: (String) -> Unit,
  onPercentileChanged: (Double) -> Unit,
  onClearClicked: () -> Unit,
  onLoadSampleClicked: () -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    SingleDatasetInputs(
      input = input,
      count = count,
      minRequired = minRequired,
      onInputChanged = onInputChanged,
      onClearClicked = onClearClicked,
      onLoadSampleClicked = onLoadSampleClicked
    )

    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Percentile Rank (P)",
            style = MaterialTheme.typography.titleSmall.copy(
              color = Navy900,
              fontWeight = FontWeight.Bold
            )
          )
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = VioletLight
          ) {
            Text(
              text = "${percentileP.toInt()}th Percentile",
              style = MaterialTheme.typography.labelMedium.copy(
                color = VioletPrimary,
                fontWeight = FontWeight.Bold
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Slider(
          value = percentileP.toFloat(),
          onValueChange = { onPercentileChanged(it.toDouble()) },
          valueRange = 1f..99f,
          steps = 97,
          colors = SliderDefaults.colors(
            thumbColor = VioletPrimary,
            activeTrackColor = VioletPrimary,
            inactiveTrackColor = Navy100
          ),
          modifier = Modifier.testTag("percentile_slider")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("1%", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
          Text("25% (Q1)", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
          Text("50% (Median)", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
          Text("75% (Q3)", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
          Text("99%", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
        }
      }
    }
  }
}

@Composable
private fun DualDatasetInputs(
  inputX: String,
  inputY: String,
  countX: Int,
  countY: Int,
  minRequired: Int,
  onInputXChanged: (String) -> Unit,
  onInputYChanged: (String) -> Unit,
  onClearClicked: () -> Unit,
  onLoadSampleClicked: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Paired Variables (X and Y)",
          style = MaterialTheme.typography.titleSmall.copy(
            color = Navy900,
            fontWeight = FontWeight.Bold
          )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          OutlinedButton(
            onClick = onLoadSampleClicked,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.height(30.dp).testTag("load_dual_sample_button")
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              modifier = Modifier.size(12.dp),
              tint = VioletPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Sample",
              style = MaterialTheme.typography.labelSmall.copy(
                color = VioletPrimary,
                fontWeight = FontWeight.Bold
              )
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          if (inputX.isNotEmpty() || inputY.isNotEmpty()) {
            IconButton(
              onClick = onClearClicked,
              modifier = Modifier.size(28.dp).testTag("clear_dual_input_button")
            ) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear inputs",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Enter X and Y datasets with equal number of observations:",
        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Input X
      Text(
        text = "Variable X (Independent)",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.SemiBold,
          color = Navy900
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = inputX,
        onValueChange = onInputXChanged,
        placeholder = { Text("e.g. 2, 3, 5, 7, 8, 10, 12", color = TextMuted) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = VioletPrimary,
          unfocusedBorderColor = BorderMedium
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().testTag("dataset_input_x")
      )
      Spacer(modifier = Modifier.height(6.dp))
      ObservationBadge(count = countX, minRequired = minRequired, label = "X Observations")

      Spacer(modifier = Modifier.height(14.dp))

      // Input Y
      Text(
        text = "Variable Y (Dependent)",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.SemiBold,
          color = Navy900
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = inputY,
        onValueChange = onInputYChanged,
        placeholder = { Text("e.g. 4.5, 7.0, 11.2, 15.0, 17.8, 21.0, 25.4", color = TextMuted) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = VioletPrimary,
          unfocusedBorderColor = BorderMedium
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().testTag("dataset_input_y")
      )
      Spacer(modifier = Modifier.height(6.dp))
      ObservationBadge(count = countY, minRequired = minRequired, label = "Y Observations")

      if (countX > 0 && countY > 0 && countX != countY) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFFFE4E6)
        ) {
          Text(
            text = "Count mismatch: X has $countX values, Y has $countY values. Both must be identical.",
            style = MaterialTheme.typography.labelSmall.copy(
              color = RoseError,
              fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun ProbabilityEventInputs(
  nE: String,
  nS: String,
  onNEChanged: (String) -> Unit,
  onNSChanged: (String) -> Unit,
  onClearClicked: () -> Unit,
  onLoadSampleClicked: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Probability Inputs",
          style = MaterialTheme.typography.titleSmall.copy(
            color = Navy900,
            fontWeight = FontWeight.Bold
          )
        )
        OutlinedButton(
          onClick = onLoadSampleClicked,
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
          modifier = Modifier.height(30.dp).testTag("load_prob_sample_button")
        ) {
          Text(
            text = "Example",
            style = MaterialTheme.typography.labelSmall.copy(
              color = VioletPrimary,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Favorable Outcomes n(E):",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
      )
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = nE,
        onValueChange = onNEChanged,
        placeholder = { Text("e.g. 3 (favorable events)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().testTag("prob_input_e")
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Total Sample Space n(S):",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
      )
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = nS,
        onValueChange = onNSChanged,
        placeholder = { Text("e.g. 10 (total possible outcomes)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().testTag("prob_input_s")
      )
    }
  }
}

@Composable
private fun CombinatoricsInputs(
  n: String,
  r: String,
  operationTitle: String,
  onNChanged: (String) -> Unit,
  onRChanged: (String) -> Unit,
  onClearClicked: () -> Unit,
  onLoadSampleClicked: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "$operationTitle Inputs",
          style = MaterialTheme.typography.titleSmall.copy(
            color = Navy900,
            fontWeight = FontWeight.Bold
          )
        )
        OutlinedButton(
          onClick = onLoadSampleClicked,
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
          modifier = Modifier.height(30.dp).testTag("load_comb_sample_button")
        ) {
          Text(
            text = "Example",
            style = MaterialTheme.typography.labelSmall.copy(
              color = VioletPrimary,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Total Number of Items (n):",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
      )
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = n,
        onValueChange = onNChanged,
        placeholder = { Text("e.g. 10") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().testTag("comb_input_n")
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Items Selected / Arranged (r):",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
      )
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = r,
        onValueChange = onRChanged,
        placeholder = { Text("e.g. 3") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().testTag("comb_input_r")
      )
    }
  }
}
