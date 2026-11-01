package com.example

import com.example.domain.calculator.StatisticalCalculator
import com.example.domain.model.StatOperation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testMeanCalculation() {
    val result = StatisticalCalculator.calculate(
      operation = StatOperation.MEAN,
      inputX = "10, 20, 30",
      decimals = 2
    ).getOrThrow()

    assertEquals("20", result.mainResult)
    assertEquals(3, result.observationCount)
  }

  @Test
  fun testSampleVarianceAndStdDev() {
    // Dataset: 2, 4, 4, 4, 5, 5, 7, 9
    // N = 8, Sum = 40, Mean = 5
    // Sum of squared diffs: (2-5)^2 + 3*(4-5)^2 + 2*(5-5)^2 + (7-5)^2 + (9-5)^2 = 9 + 3 + 0 + 4 + 16 = 32
    // Sample variance = 32 / (8 - 1) = 32/7 ≈ 4.5714
    // Sample std dev = sqrt(32/7) ≈ 2.1381
    val varResult = StatisticalCalculator.calculate(
      operation = StatOperation.SAMPLE_VARIANCE,
      inputX = "2, 4, 4, 4, 5, 5, 7, 9",
      decimals = 4
    ).getOrThrow()
    assertEquals("4.5714", varResult.mainResult)

    val stdResult = StatisticalCalculator.calculate(
      operation = StatOperation.SAMPLE_STD_DEV,
      inputX = "2, 4, 4, 4, 5, 5, 7, 9",
      decimals = 4
    ).getOrThrow()
    assertEquals("2.1381", stdResult.mainResult)
  }

  @Test
  fun testPearsonCorrelationAndConstantDatasetHandling() {
    val rResult = StatisticalCalculator.calculate(
      operation = StatOperation.PEARSON_CORRELATION,
      inputX = "1, 2, 3, 4, 5",
      inputY = "2, 4, 6, 8, 10",
      decimals = 4
    ).getOrThrow()
    assertEquals("1", rResult.mainResult)

    // Constant dataset should fail gracefully
    val constantFailure = StatisticalCalculator.calculate(
      operation = StatOperation.PEARSON_CORRELATION,
      inputX = "5, 5, 5, 5",
      inputY = "1, 2, 3, 4",
      decimals = 4
    )
    assertTrue(constantFailure.isFailure)
  }

  @Test
  fun testPermutationsAndCombinations() {
    val pResult = StatisticalCalculator.calculate(
      operation = StatOperation.PERMUTATIONS,
      inputX = "5",
      inputY = "3"
    ).getOrThrow()
    assertEquals("60", pResult.mainResult)

    val cResult = StatisticalCalculator.calculate(
      operation = StatOperation.COMBINATIONS,
      inputX = "5",
      inputY = "3"
    ).getOrThrow()
    assertEquals("10", cResult.mainResult)
  }
}
