package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CalculationRecord
import com.example.domain.model.StatCategory
import com.example.domain.model.StatOperation
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanLight
import com.example.ui.theme.Navy100
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletLight
import com.example.ui.theme.VioletPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
  onStartCalculatingClicked: () -> Unit,
  onToolSelected: (StatOperation) -> Unit,
  onCategorySelected: (StatCategory) -> Unit,
  recentCalculations: List<CalculationRecord>,
  favoriteToolIds: Set<String>,
  onReopenRecord: (CalculationRecord) -> Unit,
  onToggleFavoriteTool: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen"),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // 1. Hero Banner
    item {
      HeroDashboardBanner(onStartCalculatingClicked = onStartCalculatingClicked)
    }

    // 2. Favorite Tools Section
    item {
      FavoriteToolsSection(
        favoriteToolIds = favoriteToolIds,
        onToolSelected = onToolSelected,
        onToggleFavoriteTool = onToggleFavoriteTool
      )
    }

    // 3. Two-Column Grid of 6 Statistical Tool Cards
    item {
      SectionTitle(
        title = "Statistical Tools",
        subtitle = "Select an analysis model to start calculating"
      )
    }

    item {
      StatisticalToolsGrid(
        onToolSelected = onToolSelected,
        onCategorySelected = onCategorySelected
      )
    }

    // 4. Recent Calculations (maximum three)
    item {
      RecentCalculationsSection(
        recentCalculations = recentCalculations,
        onReopenRecord = onReopenRecord,
        onStartCalculatingClicked = onStartCalculatingClicked
      )
    }
  }
}

@Composable
private fun HeroDashboardBanner(
  onStartCalculatingClicked: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(
        Brush.linearGradient(
          colors = listOf(Navy900, Navy800, Color(0xFF1B2A4A))
        )
      )
      .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(24.dp))
      .padding(24.dp)
  ) {
    Column {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = VioletPrimary,
            modifier = Modifier.size(44.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "∑",
                style = MaterialTheme.typography.titleLarge.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 24.sp
                )
              )
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "StatMate",
              style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            )
            Text(
              text = "Native Statistical Engine",
              style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "Understand your data with ease.",
        style = MaterialTheme.typography.headlineSmall.copy(
          color = Color.White,
          fontWeight = FontWeight.Bold,
          lineHeight = 28.sp
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Run descriptive, dispersion, regression, and probability analyses with zero lag and offline mathematical certainty.",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = Color(0xFFCBD5E1),
          lineHeight = 20.sp
        )
      )

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = onStartCalculatingClicked,
        colors = ButtonDefaults.buttonColors(
          containerColor = VioletPrimary,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("start_calculating_button")
      ) {
        Icon(
          imageVector = Icons.Default.Calculate,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Start Calculating",
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge.copy(
        color = Navy900,
        fontWeight = FontWeight.Bold
      )
    )
    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
    )
  }
}

@Composable
private fun FavoriteToolsSection(
  favoriteToolIds: Set<String>,
  onToolSelected: (StatOperation) -> Unit,
  onToggleFavoriteTool: (String) -> Unit
) {
  val favoriteOps = StatOperation.entries.filter { favoriteToolIds.contains(it.id) }
  if (favoriteOps.isEmpty()) return

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 8.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Star,
        contentDescription = null,
        tint = Color(0xFFF59E0B),
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Favorite Tools",
        style = MaterialTheme.typography.titleMedium.copy(
          color = Navy900,
          fontWeight = FontWeight.Bold
        )
      )
    }

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      favoriteOps.take(3).forEach { op ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .weight(1f)
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .clickable { onToolSelected(op) }
            .testTag("fav_tool_${op.id}")
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
          ) {
            Text(
              text = op.symbol,
              style = MaterialTheme.typography.titleMedium.copy(
                color = VioletPrimary,
                fontWeight = FontWeight.Bold
              )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = op.title,
              style = MaterialTheme.typography.labelMedium.copy(
                color = Navy900,
                fontWeight = FontWeight.SemiBold
              ),
              maxLines = 1
            )
            Text(
              text = op.category.title.substringBefore(" "),
              style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
            )
          }
        }
      }
    }
  }
}

data class ToolGridItem(
  val category: StatCategory,
  val defaultOperation: StatOperation,
  val title: String,
  val subtitle: String,
  val icon: ImageVector,
  val accentColor: Color
)

