package com.example.skripsinongkrong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.skripsinongkrong.ui.screens.detail.DetailScreen
import com.example.skripsinongkrong.ui.screens.home.HomeScreen
import com.example.skripsinongkrong.ui.screens.search.SearchScreen
import com.example.skripsinongkrong.ui.theme.SkripsiNongkrongTheme
import dagger.hilt.android.AndroidEntryPoint

// 1. Definisikan Enum di luar class agar bisa diakses global
enum class AppScreen { MENU, CARI, DETAIL }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkripsiNongkrongTheme {
                // State untuk mengatur halaman mana yang tampil
                var currentScreen by remember { mutableStateOf(AppScreen.MENU) }
                var selectedPlaceId by remember { mutableStateOf<String?>(null) }

                Surface(modifier = Modifier.fillMaxSize()) {

                    when (currentScreen) {
                        // 1. TAMPILAN MENU UTAMA
                        AppScreen.MENU -> {
                            HomeScreen(
                                onNavigateToSearch = { currentScreen = AppScreen.CARI },
                                onNavigateToReview = { /* Nanti buat ReviewScreen */ }
                            )
                        }

                        // 2. TAMPILAN LIST PENCARIAN
                        AppScreen.CARI -> {
                            SearchScreen(
                                onNavigateToDetail = { placeId ->
                                    selectedPlaceId = placeId
                                    currentScreen = AppScreen.DETAIL
                                },
                                onBackClick = { currentScreen = AppScreen.MENU }
                            )
                        }

                        // 3. TAMPILAN DETAIL (Contoh, nanti diganti DetailScreen asli)
                        AppScreen.DETAIL -> {
                            // DetailScreen(...)
                            // Untuk sementara tombol back saja
                            // Cek agar tidak error jika ID null
                            if (selectedPlaceId != null) {
                                DetailScreen(
                                    placeId = selectedPlaceId!!,
                                    onBackClick = { currentScreen = AppScreen.CARI }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
