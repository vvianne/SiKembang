package com.example.sikembang.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sikembang.data.model.PosyanduLengkap
import com.example.sikembang.ui.viewmodel.PosyanduLokasiViewModel
import com.example.sikembang.utils.LocationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPosyanduScreen(
    posyanduId: String,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val viewModel: PosyanduLokasiViewModel = viewModel(
        factory = PosyanduLokasiViewModelFactory(locationHelper)
    )

    val selectedPosyandu by viewModel.selectedPosyandu.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(posyanduId) {
        viewModel.loadPosyanduById(posyanduId)
        if (userLocation == null) {
            viewModel.getCurrentLocation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Posyandu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryPurple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryPurple)
            }
        } else {
            selectedPosyandu?.let { posyandu ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(BackgroundWhite)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Card
                    HeaderCard(posyandu, userLocation)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Informasi Alamat
                    SectionCard(title = "Alamat & Kontak") {
                        DetailRow(Icons.Default.Place, "Alamat", posyandu.getAlamatLengkapFormat())
                        if (posyandu.telepon.isNotEmpty()) {
                            DetailRow(Icons.Default.Phone, "Telepon", posyandu.telepon)
                        }
                        if (posyandu.email.isNotEmpty()) {
                            DetailRow(Icons.Default.Email, "Email", posyandu.email)
                        }
                        if (posyandu.penanggungJawab.isNotEmpty()) {
                            DetailRow(Icons.Default.Person, "Penanggung Jawab", posyandu.penanggungJawab)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Jam Operasional
                    SectionCard(title = "Jam Operasional") {
                        JamOperasionalSection(posyandu.jamOperasional)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fasilitas
                    if (posyandu.fasilitasTersedia.isNotEmpty()) {
                        SectionCard(title = "Fasilitas Tersedia") {
                            posyandu.fasilitasTersedia.forEach { fasilitas ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(fasilitas, fontSize = 14.sp, color = TextDark)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Kegiatan Terbaru
                    if (posyandu.kegiatanTerbaru.isNotEmpty()) {
                        SectionCard(title = "Kegiatan Terbaru") {
                            Text(
                                text = posyandu.kegiatanTerbaru,
                                fontSize = 14.sp,
                                color = TextDark
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Lokasi & Navigasi
                    SectionCard(title = "Lokasi & Navigasi") {
                        DetailRow(
                            Icons.Default.MyLocation,
                            "Koordinat",
                            "${posyandu.latitude}, ${posyandu.longitude}"
                        )

                        if (userLocation != null) {
                            DetailRow(
                                Icons.Default.NearMe,
                                "Jarak dari Anda",
                                posyandu.getJarakFormatted(
                                    userLocation.latitude,
                                    userLocation.longitude
                                )
                            )
                            DetailRow(
                                Icons.Default.Schedule,
                                "Estimasi Waktu",
                                posyandu.getEstimasiWaktu(
                                    userLocation.latitude,
                                    userLocation.longitude
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(posyandu.getGoogleMapsViewUrl())
                                    )
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Map,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Lihat Peta")
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(posyandu.getGoogleMapsUrl())
                                    )
                                    context.startActivity(intent)
                                },
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
                                Text("Navigasi")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Data tidak ditemukan", color = TextGray)
                }
            }
        }
    }
}

@Composable
fun HeaderCard(
    posyandu: PosyanduLengkap,
    userLocation: android.location.Location?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryPurple)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Nama Posyandu
            Text(
                text = posyandu.namaPosyandu,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status dan Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(posyandu.getStatusColor())
                ) {
                    Text(
                        text = posyandu.getStatusBuka(),
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Rating
                if (posyandu.rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", posyandu.rating),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = " (${posyandu.jumlahUlasan})",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Jarak (jika tersedia)
            if (userLocation != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoChip(
                        icon = Icons.Default.NearMe,
                        text = posyandu.getJarakFormatted(
                            userLocation.latitude,
                            userLocation.longitude
                        )
                    )
                    InfoChip(
                        icon = Icons.Default.Schedule,
                        text = posyandu.getEstimasiWaktu(
                            userLocation.latitude,
                            userLocation.longitude
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = TextGray
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextGray
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = TextDark
            )
        }
    }
}

@Composable
fun JamOperasionalSection(jamOperasional: PosyanduLengkap.JamOperasional) {
    val hariList = listOf(
        "Senin" to jamOperasional.senin,
        "Selasa" to jamOperasional.selasa,
        "Rabu" to jamOperasional.rabu,
        "Kamis" to jamOperasional.kamis,
        "Jumat" to jamOperasional.jumat,
        "Sabtu" to jamOperasional.sabtu,
        "Minggu" to jamOperasional.minggu
    )

    hariList.forEach { (hari, jam) ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = hari,
                fontSize = 14.sp,
                color = TextDark,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = jam,
                fontSize = 14.sp,
                color = if (jam == "Tutup") Color.Red else TextGray
            )
        }
        if (hari != "Minggu") {
            Divider(modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}