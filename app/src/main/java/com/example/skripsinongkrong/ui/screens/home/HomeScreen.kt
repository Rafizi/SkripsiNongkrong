package com.example.skripsinongkrong.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skripsinongkrong.ui.theme.CharcoalText
import com.example.skripsinongkrong.ui.theme.Terracotta
import com.example.skripsinongkrong.ui.theme.WhiteCard
import com.example.skripsinongkrong.ui.viewmodel.AuthViewModel
import com.example.skripsinongkrong.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSearch: () -> Unit, // Tambahan: Kabel ke Halaman Cari
    onLogoutSuccess: () -> Unit,
    onNavigateToReview: () -> Unit,  // Tambahan: Kabel ke Halaman Review
    authViewModel: AuthViewModel = hiltViewModel()

) {
    val userEmail by authViewModel.userEmail.collectAsState()
    val adminEmail = "naufal.rafizi@gmail.com" // Sesuaikan email kamu

    // State untuk menampilkan Dialog Konfirmasi Logout
    var showLogoutDialog by remember { mutableStateOf(false) }

    // --- LOGIC: TAMPILKAN DIALOG ---
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar dari aplikasi?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout() // 1. Hapus Sesi Firebase
                        onLogoutSuccess()      // 2. Pindah Layar ke Login
                    }
                ) {
                    Text("Ya, Keluar", color = Terracotta, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Halo, Nongkrongers!", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Mau kemana hari ini?", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                    }
                },
                // --- TOMBOL LOGOUT DI KANAN ATAS ---
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Terracotta)
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp), // Margin kiri-kanan
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Konten di tengah vertikal
        ) {

            // --- KARTU 1: REVIEWNYA DULU (Input Crowdsourcing) ---
            HomeMenuCard(
                title = "Reviewnya Dulu",
                icon = Icons.Default.Edit, // Ganti dengan ikon Pen jika punya aset SVG
                onClick = { onNavigateToReview() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- KARTU 2: YUK CARI (Output SPK) ---
            HomeMenuCard(
                title = "Cari Tempat",
                icon = Icons.Default.Map, // Ganti dengan ikon Peta/Search
                onClick = { onNavigateToSearch() }
            )

            Spacer(modifier = Modifier.height(64.dp))

            // --- TOMBOL ADMIN (Tetap ada untuk fungsi Cache) ---
            // Saya buat agak transparan/kecil biar tidak merusak desain utama
            if (userEmail == adminEmail) {
                Button(
                    onClick = { viewModel.jalankanPengisianDatabase() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("ADMIN ONLY: SYNC DATA")
                }
            }

            // Tambahan: Jarak aman di bawah biar enak scrollnya
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// --- KOMPONEN KARTU MENU (Reusable) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMenuCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth().height(120.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Normal,
                color = CharcoalText,
                lineHeight = 28.sp
            )
            Icon(icon, null, tint = Terracotta, modifier = Modifier.size(48.dp))
        }
    }
}