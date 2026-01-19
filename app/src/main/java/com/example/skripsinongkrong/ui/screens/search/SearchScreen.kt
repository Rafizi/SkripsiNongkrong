package com.example.skripsinongkrong.ui.screens.search

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // <--- PENTING: Solusi Error ke-2
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skripsinongkrong.ui.components.PlaceListItem
import com.example.skripsinongkrong.ui.viewmodel.TempatViewModel // <--- PENTING: Solusi Error ke-1
import com.example.skripsinongkrong.ui.theme.Terracotta
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    // PERBAIKAN: Gunakan TempatViewModel, BUKAN HomeViewModel
    viewModel: TempatViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.all { it }
        if (isGranted) {
            // Kalau diizinkan, langsung hitung jarak!
            viewModel.hitungJarakLokasiUser()
        }
    }
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    // 3. TAMBAH "Terdekat" KE LIST FILTER
    val filters = listOf("Semua", "Terdekat", "Rasa", "Suasana", "Kebersihan", "Colokan", "Mushola")
    // Error 'tempatList' akan hilang karena TempatViewModel punya variabel ini
    val listData by viewModel.tempatList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Tempat", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Terracotta)
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            // Error 'Type Mismatch Int' akan hilang karena import .items di atas
            items(listData) { tempat ->
                PlaceListItem(
                    tempat = tempat,
                    onItemClick = { placeId -> onNavigateToDetail(placeId) }
                )
            }
        }
    }
}