package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_records")
data class CalculationRecord(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val operationId: String,
  val operationName: String,
  val categoryId: String,
  val categoryName: String,
  val datasetX: String,
  val datasetY: String? = null,
  val primaryResult: String,
  val formula: String,
  val interpretation: String,
  val stepsRaw: String, // newline separated steps
  val observationCount: Int,
  val isFavorite: Boolean = false,
  val timestamp: Long = System.currentTimeMillis()
)
