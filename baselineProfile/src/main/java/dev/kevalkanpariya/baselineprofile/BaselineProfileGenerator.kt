package com.sagar.baselineprofile

import android.Manifest
import android.os.Build
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a basic startup baseline profile for the target package.
 *
 * We recommend you start with this but add important user flows to the profile to improve their performance.
 * Refer to the [baseline profile documentation](https://d.android.com/topic/performance/baselineprofiles)
 * for more information.
 *
 * You can run the generator with the "Generate Baseline Profile" run configuration in Android Studio or
 * the equivalent `generateBaselineProfile` gradle task:
 * ```
 * ./gradlew :app:generateReleaseBaselineProfile
 * ```
 * The run configuration runs the Gradle task and applies filtering to run only the generators.
 *
 * Check [documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)
 * for more information about available instrumentation arguments.
 *
 * After you run the generator, you can verify the improvements running the [StartupBenchmarks] benchmark.
 *
 * When using this class to generate a baseline profile, only API 33+ or rooted API 28+ are supported.
 *
 * The minimum required version of androidx.benchmark to generate a baseline profile is 1.2.0.
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator { //:app:generateReleaseBaselineProfile -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        // The application id for the running build variant is read from the instrumentation arguments.
        rule.collect(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),

            // See: https://d.android.com/topic/performance/baselineprofiles/dex-layout-optimizations
            includeInStartupProfile = true
        ) {
            // This block defines the app's critical user journey. Here we are interested in
            // optimizing for app startup. But you can also navigate and scroll through your most important UI.

            // Start default activity for your app
            pressHome()
            startActivityAndWait()

            waitForAsyncCall()
            scrollDown()
//            goToDetailsScreen()
        }
    }
}

fun MacrobenchmarkScope.waitForAsyncCall(){
    device.wait(Until.hasObject(By.res("items_list")), 20_000)

    val list = device.findObject(By.res("items_list"))
    list.wait(Until.hasObject(By.res("items_list")), 15_000)
}


fun MacrobenchmarkScope.scrollDown() {
    val list = device.findObject(By.res("items_list"))
    // Set gesture margin to avoid triggering gesture navigation.
    list.setGestureMargin(device.displayWidth / 5)
    list.fling(Direction.DOWN)
    device.waitForIdle()
}

fun MacrobenchmarkScope.goToDetailsScreen() {
    val list = device.findObject(By.res("items_list"))
    val items = list.findObjects(By.res("item"))
    // Select snack from the list based on running iteration.
    val index = (iteration ?: 0) % items.size
    items[index].click()
    // Wait until the screen is gone = the detail is shown.
    device.wait(Until.gone(By.res("items_list")), 5_000)
}

fun MacrobenchmarkScope.allowPermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val command = "pm grant $packageName ${Manifest.permission.POST_NOTIFICATIONS}"
        val output = device.executeShellCommand(command)
        Assert.assertEquals("", output)
    }
    val command = "pm grant $packageName ${Manifest.permission.ACCESS_FINE_LOCATION}"
    val output = device.executeShellCommand(command)
    Assert.assertEquals("", output)
}