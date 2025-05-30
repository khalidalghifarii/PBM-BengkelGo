<p align="center">
  <img src="https://github.com/user-attachments/assets/6971f1da-6324-4b7c-b10f-6d0e95f255da" alt="![image](https://github.com/user-attachments/assets/6971f1da-6324-4b7c-b10f-6d0e95f255da)" width="200"/>
</p>

# BengkelGo

> Servis Kendaraan On-Demand – Teknisi Datang ke Lokasi Pelanggan

BengkelGo adalah aplikasi mobile Android yang dirancang untuk memudahkan pemilik kendaraan mendapatkan layanan servis dan perbaikan kendaraan langsung di lokasi mereka. Aplikasi ini juga menyediakan fitur panggilan darurat dan toko suku cadang terintegrasi.

## Tentang Proyek

Aplikasi BengkelGo bertujuan untuk menghubungkan pemilik kendaraan dengan teknisi atau bengkel secara efisien. Pengguna dapat memesan berbagai layanan servis ringan, melakukan panggilan darurat untuk kondisi mendesak, hingga membeli suku cadang kendaraan. Proyek ini dibangun menggunakan Kotlin dengan Firebase sebagai backend untuk autentikasi dan database.

## Fitur Utama

Berikut adalah fitur-fitur utama yang ada atau direncanakan dalam aplikasi BengkelGo:

- *Layanan Servis Kendaraan di Lokasi Pelanggan*: Memungkinkan pengguna memesan layanan seperti ganti oli, tambal ban, pengecekan aki, tune-up mesin, atau perbaikan kecil lainnya langsung di lokasi pengguna. Pengguna dapat memilih jenis kendaraan, jenis servis, lokasi, jadwal, dan metode pembayaran.
- *Panggilan Darurat 24/7*: Fitur untuk membantu pengguna dalam keadaan darurat seperti ban bocor atau kendaraan mogok di jalan, dengan menghubungkan ke nomor darurat bengkel.
- *Toko Suku Cadang (Spare Part Shop)*: Pengguna dapat menjelajahi, mencari, dan membeli berbagai suku cadang kendaraan langsung dari aplikasi. Fitur ini mencakup detail produk, keranjang belanja, dan proses checkout.
- *Manajemen Akun Pengguna*: Pengguna dapat mendaftar, login, mereset password, dan melihat profil mereka.
- *Estimasi Biaya & Booking Teknisi*: (Fitur direncanakan) Pelanggan dapat melihat perkiraan biaya sebelum memesan layanan.
- *Fitur Tracking Teknisi*: (Fitur direncanakan) Melacak posisi tukang bengkel yang dalam perjalanan.

## Kontribusi terhadap SDGs

BengkelGo mendukung *SDG 8 (Pekerjaan Layak dan Pertumbuhan Ekonomi)* dengan membuka lebih banyak peluang kerja bagi tukang bengkel lepas dan teknisi kendaraan yang ingin menawarkan jasanya secara fleksibel. Selain itu, aplikasi ini juga meningkatkan efisiensi servis kendaraan, mengurangi antrean di bengkel, dan memberikan solusi yang lebih ramah lingkungan dengan mengurangi kebutuhan mobil derek untuk perbaikan ringan.

## Teknologi yang Digunakan

- *Bahasa Pemrograman*: Kotlin
- *Platform*: Android
- *Arsitektur Database*: Firebase Firestore
- *Autentikasi*: Firebase Authentication
- *Desain UI*: Material Components / Material 3
- *Build Tool*: Gradle

## Memulai Proyek

Untuk menjalankan proyek ini di lingkungan lokal Anda, ikuti langkah-langkah berikut:

### Prasyarat

- Android Studio (versi terbaru direkomendasikan)
- JDK 17 (atau sesuai konfigurasi Gradle)
- Emulator Android atau perangkat Android fisik

### Instalasi & Setup Firebase

1.  *Clone Repositori*:
    bash
    git clone [https://github.com/khalidalghifarii/pbm-bengkelgo.git](https://github.com/khalidalghifarii/pbm-bengkelgo.git)
    cd pbm-bengkelgo
    
2.  *Buka di Android Studio*: Buka proyek melalui Android Studio.
3.  *Konfigurasi Firebase*:
    - Buat proyek baru di [Firebase Console](https://console.firebase.google.com/).
    - Tambahkan aplikasi Android ke proyek Firebase Anda dengan package name com.example.bengkelgo (sesuai AndroidManifest.xml dan build.gradle.kts).
    - Unduh file konfigurasi google-services.json dari Firebase console.
    - Letakkan file google-services.json tersebut di direktori app/ proyek Anda.
    - Aktifkan layanan *Authentication* (dengan metode Email/Password) dan *Cloud Firestore* di Firebase Console.
    - Konfigurasikan *Security Rules* untuk Cloud Firestore sesuai kebutuhan (lihat contoh di pembahasan sebelumnya untuk spareParts, userCarts, dll.).
4.  *Sinkronkan Proyek Gradle*: Biarkan Android Studio menyinkronkan proyek dengan file Gradle.
5.  *Jalankan Aplikasi*: Pilih target emulator atau perangkat, lalu jalankan aplikasi.

## Struktur Proyek (Ringkasan)

- **java/com/example/bengkelgo**:
  - *Activities*: MainActivity, LoginActivity, RegisterActivity, ProfileActivity, VehicleRepairFormActivity, OrderSummaryActivity, OrderConfirmationActivity, SplashActivity, ResetPasswordActivity, SparePartListActivity, SparePartDetailActivity, ShoppingCartActivity, SparePartCheckoutActivity.
  - *Data Classes*: VehicleServiceOrder, SparePart, CartItem, SparePartOrder.
  - *Adapters*: SparePartAdapter, CartAdapter (diasumsikan ada di package adapter).
- **res/layout**: Berisi file XML untuk semua layout activity dan item RecyclerView.
- **res/drawable**: Berisi aset gambar dan ikon.
- **res/values**: Berisi resource seperti string, warna, dan tema.

## Operasi CRUD

Aplikasi ini menerapkan operasi CRUD (Create, Read, Update, Delete) dalam beberapa konteks:

- *Create*:
  - Pengguna membuat akun baru (Registrasi).
  - Pengguna membuat pesanan servis kendaraan.
  - Pengguna menambahkan item ke keranjang suku cadang.
  - Pengguna membuat pesanan suku cadang.
  - (Brief) Teknisi bengkel bisa membuat profil dan daftar layanan yang mereka tawarkan.
- *Read*:
  - Pengguna melihat daftar produk suku cadang.
  - Pengguna melihat detail produk suku cadang.
  - Pengguna melihat item di keranjang belanja.
  - Pengguna melihat data profilnya.
  - (Brief) Pelanggan bisa mencari layanan bengkel berdasarkan lokasi dan ulasan.
- *Update*:
  - Pengguna mengubah kuantitas item di keranjang suku cadang.
  - (Brief) Teknisi bisa memperbarui jadwal kerja, harga, dan layanan.
- *Delete*:
  - Pengguna menghapus item dari keranjang suku cadang.
  - (Brief) Pelanggan bisa membatalkan pesanan sebelum teknisi tiba.

## Kontribusi

Kontribusi selalu diterima! Jika Anda memiliki ide untuk perbaikan atau menemukan bug, silakan buat issue atau pull request.

1.  Fork Proyek
2.  Buat Branch Fitur Anda (git checkout -b fitur/FiturBaru)
3.  Commit Perubahan Anda (git commit -m 'Menambahkan FiturBaru')
4.  Push ke Branch (git push origin fitur/FiturBaru)
5.  Buka Pull Request
