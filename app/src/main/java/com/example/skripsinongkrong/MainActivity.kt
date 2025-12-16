package com.example.skripsinongkrong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.skripsinongkrong.ui.screens.home.HomeScreen
import com.example.skripsinongkrong.ui.theme.SkripsiNongkrongTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkripsiNongkrongTheme {
                // Panggil HomeScreen yang sudah Anda buat, bukan Greeting
                HomeScreen()
            }
        }
    }
}
