package com.example.sikembang.ui.posyandu // Sesuaikan package

import android.preference.PreferenceManager
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sikembang.BackgroundWhite
import com.example.sikembang.BottomNavigationBar
import com.example.sikembang.PrimaryPurple
import com.example.sikembang.TextDark
import com.example.sikembang.TextGray
import com.example.sikembang.TopHeaderSection
import com.example.sikembang.data.model.AlamatPosyandu
import com.example.sikembang.utils.LocationHelper
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun PetaScreen(
    onNavigateToDetail: (String) -> Unit, // Tambah ini buat navigasi
    onNavigateToHome: () -> Unit,
    onNavigateToJurnal: () -> Unit
) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    // 1. Konfigurasi OSM
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = context.packageName
    }

    // 2. Panggil ViewModel
    val viewModel: PetaViewModel = viewModel(
        factory = PetaViewModelFactory(locationHelper)
    )

    val listPosyandu by viewModel.listPosyandu.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 3. Load Data
    LaunchedEffect(Unit) {
        viewModel.getCurrentLocation()
        viewModel.getAllPosyandu()
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
                    AndroidView(
                        factory = { ctx ->
                            MapView(ctx).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(15.0)

                                val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                                locationOverlay.enableMyLocation()
                                overlays.add(locationOverlay)
                            }
                        },
                        // Update Map saat data berubah
                        update = { map ->
                            userLocation?.let { loc ->
                                val center = GeoPoint(loc.latitude, loc.longitude)
                                map.controller.animateTo(center)
                            } ?: run {
                                map.controller.setCenter(GeoPoint(-7.9826, 112.6308))
                            }

                            if (map.overlays.size > 1) {
                                map.overlays.subList(1, map.overlays.size).clear()
                            }

                            listPosyandu.forEach { posyandu ->
                                val lat = posyandu.latitude ?: 0.0
                                val lng = posyandu.longitude ?: 0.0

                                if (lat != 0.0 && lng != 0.0) {
                                    val marker = Marker(map)
                                    marker.position = GeoPoint(lat, lng)
                                    marker.title = posyandu.namaPosyandu
                                    marker.snippet = posyandu.alamatLengkap
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                                    marker.setOnMarkerClickListener { _, _ ->
                                        onNavigateToDetail(posyandu.id.toString())
                                        true
                                    }
                                    map.overlays.add(marker)
                                }
                            }
                            map.invalidate()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            item { SearchBarPeta() }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryPurple,
                            strokeWidth = 4.dp
                        )
                    }
                }
            } else {
                items(listPosyandu) { posyandu ->
                    PosyanduListItem(
                        data = posyandu,
                        onClick = { onNavigateToDetail(posyandu.id.toString()) }
                    )
                }

                if (listPosyandu.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("Tidak ada data posyandu", color = TextGray)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// Helper Functions
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
fun PosyanduListItem(data: AlamatPosyandu, onClick: () -> Unit) {
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
                Text(data.namaPosyandu, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                // Pakai alamat dari Supabase
                Text(data.alamatLengkap ?: "Alamat tidak tersedia", fontSize = 12.sp, color = TextGray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = TextDark)
        }
    }
}