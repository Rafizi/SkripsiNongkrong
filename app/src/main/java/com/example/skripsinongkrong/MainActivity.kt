package com.example.skripsinongkrong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.skripsinongkrong.data.repository.AuthRepository
import com.example.skripsinongkrong.ui.screens.detail.DetailScreen
import com.example.skripsinongkrong.ui.screens.home.HomeScreen
import com.example.skripsinongkrong.ui.screens.login.LoginScreen
import com.example.skripsinongkrong.ui.screens.search.SearchScreen
import com.example.skripsinongkrong.ui.theme.SkripsiNongkrongTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

enum class AppScreen { LOGIN, MENU, LIST_REVIEW, LIST_CARI, DETAIL }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository // 1. Inject Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkripsiNongkrongTheme {
                // 2. Cek Status Login di Awal
                val startScreen = if (authRepository.isUserLoggedIn()) AppScreen.MENU else AppScreen.LOGIN

                var currentScreen by remember { mutableStateOf(startScreen) }
                var selectedPlaceId by remember { mutableStateOf<String?>(null) }

                // 3. State untuk membedakan Mode (Review Dulu vs Cari Tempat)
                var isReviewMode by remember { mutableStateOf(false) }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when (currentScreen) {
                        AppScreen.LOGIN -> {
                            LoginScreen(
                                onLoginSuccess = { AppScreen.MENU }
                            )
                        }

                        AppScreen.MENU -> {
                            HomeScreen(
                                onNavigateToReview = {
                                    AppScreen.LIST_REVIEW
                                },
                                onNavigateToSearch = {
                                    AppScreen.LIST_CARI
                                }
                            )
                        }

                        // Kita gunakan SearchScreen untuk kedua list (karena isinya sama: daftar tempat)
                        AppScreen.LIST_REVIEW, AppScreen.LIST_CARI -> {
                            SearchScreen(
                                // Anda bisa tambahkan parameter title di SearchScreen jika mau judulnya dinamis
                                onNavigateToDetail = { _ ->
                                    AppScreen.DETAIL
                                },
                                onBackClick = { AppScreen.MENU }
                            )
                        }

                        AppScreen.DETAIL -> {
                            if (selectedPlaceId != null) {
                                DetailScreen(
                                    placeId = selectedPlaceId!!,
                                    isReviewMode = isReviewMode, // 4. Kirim Mode ke Detail
                                    onBackClick = {
                                        if (isReviewMode) AppScreen.LIST_REVIEW else AppScreen.LIST_CARI
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}