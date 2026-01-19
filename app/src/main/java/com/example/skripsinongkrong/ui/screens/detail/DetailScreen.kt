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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// --- FUNGSI HELPER URL FOTO ---
fun buildDetailPhotoUrl(photoReference: String): String {
    if (photoReference.isEmpty()) return ""
    val apiKey = BuildConfig.MAPS_API_KEY
    return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photo_reference=$photoReference&key=$apiKey"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    placeId: String,
    isReviewMode: Boolean,
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

    // State Input Review
    var rateRasa by remember { mutableIntStateOf(0) }
    var rateSuasana by remember { mutableIntStateOf(0) }
    var rateKebersihan by remember { mutableIntStateOf(0) }
    var ratePelayanan by remember { mutableIntStateOf(0) }
    var ulasanText by remember { mutableStateOf("") }
    var adaColokan by remember { mutableStateOf(false) }
    var adaMushola by remember { mutableStateOf(false) }


    // 2. LOGIKA DIALOG SUKSES
    if (submitStatus == true) {
        AlertDialog(
            onDismissRequest = {
                // Opsional: Kalau user klik luar, mau tutup atau diam?
                // Kita biarkan kosong agar user WAJIB klik OK
            },
            icon = {
                Icon(
                    Icons.Outlined.CheckCircle,
                    null,
                    tint = Color(0xFF4CAF50)
                )
            }, // Ikon Centang Hijau
            title = { Text("Review Terkirim!") },
            text = { Text("Terima kasih sudah berbagi pengalamanmu.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetSubmitStatus() // Reset status biar dialog hilang
                        onBackClick() // BARU KEMBALI KE MENU
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) {
                    Text("Mantap")
                }
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isReviewMode) "Tulis Review" else "Detail & Ulasan",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Terracotta)
            )
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
                    .background(Color(0xFFF9F9F9))
            ) {
                // --- HEADER GAMBAR ---
                Box(contentAlignment = Alignment.BottomStart) {
                    AsyncImage(
                        model = buildDetailPhotoUrl(tempat!!.photoReference),
                        contentDescription = "Foto Tempat",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    // Badge Rating
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(topEnd = 16.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(text = "${tempat!!.rating}", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        tempat!!.nama,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(tempat!!.alamat, color = Color.Gray)

                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(24.dp))

                    if (isReviewMode) {
                        // === MODE 1: FORM INPUT REVIEW ===
                        Text(
                            "Form Penilaian",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(16.dp))

                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                StarRatingInput("Rasa", rateRasa) { rateRasa = it }
                                StarRatingInput("Suasana", rateSuasana) { rateSuasana = it }
                                StarRatingInput("Kebersihan", rateKebersihan) {
                                    rateKebersihan = it
                                }
                                StarRatingInput("Pelayanan", ratePelayanan) { ratePelayanan = it }

                                Spacer(Modifier.height(12.dp))
                                Text("Fasilitas:", fontWeight = FontWeight.Bold)
                                FacilitySwitchItem(
                                    "Ada Colokan?",
                                    Icons.Outlined.ElectricalServices,
                                    adaColokan
                                ) { adaColokan = it }
                                FacilitySwitchItem(
                                    "Ada Mushola?",
                                    Icons.Outlined.Mosque,
                                    adaMushola
                                ) { adaMushola = it }

                                Spacer(Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = ulasanText,
                                    onValueChange = { ulasanText = it },
                                    label = { Text("Ceritakan pengalamanmu") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3
                                )
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = {
                                // PANGGIL VIEWMODEL SAJA (Jangan panggil onBackClick di sini)
                                viewModel.submitReview(
                                    placeId, rateRasa.toDouble(), rateSuasana.toDouble(),
                                    rateKebersihan.toDouble(), ratePelayanan.toDouble(),
                                    ulasanText, adaColokan, adaMushola
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = !isLoading, // Matikan tombol saat loading
                            colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text("Kirim Review", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // === MODE 2: LIHAT REVIEW (DETAIL PER POINT) ===
                        Text(
                            "Ulasan Pengunjung",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(16.dp))

                        if (reviews.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Belum ada review. Yuk jadi yang pertama!",
                                    color = Color.Gray
                                )
                            }
                        } else {
                            reviews.forEach { review ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        // Header User
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Person,
                                                null,
                                                modifier = Modifier.size(24.dp),
                                                tint = Color.Gray
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    review.userName,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Text(
                                                    review.getFormattedDate(),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(8.dp))

                                        // Teks Komentar
                                        if (review.text.isNotEmpty()) {
                                            Text(
                                                review.text,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Spacer(Modifier.height(12.dp))
                                            HorizontalDivider(
                                                thickness = 0.5.dp,
                                                color = Color.LightGray
                                            )
                                            Spacer(Modifier.height(12.dp))
                                        }

                                        // DETAIL RATING PER POINT (Grid 2 Kolom)
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            // Kolom Kiri
                                            Column(modifier = Modifier.weight(1f)) {
                                                SmallRatingDisplay("Rasa", review.ratingRasa)
                                                SmallRatingDisplay(
                                                    "Suasana",
                                                    review.ratingSuasana
                                                )
                                            }
                                            Spacer(Modifier.width(16.dp))
                                            // Kolom Kanan
                                            Column(modifier = Modifier.weight(1f)) {
                                                SmallRatingDisplay(
                                                    "Kebersihan",
                                                    review.ratingKebersihan
                                                )
                                                SmallRatingDisplay(
                                                    "Pelayanan",
                                                    review.ratingPelayanan
                                                )
                                            }
                                        }

                                        // TOGGLE / BADGE FASILITAS
                                        // Hanya tampilkan jika user bilang "Ada" (True)
                                        if (review.adaColokan || review.adaMushola) {
                                            Spacer(Modifier.height(12.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                if (review.adaColokan) {
                                                    ReviewBadge("Colokan")
                                                }
                                                if (review.adaMushola) {
                                                    ReviewBadge("Mushola")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// HELPER COMPONENTS (UI Components)
// ==========================================

// Tampilan kecil untuk rating di dalam kartu review (misal: Rasa ⭐ 4.0)
@SuppressLint("DefaultLocale")
@Composable
fun SmallRatingDisplay(label: String, score: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Star,
                null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(2.dp))
            Text(
                String.format("%.0f", score),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Badge hijau untuk fasilitas yang dikonfirmasi user
@Composable
fun ReviewBadge(text: String) {
    Surface(
        color = Color(0xFFE8F5E9), // Hijau muda lembut
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.CheckCircle,
                null,
                tint = Color(0xFF388E3C),
                modifier = Modifier.size(14.dp)
            ) // Ikon Centang
            Spacer(Modifier.width(4.dp))
            Text(
                text,
                fontSize = 11.sp,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Input Switch untuk Form
@Composable
fun FacilitySwitchItem(
    label: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = Terracotta.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Terracotta, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(text = label, fontWeight = FontWeight.Medium)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckChanged,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Terracotta,
                checkedTrackColor = Terracotta.copy(alpha = 0.3f)
            )
        )
    }
}

// Input Bintang untuk Form
@Composable
fun StarRatingInput(label: String, rating: Int, onRatingChanged: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            Row {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "$i Star",
                        tint = if (i <= rating) Color(0xFFFFB300) else Color.LightGray,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onRatingChanged(i) }
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}