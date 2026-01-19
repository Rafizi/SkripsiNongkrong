package com.example.skripsinongkrong.ui.screens.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skripsinongkrong.ui.components.PlaceListItem
import com.example.skripsinongkrong.ui.viewmodel.TempatViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: TempatViewModel = hiltViewModel() // Cukup TempatViewModel saja
) {
    val places by viewModel.tempatList.collectAsState()

    // Izin Lokasi
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
        viewModel.hitungJarakLokasiUser()
    }

    // STATE FILTER (Disimpan di layar ini)
    var filterColokan by remember { mutableStateOf(false) }
    var filterMushola by remember { mutableStateOf(false) }
    var filterWifi by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. HEADER FILTER CHIPS (Tanpa Syarat Login!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Chip Colokan
            FilterChip(
                selected = filterColokan,
                onClick = {
                    filterColokan = !filterColokan
                    viewModel.filterData(filterColokan, filterMushola, filterWifi)
                },
                label = { Text("Colokan") },
                leadingIcon = if (filterColokan) {
                    { Icon(Icons.Default.Check, contentDescription = null) }
                } else null
            )

            // Chip Mushola
            FilterChip(
                selected = filterMushola,
                onClick = {
                    filterMushola = !filterMushola
                    viewModel.filterData(filterColokan, filterMushola, filterWifi)
                },
                label = { Text("Mushola") },
                leadingIcon = if (filterMushola) {
                    { Icon(Icons.Default.Check, contentDescription = null) }
                } else null
            )

            // Chip WiFi
            FilterChip(
                selected = filterWifi,
                onClick = {
                    filterWifi = !filterWifi
                    viewModel.filterData(filterColokan, filterMushola, filterWifi)
                },
                label = { Text("WiFi") },
                leadingIcon = if (filterWifi) {
                    { Icon(Icons.Default.Check, contentDescription = null) }
                } else null
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. LIST TEMPAT
        if (places.isEmpty()) {
            // Tampilan jika hasil filter 0 (kosong)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Tidak ada tempat dengan fasilitas ini.", color = androidx.compose.ui.graphics.Color.Gray)
            }
        } else {
            LazyColumn {
                items(places) { placeData ->
                    PlaceListItem(
                        tempat = placeData,
                        onItemClick = { onNavigateToDetail(placeData.id) }
                    )
                }
            }
        }
    }
}