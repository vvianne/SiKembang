package com.example.sikembang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sikembang.ui.theme.SiKembangTheme

// --- Warna Kustom (Sesuai Gambar) ---
val PrimaryPurple = Color(0xFF9580FF)
val BackgroundWhite = Color(0xFFF9F9F9)
val TextDark = Color(0xFF333333)
val TextGray = Color(0xFF888888)
val LightPurpleBg = Color(0xFFF2F0FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Scaffold(
        bottomBar = { BottomNavigationBar() },
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
            Spacer(modifier = Modifier.height(8.dp))
            PagerIndicator()
            Spacer(modifier = Modifier.height(24.dp))
            MenuGridSection()
            Spacer(modifier = Modifier.height(24.dp))
//            EducationSectionHeader()
//            Spacer(modifier = Modifier.height(12.dp))
//            EducationModuleCard()
//            Spacer(modifier = Modifier.height(24.dp))
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
@Composable
fun BannerSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.banner_kids),
            contentDescription = "Banner Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Stimulasi Motorik Halus\nTingkatkan Kreativitas Anak",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rutin bermain balok atau menggambar\npada usia 1-3 tahun dapat membantu...",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

// 4. Pager Indicator (Titik-titik di bawah banner)
@Composable
fun PagerIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(PrimaryPurple)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
    }
}

// 5. Menu Grid (Jurnal & Peta Posyandu)
@Composable
fun MenuGridSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Kartu Kiri: Jurnal
        MenuCard(
            text = "Masukkan Jurnal",
            icon = Icons.Default.AddCircle,
            modifier = Modifier.weight(1f)
        )
        // Kartu Kanan: Peta
        MenuCard(
            text = "Posyandu Terdekat",
            icon = Icons.Default.Place,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MenuCard(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
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

//// 6. Header Section Edukasi
//@Composable
//fun EducationSectionHeader() {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = "Modul Edukasi",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            color = TextDark
//        )
//        Text(
//            text = "Lihat Selengkapnya",
//            fontSize = 12.sp,
//            color = PrimaryPurple,
//            fontWeight = FontWeight.SemiBold
//        )
//    }
//}

//// 7. Modul Edukasi Card
//@Composable
//fun EducationModuleCard() {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(140.dp)
//            .clip(RoundedCornerShape(16.dp))
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.img_edukasi),
//            contentDescription = "Background Edukasi",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.matchParentSize()
//        )
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "Edukasi Anak",
//                color = Color.White.copy(alpha = 0.8f),
//                fontSize = 12.sp
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//
//            Text(
//                text = "Modul Kreativitas & Imajinasi\nUsia 1–3 Tahun",
//                color = Color.White,
//                fontWeight = FontWeight.Bold,
//                fontSize = 16.sp,
//                lineHeight = 22.sp
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = "Menstimulasi daya imajinasi melalui bermain dan bercerita",
//                color = Color.White.copy(alpha = 0.9f),
//                fontSize = 11.sp,
//                lineHeight = 14.sp
//            )
//        }
//    }
//}

// 8. Bottom Navigation Bar
@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
            label = { Text("Beranda") },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryPurple,
                selectedTextColor = PrimaryPurple,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Jurnal") },
            label = { Text("Jurnal") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Place, contentDescription = "Peta") },
            label = { Text("Peta") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        HomeScreen()
    }
}