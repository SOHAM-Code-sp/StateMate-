package com.example.domain.calculator

import com.example.domain.model.CalculationResult
import com.example.domain.model.StatOperation
import java.math.BigInteger
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt

object StatisticalCalculator {

  fun parseNumbers(rawInput: String): Result<List<Double>> {
    val trimmed = rawInput.trim()
    if (trimmed.isEmpty()) {
      return Result.failure(IllegalArgumentException("Please enter a dataset of numbers."))
    }
    val tokens = trimmed.split(Regex("[,;\\s]+")).filter { it.isNotBlank() }
    if (tokens.isEmpty()) {
      return Result.failure(IllegalArgumentException("No valid numbers detected."))
    }
    val numbers = mutableListOf<Double>()
    for (token in tokens) {
      val num = token.toDoubleOrNull()
        ?: return Result.failure(
          IllegalArgumentException("Invalid numerical entry '$token'. Please use numbers like 12, -4.5, 0.75 separated by commas.")
        )
      if (num.isNaN() || num.isInfinite()) {
        return Result.failure(IllegalArgumentException("Values cannot be NaN or infinite."))
      }
      numbers.add(num)
    }
    return Result.success(numbers)
  }

  fun formatNum(value: Double, decimals: Int = 4): String {
    if (value.isNaN()) return "Undefined"
    if (value.isInfinite()) return if (value > 0) "+∞" else "-∞"
    // If it's an exact integer
    if (abs(value - value.roundToLong()) < 1e-9 && abs(value) < 1e12) {
      return value.roundToLong().toString()
    }
    val format = "%.${decimals}f"
    val formatted = String.format(Locale.US, format, value)
    return formatted.trimEnd('0').trimEnd('.')
  }

  fun calculate(
    operation: StatOperation,
    inputX: String,
    inputY: String = "",
    extraParam: Double = 50.0, // Used for percentile p, or events
    decimals: Int = 4
  ): Result<CalculationResult> {
    return runCatching {
      when (operation) {
        // --- DESCRIPTIVE ---
        StatOperation.MEAN -> calculateMean(inputX, decimals)
        StatOperation.MEDIAN -> calculateMedian(inputX, decimals)
        StatOperation.MODE -> calculateMode(inputX)

        // --- DISPERSION ---
        StatOperation.POPULATION_VARIANCE -> calculateVariance(inputX, isSample = false, decimals)
        StatOperation.SAMPLE_VARIANCE -> calculateVariance(inputX, isSample = true, decimals)
        StatOperation.POPULATION_STD_DEV -> calculateStdDev(inputX, isSample = false, decimals)
        StatOperation.SAMPLE_STD_DEV -> calculateStdDev(inputX, isSample = true, decimals)
        StatOperation.RANGE -> calculateRange(inputX, decimals)

        // --- POSITION ---
        StatOperation.QUARTILES -> calculateQuartiles(inputX, decimals)
        StatOperation.INTERQUARTILE_RANGE -> calculateIQR(inputX, decimals)
        StatOperation.PERCENTILE -> calculatePercentile(inputX, extraParam, decimals)

        // --- PROBABILITY ---
        StatOperation.BASIC_PROBABILITY -> calculateBasicProbability(inputX, inputY, decimals)
        StatOperation.PERMUTATIONS -> calculatePermutations(inputX, inputY)
        StatOperation.COMBINATIONS -> calculateCombinations(inputX, inputY)

        // --- RELATIONSHIP ---
        StatOperation.COVARIANCE -> calculateCovariance(inputX, inputY, decimals)
        StatOperation.PEARSON_CORRELATION -> calculateCorrelation(inputX, inputY, decimals)

        // --- REGRESSION ---
        StatOperation.LINEAR_REGRESSION -> calculateLinearRegression(inputX, inputY, decimals)
      }
    }
  }

  // 1. MEAN
  private fun calculateMean(inputX: String, decimals: Int): CalculationResult {
    val data = parseNumbers(inputX).getOrThrow()
    val n = data.size
    val sum = data.sum()
    val mean = sum / n

    val steps = listOf(
      "1. Count total observations: N = $n",
      "2. Compute sum of all values: ∑x = ${data.joinToString(" + ") { formatNum(it, decimals) }} = ${formatNum(sum, decimals)}",
      "3. Divide sum by sample size: x̄ = ${formatNum(sum, decimals)} / $n = ${formatNum(mean, decimals)}"
    )

    return CalculationResult(
      operation = StatOperation.MEAN,
      mainResult = formatNum(mean, decimals),
      formula = "x̄ = (∑x) / N",
      steps = steps,
      interpretation = "The arithmetic mean is ${formatNum(mean, decimals)}. This represents the balance point or expected center of the $n observations.",
      formattedDataset = data.joinToString(", ") { formatNum(it, decimals) },
      observationCount = n,
      additionalMetrics = mapOf(
        "Sum (∑x)" to formatNum(sum, decimals),
        "Observations (N)" to n.toString()
      )
    )
  }

