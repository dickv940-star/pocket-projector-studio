# Pocket Projector Studio

Aplikasi Android untuk menjadikan layar ponsel sebagai sumber visual fullscreen bagi perangkat optik/lensa eksternal.

## Kompatibilitas Android
- Minimum: Android 6.0 (API 23).
- Target build: Android 15 (API 35).
- Dibuat dengan Kotlin, AndroidX, dan Media3.
- Antarmuka menyesuaikan ukuran layar; performa video bergantung pada chipset, RAM, codec, dan resolusi tiap perangkat.
- Dukungan seluruh perangkat Android tidak bisa dijamin. Perangkat di bawah Android 6.0 tidak didukung oleh build ini.

## Fitur MVP 0.1
- Pilih dan putar video lokal.
- Pilih dan tampilkan gambar lokal.
- Tampilan fullscreen immersive.
- Flip horizontal dan vertikal untuk koreksi orientasi optik.
- Pengaturan kecerahan fullscreen.
- Pola grid/crosshair untuk uji fokus.
- Auto-exit 30 menit.

## Build APK otomatis
Setiap push ke branch `main` menjalankan GitHub Actions:
1. Buka tab **Actions** di repository.
2. Pilih workflow **Build Android APK** dan buka run terbaru.
3. Setelah status sukses, unduh artifact `pocket-projector-studio-debug`.
4. Ekstrak ZIP artifact dan instal `app-debug.apk` pada perangkat Android.

Build juga dapat dijalankan manual melalui **Actions → Build Android APK → Run workflow**.

## Batasan optik
Aplikasi tidak dapat memproyeksikan gambar ke dinding tanpa perangkat optik. Layar ponsel hanya menjadi sumber gambar; proyeksi nyata memerlukan lensa/assembly optik eksternal, dudukan stabil, ventilasi, dan ruangan gelap. Flash LED ponsel tidak dapat diubah menjadi proyektor video.

## Roadmap
- MVP 1: media fullscreen, koreksi orientasi, pola uji.
- MVP 2: animasi galaxy, nebula, aurora dan visual bergerak.
- MVP 3: preset optik, kontrol fokus, dan pengujian perangkat.
- MVP 4: playlist dan kontrol pemutaran lanjutan.
