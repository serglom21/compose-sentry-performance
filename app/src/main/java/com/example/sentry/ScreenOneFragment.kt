package com.example.sentry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sentry.ui.theme.SentryPerformanceTheme

class ScreenOneFragment : Fragment() {

    private val args: ScreenOneFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner),
        )

        val navigation = this@ScreenOneFragment.findNavController()

        setContent {
            SentryPerformanceTheme {
                Scaffold {
                    Column(modifier = Modifier.padding(it)) {
                        Text(text = "Screen one with arg: ${args.argItemId}")
                        Button(
                            onClick = { navigation.navigate(ScreenOneFragmentDirections.actionScreenOneFragmentToScreenTwoFragment()) },
                        ) {
                            Text(text = "Navigate next")
                        }
                    }
                }
            }
        }
    }
}
