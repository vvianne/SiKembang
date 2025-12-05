package com.example.sikembang

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sikembang.ui.screen.*
import java.time.LocalDate
import androidx.compose.foundation.pager.*
import kotlinx.coroutines.delay
import androidx.compose.foundation.interaction.DragInteraction

val PrimaryPurple = Color(0xFF9580FF)
val BackgroundWhite = Color(0xFFF9F9F9)
val TextDark = Color(0xFF333333)
val TextGray = Color(0xFF888888)
val LightPurpleBg = Color(0xFFF2F0FF)

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainNavigation()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigation() {
    var currentScreen by remember { mutableStateOf("home") }
    var selectedDateForJournal by remember { mutableStateOf<LocalDate?>(null) }
    var selectedPosyanduId by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        "home" -> HomeScreen(
            onNavigateToJurnal = { currentScreen = "jurnal" },
            onNavigateToPeta = { currentScreen = "peta" }
        )
        "jurnal" -> JurnalScreen(
            onNavigateToHome = { currentScreen = "home" },
            onNavigateToPeta = { currentScreen = "peta" },
            selectedDateForJournal = selectedDateForJournal,
            onNavigateToTambahJurnal = { selectedDate ->
                selectedDateForJournal = selectedDate
                currentScreen = "tambah_jurnal"
            }
        )
        "peta" -> PetaScreen(
            onNavigateToHome = { currentScreen = "home" },
            onNavigateToJurnal = { currentScreen = "jurnal" },
            onPosyanduClick = { idYangDiklik ->
                selectedPosyanduId = idYangDiklik
                currentScreen = "detail_posyandu"
            }
        )

        "detail_posyandu" -> DetailPosyanduScreen(
            posyanduId = selectedPosyanduId ?: "1",
            onNavigateBack = { currentScreen = "peta" },
            onNavigateToHome = { currentScreen = "home" },
            onNavigateToJurnal = { currentScreen = "jurnal" }
        )
        "tambah_jurnal" -> TambahJurnalScreen(
            selectedDate = selectedDateForJournal,
            onNavigateBack = { currentScreen = "jurnal" }
        )
    }
}

@Composable
fun HomeScreen(onNavigateToJurnal: () -> Unit, onNavigateToPeta: () -> Unit) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = "home",
                onHomeClick = { },
                onJurnalClick = onNavigateToJurnal,
                onPetaClick = onNavigateToPeta
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            TopHeaderSection()
            Spacer(modifier = Modifier.height(20.dp))
            SearchBar()
            Spacer(modifier = Modifier.height(24.dp))
            BannerSection()
            Spacer(modifier = Modifier.height(24.dp))
            MenuGridSection(
                onJurnalClick = onNavigateToJurnal,
                onPetaClick = onNavigateToPeta
            )
            Spacer(modifier = Modifier.height(24.dp))
            UlasanSection()
        }
    }
}

// 1. Header (Profil & Notifikasi)
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

// 2. Search Bar
@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(LightPurpleBg)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = TextGray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Cari", color = TextGray)
    }
}

// 3. Banner Utama (Stimulasi Motorik)
// Data Class untuk konten Banner
data class BannerItem(
    val imageRes: Int,
    val title: String,
    val description: String
)

// List Data Dummy
val bannerList = listOf(
    BannerItem(
        imageRes = R.drawable.banner_kids,
        title = "Stimulasi Motorik Halus\nTingkatkan Kreativitas",
        description = "Rutin bermain balok atau menggambar pada usia 1-3 tahun..."
    ),
    BannerItem(
        imageRes = R.drawable.banner_kids,
        title = "Pentingnya Imunisasi\nUntuk Kekebalan Tubuh",
        description = "Lindungi si kecil dari berbagai penyakit berbahaya sejak dini..."
    ),
    BannerItem(
        imageRes = R.drawable.banner_kids,
        title = "Makanan Bergizi\nCegah Stunting",
        description = "Penuhi asupan protein hewani dan sayuran untuk tumbuh kembang..."
    )
)

@Composable
fun BannerSection() {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { bannerList.size })
//
//    val isDraggedState = remember { mutableStateOf(false) }
//    LaunchedEffect(pagerState) {
//        pagerState.interactionSource.interactions.collect { interaction ->
//            when (interaction) {
//                is DragInteraction.Start -> isDraggedState.value = true
//                is DragInteraction.Stop, is DragInteraction.Cancel -> isDraggedState.value = false
//            }
//        }
//    }
//    val isDragged = isDraggedState.value
//
//    LaunchedEffect(pagerState.currentPage) {
//        if (isDragged) return@LaunchedEffect
//
//        delay(3000)
//        val nextPage = (pagerState.currentPage + 1) % bannerList.size
//        pagerState.animateScrollToPage(nextPage)
//    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) { page ->
            val banner = bannerList[page]

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = banner.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

//                // Overlay gelap
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color.Black.copy(alpha = 0.3f))
//                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = banner.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = banner.description,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = 2
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

// 4. Pager Indicator (Titik-titik di bawah banner)
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) PrimaryPurple else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(if (pagerState.currentPage == iteration) 12.dp else 8.dp) // Yang aktif lebih besar dikit
                )
            }
        }
    }
}

// 5. Menu Grid (Jurnal & Peta Posyandu)
@Composable
fun MenuGridSection(onJurnalClick: () -> Unit, onPetaClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Kartu Kiri: Jurnal
        MenuCard(
            text = "Masukkan Jurnal",
            icon = Icons.Default.AddCircle,
            modifier = Modifier.weight(1f),
            onClick = onJurnalClick
        )
        // Kartu Kanan: Peta
        MenuCard(
            text = "Posyandu Terdekat",
            icon = Icons.Default.Place,
            modifier = Modifier.weight(1f),
            onClick = onPetaClick
        )
    }
}

@Composable
fun MenuCard(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

// 8. Bottom Navigation Bar
@Composable
fun BottomNavigationBar(
    selectedScreen: String,
    onHomeClick: () -> Unit,
    onJurnalClick: () -> Unit,
    onPetaClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
            label = { Text("Beranda") },
            selected = selectedScreen == "home",
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryPurple,
                selectedTextColor = PrimaryPurple,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Jurnal") },
            label = { Text("Jurnal") },
            selected = selectedScreen == "jurnal",
            onClick = onJurnalClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryPurple,
                selectedTextColor = PrimaryPurple,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Place, contentDescription = "Peta") },
            label = { Text("Peta") },
            selected = selectedScreen == "peta",
            onClick = onPetaClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryPurple,
                selectedTextColor = PrimaryPurple,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = Color.Transparent
            )
        )
    }
}

// 9. Ulasan (Tambahan)
@Composable
fun UlasanSection() {
    SectionCard(title = "Apa Kata Bunda?") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            UlasanItem("Bunda Ani", "Pelayanannya sangat ramah, bidannya sabar banget jelasin gizi anak.", 5)
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            UlasanItem("Bunda Siti", "Tempatnya bersih dan nyaman buat balita. Mainannya banyak!", 5)
        }
    }
}

@Composable
fun UlasanItem(nama: String, komen: String, bintang: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {
            Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(nama, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Row {
                repeat(bintang) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(komen, fontSize = 13.sp, color = TextGray, lineHeight = 18.sp)
        }
    }
}