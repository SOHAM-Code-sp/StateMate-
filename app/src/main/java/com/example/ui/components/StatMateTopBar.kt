package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.Navy100
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletLight
import com.example.ui.theme.VioletPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatMateTopBar(
  currentPrecision: Int,
  onPrecisionSelected: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var showPrecisionDialog by remember { mutableStateOf(false) }

  TopAppBar(
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Navy800,
          modifier = Modifier.size(36.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "∑",
              style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
              )
            )
          }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "StatMate",
            style = MaterialTheme.typography.titleLarge.copy(
              color = Navy900,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
          )
          Text(
            text = "Statistical Calculator",
            style = MaterialTheme.typography.labelSmall.copy(
              color = TextSecondary,
              letterSpacing = 0.3.sp
            )
          )
        }
      }
    },
    actions = {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = VioletLight,
        modifier = Modifier
          .padding(end = 8.dp)
          .clip(RoundedCornerShape(12.dp))
      ) {
        TextButton(
          onClick = { showPrecisionDialog = true },
          modifier = Modifier.testTag("precision_button")
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Precision settings",
            tint = VioletPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${currentPrecision} Decimals",
            style = MaterialTheme.typography.labelMedium.copy(
              color = VioletPrimary,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = Color.White,
      titleContentColor = Navy900
    ),
    modifier = modifier
  )

  if (showPrecisionDialog) {
    AlertDialog(
      onDismissRequest = { showPrecisionDialog = false },
      title = {
        Text(
          text = "Decimal Precision",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Column {
          Text(
            text = "Choose the number of decimal places to format calculated values:",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
          )
          Spacer(modifier = Modifier.size(12.dp))
          listOf(2, 3, 4, 6).forEach { precision ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  onPrecisionSelected(precision)
                  showPrecisionDialog = false
                }
                .padding(vertical = 6.dp)
            ) {
              RadioButton(
                selected = (currentPrecision == precision),
                onClick = {
                  onPrecisionSelected(precision)
                  showPrecisionDialog = false
                }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "$precision decimal places (e.g. ${"%.${precision}f".format(3.1415926)})",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = if (currentPrecision == precision) FontWeight.Bold else FontWeight.Normal,
                  color = Navy900
                )
              )
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showPrecisionDialog = false }) {
          Text("Done", color = VioletPrimary, fontWeight = FontWeight.Bold)
        }
      }
    )
  }
}
