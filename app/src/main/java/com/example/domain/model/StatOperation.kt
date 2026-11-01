package com.example.domain.model

enum class InputType {
  SINGLE_DATASET,
  SINGLE_DATASET_WITH_P,
  DUAL_DATASET,
  PROBABILITY_EVENTS,
  COMBINATORICS
}

enum class StatOperation(
  val id: String,
  val category: StatCategory,
  val title: String,
  val symbol: String,
  val shortFormula: String,
  val description: String,
  val inputType: InputType,
  val minObservations: Int = 1
) {
  // A. Descriptive
  MEAN(
    id = "mean",
    category = StatCategory.DESCRIPTIVE,
    title = "Mean",
    symbol = "x̄",
    shortFormula = "x̄ = (∑x) / N",
    description = "The arithmetic average of all observations.",
    inputType = InputType.SINGLE_DATASET,
    minObservations = 1
  ),
  MEDIAN(
    id = "median",
    category = StatCategory.DESCRIPTIVE,
    title = "Median",
    symbol = "M",
    shortFormula = "M = (N + 1)/2-th value",
    description = "The middle value separating the higher half from the lower half.",
    inputType = InputType.SINGLE_DATASET,
    minObservations = 1
  ),
  MODE(
    id = "mode",
    category = StatCategory.DESCRIPTIVE,
    title = "Mode",
    symbol = "Mo",
    shortFormula = "Mo = argmax(freq(x))",
    description = "The most frequently occurring value(s) in the dataset.",
    inputType = InputType.SINGLE_DATASET,
    minObservations = 1
  ),

  // B. Dispersion
  POPULATION_VARIANCE(
    id = "pop_var",
    category = StatCategory.DISPERSION,
    title = "Population Variance",
    symbol = "σ²",
    shortFormula = "σ² = ∑(x - μ)² / N",
    description = "Average squared deviation of entire population observations from mean.",
    inputType = InputType.SINGLE_DATASET,
    minObservations = 1
  ),
  SAMPLE_VARIANCE(
    id = "sample_var",
    category = StatCategory.DISPERSION,
    title = "Sample Variance",
    symbol = "s²",
    shortFormula = "s² = ∑(x - x̄)² / (N - 1)",
    description = "Unbiased sample variance estimator using Bessel's correction.",
    inputType = InputType.SINGLE_DATASET,
    minObservations = 2
  ),
  POPULATION_STD_DEV(
    id = "pop_std",
    category = StatCategory.DISPERSION,
    title = "Population Std Dev",
    symbol = "σ",
    shortFormula = "σ = √(σ²)",
    description = "Square root of the population variance in original measurement units.",
    inputType = InputType.SINGLE_DATASET,
    minObservations = 1
  ),
  SAMPLE_STD_DEV(
    id = "sample_std",
    category = StatCategory.DISPERSION,
    title = "Sample Std Dev",
    symbol = "s",
    shortFormula = "s = √(s²)",
    description = "Sample standard deviation estimating population dispersion.",
    inputType = InputType.SINGLE_DATASET,
    minObservations = 2
  ),
  RANGE(
    id = "range",
    category = StatCategory.DISPERSION,
    title = "Range",
    symbol = "R",
    shortFormula = "R = Max - Min",
    description = "The arithmetic difference between highest and lowest observations.",
    inputType = InputType.SINGLE_DATASET,
    minObservations = 1
  ),

  // C. Position
  QUARTILES(
    id = "quartiles",
    category = StatCategory.POSITION,
    title = "Quartiles (Q1, Q2, Q3)",
    symbol = "Q",
    shortFormula = "Q1 (25%), Q2 (50%), Q3 (75%)",
    description = "Divides ranked dataset into four equal segments.",
    inputType = InputType.SINGLE_DATASET,
    minObservations = 3
  ),
  INTERQUARTILE_RANGE(
    id = "iqr",
    category = StatCategory.POSITION,
    title = "Interquartile Range",
    symbol = "IQR",
    shortFormula = "IQR = Q3 - Q1",
    description = "Middle 50% statistical spread, robust against extreme outliers.",
    inputType = InputType.SINGLE_DATASET,
    minObservations = 3
  ),
  PERCENTILE(
    id = "percentile",
    category = StatCategory.POSITION,
    title = "Percentile",
    symbol = "Pₚ",
    shortFormula = "P = value at rank (p/100)(N + 1)",
    description = "Value below which a given percentage of observations falls.",
    inputType = InputType.SINGLE_DATASET_WITH_P,
    minObservations = 1
  ),

  // D. Probability
  BASIC_PROBABILITY(
    id = "basic_prob",
    category = StatCategory.PROBABILITY,
    title = "Basic Probability",
    symbol = "P(E)",
    shortFormula = "P(E) = n(E) / n(S)",
    description = "Likelihood of an event occurring relative to total sample space.",
    inputType = InputType.PROBABILITY_EVENTS,
    minObservations = 1
  ),
  PERMUTATIONS(
    id = "permutations",
    category = StatCategory.PROBABILITY,
    title = "Permutations",
    symbol = "P(n, r)",
    shortFormula = "ⁿPᵣ = n! / (n - r)!",
    description = "Number of ordered arrangements of r items taken from n items.",
    inputType = InputType.COMBINATORICS,
    minObservations = 1
  ),
  COMBINATIONS(
    id = "combinations",
    category = StatCategory.PROBABILITY,
    title = "Combinations",
    symbol = "C(n, r)",
    shortFormula = "ⁿCᵣ = n! / [r!(n - r)!]",
    description = "Number of unordered selections of r items taken from n items.",
    inputType = InputType.COMBINATORICS,
    minObservations = 1
  ),

  // E. Relationship
  COVARIANCE(
    id = "covariance",
    category = StatCategory.RELATIONSHIP,
    title = "Covariance",
    symbol = "Cov(X,Y)",
    shortFormula = "Cov = ∑(x - x̄)(y - ȳ) / (N - 1)",
    description = "Direction of linear relationship between variable X and variable Y.",
    inputType = InputType.DUAL_DATASET,
    minObservations = 2
  ),
  PEARSON_CORRELATION(
    id = "pearson_r",
    category = StatCategory.RELATIONSHIP,
    title = "Pearson Correlation",
    symbol = "r",
    shortFormula = "r = Cov(X,Y) / (sₓ · sᵧ)",
    description = "Standardized linear correlation strength ranging from -1 to +1.",
    inputType = InputType.DUAL_DATASET,
    minObservations = 2
  ),

  // F. Regression
  LINEAR_REGRESSION(
    id = "linear_regression",
    category = StatCategory.REGRESSION,
    title = "Simple Linear Regression",
    symbol = "ŷ",
    shortFormula = "y = mx + b (R²)",
    description = "Fits best least-squares trendline modeling Y as function of X.",
    inputType = InputType.DUAL_DATASET,
    minObservations = 2
  );

  companion object {
    fun fromId(id: String): StatOperation {
      return entries.find { it.id.equals(id, ignoreCase = true) } ?: MEAN
    }

    fun getByCategory(category: StatCategory): List<StatOperation> {
      return entries.filter { it.category == category }
    }
  }
}
