package com.example.sikembang.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sikembang.R
import com.example.sikembang.PrimaryPurple
import com.example.sikembang.BackgroundWhite
import com.example.sikembang.TextDark
import com.example.sikembang.TextGray
import com.example.sikembang.data.model.AlamatPosyandu
import com.example.sikembang.utils.LocationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPosyanduScreen(
    posyanduId: String,
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToJurnal: () -> Unit = {}
) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    val viewModel: DetailPosyanduViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DetailPosyanduViewModel(locationHelper) as T
            }
        }
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
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = "peta",
                onHomeClick = onNavigateToHome,
                onJurnalClick = onNavigateToJurnal,
                onPetaClick = { }
            )
        },
        containerColor = BackgroundWhite
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Item 1: Header Profil
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        TopHeaderSection()
                        Spacer(modifier = Modifier.height(20.dp))
                        TextButton(
                            onClick = onNavigateBack,
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, "Kembali", tint = TextGray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kembali ke Daftar", color = TextGray)
                        }
                    }

                    // Item 2: Kartu Utama Posyandu
                    item {
                        HeaderCard(posyandu, userLocation)
                    }

                    // Item 3: Alamat
                    item {
                        SectionCard(title = "Alamat & Kontak") {
                            DetailRow(Icons.Default.Place, "Alamat", posyandu.getAlamatLengkapFormat())
                            if (posyandu.telepon.isNotEmpty()) DetailRow(Icons.Default.Phone, "Telepon", posyandu.telepon)
                            if (posyandu.email.isNotEmpty()) DetailRow(Icons.Default.Email, "Email", posyandu.email)
                            if (posyandu.penanggungJawab.isNotEmpty()) DetailRow(Icons.Default.Person, "Penanggung Jawab", posyandu.penanggungJawab)
                        }
                    }

                    // Item 4: Jam Operasional
                    item {
                        SectionCard(title = "Jam Operasional") {
                            JamOperasionalSection(posyandu.jamOperasional)
                        }
                    }

                    // Item 5: Fasilitas
                    if (posyandu.fasilitasTersedia.isNotEmpty()) {
                        item {
                            SectionCard(title = "Fasilitas Tersedia") {
                                posyandu.fasilitasTersedia.forEach { fasilitas ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(fasilitas, fontSize = 14.sp, color = TextDark)
                                    }
                                }
                            }
                        }
                    }

                    // Item 6: Kegiatan
                    if (posyandu.kegiatanTerbaru.isNotEmpty()) {
                        item {
                            SectionCard(title = "Kegiatan Terbaru") {
                                Text(text = posyandu.kegiatanTerbaru, fontSize = 14.sp, color = TextDark)
                            }
                        }
                    }

                    // Item 7: Lokasi & Tombol
                    item {
                        SectionCard(title = "Lokasi & Navigasi") {
                            DetailRow(Icons.Default.MyLocation, "Koordinat", "${posyandu.latitude}, ${posyandu.longitude}")
                            userLocation?.let { location ->
                                DetailRow(Icons.Default.NearMe, "Jarak dari Anda", posyandu.getJarakFormat(location.latitude, location.longitude))
                                DetailRow(Icons.Default.Schedule, "Estimasi Waktu", posyandu.getEstimasiWaktu(location.latitude, location.longitude))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(posyandu.getGoogleMapsViewUrl()))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Map, null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Lihat Peta")
                                }
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(posyandu.getGoogleMapsUrl()))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                                ) {
                                    Icon(Icons.Default.Directions, null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Navigasi")
                                }
                            }
                        }
                        // Spacer bawah agar konten paling bawah tidak mepet banget
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Data tidak ditemukan", color = TextGray)
                }
            }
        }
    }
}

// --- Komponen Pendukung Tetap Sama ---

@Composable
fun TopHeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = Color.LightGray
            ) {
                Image(
                    painter = painterResource(id = R.drawable.foto_profil),
                    contentDescription = "Foto Profil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Halo, Suarni!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "Ayo pantau kondisi anakmu!",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
        }
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifikasi",
                tint = TextDark
            )
        }
    }
}

@Composable
fun BottomNavigationBar(selectedScreen: String, onHomeClick: () -> Unit, onJurnalClick: () -> Unit, onPetaClick: () -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, "Beranda") },
            label = { Text("Beranda") },
            selected = selectedScreen == "home",
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryPurple, selectedTextColor = PrimaryPurple, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, "Jurnal") },
            label = { Text("Jurnal") },
            selected = selectedScreen == "jurnal",
            onClick = onJurnalClick,
            colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryPurple, selectedTextColor = PrimaryPurple, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Place, "Peta") },
            label = { Text("Peta") },
            selected = selectedScreen == "peta",
            onClick = onPetaClick,
            colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryPurple, selectedTextColor = PrimaryPurple, indicatorColor = Color.Transparent)
        )
    }
}

@Composable
fun HeaderCard(
    posyandu: AlamatPosyandu,
    userLocation: android.location.Location?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Gambar Latar Belakang
            Image(
                painter = painterResource(id = R.drawable.posyandu),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlay Warna Ungu Transparan
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryPurple.copy(alpha = 0.7f))
            )

            // 3. Konten Kartu
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = posyandu.namaPosyandu,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(posyandu.getStatusColor())
                    ) {
                        Text(
                            text = posyandu.getStatusBuka(),
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Bagian Bawah: Jarak, Waktu, dan Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Kiri: Jarak & Waktu
                    if (userLocation != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoChip(
                                Icons.Default.NearMe,
                                posyandu.getJarakFormat(userLocation.latitude, userLocation.longitude)
                            )
                            InfoChip(
                                Icons.Default.Schedule,
                                posyandu.getEstimasiWaktu(userLocation.latitude, userLocation.longitude)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    // Kanan: Rating
                    if (posyandu.rating > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.3f)) // Background rating sedikit lebih terang
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                null,
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
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.2f)).padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = TextGray)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 12.sp, color = TextGray)
            Text(value, fontSize = 14.sp, color = TextDark)
        }
    }
}

@Composable
fun JamOperasionalSection(jamOperasional: AlamatPosyandu.JamOperasional) {
    val hariList = listOf(
        "Senin" to jamOperasional.senin, "Selasa" to jamOperasional.selasa,
        "Rabu" to jamOperasional.rabu, "Kamis" to jamOperasional.kamis,
        "Jumat" to jamOperasional.jumat, "Sabtu" to jamOperasional.sabtu,
        "Minggu" to jamOperasional.minggu
    )
    hariList.forEachIndexed { index, (hari, jam) ->
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(hari, fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Medium)
            Text(jam, fontSize = 14.sp, color = if (jam == "Tutup") Color.Red else TextGray)
        }
        if (index < hariList.size - 1) Divider(modifier = Modifier.padding(vertical = 4.dp))
    }
}