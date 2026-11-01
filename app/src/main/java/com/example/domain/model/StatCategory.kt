package com.example.domain.model

enum class StatCategory(
  val id: String,
  val title: String,
  val subtitle: String,
  val iconName: String
) {
  DESCRIPTIVE(
    id = "descriptive",
    title = "Descriptive Statistics",
    subtitle = "Mean, Median, Mode",
    iconName = "analytics"
  ),
  DISPERSION(
    id = "dispersion",
    title = "Dispersion",
    subtitle = "Variance, Std Dev, Range",
    iconName = "tune"
  ),
  POSITION(
    id = "position",
    title = "Quartiles & Position",
    subtitle = "Q1, Q2, Q3, IQR, Percentiles",
    iconName = "view_column"
  ),
  PROBABILITY(
    id = "probability",
    title = "Probability",
    subtitle = "Basic, Permutations, Combinations",
    iconName = "casino"
  ),
  RELATIONSHIP(
    id = "relationship",
    title = "Relationship & Correlation",
    subtitle = "Covariance, Pearson r",
    iconName = "hub"
  ),
  REGRESSION(
    id = "regression",
    title = "Regression Analysis",
    subtitle = "Slope, Intercept, R²",
    iconName = "trending_up"
  );

  companion object {
    fun fromId(id: String): StatCategory {
      return entries.find { it.id.equals(id, ignoreCase = true) } ?: DESCRIPTIVE
    }
  }
}
