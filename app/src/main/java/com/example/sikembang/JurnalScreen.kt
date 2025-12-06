package com.example.sikembang

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext // Penting!
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.sikembang.data.model.JournalEntry
import com.example.sikembang.data.repository.JournalRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaInstant // Import untuk konversi waktu
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JurnalScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToPeta: () -> Unit,
    selectedDateForJournal: LocalDate?,
    onNavigateToTambahJurnal: (LocalDate) -> Unit
) {
    var selectedDate by remember { mutableStateOf(selectedDateForJournal ?: LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var journals by remember { mutableStateOf<List<JournalEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val repository = remember { JournalRepository(context) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedDate) {
        isLoading = true
        val result = repository.getJournalsByDate(selectedDate)
        if (result.isSuccess) {
            journals = result.getOrNull() ?: emptyList()
        }
        isLoading = false
    }

    LaunchedEffect(selectedDateForJournal) {
        if (selectedDateForJournal != null) {
            selectedDate = selectedDateForJournal
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = "jurnal",
                onHomeClick = onNavigateToHome,
                onJurnalClick = { },
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

            // Header
            JurnalHeaderSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Title Jurnal
            Text(
                text = "Jurnal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar
            CalendarSection(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                onDateSelected = { newDate ->
                    selectedDate = newDate
                },
                onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add New Entry Button
            TextButton(
                onClick = { onNavigateToTambahJurnal(selectedDate) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Masukkan Entri Baru",
                    color = PrimaryPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Entry Cards atau Loading/Empty State
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            } else if (journals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8F8F8)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Tidak ada jurnal",
                            modifier = Modifier.size(48.dp),
                            tint = TextGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada jurnal untuk tanggal ini",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                    }
                }
            } else {
                journals.forEach { journal ->
                    JournalEntryCard(
                        journal = journal,
                        onDeleteClick = { journalId ->
                            coroutineScope.launch {
                                isLoading = true
                                val deleteResult = repository.deleteJournal(journalId)

                                if (deleteResult.isSuccess) {
                                    // Refresh data setelah hapus
                                    val refreshResult = repository.getJournalsByDate(selectedDate)
                                    if (refreshResult.isSuccess) {
                                        journals = refreshResult.getOrNull() ?: emptyList()
                                    }
                                }
                                isLoading = false
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun JurnalHeaderSection() {
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarSection(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8F8F8))
            .padding(16.dp)
    ) {
        // Month and Year Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    tint = PrimaryPurple
                )
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("id", "ID"))} ${currentMonth.year}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )

            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = PrimaryPurple
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Day Headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min").forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value
        val daysInMonth = currentMonth.lengthOfMonth()

        var dayCounter = 1
        for (week in 0..5) {
            if (dayCounter > daysInMonth) break

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 1..7) {
                    val dayNumber = if (week == 0 && dayOfWeek < firstDayOfMonth) {
                        0
                    } else if (dayCounter <= daysInMonth) {
                        dayCounter++
                    } else {
                        0
                    }

                    val dateForThisDay = if (dayNumber > 0) {
                        currentMonth.atDay(dayNumber)
                    } else {
                        null
                    }

                    CalendarDay(
                        day = dayNumber,
                        isSelected = dateForThisDay == selectedDate,
                        onClick = {
                            if (dateForThisDay != null) {
                                onDateSelected(dateForThisDay)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun CalendarDay(
    day: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .then(
                if (day > 0) Modifier.clickable(onClick = onClick) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (day > 0) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) PrimaryPurple else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    fontSize = 14.sp,
                    color = if (isSelected) Color.White else TextDark,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JournalEntryCard(
    journal: JournalEntry,
    onDeleteClick: (String) -> Unit) {

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))
    val displayDate = journal.getLocalDate().format(dateFormatter)

    var showDetailDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PrimaryPurple)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayDate,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Button(
                onClick = { showDetailDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.25f)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Baca Selengkapnya",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = journal.deskripsi,
                fontSize = 14.sp,
                color = TextDark,
                lineHeight = 20.sp,
                maxLines = 3
            )
        }
    }

    if (showDetailDialog) {
        JournalDetailDialog(
            journal = journal,
            onDismiss = { showDetailDialog = false },
            onDelete = { journalId -> onDeleteClick(journalId)
            }
        )
    }
}

@Composable
fun EntryBulletPointDark(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = "• ",
            fontSize = 14.sp,
            color = TextDark
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = TextDark,
            lineHeight = 20.sp
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JournalDetailDialog(
    journal: JournalEntry,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit // 1. Tambah Parameter onDelete
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))
    val displayDate = journal.getLocalDate().format(dateFormatter)

    // State untuk memunculkan dialog konfirmasi
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // --- DIALOG UTAMA (DETAIL) ---
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text("Detail Jurnal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text(displayDate, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = PrimaryPurple)
                Spacer(modifier = Modifier.height(16.dp))

                if (journal.fotoURL.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray)) {
                        Image(
                            painter = rememberAsyncImagePainter(journal.fotoURL),
                            contentDescription = "Foto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text("Deskripsi Perkembangan", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text(journal.deskripsi, fontSize = 14.sp, color = TextDark, lineHeight = 20.sp)
            }
        },
        // KITA PAKAI LOGIKA ROW DI SINI BIAR TOMBOLNYA PISAH KIRI-KANAN
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween // Ini kuncinya: Hapus di Kiri, Tutup di Kanan
            ) {
                // Tombol Delete (Merah) di Kiri
                TextButton(onClick = { showDeleteConfirmation = true }) {
                    Text("Hapus", color = Color.Red, fontWeight = FontWeight.Bold)
                }

                // Tombol Tutup (Ungu) di Kanan
                TextButton(onClick = onDismiss) {
                    Text("Tutup", color = PrimaryPurple, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    )

    // --- DIALOG KONFIRMASI HAPUS (Muncul jika showDeleteConfirmation = true) ---
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            containerColor = Color.White,
            title = { Text("Hapus Jurnal?", fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                Text("Apakah Anda yakin ingin menghapus jurnal tanggal $displayDate? Data yang dihapus tidak dapat dikembalikan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Panggil fungsi delete, lalu tutup semua dialog
                        journal.id?.let { onDelete(it) }
                        showDeleteConfirmation = false
                        onDismiss()
                    }
                ) {
                    Text("Ya, Hapus", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Batal", color = TextGray)
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun JournalEntry.getLocalDate(): LocalDate {
    return this.tanggal.toJavaInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}