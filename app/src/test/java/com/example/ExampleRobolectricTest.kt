package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.detector.SecurityAuditEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BootCheck", appName)
  }

  @Test
  fun `run security audit produces valid deep report`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val report = SecurityAuditEngine.runAudit(context)
    assertNotNull(report)
    assertTrue("Score should be within 0..100", report.overallScore in 0..100)
    assertNotNull(report.bootloader)
    assertNotNull(report.bootloader.kernelVersion)
    assertNotNull(report.bootloader.kernelCmdline)
    assertNotNull(report.root)
    assertNotNull(report.appCheck)
    assertNotNull(report.appCheck.hardwareKeystore)
    assertNotNull(report.appCheck.deepVerdict)
    assertTrue("Token claims should exist", report.appCheck.deepVerdict.tokenClaims.isNotEmpty())
    assertTrue("Issues list should not be empty", report.issues.isNotEmpty())
  }
}
