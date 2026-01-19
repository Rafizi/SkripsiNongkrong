package com.example.skripsinongkrong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skripsinongkrong.ui.screens.detail.DetailScreen
import com.example.skripsinongkrong.ui.screens.home.HomeScreen
import com.example.skripsinongkrong.ui.screens.login.LoginScreen
import com.example.skripsinongkrong.ui.screens.profile.ProfileScreen
import com.example.skripsinongkrong.ui.theme.SkripsiNongkrongTheme
import com.example.skripsinongkrong.ui.viewmodel.AuthViewModel
import com.example.skripsinongkrong.ui.viewmodel.TempatViewModel
import dagger.hilt.android.AndroidEntryPoint

enum class AppScreen { LOGIN, MENU, DETAIL, PROFILE }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkripsiNongkrongTheme {
                MainApp()
            }
        }
    }
}
@Composable
fun MainApp(
    authViewModel: AuthViewModel = hiltViewModel(),
    tempatViewModel: TempatViewModel = hiltViewModel()
) {
    // State Navigasi Manual
    var currentScreen by remember { mutableStateOf(AppScreen.MENU) }

    // State Login dari ViewModel
    val isLoggedIn by authViewModel.isUserLoggedIn.collectAsState(initial = false)

    // STATE PENTING: Simpan ID tempat yang dipilih
    var selectedPlaceId by remember { mutableStateOf("") }

    // --- [FIX] CCTV LOGOUT OTOMATIS ---
    // Logika: Setiap kali 'isLoggedIn' berubah, cek kondisinya.
    // Jika user TIDAK login (false) DAN sedang di layar Profile,
    // MAKA paksa pindah layar ke LOGIN.
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn && currentScreen == AppScreen.PROFILE) {
            currentScreen = AppScreen.LOGIN
        }
    }
    // ----------------------------------

    Scaffold(
        bottomBar = {
            if (currentScreen != AppScreen.DETAIL) {
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentScreen == AppScreen.MENU,
                        onClick = { currentScreen = AppScreen.MENU }
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (isLoggedIn) Icons.Default.Person else Icons.Default.Lock,
                                contentDescription = "Akun"
                            )
                        },
                        label = { Text(if (isLoggedIn) "Profil" else "Login") },
                        selected = currentScreen == AppScreen.LOGIN || currentScreen == AppScreen.PROFILE,
                        onClick = {
                            // Logika Bottom Bar
                            currentScreen = if (isLoggedIn) AppScreen.PROFILE else AppScreen.LOGIN
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                AppScreen.LOGIN -> LoginScreen(
                    onLoginSuccess = {
                        // Jika login sukses, pindah ke Menu/Home
                        currentScreen = AppScreen.MENU
                    }
                )

                AppScreen.MENU -> HomeScreen(
                    onNavigateToDetail = { placeId ->
                        selectedPlaceId = placeId
                        tempatViewModel.loadDetail(placeId)
                        currentScreen = AppScreen.DETAIL
                    }
                )

                AppScreen.PROFILE -> ProfileScreen(
                    onBackClick = { currentScreen = AppScreen.MENU },
                    onLogoutSuccess = { currentScreen = AppScreen.LOGIN },

                    // --- [SOLUSI] OPER VIEWMODEL UTAMA KE SINI ---
                    // Supaya tombol logout di ProfileScreen mengubah state
                    // yang sedang dipantau oleh MainApp!
                    viewModel = authViewModel
                )

                AppScreen.DETAIL -> {
                    BackHandler { currentScreen = AppScreen.MENU }
                    DetailScreen(
                        placeId = selectedPlaceId,
                        isLoggedIn = isLoggedIn,
                        onBackClick = { currentScreen = AppScreen.MENU }
                    )
                }
            }
        }
    }
}