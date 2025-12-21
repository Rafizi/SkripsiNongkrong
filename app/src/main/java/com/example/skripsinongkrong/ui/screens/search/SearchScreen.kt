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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    // PERBAIKAN: Gunakan TempatViewModel, BUKAN HomeViewModel
    viewModel: TempatViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onBackClick: () -> Unit
) {
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