package com.example.skripsinongkrong.ui.screens.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skripsinongkrong.ui.components.PlaceListItem
import com.example.skripsinongkrong.ui.viewmodel.AuthViewModel
import com.example.skripsinongkrong.ui.viewmodel.HomeViewModel
import androidx.compose.foundation.lazy.items // <--- INI KUNCINYA AGAR LIST BISA DIBACA
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.runtime.collectAsState // Untuk collectAsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()

) {
    val places by viewModel.places.collectAsState()
    val isLoggedIn by authViewModel.isUserLoggedIn.collectAsState(initial = false)
    val context = LocalContext.current

    // 1. IZIN LOKASI (Langsung minta saat Home dibuka)
    val locationPermissionState =
        rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)


    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
        viewModel.getPlacesByLocation() // Panggil fungsi load data LBS
        authViewModel.checkLoginStatus()
    }
    // State untuk Filter
    var filterColokan by remember { mutableStateOf(false) }
    var filterMushola by remember { mutableStateOf(false) }
    var filterWifi by remember { mutableStateOf(false) }

    // State untuk menampilkan Dialog Konfirmasi Logout

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 2. HEADER (Ganti Search Bar dengan Filter Chips)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChipItem("Colokan", filterColokan, isLoggedIn) {
                filterColokan = !filterColokan
                viewModel.filterData(filterColokan, filterMushola, filterWifi) // Update List
            }
            FilterChipItem("Mushola", filterMushola, isLoggedIn) {
                filterMushola = !filterMushola
                viewModel.filterData(filterColokan, filterMushola, filterWifi)
            }
            FilterChipItem("WiFi", filterWifi, isLoggedIn) {
                filterWifi = !filterWifi
                viewModel.filterData(filterColokan, filterMushola, filterWifi)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. LIST TEMPAT (Langsung List)
        LazyColumn {
            items(places) { placeData -> // Ganti nama variabel lambda biar gak bingung
                PlaceListItem(
                    tempat = placeData, // Sesuaikan dengan parameter asli PlaceListItem kamu
                    onItemClick = { onNavigateToDetail(placeData.id) } // Sesuaikan param asli
                )
            }
        }
    }
}

@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    isLoggedIn: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    FilterChip(
        selected = isSelected,
        onClick = {
            if (isLoggedIn) {
                onClick()
            } else {
                Toast.makeText(context, "Login dulu untuk filter!", Toast.LENGTH_SHORT).show()
            }
        },
        label = { Text(text) },
        leadingIcon = if (isSelected) {
            { Icon(Icons.Default.Check, contentDescription = null) }
        } else null
    )

}