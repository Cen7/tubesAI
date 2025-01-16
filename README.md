# tubesAI : Yin-Yang Puzzle Solver

## Deskripsi
Proyek ini adalah solver untuk Yin-Yang Puzzle yang diimplementasikan menggunakan Java dan algoritma genetik. Puzzle dan parameter algoritma dapat diatur melalui file input.

## Cara Menjalankan Kode

1. **Kompilasi kode Java:**
   ```bash
   javac *.java
   ```

2. **Jalankan program:**
   ```bash
   java Main puzzle.txt params.txt
   ```

## Penjelasan File Input

### 1. puzzle.txt
File ini mendefinisikan puzzle Yin-Yang yang akan diselesaikan. Formatnya adalah:

```
6 Hard 6990749
_ _ _ B _ B
_ _ W _ B _
_ _ _ _ _ _
_ _ W _ B _
_ _ _ _ _ B
_ _ _ _ _ _
```

#### Deskripsi :
- **6**: Ukuran puzzle (6x6).
- **Hard**: Tingkat kesulitan puzzle.
- **6990749**: ID unik puzzle yang dapat dicoba pada :[Yin-Yang Puzzle](https://www.puzzle-yin-yang.com/specific.php).
- **_**: sel kosong.
- **W**: sel putih.
- **B**: sel hitam.


### 2. params.txt
File ini mendefinisikan parameter untuk algoritma genetik:

```
PopulationSize: 1000
Generations: 1000
MutationRate: 0.04
```

#### Penjelasan:
- **PopulationSize**: Jumlah individu dalam setiap generasi (contoh: 1000).
- **Generations**: Jumlah generasi yang akan dijalankan (contoh: 1000).
- **MutationRate**: Tingkat mutasi dalam algoritma genetik (contoh: 0.04).
