package com.example.skripsinongkrong.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    viewModel: AuthViewModel,
    tempatViewModel: TempatViewModel = hiltViewModel()
) {
    // 1. Ambil Data User
    val name by viewModel.userName.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val photoUrl by viewModel.userPhotoUrl.collectAsState()

    // 2. Ambil Status Loading untuk Tombol Sync
    val isLoading by tempatViewModel.isLoading.collectAsState()

    val context = LocalContext.current

    // 1. CCTV: Ambil status login dari ViewModel
    // Karena kita sudah logout di backend, viewModel.isUserLoggedIn harusnya jadi FALSE
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

    // 2. REAKSI: Jika status berubah jadi FALSE, pindah layar!
    LaunchedEffect(isUserLoggedIn) {
        // Logika: Jika user TIDAK login lagi, berarti logout sukses
        if (!isUserLoggedIn) {
            Toast.makeText(context, "Berhasil Logout", Toast.LENGTH_SHORT).show()
            onLogoutSuccess() // <--- TENDANG KE LAYAR LOGIN
        }
    }

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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp) // Ukuran Lingkaran
                    .clip(CircleShape) // Potong jadi bulat
                    .background(Color.LightGray) // Warna dasar kalau foto belum muncul
            ) {
                if (photoUrl != null) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Foto Profil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Tampilkan Icon Orang jika tidak ada foto (atau user belum login)
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. INFO USER (Sudah dinamis)
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // 4. TAMPILKAN EMAIL
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f)) // Dorong tombol ke bawah

            // TOMBOL LOGOUT
            Button(
                onClick = {
                    viewModel.logout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                enabled = !isLoading // Matikan tombol saat proses logout
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Keluar (Logout)")
                }
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