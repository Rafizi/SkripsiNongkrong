package com.example.skripsinongkrong.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    viewModel: TempatViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    // 1. Ambil data saat layar dibuka
    LaunchedEffect(placeId) {
        viewModel.loadDetail(placeId)
    }

    val tempat by viewModel.selectedTempat.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Tempat", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Terracotta)
            )
        }
    ) { paddingValues ->
        // Loading State
        if (tempat == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Terracotta)
            }
        } else {
            // Content State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF9F9F9)) // Background agak abu terang biar elegan
            ) {
                // --- 1. HEADER GAMBAR ---
                Box(contentAlignment = Alignment.BottomStart) {
                    AsyncImage(
                        model = buildDetailPhotoUrl(tempat!!.photoReference),
                        contentDescription = "Foto Tempat",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Badge Rating di atas gambar
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(topEnd = 16.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(text = "${tempat!!.rating}", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    // --- 2. JUDUL & ALAMAT ---
                    Text(
                        text = tempat!!.nama,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Place, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = tempat!!.alamat,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Status Buka/Tutup (Chip Style)
                    val isOpen = tempat!!.isOpenNow ?: false
                    Surface(
                        color = if (isOpen) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isOpen) "• Buka Sekarang" else "• Tutup",
                            color = if (isOpen) Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(Modifier.height(24.dp))

                    // --- 3. FITUR UTAMA SKRIPSI: CROWDSOURCING ---
                    Text(
                        text = "Berikan Penilaian Kamu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Penilaianmu akan membantu perhitungan ranking TOPSIS.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(16.dp))

                    // KARTU INPUT PENILAIAN
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            // A. STATE LOKAL UNTUK INPUT USER
                            // (Nanti ini dikirim ke ViewModel)
                            var rateRasa by remember { mutableIntStateOf(0) }
                            var rateSuasana by remember { mutableIntStateOf(0) }
                            var rateKebersihan by remember { mutableIntStateOf(0) }
                            var ratePelayanan by remember { mutableIntStateOf(0) }
                            var rateHarga by remember { mutableIntStateOf(0) }

                            // B. INPUT BINTANG (KRITERIA TOPSIS)
                            StarRatingInput("Kualitas Rasa", rateRasa) { rateRasa = it }
                            StarRatingInput("Suasana", rateSuasana) { rateSuasana = it }
                            StarRatingInput("Kebersihan", rateKebersihan) { rateKebersihan = it }
                            StarRatingInput("Pelayanan", ratePelayanan) { ratePelayanan = it }
                            StarRatingInput("Harga", rateHarga) { rateHarga = it }

                            Divider(modifier = Modifier.padding(vertical = 16.dp))

                            // C. INPUT FASILITAS (YANG TADI)
                            Text("Fasilitas Tambahan:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(Modifier.height(8.dp))

                            FacilitySwitchItem("Banyak Colokan", Icons.Outlined.ElectricalServices)
                            FacilitySwitchItem("Tersedia Mushola", Icons.Outlined.Mosque)
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // TOMBOL KIRIM
                    Button(
                        onClick = {
                            // TODO: Di sinilah logika "Kirim Review" akan dipasang
                            // Kita akan kirim: rateRasa, rateSuasana, dll ke Firestore
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                    ) {
                        Text("Kirim Review", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- KOMPONEN KECIL BIAR RAPI ---
@Composable
fun FacilitySwitchItem(label: String, icon: ImageVector) {
    var checked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
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
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Terracotta, checkedTrackColor = Terracotta.copy(alpha = 0.3f))
        )

    }


}
@Composable
fun StarRatingInput(
    label: String,
    rating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)

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
