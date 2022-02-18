# TUGAS BESAR STRATEGI ALGORITMA
## Pemanfaatan Algoritma Greedy dalam Aplikasi Permainan “Overdrive”

Author:

| NIM       | Nama                     |
| --------- | ------------------------ |
| 13520072  | Jova Andres Riski Sirait |
| 13520144  | Zayd Muhammad Kawakibi Zuhri |
| 13520151  | Rizky Ramadhana P. K. |

```
TUGAS BESAR 1 - STRATEGI ALGORITMA IF2211
Bandung Institute of Technology
```

## Table of Contents
- [General Info](#general-information)
- [Installation](#installation)

## General Information

Overdrive adalah sebuah game yang mempertandingan 2 bot mobil dalam sebuah ajang
balapan. Setiap pemain akan memiliki sebuah bot mobil dan masing-masing bot akan saling
bertanding untuk mencapai garis finish dan memenangkan pertandingan. Agar dapat
memenangkan pertandingan, setiap pemain harus mengimplementasikan strategi tertentu untuk
dapat mengalahkan lawannya.

Alternatif solusi greedy yang dipilih adalah membuat urutan prioritas langkah mana yang harus dieksekusi terlebih dahulu. Urutan prioritas tersebut dieksekusi menurut keadaan pemain ataupun lawan saat ini. Secara garis besar alternatif solusi greedy yang pertama adalah sebagai berikut. Bila mobil rusak berat, maka perbaiki terlebih dahulu. Selanjutnya akan diperiksa bila mobil tidak rusak berat, stok powerups menipis, dan terdapat powerups yang dapat dijangkau maka akan diprioritaskan untuk mengambil powerups tersebut. Bila mobil tidak dalam keadaan rusak berat dan tidak ada powerups yang bisa diambil, maka strategi terbaiknya adalah menghindar dari rintangan yang ada. Bila masih tidak ada rintangan di depan mobil yang ingin dihindari, maka bisa digunakan powerups seperti OIL, TWEET, EMP, ataupun BOOST.


## Installation
#### Before you start <br><br>
Sebelum memulai, pastikan telah memiliki requirement berikut ini.

- [Java 17 or higher](https://www.java.com/en/)
- [Maven](https://maven.apache.org/)

#### Template and Dependencies

* Clone repository:

  ```
  $ git clone https://github.com/rizkyramadhana26/Tubes-Stima-1.git
  ```

* Install bot dengan Maven:
  ```
  Pada tab Maven, pilih folder bot yang ingin diinstall, pilih menu Lifecycle -> Install.
  Hasil kompilasi program akan muncul pada folder target dengan nama "java-sample-bot-jar-with-dependencies.jar".
  ```

* Jalankan game engine:
  ```
  Jalankan run.bat yang ada pada folder utama.
  Dengan menggunakan konfigurasi utama, bot yang dipertandingkan adalah bot pada folder "starter-bot" dan "reference-bot".
  Pengubahan konfigurasi dapat dilakukan pada file "game-runner-config.json".
  ```
  
 * Gunakan visualizer:
  ```
  Untuk memudahkan visualisasi, hasil pertandingan pada folder match-logs dapat diunggah ke visualizer berikut ini.
  ```
  [Entellect Challenge 2020 - Replayer](https://entelect-replay.raezor.co.za)

```
DONE!
```
