package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.domain.model.CalculationResult
import com.example.domain.model.StatCategory
import com.example.domain.model.StatOperation
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.Navy100
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoseError
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletLight
import com.example.ui.theme.VioletPrimary

@Composable
fun ObservationBadge(
  count: Int,
  minRequired: Int,
  modifier: Modifier = Modifier,
  label: String = "Observations"
) {
  val isValid = count >= minRequired
  val bgColor = if (count == 0) Navy100 else if (isValid) Color(0xFFDCFCE7) else Color(0xFFFFE4E6)
  val textColor = if (count == 0) TextSecondary else if (isValid) EmeraldSuccess else RoseError

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bgColor,
    modifier = modifier
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
      Text(
        text = if (count == 0) "No data entered" else "$label: N = $count",
        style = MaterialTheme.typography.labelMedium.copy(
          color = textColor,
          fontWeight = FontWeight.Bold
        )
      )
      if (count > 0 && !isValid) {
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "(min $minRequired needed)",
          style = MaterialTheme.typography.labelSmall.copy(color = textColor)
        )
      }
    }
  }
}

@Composable
fun CalculationResultSection(
  result: CalculationResult,
  isSaved: Boolean,
  onSaveClicked: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var stepsExpanded by remember { mutableStateOf(true) }

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
      .testTag("result_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Header: Operation and action buttons
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          Text(
            text = result.operation.title,
            style = MaterialTheme.typography.titleMedium.copy(
              color = Navy900,
              fontWeight = FontWeight.Bold
            )
          )
          Text(
            text = result.operation.category.title,
            style = MaterialTheme.typography.labelMedium.copy(color = VioletPrimary)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Copy button
          IconButton(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("StatMate Result", "${result.operation.title}: ${result.mainResult}")
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Result copied to clipboard", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.testTag("copy_result_button")
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy result",
              tint = Navy700
            )
          }

          // Save button
          Button(
            onClick = onSaveClicked,
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isSaved) Color(0xFFDCFCE7) else VioletPrimary,
              contentColor = if (isSaved) EmeraldSuccess else Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("save_result_button")
          ) {
            Icon(
              imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Bookmark,
              contentDescription = if (isSaved) "Saved" else "Save result",
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isSaved) "Saved" else "Save",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Main Calculated Value Card
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF1F5F9),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Calculated Value (${result.operation.symbol})",
            style = MaterialTheme.typography.labelMedium.copy(
              color = TextSecondary,
              letterSpacing = 0.5.sp
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = result.mainResult,
            style = MaterialTheme.typography.headlineMedium.copy(
              color = Navy900,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Default
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Formula banner
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = VioletLight,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Functions,
            contentDescription = "Formula",
            tint = VioletPrimary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Mathematical Formula",
              style = MaterialTheme.typography.labelSmall.copy(
                color = VioletPrimary,
                fontWeight = FontWeight.Bold
              )
            )
            Text(
              text = result.formula,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = Navy900
              )
            )
          }
        }
      }

      // Additional metrics if any
      if (result.additionalMetrics.isNotEmpty()) {
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = "Key Metrics",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = TextSecondary
          )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Column(
          verticalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          result.additionalMetrics.forEach { (label, value) ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
              )
              Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Navy900
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Simple Statistical Interpretation
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = CyanLight,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = "Interpretation",
            tint = CyanAccent,
            modifier = Modifier
              .size(20.dp)
              .padding(top = 2.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Statistical Interpretation",
              style = MaterialTheme.typography.labelSmall.copy(
                color = CyanAccent,
                fontWeight = FontWeight.Bold
              )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = result.interpretation,
              style = MaterialTheme.typography.bodyMedium.copy(
                color = Navy900,
                lineHeight = 20.sp
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Step-by-step breakdown toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable { stepsExpanded = !stepsExpanded }
          .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Steps",
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Step-by-step Calculation (${result.steps.size} steps)",
            style = MaterialTheme.typography.titleMedium.copy(
              color = Navy900,
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold
            )
          )
        }
        Icon(
          imageVector = if (stepsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = if (stepsExpanded) "Collapse" else "Expand",
          tint = TextSecondary
        )
      }

      AnimatedVisibility(
        visible = stepsExpanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          result.steps.forEach { step ->
            Text(
              text = step,
              style = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimary,
                fontFamily = if (step.contains("=") || step.contains("•")) FontFamily.Monospace else FontFamily.Default,
                fontSize = 13.sp,
                lineHeight = 18.sp
              )
            )
          }
        }
      }
    }
  }
}