@Composable
private fun StatisticalToolsGrid(
  onToolSelected: (StatOperation) -> Unit,
  onCategorySelected: (StatCategory) -> Unit
) {
  // 6 Statistical tool cards as requested:
  // 1. Descriptive Statistics
  // 2. Probability
  // 3. Correlation
  // 4. Regression
  // 5. Dispersion
  // 6. Quartiles and Percentiles
  val tools = listOf(
    ToolGridItem(
      category = StatCategory.DESCRIPTIVE,
      defaultOperation = StatOperation.MEAN,
      title = "Descriptive Statistics",
      subtitle = "Mean, Median, Mode",
      icon = Icons.Default.Analytics,
      accentColor = VioletPrimary
    ),
    ToolGridItem(
      category = StatCategory.PROBABILITY,
      defaultOperation = StatOperation.BASIC_PROBABILITY,
      title = "Probability",
      subtitle = "Basic, nPr, nCr",
      icon = Icons.Default.Casino,
      accentColor = CyanAccent
    ),
    ToolGridItem(
      category = StatCategory.RELATIONSHIP,
      defaultOperation = StatOperation.PEARSON_CORRELATION,
      title = "Correlation",
      subtitle = "Pearson r, Covariance",
      icon = Icons.Default.Hub,
      accentColor = Color(0xFF0D9488)
    ),
    ToolGridItem(
      category = StatCategory.REGRESSION,
      defaultOperation = StatOperation.LINEAR_REGRESSION,
      title = "Regression",
      subtitle = "y = mx + b, Slope, R²",
      icon = Icons.Default.TrendingUp,
      accentColor = Color(0xFF4338CA)
    ),
    ToolGridItem(
      category = StatCategory.DISPERSION,
      defaultOperation = StatOperation.SAMPLE_STD_DEV,
      title = "Dispersion",
      subtitle = "Variance, Std Dev, Range",
      icon = Icons.Default.Tune,
      accentColor = Color(0xFFD97706)
    ),
    ToolGridItem(
      category = StatCategory.POSITION,
      defaultOperation = StatOperation.QUARTILES,
      title = "Quartiles & Percentiles",
      subtitle = "Q1, Q2, Q3, IQR, P-th",
      icon = Icons.Default.ViewColumn,
      accentColor = Color(0xFF0284C7)
    )
  )

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Render as 3 rows of 2 cards
    for (i in tools.indices step 2) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        val tool1 = tools[i]
        ToolCard(
          tool = tool1,
          modifier = Modifier.weight(1f),
          onClick = {
            onCategorySelected(tool1.category)
            onToolSelected(tool1.defaultOperation)
          }
        )
        if (i + 1 < tools.size) {
          val tool2 = tools[i + 1]
          ToolCard(
            tool = tool2,
            modifier = Modifier.weight(1f),
            onClick = {
              onCategorySelected(tool2.category)
              onToolSelected(tool2.defaultOperation)
            }
          )
        } else {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
private fun ToolCard(
  tool: ToolGridItem,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
      .clip(RoundedCornerShape(18.dp))
      .clickable { onClick() }
      .testTag("tool_card_${tool.category.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = tool.accentColor.copy(alpha = 0.12f),
        modifier = Modifier.size(42.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = tool.icon,
            contentDescription = null,
            tint = tool.accentColor,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = tool.title,
        style = MaterialTheme.typography.titleMedium.copy(
          color = Navy900,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        ),
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = tool.subtitle,
        style = MaterialTheme.typography.bodySmall.copy(
          color = TextSecondary,
          fontSize = 12.sp,
          lineHeight = 16.sp
        ),
        maxLines = 2
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "Open tool",
          style = MaterialTheme.typography.labelSmall.copy(
            color = tool.accentColor,
            fontWeight = FontWeight.Bold
          )
        )
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = tool.accentColor,
          modifier = Modifier.size(14.dp)
        )
      }
    }
  }
}

@Composable
private fun RecentCalculationsSection(
  recentCalculations: List<CalculationRecord>,
  onReopenRecord: (CalculationRecord) -> Unit,
  onStartCalculatingClicked: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.History,
          contentDescription = null,
          tint = Navy700,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Recent Calculations",
          style = MaterialTheme.typography.titleMedium.copy(
            color = Navy900,
            fontWeight = FontWeight.Bold
          )
        )
      }
      Text(
        text = "Max 3 items",
        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (recentCalculations.isEmpty()) {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
          .padding(20.dp)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "No calculations performed yet",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = TextSecondary,
              fontWeight = FontWeight.Medium
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Your recent calculations will be displayed here for instant recall.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
          )
        }
      }
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
        recentCalculations.take(3).forEach { record ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
              .clickable { onReopenRecord(record) }
              .testTag("recent_record_${record.id}")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = record.operationName,
                    style = MaterialTheme.typography.titleSmall.copy(
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
                        fontSize = 10.sp
                      ),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Data: ${record.datasetX}",
                  style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                  maxLines = 1
                )
                Text(
                  text = dateFormat.format(Date(record.timestamp)),
                  style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = record.primaryResult,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = VioletPrimary
                  )
                )
                Text(
                  text = "Tap to open",
                  style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
              }
            }
          }
        }
      }
    }
  }
}
