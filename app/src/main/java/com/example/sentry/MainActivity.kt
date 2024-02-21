package com.example.sentry

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import io.sentry.android.navigation.SentryNavigationListener

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val sentryNavListener = SentryNavigationListener(
        enableNavigationBreadcrumbs = true,
        enableNavigationTracing = true,
    )

    private val navController get() =
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!.findNavController()

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(sentryNavListener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(sentryNavListener)
    }
}
