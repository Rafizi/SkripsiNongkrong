package com.example.skripsinongkrong.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    // Hilt akan otomatis menyuntikkan ViewModel ini saat layar dibuat
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Scaffold menyediakan struktur dasar layout Material Design
    Scaffold { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Menu Utama",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL FITUR UTAMA (Nanti diisi Card UI Anda) ---

            Button(onClick = { /* Navigasi ke ReviewListScreen */ }) {
                Text("Reviewnya Dulu")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Navigasi ke SearchListScreen */ }) {
                Text("Yuk Cari Tempat Nongkrong")
            }

            Spacer(modifier = Modifier.height(64.dp))

            // --- TOMBOL RAHASIA ADMIN (UNTUK "GET & CACHE") ---
            // Tombol ini yang akan kita pakai untuk tes sekarang
            Button(
                onClick = {
                    // PANGGIL FUNGSI DI VIEWMODEL
                    viewModel.jalankanPengisianDatabase()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Merah biar kelihatan beda
            ) {
                Text("ADMIN: ISI DATABASE (GET & CACHE)")
            }
        }
    }
}