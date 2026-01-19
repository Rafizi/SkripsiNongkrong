package com.example.skripsinongkrong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
                // 1. Cek Status Login di Awal
                val startScreen = if (authRepository.isUserLoggedIn()) AppScreen.MENU else AppScreen.LOGIN

                var currentScreen by remember { mutableStateOf(startScreen) }
                var selectedPlaceId by remember { mutableStateOf<String?>(null) }

                // 2. State untuk membedakan Mode (Review Dulu vs Cari Tempat)
                var isReviewMode by remember { mutableStateOf(false) }
                var listTitle by remember { mutableStateOf("Daftar Tempat") }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    when (currentScreen) {
                        AppScreen.LOGIN -> {
                            // Di Login Screen, Back Button biasanya keluar aplikasi (default behavior)
                            // Jadi kita tidak perlu BackHandler di sini.
                            LoginScreen(
                                onLoginSuccess = { currentScreen = AppScreen.MENU }
                            )
                        }

                        AppScreen.MENU -> {
                            // Di Home/Menu, Back Button juga keluar aplikasi (default behavior)
                            // Kecuali Anda mau double-tap to exit, tapi defaultnya keluar sudah benar.
                            HomeScreen(
                                onNavigateToReview = {
                                    isReviewMode = true
                                    listTitle = "Pilih Tempat Review"
                                    currentScreen = AppScreen.LIST_REVIEW
                                },
                                onNavigateToSearch = {
                                    isReviewMode = false
                                    listTitle = "Rekomendasi Tempat"
                                    currentScreen = AppScreen.LIST_CARI
                                },
                                onLogoutSuccess = {
                                    currentScreen = AppScreen.LOGIN // Kembali ke Login Screen
                                }
                            )
                        }

                        // SEARCH SCREEN (LIST)
                        AppScreen.LIST_REVIEW, AppScreen.LIST_CARI -> {
                            // --- PENAMBAHAN PENTING: BACK HANDLER ---
                            // Jika tombol Back ditekan saat di list, kembali ke MENU
                            BackHandler {
                                currentScreen = AppScreen.MENU
                            }

                            SearchScreen(
                                // Kirim judul dinamis ke SearchScreen (opsional, jika SearchScreen mendukungnya)
                                // title = listTitle,
                                onNavigateToDetail = { placeId ->
                                    selectedPlaceId = placeId
                                    currentScreen = AppScreen.DETAIL
                                },
                                onBackClick = { currentScreen = AppScreen.MENU }
                            )
                        }

                        // DETAIL SCREEN
                        AppScreen.DETAIL -> {
                            // --- PENAMBAHAN PENTING: BACK HANDLER ---
                            // Jika tombol Back ditekan saat di detail, kembali ke LIST sebelumnya
                            BackHandler {
                                currentScreen = if (isReviewMode) AppScreen.LIST_REVIEW else AppScreen.LIST_CARI
                            }

                            if (selectedPlaceId != null) {
                                DetailScreen(
                                    placeId = selectedPlaceId!!,
                                    isReviewMode = isReviewMode,
                                    onBackClick = {
                                        // Aksi ini sama dengan BackHandler di atas
                                        currentScreen = if (isReviewMode) AppScreen.LIST_REVIEW else AppScreen.LIST_CARI
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