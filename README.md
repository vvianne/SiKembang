# SiKembang - Aplikasi Pemantau Tumbuh Kembang Anak

**Projek Akhir - Pengembangan Aplikasi Perangkat Bergerak B**
**Program Studi Teknik Informatika, Universitas Brawijaya (2025)**

## 📋 Tentang Aplikasi
SiKembang adalah aplikasi digital yang dirancang untuk membantu orang tua memantau dan mendukung tumbuh kembang anak secara sistematis, mudah, dan terukur. Aplikasi ini hadir sebagai respon terhadap isu stunting dan kebutuhan akan solusi digital bagi pola asuh masa kini.

### 🌍 Dukungan terhadap Sustainable Development Goals (SDGs)
Aplikasi ini dikembangkan untuk mendukung pencapaian target global SDGs, khususnya:
* **SDG 2.2 (Zero Hunger):** Mengakhiri segala bentuk malnutrisi, termasuk penanganan isu stunting pada balita.
* **SDG 3.2 (Good Health and Well-being):** Mengurangi kematian bayi dan balita yang dapat dicegah melalui pemantauan kesehatan rutin.
* **SDG 3.8 (Universal Health Coverage):** Mendukung pemerataan akses terhadap layanan kesehatan dasar yang berkualitas dan terjangkau.
* **SDG 4.2 (Quality Education):** Menjamin bahwa semua anak perempuan dan laki-laki memiliki akses terhadap perkembangan anak usia dini yang berkualitas.

## 👥 Anggota Kelompok 6
| Nama | NIM |
| :--- | :--- |
| **Ana Zahratul Firdausi** | 235150201111049 |
| **Vincentia Melody Vivianne** | 235150201111047 |
| **Silvana Arayunda Marbun** | 235150201111076 |

## 🚀 Fitur Utama (MVP)

### 1. 📝 Journaling Tumbuh Kembang (Powered by Supabase)
Fitur pencatatan perkembangan anak yang telah dimigrasi menggunakan **Supabase** untuk menangani penyimpanan data visual yang lebih optimal.
* **Dokumentasi Visual:** Menggunakan sensor kamera untuk mengambil foto anak. Foto disimpan menggunakan **Supabase Storage** karena kebutuhan upload file yang lebih kompleks yang sebelumnya terkendala di Firebase.
* **Catatan Detail:** Setiap entri dilengkapi data tinggi badan, berat badan, dan kemampuan motorik.
* **Monitoring Kronologis:** Membantu orang tua melihat riwayat tumbuh kembang secara terstruktur.

### 2. 📍 Lokasi Posyandu (Berbasis Sensor Maps)
Fitur pencarian fasilitas kesehatan terdekat yang memanfaatkan sensor GPS pada perangkat.
* **Pencarian Akurat:** Menampilkan daftar posyandu terdekat lengkap dengan jarak dan rute.
* **Informasi Fasilitas:** Menyediakan informasi detail seperti alamat lengkap, jam operasional, dan kontak penanggung jawab.

### 3. 📰 Berita & Informasi (Fitur Tambahan)
Menyediakan *update* informasi terkini terkait gizi, pola asuh, dan panduan stimulasi anak dari sumber terpercaya.

## 🛠️ Tech Stack & Tools
Teknologi yang digunakan dalam pengembangan aplikasi:
* **Bahasa:** Kotlin
* **UI Framework:** Jetpack Compose (Declarative UI)
* **Architecture:** UI State & Navigation Component
* **Database & Storage:**
    * **Supabase:** Digunakan khusus pada fitur **Jurnal** untuk penyimpanan database relasional dan *object storage* (foto perkembangan anak), menggantikan Firebase karena kebutuhan upload file.
    * **Firebase:** (Jika masih digunakan untuk fitur lain seperti Auth/Maps data, jika tidak bisa dihapus).
* **Asynchronous:** Coroutines
* **Sensors:** GPS (Location) & Camera
* **IDE:** Android Studio

---
*Dibuat untuk memenuhi tugas Projek Akhir Mata Kuliah PAPB - Fakultas Ilmu Komputer, Universitas Brawijaya.*
