package iad1tya.echo.music.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import iad1tya.echo.music.utils.ErrorHandler
import iad1tya.echo.music.utils.MemoryOptimizer
import iad1tya.echo.music.utils.PerformanceMonitor

/**
 * Integration tests for critical utility classes
 */
@RunWith(AndroidJUnit4::class)
class UtilityIntegrationTest {

    @Test
    fun `error handling integration should work with real context`() {
        // Given
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val throwable = RuntimeException("Integration test error")

        // When
        ErrorHandler.handleError(throwable, "IntegrationTest")

        // Then
        // Should not crash the app
        // Error should be handled gracefully
    }

    @Test
    fun `memory optimizer integration should work with real context`() {
        // Given
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val memoryOptimizer = MemoryOptimizer.getInstance(context)

        // When
        val memoryInfo = memoryOptimizer.getMemoryInfo()

        // Then
        assertNotNull(memoryInfo)
        assertTrue(memoryInfo.totalMemoryMB > 0)
    }

    @Test
    fun `performance monitor integration should work with real context`() {
        // Given
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val performanceMonitor = PerformanceMonitor.getInstance(context)

        // When
        val metrics = performanceMonitor.getCurrentMetrics()

        // Then
        assertNotNull(metrics)
        assertTrue(metrics.totalMemoryMB > 0)
    }
}
