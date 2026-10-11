package com.arttvad9r.subtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arttvad9r.subtrack.ui.SubtrackApp
import com.arttvad9r.subtrack.ui.SubtrackViewModel
import com.arttvad9r.subtrack.ui.SubtrackViewModelFactory
import com.arttvad9r.subtrack.ui.theme.SubtrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as SubtrackApplication
        setContent {
            val factory = remember { SubtrackViewModelFactory(app.container.repository) }
            val viewModel: SubtrackViewModel = viewModel(factory = factory)
            SubtrackTheme {
                SubtrackApp(viewModel)
            }
        }
    }
}
