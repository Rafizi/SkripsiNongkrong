package com.example.skripsinongkrong.ui.screens.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.skripsinongkrong.BuildConfig
import com.example.skripsinongkrong.ui.theme.Terracotta
import com.example.skripsinongkrong.ui.viewmodel.TempatViewModel

fun buildDetailPhotoUrl(photoReference: String): String {
    if (photoReference.isEmpty()) return ""
    val apiKey = BuildConfig.MAPS_API_KEY
    return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photo_reference=$photoReference&key=$apiKey"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    placeId: String,
    isLoggedIn: Boolean,
    viewModel: TempatViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    LaunchedEffect(placeId) {
        viewModel.loadDetail(placeId)
    }

    val reviews by viewModel.reviews.collectAsState()
    val tempat by viewModel.selectedTempat.collectAsState()
    val submitStatus by viewModel.submitStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // --- STATE BOTTOM SHEET ---
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // State Input Review
    var rateRasa by remember { mutableIntStateOf(0) }
    var rateSuasana by remember { mutableIntStateOf(0) }
    var rateKebersihan by remember { mutableIntStateOf(0) }
    var ratePelayanan by remember { mutableIntStateOf(0) }
    var ulasanText by remember { mutableStateOf("") }

    // Fasilitas (Input)
    var adaColokan by remember { mutableStateOf(false) }
    var adaMushola by remember { mutableStateOf(false) }
    var adaWifi by remember { mutableStateOf(false) }

    // DIALOG SUKSES
    if (submitStatus == true) {
        AlertDialog(
            onDismissRequest = {},
            icon = { Icon(Icons.Outlined.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp)) },
            title = { Text("Review Terkirim!", fontWeight = FontWeight.Bold) },
            text = { Text("Terima kasih, ulasanmu sangat membantu pengguna lain.", style = MaterialTheme.typography.bodyMedium) },
            containerColor = Color.White,
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetSubmitStatus()
                        showBottomSheet = false // Tutup sheet
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) { Text("OK") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detail & Ulasan",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Terracotta)
            )
        },
        floatingActionButton = {
            if (isLoggedIn) {
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                    containerColor = Terracotta,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Tulis Ulasan")
                }
            }
        }
    ) { paddingValues ->
        if (tempat == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Terracotta)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFFAFAFA))
            ) {
                // 1. Header Gambar
                Box(contentAlignment = Alignment.BottomStart) {
                    AsyncImage(
                        model = buildDetailPhotoUrl(tempat!!.photoReference),
                        contentDescription = "Foto Tempat",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                )
                            )
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            tempat!!.nama,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${tempat!!.rating} (${tempat!!.totalReview} ulasan)",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // 2. Konten Detail
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Alamat", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(tempat!!.alamat, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Ulasan Pengunjung", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        BadgeCount(reviews.size)
                    }
                    Spacer(Modifier.height(16.dp))

                    if (reviews.isEmpty()) {
                        EmptyStateReview()
                    } else {
                        reviews.forEach { review ->
                            ReviewItemCard(review)
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    // --- BOTTOM SHEET (TAMPILAN UI FORM SEPERTI GAMBAR PERTAMA) ---
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Popup
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tulis Review", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Terracotta)
                    IconButton(onClick = { showBottomSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.Gray)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Bagian 1: Rating Bintang
                // Tampilan: Label di kiri, Bintang di kanan (Persis gambar)
                StarRatingInput("Rasa", rateRasa) { rateRasa = it }
                StarRatingInput("Suasana", rateSuasana) { rateSuasana = it }
                StarRatingInput("Kebersihan", rateKebersihan) { rateKebersihan = it }
                StarRatingInput("Pelayanan", ratePelayanan) { ratePelayanan = it }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(Modifier.height(16.dp))

                // Bagian 2: Fasilitas dengan SWITCH (Persis Gambar)
                Text("Fasilitas Tersedia:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))

                FacilitySwitchItem("Ada Colokan?", Icons.Outlined.ElectricalServices, adaColokan) { adaColokan = it }
                FacilitySwitchItem("Ada WiFi?", Icons.Outlined.Wifi, adaWifi) { adaWifi = it }
                FacilitySwitchItem("Ada Mushola?", Icons.Outlined.Mosque, adaMushola) { adaMushola = it }

                Spacer(Modifier.height(24.dp))

                // Bagian 3: Text Field
                OutlinedTextField(
                    value = ulasanText,
                    onValueChange = { ulasanText = it },
                    placeholder = { Text("Ceritakan pengalamanmu") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Terracotta,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(Modifier.height(24.dp))

                // Bagian 4: Tombol Kirim (Warna Terracotta/Coklat)
                Button(
                    onClick = {
                        viewModel.submitReview(
                            placeId, rateRasa.toDouble(), rateSuasana.toDouble(),
                            rateKebersihan.toDouble(), ratePelayanan.toDouble(),
                            ulasanText, adaColokan, adaMushola, adaWifi
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta), // Sesuai request
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Kirim Review", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

// ==========================================
// KOMPONEN UI PENDUKUNG
// ==========================================

// 1. SWITCH ITEM (Sesuai Gambar Request)
@Composable
fun FacilitySwitchItem(label: String, icon: ImageVector, isChecked: Boolean, onCheckChanged: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Black)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckChanged,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Terracotta,
                checkedTrackColor = Terracotta.copy(alpha = 0.3f),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

// 2. RATING INPUT
@Composable
fun StarRatingInput(label: String, rating: Int, onRatingChanged: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray, fontSize = 16.sp)
        Row {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = if (i <= rating) Color(0xFFFFB300) else Color.LightGray,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onRatingChanged(i) }
                        .padding(2.dp)
                )
            }
        }
    }
}

// 3. CARD REVIEW (Tampilan List)
@Composable
fun ReviewItemCard(review: com.example.skripsinongkrong.data.model.Review) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Header: Avatar, Nama, Tanggal
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Terracotta.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.userName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Terracotta
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(review.userName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(review.getFormattedDate(), color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            // 2. Isi Review
            if (review.text.isNotEmpty()) {
                Text(
                    text = review.text,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = Color(0xFF333333)
                )
                Spacer(Modifier.height(16.dp))
            }

            // 3. Rating Grid (FIX RAPIH & ESTETIK)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp)) // Warna abu lebih soft
                    .padding(12.dp)
            ) {
                // Gunakan Row dengan Arrangement.spacedBy agar ada jarak antar kolom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp) // Jarak antar kolom kiri & kanan
                ) {
                    // KOLOM KIRI (Rasa & Suasana) - Ambil 50% Lebar
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MiniRating("Rasa", review.ratingRasa)
                        MiniRating("Suasana", review.ratingSuasana)
                    }

                    // KOLOM KANAN (Kebersihan & Pelayanan) - Ambil 50% Lebar
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MiniRating("Kebersihan", review.ratingKebersihan)
                        MiniRating("Pelayanan", review.ratingPelayanan)
                    }
                }
            }

            // 4. Fasilitas Badge
            if (review.adaColokan || review.adaMushola || review.adaWifi) {
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (review.adaWifi) FacilityBadge("WiFi", Icons.Outlined.Wifi)
                    if (review.adaColokan) FacilityBadge("Colokan", Icons.Outlined.ElectricalServices)
                    if (review.adaMushola) FacilityBadge("Mushola", Icons.Outlined.Mosque)
                }
            }
        }
    }
}

// PERBAIKAN MINI RATING (Hapus Fixed Width)
@SuppressLint("DefaultLocale")
@Composable
fun MiniRating(label: String, score: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(), // Isi penuh lebar kolom (weight 1f tadi)
        horizontalArrangement = Arrangement.SpaceBetween, // Teks Kiri, Bintang Kanan (Auto Rapi)
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label tanpa batasan width, biar "Kebersihan" muat
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )

        // Group Bintang & Nilai (Rata Kanan)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = String.format("%.0f", score),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun FacilityBadge(text: String, icon: ImageVector) {
    Surface(
        color = Color(0xFFE0F2F1),
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF80CBC4))
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color(0xFF00695C), modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text(text, fontSize = 11.sp, color = Color(0xFF00695C), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun BadgeCount(count: Int) {
    Surface(color = Terracotta.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
        Text("$count Ulasan", fontSize = 12.sp, color = Terracotta, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
fun EmptyStateReview() {
    Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Outlined.CheckCircle, null, tint = Color.LightGray, modifier = Modifier.size(56.dp))
        Spacer(Modifier.height(12.dp))
        Text("Belum ada ulasan", color = Color.Gray, fontWeight = FontWeight.Medium)
    }
}