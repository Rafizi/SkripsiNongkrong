package com.example.skripsinongkrong.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skripsinongkrong.ui.components.PlaceListItem
import com.example.skripsinongkrong.ui.theme.Terracotta
import com.example.skripsinongkrong.ui.viewmodel.TempatViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    // Gunakan TempatViewModel karena logika filter ada di sana
    viewModel: TempatViewModel = hiltViewModel()
) {
    val places by viewModel.tempatList.collectAsState()
    val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    // State untuk Filter yang sedang aktif
    var selectedCategory by remember { mutableStateOf("Semua") }

    // Efek saat pertama kali dibuka: Cek Lokasi
    LaunchedEffect(Unit) {
        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
        } else {
            viewModel.hitungJarakLokasiUser()
        }
    }

    // Daftar Kategori Filter
    val categories = listOf("Semua", "Terdekat", "Colokan", "Mushola", "Wifi", "Rasa", "Suasana")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        // 1. HEADER (Lokasi & Search)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Terracotta)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Lokasi Kamu", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text("Jakarta, Indonesia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar Dummy
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cari tempat nongkrong...", color = Color.Gray)
                }
            }
        }

        // 2. FILTER CHIPS (Scroll Samping)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        // Logika Toggle: Jika diklik lagi, kembali ke "Semua"
                        if (isSelected && category != "Semua") {
                            selectedCategory = "Semua"
                            viewModel.filterByKriteria("Semua")
                        } else {
                            selectedCategory = category
                            viewModel.filterByKriteria(category)
                        }
                    },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Terracotta,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = Color.Black
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) Terracotta else Color.LightGray,
                        enabled = true, selected = isSelected
                    )
                )
            }
        }

        // 3. LIST TEMPAT
        if (places.isEmpty()) {
            // Tampilan jika Kosong (Empty State)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Belum ada tempat yang cocok.", color = Color.Gray)
                    if (selectedCategory != "Semua") {
                        TextButton(onClick = {
                            selectedCategory = "Semua"
                            viewModel.filterByKriteria("Semua")
                        }) {
                            Text("Reset Filter", color = Terracotta)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp) // Padding bawah biar gak ketutup navbar
            ) {
                items(places) { place ->
                    PlaceListItem(
                        tempat = place,
                        onItemClick = { onNavigateToDetail(place.id) }
                    )
                }
            }
        }
    }
}