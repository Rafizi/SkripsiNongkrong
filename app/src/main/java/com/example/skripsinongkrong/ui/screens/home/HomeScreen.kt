package com.example.skripsinongkrong.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skripsinongkrong.ui.theme.* // Import warna dari langkah 1
import com.example.skripsinongkrong.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSearch: () -> Unit, // Tambahan: Kabel ke Halaman Cari
    onNavigateToReview: () -> Unit  // Tambahan: Kabel ke Halaman Review

) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NongkrongDimana?",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                },
                actions = {
                    // Ikon User di pojok kanan atas (sesuai desain)
                    IconButton(onClick = { /* TODO: Profil? */ }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Terracotta // Warna Header
                )
            )
        },
        containerColor = CreamBackground // Warna Latar Belakang
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
                title = "Yuk Cari\nTempat\nNongkrong",
                icon = Icons.Default.Map, // Ganti dengan ikon Peta/Search
                onClick = { onNavigateToSearch() }
            )

            Spacer(modifier = Modifier.height(64.dp))

            // --- TOMBOL ADMIN (Tetap ada untuk fungsi Cache) ---
            // Saya buat agak transparan/kecil biar tidak merusak desain utama
            Button(
                onClick = { viewModel.jalankanPengisianDatabase() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.alpha(0.5f) // Setengah transparan
            ) {
                Text("ADMIN: SYNC DATA", fontSize = 10.sp)
            }
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