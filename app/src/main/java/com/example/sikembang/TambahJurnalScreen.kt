package com.example.sikembang

import androidx.compose.material.icons.filled.CameraAlt
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahJurnalScreen(
    selectedDate: LocalDate?,
    onNavigateBack: () -> Unit
) {
    // Format tanggal dari LocalDate
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))
    val displayDate = selectedDate?.format(dateFormatter) ?: LocalDate.now().format(dateFormatter)
    val journalDate = selectedDate ?: LocalDate.now()

    var deskripsi by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
//    val repository = remember { JournalRepository() }
    val repository = remember { JournalRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCamera = true
        } else {
            showPermissionDialog = true
        }
    }

    // Check if form is valid
    val isFormValid = deskripsi.isNotBlank() && capturedImageUri != null

    // Show Camera Screen
    if (showCamera) {
        CameraScreen(
            onImageCaptured = { uri ->
                capturedImageUri = uri
                showCamera = false
            },
            onError = {
                showCamera = false
            },
            onDismiss = {
                showCamera = false
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Jurnal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
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

            // Tanggal Section
            DateSelectionSection(
                selectedDate = displayDate,
                onDateClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Deskripsi Perkembangan (Required)
            Row {
                Text(
                    text = "Deskripsi Perkembangan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Text(
                    text = " *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                placeholder = {
                    Text(
                        text = "Masukkan deskripsi",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = PrimaryPurple,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                isError = deskripsi.isEmpty()
            )

            if (deskripsi.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Deskripsi wajib diisi",
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Foto Perkembangan (Required)
            Row {
                Text(
                    text = "Foto Perkembangan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Text(
                    text = " *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            PhotoCaptureSection(
                capturedImageUri = capturedImageUri,
                onCameraClick = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) -> {
                            showCamera = true
                        }
                        else -> {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                },
                onRemovePhoto = { capturedImageUri = null }
            )

            if (capturedImageUri == null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Foto wajib diambil",
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Simpan Button
            Button(
                onClick = {
                    if (isFormValid && capturedImageUri != null) {
                        isSaving = true
                        coroutineScope.launch {
                            val result = repository.saveJournal(
                                date = journalDate,
                                deskripsi = deskripsi,
                                photoUri = capturedImageUri!!
                            )

                            isSaving = false

                            if (result.isSuccess) {
                                onNavigateBack()
                            } else {
                                errorMessage = result.exceptionOrNull()?.message
                                    ?: "Gagal menyimpan jurnal"
                                showErrorDialog = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) PrimaryPurple else Color.Gray,
                    disabledContainerColor = Color.Gray
                ),
                enabled = isFormValid && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Simpan Jurnal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            if (!isFormValid) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Harap lengkapi semua field yang wajib diisi",
                    fontSize = 12.sp,
                    color = Color.Red,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Permission Dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = {
                Text(
                    text = "Izin Kamera Diperlukan",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Aplikasi memerlukan akses ke kamera untuk mengambil foto perkembangan anak. Silakan berikan izin di pengaturan aplikasi."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        val intent = android.content.Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        ).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("Buka Pengaturan", color = PrimaryPurple)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("Batal", color = TextGray)
                }
            }
        )
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Text(
                    text = "Gagal Menyimpan",
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            },
            text = {
                Text(text = errorMessage)
            },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog = false }
                ) {
                    Text("OK", color = PrimaryPurple)
                }
            }
        )
    }
}

@Composable
fun CameraScreen(
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = remember { Preview.Builder().build() }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }
    val cameraSelector = remember { CameraSelector.DEFAULT_BACK_CAMERA }

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Top Bar with Close Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup",
                    tint = Color.White
                )
            }
        }

        // Capture Button at Bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    val photoFile = File(
                        context.cacheDir,
                        "journal_photo_${System.currentTimeMillis()}.jpg"
                    )

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                val savedUri = Uri.fromFile(photoFile)
                                onImageCaptured(savedUri)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                onError(exception)
                            }
                        }
                    )
                },
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White, CircleShape)
                    .border(4.dp, PrimaryPurple, CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White, CircleShape)
                )
            }
        }
    }
}

suspend fun android.content.Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

@Composable
fun DateSelectionSection(selectedDate: String, onDateClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightPurpleBg)
            .clickable(onClick = onDateClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Tanggal : $selectedDate",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryPurple
            )
        }

        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Pilih Tanggal",
            tint = PrimaryPurple,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun PhotoCaptureSection(
    capturedImageUri: Uri?,
    onCameraClick: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    if (capturedImageUri != null) {
        // Show captured image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = PrimaryPurple,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Image(
                painter = rememberAsyncImagePainter(capturedImageUri),
                contentDescription = "Foto Perkembangan",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Remove button
            IconButton(
                onClick = onRemovePhoto,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.8f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Hapus Foto",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    } else {
        // Show camera button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(12.dp)
                )
                .background(Color.White)
                .clickable(onClick = onCameraClick),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Ambil Foto",
                    tint = TextGray,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ambil foto dengan kamera",
                    fontSize = 14.sp,
                    color = TextGray
                )
            }
        }
    }
}