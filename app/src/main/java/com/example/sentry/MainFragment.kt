package com.example.sentry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.testTag
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.example.sentry.App.Companion.client
import com.example.sentry.ui.theme.SentryPerformanceTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import kotlin.random.Random

class MainFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner),
        )
        val navigation = this@MainFragment.findNavController()

        setContent {
            MainScreen(viewModel, navigation)
        }
    }
}

@Composable
private fun MainScreen(
    viewModel: MainViewModel,
    navigation: NavController,
) {
    val state by viewModel.state.collectAsState()

    SentryPerformanceTheme {
        Scaffold {
            Column(modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState()),
            ) {
                Text(text = "Main Fragment")
                Text(text = state)
                Button(
                    onClick = {
                        // TODO: This navigation breadcrumb has no label of the screen
                        navigation.navigate(MainFragmentDirections.actionMainFragmentToImportantFlow(1))
                    },
                ) {
                    Text(text = "Navigate")
                }
                Button(
                    modifier = Modifier.testTag("btn-logcat"),
                    onClick = {
                        Log.w("MainFragment", "Logcat message")
                    },
                ) {
                    Text(text = "Log Timber Warning")
                }
                Button(
                    modifier = Modifier.testTag("btn-timber"),
                    onClick = {
                        Timber.w("a Timber warn")
                    },
                ) {
                    Text(text = "Log Timber Warning")
                }
                Button(
                    onClick = {
                        Timber.e("a Timber err")
                    },
                ) {
                    Text(text = "Log Timber Error")
                }
                Button(
                    onClick = {
                        viewModel.doDbInsert()
                    },
                ) {
                    Text(text = "Do DB insert")
                }
                Button(
                    onClick = {
                        throw IllegalStateException("a test")
                    },
                ) {
                    Text(text = "Fatal crash")
                }
                Button(
                    onClick = {
                        viewModel.doSuccessfulHttpCall()
                    },
                ) {
                    Text(text = "Successful HTTP call")
                }
                Button(
                    onClick = {
                        //TODO: this crash is not reported to Sentry
                        viewModel.doFailingHttpCall()
                    },
                ) {
                    Text(text = "Failing HTTP call")
                }
            }
        }
    }
}

class MainViewModel : ViewModel() {
    private val _state = flow {
        delay(1000)
        val apiResponse = doHttpCall()

        val users = App.db.userDao().getAll()
        println("users = $users")

        emit(apiResponse)
    }

    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "loading...",
    )

    private suspend fun doHttpCall(): String {
        val request = Request.Builder()
            .url("https://loripsum.net/api/1/short/plaintext")
            .build()
        val response = withContext(Dispatchers.IO) {
            client.newCall(request).execute()
        }
        return response.body?.string()?.trim() ?: "error"
    }

    fun doSuccessfulHttpCall() {
        viewModelScope.launch {
            doHttpCall()
        }
    }

    fun doFailingHttpCall() {
        viewModelScope.launch {
            val request = Request.Builder()
                .url("https://nonexisting.local")
                .build()
            withContext(Dispatchers.IO) { client.newCall(request).execute() }
        }
    }

    fun doDbInsert() {
        viewModelScope.launch {
            App.db.userDao().insertAll(User(Random.nextInt(), null, null))
        }
    }
}
