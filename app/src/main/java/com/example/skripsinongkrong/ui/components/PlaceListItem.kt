package com.example.skripsinongkrong.ui.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.skripsinongkrong.BuildConfig
import com.example.skripsinongkrong.data.model.TempatNongkrong

fun buildPhotoUrl(photoReference: String, maxWidth: Int = 400): String {
    // Jika kosong atau link web biasa, kembalikan aslinya
    if (photoReference.isEmpty() || photoReference.startsWith("http")) return photoReference

    // PANGGIL DARI GLOBAL CONFIG (Aman & Otomatis)
    val apiKey = BuildConfig.MAPS_API_KEY

    return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxWidth&photo_reference=$photoReference&key=$apiKey"
}

@Composable
fun PlaceListItem(
    tempat: TempatNongkrong,
    onItemClick: (String) -> Unit // Callback saat item diklik
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick(tempat.id) }, // Navigasi saat diklik
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Gambar Thumbnail (Menggunakan Coil)
            AsyncImage(
                model = buildPhotoUrl(tempat.photoReference),
                contentDescription = "Foto ${tempat.nama}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Teks Informasi (Nama & Kategori/Alamat)
            Column(
                modifier = Modifier.weight(1f) // Mengisi ruang tengah
            ) {
                Text(
                    text = tempat.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Coffee Shop", // Nanti bisa diganti data dinamis
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rating: ⭐ ${tempat.rating}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // 3. Tombol Detail
            Button(
                onClick = { onItemClick(tempat.id) },
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(text = "Detail", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}