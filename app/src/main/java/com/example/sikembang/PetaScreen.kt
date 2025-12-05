package com.example.sikembang.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.sikembang.BackgroundWhite
import com.example.sikembang.TextDark
import com.example.sikembang.TextGray
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

// Data Dummy Posyandu
data class PosyanduSimple(
    val id: String,
    val nama: String,
    val alamat: String,
    val lokasi: GeoPoint
)

@Composable
fun PetaScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToJurnal: () -> Unit,
    onPosyanduClick: (String) -> Unit
) {
    val context = LocalContext.current

    // Konfigurasi User Agent
    Configuration.getInstance().userAgentValue = context.packageName

    // 1. Data Dummy (Lokasi Malang & Bandung)
    val listPosyandu = listOf(
        PosyanduSimple("1", "Posyandu Ibu dan Anak", "Malang Kota", GeoPoint(-7.9666, 112.6326)),
        PosyanduSimple("2", "Posyandu Anak Sehat", "Suhat Malang", GeoPoint(-7.9425, 112.6226)),
        PosyanduSimple("3", "Posyandu Melati", "Cipadung Bandung", GeoPoint(-6.9288, 107.7176)),
        PosyanduSimple("4", "Posyandu Mawar", "Braga Bandung", GeoPoint(-6.9175, 107.6096))
    )

    var isLocationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isLocationPermissionGranted = permissions.values.all { it }
    }

    LaunchedEffect(Unit) {
        if (!isLocationPermissionGranted) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                TopHeaderSection()
            }

            // Peta (OSM DROID)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                ) {
                    if (isLocationPermissionGranted) {
                        AndroidView(
                            factory = { ctx ->
                                MapView(ctx).apply {
                                    setTileSource(TileSourceFactory.MAPNIK)
                                    setMultiTouchControls(true)
                                    controller.setZoom(15.0)

                                    // 1. Lokasi User
                                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                                    locationOverlay.enableMyLocation()
                                    locationOverlay.enableFollowLocation()
                                    overlays.add(locationOverlay)

                                    // 2. Marker Posyandu
                                    listPosyandu.forEach { posyandu ->
                                        val marker = Marker(this)
                                        marker.position = posyandu.lokasi
                                        marker.title = posyandu.nama
                                        marker.snippet = posyandu.alamat
                                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        marker.setOnMarkerClickListener { _, _ ->
                                            onPosyanduClick(posyandu.id)
                                            true
                                        }
                                        overlays.add(marker)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Butuh izin lokasi untuk menampilkan peta")
                        }
                    }
                }
            }

            item { SearchBarPeta() }

            items(listPosyandu) { posyandu ->
                PosyanduListItem(
                    data = posyandu,
                    onClick = { onPosyanduClick(posyandu.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// --- Helper Functions ---
@Composable
fun SearchBarPeta() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2F0FF))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, "Search", tint = TextGray)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Cari Posyandu...", color = TextGray)
    }
}

@Composable
fun PosyanduListItem(data: PosyanduSimple, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F0FF))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(data.nama, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text(data.alamat, fontSize = 12.sp, color = TextGray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = TextDark)
        }
    }
}