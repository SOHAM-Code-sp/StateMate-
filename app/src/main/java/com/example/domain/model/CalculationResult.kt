package com.example.domain.model

data class CalculationResult(
  val operation: StatOperation,
  val mainResult: String,
  val formula: String,
  val steps: List<String>,
  val interpretation: String,
  val formattedDataset: String,
  val formattedDatasetY: String? = null,
  val observationCount: Int = 0,
  val observationCountY: Int? = null,
  val additionalMetrics: Map<String, String> = emptyMap()
)
