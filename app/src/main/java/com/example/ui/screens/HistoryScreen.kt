package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CalculationRecord
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.Navy100
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoseError
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletLight
import com.example.ui.theme.VioletPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
  records: List<CalculationRecord>,
  searchQuery: String,
  selectedFilter: String,
  onSearchQueryChanged: (String) -> Unit,
  onFilterChanged: (String) -> Unit,
  onReopenRecord: (CalculationRecord) -> Unit,
  onToggleFavorite: (CalculationRecord) -> Unit,
  onDeleteRecord: (Long) -> Unit,
  onClearAllHistory: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showClearConfirmDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("history_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp)
  ) {
    // 1. Header with Count & Clear All Button
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Calculation History",
            style = MaterialTheme.typography.titleLarge.copy(
              color = Navy900,
              fontWeight = FontWeight.Bold
            )
          )
          Text(
            text = "${records.size} saved record(s)",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
          )
        }

        if (records.isNotEmpty()) {
          TextButton(
            onClick = { showClearConfirmDialog = true },
            colors = ButtonDefaults.textButtonColors(contentColor = RoseError),
            modifier = Modifier.testTag("clear_all_history_button")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Clear All",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }

    // 2. Search Field
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        placeholder = { Text("Search by operation, dataset, or value...", color = TextMuted) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = TextSecondary
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChanged("") }) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear search",
                tint = TextSecondary
              )
            }
          }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = VioletPrimary,
          unfocusedBorderColor = BorderMedium,
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("history_search_input")
      )
      Spacer(modifier = Modifier.height(12.dp))
    }

    // 3. Filter Chips
    item {
      val filterOptions = listOf(
        "All",
        "Favorites",
        "Descriptive Statistics",
        "Dispersion",
        "Quartiles & Position",
        "Probability",
        "Relationship & Correlation",
        "Regression Analysis"
      )

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(filterOptions) { filter ->
          val isSelected = filter.equals(selectedFilter, ignoreCase = true)
          FilterChip(
            selected = isSelected,
            onClick = { onFilterChanged(filter) },
            label = {
              Text(
                text = filter,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = VioletPrimary,
              selectedLabelColor = Color.White,
              containerColor = Color.White,
              labelColor = Navy900
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = BorderMedium,
              selectedBorderColor = VioletPrimary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.testTag("filter_chip_$filter")
          )
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // 4. Record Cards or Empty State
    if (records.isEmpty()) {
      item {
        EmptyHistoryState(hasQuery = searchQuery.isNotEmpty())
      }
    } else {
      items(records, key = { it.id }) { record ->
        HistoryItemCard(
          record = record,
          onReopen = { onReopenRecord(record) },
          onToggleFavorite = { onToggleFavorite(record) },
          onDelete = { onDeleteRecord(record.id) }
        )
        Spacer(modifier = Modifier.height(10.dp))
      }
    }
  }

  // Clear All Confirmation Dialog
  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      title = {
        Text(
          text = "Clear History?",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Text(
          text = "Are you sure you want to delete all saved calculations? This action cannot be undone.",
          style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            onClearAllHistory()
            showClearConfirmDialog = false
          },
          colors = ButtonDefaults.textButtonColors(contentColor = RoseError)
        ) {
          Text("Delete All", fontWeight = FontWeight.Bold)
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
private fun HistoryItemCard(
  record: CalculationRecord,
  onReopen: () -> Unit,
  onToggleFavorite: () -> Unit,
  onDelete: () -> Unit
) {
  val context = LocalContext.current
  val dateFormat = SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault())

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
      .testTag("history_card_${record.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header: Operation, Category Badge, Favorite Star & Delete
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = record.operationName,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Navy900
            )
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = VioletLight
          ) {
            Text(
              text = record.categoryName,
              style = MaterialTheme.typography.labelSmall.copy(
                color = VioletPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(32.dp).testTag("fav_btn_${record.id}")
          ) {
            Icon(
              imageVector = if (record.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
              contentDescription = if (record.isFavorite) "Starred" else "Star",
              tint = if (record.isFavorite) Color(0xFFF59E0B) else TextMuted,
              modifier = Modifier.size(20.dp)
            )
          }
          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp).testTag("delete_btn_${record.id}")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Delete record",
              tint = TextMuted,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Calculated Primary Result
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = "Calculated Result",
              style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )
            Text(
              text = record.primaryResult,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Navy900
              )
            )
          }

          IconButton(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("StatMate Result", "${record.operationName}: ${record.primaryResult}")
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Copied result to clipboard", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy result",
              tint = TextSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Dataset preview
      Text(
        text = "Input: ${record.datasetX}${record.datasetY?.let { " | $it" } ?: ""}",
        style = MaterialTheme.typography.bodySmall.copy(
          color = TextSecondary,
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp
        ),
        maxLines = 2
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Formula preview
      Text(
        text = "Formula: ${record.formula}",
        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Footer: Timestamp and Reopen button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = dateFormat.format(Date(record.timestamp)),
          style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
        )

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Navy800,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onReopen() }
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Reopen",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun EmptyHistoryState(hasQuery: Boolean) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = Color.White,
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
      .padding(32.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Surface(
        shape = CircleShape,
        color = Navy100,
        modifier = Modifier.size(56.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = Navy800,
            modifier = Modifier.size(28.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = if (hasQuery) "No matching calculations found" else "History is empty",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Navy900
        )
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = if (hasQuery) "Try adjusting your search terms or filter chips." else "Perform calculations on the Calculator screen and tap 'Save' to retain them here.",
        style = MaterialTheme.typography.bodySmall.copy(
          color = TextSecondary,
          lineHeight = 18.sp
        )
      )
    }
  }
}
