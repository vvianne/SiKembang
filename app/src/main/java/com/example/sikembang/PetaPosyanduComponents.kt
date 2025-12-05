package com.example.sikembang.ui.screen

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sikembang.*
import com.example.sikembang.data.model.AlamatPosyandu

@Composable
fun PosyanduCard(
    posyandu: AlamatPosyandu,
    userLocation: Location?,
    onClick: () -> Unit,
    onNavigate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Nama dan Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = posyandu.namaPosyandu,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Status Buka/Tutup
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(posyandu.getStatusColor())
                    ) {
                        Text(
                            text = posyandu.getStatusBuka(),
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Rating
                if (posyandu.rating > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", posyandu.rating),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                        Text(
                            text = "(${posyandu.jumlahUlasan})",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Alamat
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TextGray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = posyandu.alamatLengkap,
                    fontSize = 13.sp,
                    color = TextGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Kecamatan
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationCity,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TextGray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${posyandu.kelurahan}, ${posyandu.kecamatan}",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }

            // Jarak dan Waktu (jika lokasi tersedia)
            if (userLocation != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.NearMe,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = PrimaryPurple
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = posyandu.getJarakFormat(
                                userLocation.latitude,
                                userLocation.longitude
                            ),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryPurple
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = TextGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = posyandu.getJarakFormat(
                                userLocation.latitude,
                                userLocation.longitude
                            ),
                            fontSize = 13.sp,
                            color = TextGray
                        )
                    }
                }
            }

            // Jam Operasional Hari Ini
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TextGray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hari ini: ${posyandu.jamOperasional.getJamHariIni("Senin")}",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }

            // Kegiatan Terbaru (jika ada)
            if (posyandu.kegiatanTerbaru.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = LightPurpleBg
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = PrimaryPurple
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = posyandu.kegiatanTerbaru,
                            fontSize = 12.sp,
                            color = TextDark,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryPurple
                    )
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Detail", fontSize = 13.sp)
                }

                Button(
                    onClick = onNavigate,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple
                    )
                ) {
                    Icon(
                        Icons.Default.Directions,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rute", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = TextGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tidak ada posyandu ditemukan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Coba ubah filter atau kata kunci pencarian",
                fontSize = 13.sp,
                color = TextGray
            )
        }
    }
}

@Composable
fun FilterDialog(
    daftarKecamatan: List<String>,
    selectedKecamatan: String?,
    onKecamatanSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Posyandu") },
        text = {
            Column {
                Text("Pilih Kecamatan:", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))

                // Option: Semua
                FilterChip(
                    selected = selectedKecamatan == null,
                    onClick = {
                        onKecamatanSelected(null)
                        onDismiss()
                    },
                    label = { Text("Semua Kecamatan") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // List Kecamatan
                daftarKecamatan.forEach { kecamatan ->
                    FilterChip(
                        selected = selectedKecamatan == kecamatan,
                        onClick = {
                            onKecamatanSelected(kecamatan)
                            onDismiss()
                        },
                        label = { Text(kecamatan) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun PosyanduCompactCard(
    posyandu: AlamatPosyandu,
    userLocation: Location?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = posyandu.namaPosyandu,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (userLocation != null) {
                    Text(
                        text = posyandu.getJarakFormat(
                            userLocation.latitude,
                            userLocation.longitude
                        ),
                        fontSize = 12.sp,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = posyandu.kecamatan,
                fontSize = 12.sp,
                color = TextGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(posyandu.getStatusColor())
            ) {
                Text(
                    text = posyandu.getStatusBuka(),
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}