  // 2. MEDIAN
  private fun calculateMedian(inputX: String, decimals: Int): CalculationResult {
    val data = parseNumbers(inputX).getOrThrow()
    val n = data.size
    val sorted = data.sorted()
    val median: Double
    val steps = mutableListOf<String>()

    steps.add("1. Sort the observations in ascending order: [${sorted.joinToString(", ") { formatNum(it, decimals) }}]")
    steps.add("2. Determine dataset size: N = $n")

    if (n % 2 == 1) {
      val midIdx = n / 2
      median = sorted[midIdx]
      steps.add("3. Since N is odd ($n), the median is the middle value at position ${(n + 1) / 2}:")
      steps.add("   Median = sorted[$midIdx] = ${formatNum(median, decimals)}")
    } else {
      val midIdx1 = (n / 2) - 1
      val midIdx2 = n / 2
      val v1 = sorted[midIdx1]
      val v2 = sorted[midIdx2]
      median = (v1 + v2) / 2.0
      steps.add("3. Since N is even ($n), the median is the average of middle positions $midIdx1 and $midIdx2:")
      steps.add("   Values: ${formatNum(v1, decimals)} and ${formatNum(v2, decimals)}")
      steps.add("   Median = (${formatNum(v1, decimals)} + ${formatNum(v2, decimals)}) / 2 = ${formatNum(median, decimals)}")
    }

    return CalculationResult(
      operation = StatOperation.MEDIAN,
      mainResult = formatNum(median, decimals),
      formula = if (n % 2 == 1) "M = x_((N+1)/2)" else "M = (x_(N/2) + x_(N/2 + 1)) / 2",
      steps = steps,
      interpretation = "50% of the observations fall below ${formatNum(median, decimals)} and 50% fall above it, providing a robust measure of central tendency unaffected by extreme outliers.",
      formattedDataset = data.joinToString(", ") { formatNum(it, decimals) },
      observationCount = n,
      additionalMetrics = mapOf(
        "Sorted Minimum" to formatNum(sorted.first(), decimals),
        "Sorted Maximum" to formatNum(sorted.last(), decimals)
      )
    )
  }

  // 3. MODE
  private fun calculateMode(inputX: String): CalculationResult {
    val data = parseNumbers(inputX).getOrThrow()
    val n = data.size
    val frequencies = mutableMapOf<Double, Int>()
    for (num in data) {
      frequencies[num] = (frequencies[num] ?: 0) + 1
    }

    val maxFreq = frequencies.values.maxOrNull() ?: 1
    val modes = frequencies.filter { it.value == maxFreq }.keys.sorted()

    val steps = mutableListOf<String>()
    steps.add("1. Tally frequency of each distinct value:")
    frequencies.entries.sortedByDescending { it.value }.forEach {
      steps.add("   • Value ${formatNum(it.key, 4)}: appeared ${it.value} time(s)")
    }

    val resultStr: String
    val interpretation: String

    if (maxFreq == 1 && n > 1) {
      resultStr = "No unique mode"
      steps.add("2. Maximum frequency is 1. Since every value appears exactly once, there is no distinct mode (uniform distribution).")
      interpretation = "All $n observations occurred with equal frequency (1 time). The dataset exhibits no modal concentration."
    } else if (modes.size == data.distinct().size && n > 1) {
      resultStr = "No mode (uniform)"
      steps.add("2. All distinct values share the identical frequency of $maxFreq.")
      interpretation = "Every distinct value in the sample appears $maxFreq times, indicating a uniform multi-occurrence dataset without a central peak."
    } else {
      val modeStr = modes.joinToString(", ") { formatNum(it, 4) }
      val typeStr = when (modes.size) {
        1 -> "Unimodal"
        2 -> "Bimodal"
        3 -> "Trimodal"
        else -> "Multimodal"
      }
      resultStr = modeStr
      steps.add("2. Maximum frequency achieved is $maxFreq.")
      steps.add("3. Value(s) appearing $maxFreq time(s): $modeStr ($typeStr)")
      interpretation = "$typeStr distribution: $modeStr occurred most frequently ($maxFreq times out of $n observations)."
    }

    return CalculationResult(
      operation = StatOperation.MODE,
      mainResult = resultStr,
      formula = "Mo = argmax(frequency)",
      steps = steps,
      interpretation = interpretation,
      formattedDataset = data.joinToString(", ") { formatNum(it, 4) },
      observationCount = n,
      additionalMetrics = mapOf(
        "Highest Frequency" to "$maxFreq times",
        "Unique Values" to "${frequencies.size}"
      )
    )
  }

