package com.example.skripsinongkrong.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.skripsinongkrong.ui.theme.Terracotta
import com.example.skripsinongkrong.ui.viewmodel.AuthViewModel
import com.example.skripsinongkrong.ui.viewmodel.TempatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    // INJECT 2 VIEWMODEL DI SINI
    viewModel: AuthViewModel = hiltViewModel(),
    tempatViewModel: TempatViewModel = hiltViewModel()
) {
    // 1. Ambil Data User
    val userEmail by viewModel.userEmail.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userPhoto by viewModel.userPhotoUrl.collectAsState()

    // 2. Ambil Status Loading untuk Tombol Sync
    val isLoading by tempatViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Terracotta)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 1. FOTO PROFIL (Sudah dinamis)
            AsyncImage(
                model = userPhoto ?: "", // Ambil dari ViewModel
                contentDescription = "Foto Profil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. INFO USER (Sudah dinamis)
            Text(
                text = userName ?: "Pengguna",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = userEmail ?: "-",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f)) // Dorong tombol ke bawah

            // 3. TOMBOL LOGOUT
            Button(
                onClick = {
                    viewModel.logout()
                    onLogoutSuccess()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), // Merah
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Keluar Aplikasi", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. TOMBOL ADMIN SYNC (Posisi Benar di Sini)
            if (isLoading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Sedang mengambil data dari Google...", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                TextButton(
                    onClick = {
                        // Panggil fungsi di TempatViewModel yang baru kita buat
                        tempatViewModel.syncDataAdmin()
                    }
                ) {
                    Text("Admin Only: Sync Data", color = Color.Gray)
                }
            }
        }
    }
}