  // 4 & 5. VARIANCE (Population & Sample)
  private fun calculateVariance(inputX: String, isSample: Boolean, decimals: Int): CalculationResult {
    val data = parseNumbers(inputX).getOrThrow()
    val n = data.size
    if (isSample && n < 2) {
      throw IllegalArgumentException("Sample variance requires at least 2 observations to apply Bessel's correction (N - 1 = ${n - 1}).")
    }

    val sum = data.sum()
    val mean = sum / n
    val squaredDeviations = data.map { (it - mean).pow(2) }
    val sumSquaredDev = squaredDeviations.sum()

    val divisor = if (isSample) (n - 1) else n
    val variance = sumSquaredDev / divisor

    val steps = mutableListOf<String>()
    steps.add("1. Calculate mean: x̄ = ${formatNum(sum, decimals)} / $n = ${formatNum(mean, decimals)}")
    steps.add("2. Calculate squared deviations from mean (xᵢ - x̄)²:")
    data.take(5).forEachIndexed { i, x ->
      val dev = x - mean
      steps.add("   • (${formatNum(x, decimals)} - ${formatNum(mean, decimals)})² = (${formatNum(dev, decimals)})² = ${formatNum(dev.pow(2), decimals)}")
    }
    if (data.size > 5) {
      steps.add("   ... (and ${data.size - 5} additional observations)")
    }
    steps.add("3. Sum of squared deviations: SS = ∑(x - x̄)² = ${formatNum(sumSquaredDev, decimals)}")
    val formula = if (isSample) "s² = ∑(x - x̄)² / (N - 1)" else "σ² = ∑(x - μ)² / N"
    steps.add("4. Divide by degrees of freedom ($divisor):")
    steps.add("   ${if (isSample) "s²" else "σ²"} = ${formatNum(sumSquaredDev, decimals)} / $divisor = ${formatNum(variance, decimals)}")

    val interpretation = if (isSample) {
      "Sample variance is ${formatNum(variance, decimals)}. Dividing by N - 1 ($divisor) provides an unbiased estimate of the underlying population variance."
    } else {
      "Population variance is ${formatNum(variance, decimals)}. This represents the exact average squared distance of all $n population members from the true mean."
    }

    return CalculationResult(
      operation = if (isSample) StatOperation.SAMPLE_VARIANCE else StatOperation.POPULATION_VARIANCE,
      mainResult = formatNum(variance, decimals),
      formula = formula,
      steps = steps,
      interpretation = interpretation,
      formattedDataset = data.joinToString(", ") { formatNum(it, decimals) },
      observationCount = n,
      additionalMetrics = mapOf(
        "Mean" to formatNum(mean, decimals),
        "Sum of Squares (SS)" to formatNum(sumSquaredDev, decimals),
        "Divisor (${if (isSample) "N-1" else "N"})" to divisor.toString()
      )
    )
  }

  // 6 & 7. STANDARD DEVIATION (Population & Sample)
  private fun calculateStdDev(inputX: String, isSample: Boolean, decimals: Int): CalculationResult {
    val varResult = calculateVariance(inputX, isSample, decimals)
    val variance = varResult.mainResult.toDoubleOrNull() ?: 0.0
    val stdDev = sqrt(variance)

    val steps = varResult.steps.toMutableList()
    val formula = if (isSample) "s = √(∑(x - x̄)² / (N - 1))" else "σ = √(∑(x - μ)² / N)"
    steps.add("5. Take the positive square root of variance:")
    steps.add("   ${if (isSample) "s" else "σ"} = √(${formatNum(variance, decimals)}) = ${formatNum(stdDev, decimals)}")

    val interpretation = if (isSample) {
      "The sample standard deviation is ${formatNum(stdDev, decimals)}. In a normal distribution, roughly 68% of observations will fall within ±${formatNum(stdDev, decimals)} of the mean."
    } else {
      "The population standard deviation is ${formatNum(stdDev, decimals)}, measuring overall data dispersion in original measurement units."
    }

    return CalculationResult(
      operation = if (isSample) StatOperation.SAMPLE_STD_DEV else StatOperation.POPULATION_STD_DEV,
      mainResult = formatNum(stdDev, decimals),
      formula = formula,
      steps = steps,
      interpretation = interpretation,
      formattedDataset = varResult.formattedDataset,
      observationCount = varResult.observationCount,
      additionalMetrics = mapOf(
        "Variance" to formatNum(variance, decimals),
        "${if (isSample) "Sample" else "Population"} Size" to varResult.observationCount.toString()
      )
    )
  }

  // 8. RANGE
  private fun calculateRange(inputX: String, decimals: Int): CalculationResult {
    val data = parseNumbers(inputX).getOrThrow()
    val n = data.size
    val min = data.minOrNull() ?: 0.0
    val max = data.maxOrNull() ?: 0.0
    val range = max - min

    val steps = listOf(
      "1. Find maximum observation: Max = ${formatNum(max, decimals)}",
      "2. Find minimum observation: Min = ${formatNum(min, decimals)}",
      "3. Compute difference: Range = Max - Min = ${formatNum(max, decimals)} - ${formatNum(min, decimals)} = ${formatNum(range, decimals)}"
    )

    return CalculationResult(
      operation = StatOperation.RANGE,
      mainResult = formatNum(range, decimals),
      formula = "R = Max - Min",
      steps = steps,
      interpretation = "The total spread between the highest observation (${formatNum(max, decimals)}) and lowest observation (${formatNum(min, decimals)}) is ${formatNum(range, decimals)} units.",
      formattedDataset = data.joinToString(", ") { formatNum(it, decimals) },
      observationCount = n,
      additionalMetrics = mapOf(
        "Maximum" to formatNum(max, decimals),
        "Minimum" to formatNum(min, decimals)
      )
    )
  }

  // Helper for Tukey's / Split-half quartiles
  private fun computeTukeyQuartiles(sorted: List<Double>): Triple<Double, Double, Double> {
    val n = sorted.size
    val q2 = if (n % 2 == 1) sorted[n / 2] else (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0

    val lowerHalf = if (n % 2 == 1) sorted.subList(0, n / 2) else sorted.subList(0, n / 2)
    val upperHalf = if (n % 2 == 1) sorted.subList(n / 2 + 1, n) else sorted.subList(n / 2, n)

    fun medianOf(list: List<Double>): Double {
      val m = list.size
      if (m == 0) return 0.0
      return if (m % 2 == 1) list[m / 2] else (list[m / 2 - 1] + list[m / 2]) / 2.0
    }

    val q1 = medianOf(lowerHalf)
    val q3 = medianOf(upperHalf)
    return Triple(q1, q2, q3)
  }

  // 9. QUARTILES
  private fun calculateQuartiles(inputX: String, decimals: Int): CalculationResult {
    val data = parseNumbers(inputX).getOrThrow()
    if (data.size < 3) {
      throw IllegalArgumentException("Quartile computation requires at least 3 observations (supplied: ${data.size}).")
    }
    val sorted = data.sorted()
    val (q1, q2, q3) = computeTukeyQuartiles(sorted)
    val iqr = q3 - q1

    val steps = listOf(
      "1. Sort dataset: [${sorted.joinToString(", ") { formatNum(it, decimals) }}]",
      "2. Calculate Q2 (Median, 50th percentile) = ${formatNum(q2, decimals)}",
      "3. Lower half of dataset: Q1 (25th percentile) = ${formatNum(q1, decimals)}",
      "4. Upper half of dataset: Q3 (75th percentile) = ${formatNum(q3, decimals)}",
      "5. Interquartile Range: IQR = Q3 - Q1 = ${formatNum(q3, decimals)} - ${formatNum(q1, decimals)} = ${formatNum(iqr, decimals)}"
    )

    return CalculationResult(
      operation = StatOperation.QUARTILES,
      mainResult = "Q1: ${formatNum(q1, decimals)}, Q2: ${formatNum(q2, decimals)}, Q3: ${formatNum(q3, decimals)}",
      formula = "Q1 (25%), Q2 (50%), Q3 (75%) [Tukey split-half]",
      steps = steps,
      interpretation = "25% of data is ≤ ${formatNum(q1, decimals)}, 50% is ≤ ${formatNum(q2, decimals)}, and 75% is ≤ ${formatNum(q3, decimals)}. Middle 50% spread spans ${formatNum(iqr, decimals)} units.",
      formattedDataset = data.joinToString(", ") { formatNum(it, decimals) },
      observationCount = data.size,
      additionalMetrics = mapOf(
        "First Quartile (Q1)" to formatNum(q1, decimals),
        "Second Quartile (Median)" to formatNum(q2, decimals),
        "Third Quartile (Q3)" to formatNum(q3, decimals),
        "IQR (Q3 - Q1)" to formatNum(iqr, decimals)
      )
    )
  }

  // 10. INTERQUARTILE RANGE (IQR)
  private fun calculateIQR(inputX: String, decimals: Int): CalculationResult {
    val quartiles = calculateQuartiles(inputX, decimals)
    val q1Str = quartiles.additionalMetrics["First Quartile (Q1)"] ?: "0"
    val q3Str = quartiles.additionalMetrics["Third Quartile (Q3)"] ?: "0"
    val q1 = q1Str.toDoubleOrNull() ?: 0.0
    val q3 = q3Str.toDoubleOrNull() ?: 0.0
    val iqr = q3 - q1

    val steps = quartiles.steps.toMutableList()
    steps.add("6. Compute IQR: IQR = Q3 - Q1 = ${formatNum(q3, decimals)} - ${formatNum(q1, decimals)} = ${formatNum(iqr, decimals)}")

    return CalculationResult(
      operation = StatOperation.INTERQUARTILE_RANGE,
      mainResult = formatNum(iqr, decimals),
      formula = "IQR = Q3 - Q1",
      steps = steps,
      interpretation = "The middle 50% of your data has a range of ${formatNum(iqr, decimals)} units. Values outside [${formatNum(q1 - 1.5 * iqr, decimals)}, ${formatNum(q3 + 1.5 * iqr, decimals)}] are potential statistical outliers.",
      formattedDataset = quartiles.formattedDataset,
      observationCount = quartiles.observationCount,
      additionalMetrics = mapOf(
        "Q1 (25th)" to formatNum(q1, decimals),
        "Q3 (75th)" to formatNum(q3, decimals),
        "Lower Outlier Fence" to formatNum(q1 - 1.5 * iqr, decimals),
        "Upper Outlier Fence" to formatNum(q3 + 1.5 * iqr, decimals)
      )
    )
  }

  // 11. PERCENTILE
  private fun calculatePercentile(inputX: String, p: Double, decimals: Int): CalculationResult {
    val data = parseNumbers(inputX).getOrThrow()
    if (p < 0.0 || p > 100.0) {
      throw IllegalArgumentException("Percentile rank p must be between 0 and 100 (entered: $p).")
    }
    val n = data.size
    val sorted = data.sorted()

    // Using standard linear interpolation convention (NIST method)
    val rank = (p / 100.0) * (n - 1)
    val lowerIdx = rank.toInt()
    val upperIdx = if (lowerIdx + 1 < n) lowerIdx + 1 else lowerIdx
    val fraction = rank - lowerIdx

    val percentileValue = if (lowerIdx == upperIdx) {
      sorted[lowerIdx]
    } else {
      sorted[lowerIdx] + fraction * (sorted[upperIdx] - sorted[lowerIdx])
    }

    val steps = listOf(
      "1. Sort observations in ascending order: [${sorted.joinToString(", ") { formatNum(it, decimals) }}]",
      "2. Calculate percentile index: index = (P / 100) × (N - 1) = (${formatNum(p, 1)} / 100) × (${n - 1}) = ${formatNum(rank, 4)}",
      "3. Interpolate between position $lowerIdx (${formatNum(sorted[lowerIdx], decimals)}) and $upperIdx (${formatNum(sorted[upperIdx], decimals)}):",
      "   Value = ${formatNum(sorted[lowerIdx], decimals)} + ${formatNum(fraction, 4)} × (${formatNum(sorted[upperIdx], decimals)} - ${formatNum(sorted[lowerIdx], decimals)}) = ${formatNum(percentileValue, decimals)}"
    )

    return CalculationResult(
      operation = StatOperation.PERCENTILE,
      mainResult = formatNum(percentileValue, decimals),
      formula = "P_p = x_(k) + d × (x_(k+1) - x_(k))",
      steps = steps,
      interpretation = "Approximately ${formatNum(p, 1)}% of observations in the dataset fall at or below ${formatNum(percentileValue, decimals)}.",
      formattedDataset = data.joinToString(", ") { formatNum(it, decimals) },
      observationCount = n,
      additionalMetrics = mapOf(
        "Percentile Rank (p)" to "${formatNum(p, 1)}%",
        "Interpolated Index" to formatNum(rank, 3)
      )
    )
  }

  // 12. BASIC PROBABILITY
  private fun calculateBasicProbability(eventInput: String, sampleSpaceInput: String, decimals: Int): CalculationResult {
    val nE = eventInput.trim().toDoubleOrNull()
      ?: throw IllegalArgumentException("Enter a valid number for favorable outcomes n(E).")
    val nS = sampleSpaceInput.trim().toDoubleOrNull()
      ?: throw IllegalArgumentException("Enter a valid number for total sample space n(S).")

    if (nS <= 0) {
      throw IllegalArgumentException("Total sample space n(S) must be strictly greater than 0.")
    }
    if (nE < 0) {
      throw IllegalArgumentException("Favorable events n(E) cannot be negative.")
    }
    if (nE > nS) {
      throw IllegalArgumentException("Favorable events n(E) = $nE cannot exceed total sample space n(S) = $nS.")
    }

    val probability = nE / nS
    val percentage = probability * 100.0

    val steps = listOf(
      "1. Identify favorable outcomes: n(E) = ${formatNum(nE, decimals)}",
      "2. Identify total outcomes in sample space: n(S) = ${formatNum(nS, decimals)}",
      "3. Apply classical probability formula: P(E) = n(E) / n(S)",
      "4. Calculate: P(E) = ${formatNum(nE, decimals)} / ${formatNum(nS, decimals)} = ${formatNum(probability, decimals)} (${formatNum(percentage, 2)}%)"
    )

    return CalculationResult(
      operation = StatOperation.BASIC_PROBABILITY,
      mainResult = "${formatNum(probability, decimals)} (${formatNum(percentage, 2)}%)",
      formula = "P(E) = n(E) / n(S)",
      steps = steps,
      interpretation = "There is a ${formatNum(percentage, 2)}% probability of event E occurring, with an odds ratio of ${formatNum(nE, 0)} in ${formatNum(nS, 0)}.",
      formattedDataset = "n(E) = ${formatNum(nE, decimals)}, n(S) = ${formatNum(nS, decimals)}",
      observationCount = nS.toInt(),
      additionalMetrics = mapOf(
        "Favorable Outcomes n(E)" to formatNum(nE, decimals),
        "Sample Space n(S)" to formatNum(nS, decimals),
        "Complement P(E')" to "${formatNum(1.0 - probability, decimals)} (${formatNum((1.0 - probability) * 100, 2)}%)"
      )
    )
  }

  // 13. PERMUTATIONS
  private fun calculatePermutations(nStr: String, rStr: String): CalculationResult {
    val nVal = nStr.trim().toLongOrNull() ?: throw IllegalArgumentException("Total items n must be an integer ≥ 0.")
    val rVal = rStr.trim().toLongOrNull() ?: throw IllegalArgumentException("Selected items r must be an integer ≥ 0.")

    if (nVal < 0 || rVal < 0) {
      throw IllegalArgumentException("Both n and r must be non-negative integers.")
    }
    if (rVal > nVal) {
      throw IllegalArgumentException("Selected items r ($rVal) cannot exceed available items n ($nVal).")
    }

    // Permutation = n! / (n - r)! = n * (n - 1) * ... * (n - r + 1)
    var result = BigInteger.ONE
    val factors = mutableListOf<String>()
    for (i in 0 until rVal) {
      val factor = (nVal - i)
      result = result.multiply(BigInteger.valueOf(factor))
      if (factors.size < 6) factors.add(factor.toString())
    }

    val factorStr = if (factors.size < rVal) {
      factors.joinToString(" × ") + " × ... × " + (nVal - rVal + 1)
    } else {
      if (factors.isEmpty()) "1" else factors.joinToString(" × ")
    }

    val steps = listOf(
      "1. Formula for permutations: ⁿPᵣ = n! / (n - r)!",
      "2. Substitute n = $nVal and r = $rVal: $nVal! / ($nVal - $rVal)!",
      "3. Expand product of $rVal consecutive decreasing integers: $factorStr",
      "4. Compute result: ⁿPᵣ = $result"
    )

    return CalculationResult(
      operation = StatOperation.PERMUTATIONS,
      mainResult = result.toString(),
      formula = "ⁿPᵣ = n! / (n - r)!",
      steps = steps,
      interpretation = "There are $result unique ordered ways to arrange $rVal items from a set of $nVal distinct items (where order of selection matters).",
      formattedDataset = "n = $nVal, r = $rVal",
      observationCount = nVal.toInt(),
      additionalMetrics = mapOf(
        "Total items (n)" to nVal.toString(),
        "Selected items (r)" to rVal.toString()
      )
    )
  }

  // 14. COMBINATIONS
  private fun calculateCombinations(nStr: String, rStr: String): CalculationResult {
    val nVal = nStr.trim().toLongOrNull() ?: throw IllegalArgumentException("Total items n must be an integer ≥ 0.")
    val rVal = rStr.trim().toLongOrNull() ?: throw IllegalArgumentException("Selected items r must be an integer ≥ 0.")

    if (nVal < 0 || rVal < 0) {
      throw IllegalArgumentException("Both n and r must be non-negative integers.")
    }
    if (rVal > nVal) {
      throw IllegalArgumentException("Selected items r ($rVal) cannot exceed total items n ($nVal).")
    }

    // C(n, r) = n! / (r! * (n - r)!) = ⁿPᵣ / r!
    val effectiveR = if (rVal > nVal - rVal) (nVal - rVal) else rVal
    var numerator = BigInteger.ONE
    var denominator = BigInteger.ONE

    for (i in 1..effectiveR) {
      numerator = numerator.multiply(BigInteger.valueOf(nVal - i + 1))
      denominator = denominator.multiply(BigInteger.valueOf(i))
    }
    val combinations = numerator.divide(denominator)

    val steps = listOf(
      "1. Formula for combinations: ⁿCᵣ = n! / [r! × (n - r)!]",
      "2. Substitute n = $nVal, r = $rVal",
      "3. Compute product reduction: ⁿCᵣ = (ⁿPᵣ) / r!",
      "4. Evaluates to: $combinations"
    )

    return CalculationResult(
      operation = StatOperation.COMBINATIONS,
      mainResult = combinations.toString(),
      formula = "ⁿCᵣ = n! / [r!(n - r)!]",
      steps = steps,
      interpretation = "There are $combinations distinct ways to select a subgroup of $rVal items from $nVal items (where order does not matter).",
      formattedDataset = "n = $nVal, r = $rVal",
      observationCount = nVal.toInt(),
      additionalMetrics = mapOf(
        "n" to nVal.toString(),
        "r" to rVal.toString()
      )
    )
  }

  // 15. COVARIANCE
  private fun calculateCovariance(inputX: String, inputY: String, decimals: Int): CalculationResult {
    val xList = parseNumbers(inputX).getOrThrow()
    val yList = parseNumbers(inputY).getOrThrow()

    if (xList.size != yList.size) {
      throw IllegalArgumentException("Dataset X has ${xList.size} observations, but Dataset Y has ${yList.size} observations. Both datasets must have equal observation counts.")
    }
    val n = xList.size
    if (n < 2) {
      throw IllegalArgumentException("Covariance requires at least 2 pairs of observations (supplied: $n).")
    }

    val meanX = xList.sum() / n
    val meanY = yList.sum() / n

    var sumCrossProduct = 0.0
    val samplePairs = mutableListOf<String>()

    for (i in 0 until n) {
      val dx = xList[i] - meanX
      val dy = yList[i] - meanY
      val prod = dx * dy
      sumCrossProduct += prod
      if (samplePairs.size < 4) {
        samplePairs.add("(${formatNum(xList[i], decimals)} - ${formatNum(meanX, decimals)})(${formatNum(yList[i], decimals)} - ${formatNum(meanY, decimals)}) = ${formatNum(prod, decimals)}")
      }
    }

    val sampleCov = sumCrossProduct / (n - 1)
    val popCov = sumCrossProduct / n

    val steps = mutableListOf<String>()
    steps.add("1. Compute Means: x̄ = ${formatNum(meanX, decimals)}, ȳ = ${formatNum(meanY, decimals)} (N = $n pairs)")
    steps.add("2. Compute cross-product deviations (xᵢ - x̄)(yᵢ - ȳ):")
    samplePairs.forEach { steps.add("   • $it") }
    if (n > 4) steps.add("   ... (plus ${n - 4} remaining observation pairs)")
    steps.add("3. Sum of cross products: ∑(x - x̄)(y - ȳ) = ${formatNum(sumCrossProduct, decimals)}")
    steps.add("4. Sample Covariance (N - 1 = ${n - 1}): Cov(X,Y) = ${formatNum(sumCrossProduct, decimals)} / ${n - 1} = ${formatNum(sampleCov, decimals)}")
    steps.add("5. Population Covariance (N = $n): Cov_pop = ${formatNum(sumCrossProduct, decimals)} / $n = ${formatNum(popCov, decimals)}")

    val direction = when {
      sampleCov > 1e-7 -> "positive linear co-movement (as X increases, Y tends to increase)"
      sampleCov < -1e-7 -> "negative / inverse linear co-movement (as X increases, Y tends to decrease)"
      else -> "zero linear co-movement (no linear relationship detected)"
    }

    return CalculationResult(
      operation = StatOperation.COVARIANCE,
      mainResult = formatNum(sampleCov, decimals),
      formula = "Cov(X,Y) = ∑(x - x̄)(y - ȳ) / (N - 1)",
      steps = steps,
      interpretation = "Sample covariance is ${formatNum(sampleCov, decimals)}, indicating a $direction.",
      formattedDataset = "X: " + xList.joinToString(", ") { formatNum(it, decimals) },
      formattedDatasetY = "Y: " + yList.joinToString(", ") { formatNum(it, decimals) },
      observationCount = n,
      observationCountY = n,
      additionalMetrics = mapOf(
        "Sample Covariance (N-1)" to formatNum(sampleCov, decimals),
        "Population Covariance (N)" to formatNum(popCov, decimals),
        "Mean of X" to formatNum(meanX, decimals),
        "Mean of Y" to formatNum(meanY, decimals)
      )
    )
  }

  // 16. PEARSON CORRELATION COEFFICIENT
  private fun calculateCorrelation(inputX: String, inputY: String, decimals: Int): CalculationResult {
    val xList = parseNumbers(inputX).getOrThrow()
    val yList = parseNumbers(inputY).getOrThrow()

    if (xList.size != yList.size) {
      throw IllegalArgumentException("Dataset X has ${xList.size} values, while Dataset Y has ${yList.size} values. Equal length required.")
    }
    val n = xList.size
    if (n < 2) {
      throw IllegalArgumentException("Correlation requires at least 2 pairs of values.")
    }

    val meanX = xList.sum() / n
    val meanY = yList.sum() / n

    val ssX = xList.sumOf { (it - meanX).pow(2) }
    val ssY = yList.sumOf { (it - meanY).pow(2) }

    // Check for constant datasets
    if (ssX < 1e-12) {
      throw IllegalArgumentException("Dataset X is constant (all values identical). Standard deviation sx = 0, so Pearson correlation is mathematically undefined.")
    }
    if (ssY < 1e-12) {
      throw IllegalArgumentException("Dataset Y is constant (all values identical). Standard deviation sy = 0, so Pearson correlation is mathematically undefined.")
    }

    var sumCross = 0.0
    for (i in 0 until n) {
      sumCross += (xList[i] - meanX) * (yList[i] - meanY)
    }

    val sx = sqrt(ssX / (n - 1))
    val sy = sqrt(ssY / (n - 1))
    val r = sumCross / sqrt(ssX * ssY)
    val clampedR = r.coerceIn(-1.0, 1.0)
    val r2 = clampedR.pow(2)

    val strength = when {
      abs(clampedR) >= 0.9 -> "Very strong"
      abs(clampedR) >= 0.7 -> "Strong"
      abs(clampedR) >= 0.5 -> "Moderate"
      abs(clampedR) >= 0.3 -> "Weak"
      else -> "Negligible / very weak"
    }
    val dir = if (clampedR > 0) "positive" else if (clampedR < 0) "negative" else "neutral"

    val steps = listOf(
      "1. Means: x̄ = ${formatNum(meanX, decimals)}, ȳ = ${formatNum(meanY, decimals)} (N = $n)",
      "2. Sum of cross products: ∑(x - x̄)(y - ȳ) = ${formatNum(sumCross, decimals)}",
      "3. Sum of squares for X: SS_x = ∑(x - x̄)² = ${formatNum(ssX, decimals)}",
      "4. Sum of squares for Y: SS_y = ∑(y - ȳ)² = ${formatNum(ssY, decimals)}",
      "5. Apply formula: r = ∑(x - x̄)(y - ȳ) / √(SS_x × SS_y)",
      "6. r = ${formatNum(sumCross, decimals)} / √(${formatNum(ssX, decimals)} × ${formatNum(ssY, decimals)}) = ${formatNum(clampedR, decimals)}"
    )

    return CalculationResult(
      operation = StatOperation.PEARSON_CORRELATION,
      mainResult = formatNum(clampedR, decimals),
      formula = "r = ∑(x - x̄)(y - ȳ) / [√(∑(x - x̄)²) · √(∑(y - ȳ)²)]",
      steps = steps,
      interpretation = "Pearson r = ${formatNum(clampedR, decimals)} demonstrates a $strength $dir linear correlation. ${formatNum(r2 * 100, 2)}% of variance in Y is linearly explained by X.",
      formattedDataset = "X: " + xList.joinToString(", ") { formatNum(it, decimals) },
      formattedDatasetY = "Y: " + yList.joinToString(", ") { formatNum(it, decimals) },
      observationCount = n,
      observationCountY = n,
      additionalMetrics = mapOf(
        "Pearson r" to formatNum(clampedR, decimals),
        "R-squared (R²)" to formatNum(r2, decimals),
        "Sample Std Dev (sx)" to formatNum(sx, decimals),
        "Sample Std Dev (sy)" to formatNum(sy, decimals)
      )
    )
  }

  // 17. SIMPLE LINEAR REGRESSION
  private fun calculateLinearRegression(inputX: String, inputY: String, decimals: Int): CalculationResult {
    val xList = parseNumbers(inputX).getOrThrow()
    val yList = parseNumbers(inputY).getOrThrow()

    if (xList.size != yList.size) {
      throw IllegalArgumentException("Dataset X has ${xList.size} points, but Y has ${yList.size} points. Both sets must be equal length.")
    }
    val n = xList.size
    if (n < 2) {
      throw IllegalArgumentException("Linear regression requires at least 2 coordinate pairs (X, Y).")
    }

    val meanX = xList.sum() / n
    val meanY = yList.sum() / n

    val ssX = xList.sumOf { (it - meanX).pow(2) }
    val ssY = yList.sumOf { (it - meanY).pow(2) }

    if (ssX < 1e-12) {
      throw IllegalArgumentException("Dataset X is constant (variance = 0). The regression line would be vertical, having an undefined slope.")
    }

    var sumCross = 0.0
    for (i in 0 until n) {
      sumCross += (xList[i] - meanX) * (yList[i] - meanY)
    }

    val slope = sumCross / ssX
    val intercept = meanY - (slope * meanX)

    val rSquared = if (ssY > 1e-12) {
      (sumCross / sqrt(ssX * ssY)).pow(2).coerceIn(0.0, 1.0)
    } else {
      1.0
    }

    val equationSign = if (intercept >= 0) "+ " + formatNum(intercept, decimals) else "- " + formatNum(abs(intercept), decimals)
    val equation = "y = ${formatNum(slope, decimals)}x $equationSign"

    val steps = listOf(
      "1. Compute sample means: x̄ = ${formatNum(meanX, decimals)}, ȳ = ${formatNum(meanY, decimals)} (N = $n)",
      "2. Calculate SS_xx = ∑(x - x̄)² = ${formatNum(ssX, decimals)}",
      "3. Calculate SS_xy = ∑(x - x̄)(y - ȳ) = ${formatNum(sumCross, decimals)}",
      "4. Calculate slope: m = SS_xy / SS_xx = ${formatNum(sumCross, decimals)} / ${formatNum(ssX, decimals)} = ${formatNum(slope, decimals)}",
      "5. Calculate y-intercept: b = ȳ - m·x̄ = ${formatNum(meanY, decimals)} - (${formatNum(slope, decimals)} × ${formatNum(meanX, decimals)}) = ${formatNum(intercept, decimals)}",
      "6. Fit equation: $equation",
      "7. Coefficient of determination: R² = ${formatNum(rSquared, decimals)} (${formatNum(rSquared * 100, 2)}%)"
    )

    return CalculationResult(
      operation = StatOperation.LINEAR_REGRESSION,
      mainResult = equation,
      formula = "y = mx + b | m = Cov(X,Y)/Var(X), b = ȳ - mx̄",
      steps = steps,
      interpretation = "For each 1 unit increase in X, Y changes by ${formatNum(slope, decimals)} units on average. The model accounts for ${formatNum(rSquared * 100, 2)}% of variance in Y (R² = ${formatNum(rSquared, decimals)}).",
      formattedDataset = "X: " + xList.joinToString(", ") { formatNum(it, decimals) },
      formattedDatasetY = "Y: " + yList.joinToString(", ") { formatNum(it, decimals) },
      observationCount = n,
      observationCountY = n,
      additionalMetrics = mapOf(
        "Slope (m)" to formatNum(slope, decimals),
        "Intercept (b)" to formatNum(intercept, decimals),
        "R-squared (R²)" to formatNum(rSquared, decimals),
        "Fitted Equation" to equation
      )
    )
  }
}
