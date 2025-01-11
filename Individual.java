import java.util.Random;

public class Individual {
    private int[][] puzzle; // Representasi solusi
    private Puzzle initialPuzzle; // Puzzle awal dengan sel yang sudah ditentukan
    private int size; // Ukuran puzzle
    private double fitness; // Nilai fitness

    // Konstruktor dengan random generator
    public Individual(Puzzle initialPuzzle, Random gen) {
        this.initialPuzzle = initialPuzzle;
        this.size = initialPuzzle.getSize();
        this.puzzle = new int[size][size];
        randomize(gen);
        evaluateFitness();
    }

    // Randomisasi individu
    private void randomize(Random gen) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!initialPuzzle.isPresetCell(i, j)) {
                    puzzle[i][j] = gen.nextInt(2) + 1;  // Randomize hitam (1) dan putih (2)
                } else {
                    puzzle[i][j] = initialPuzzle.getPresetCellValue(i, j);
                }
            }
        }
    }

    // Revisi perhitungan fitness individu
    private void evaluateFitness() {
        fitness = 100;  // Fitness dimulai dengan nilai 100
        int totalCells = size * size;
        int filledCells = 0;
        int blackCells = 0;
        int whiteCells = 0;
    
        // Menghitung jumlah sel yang diwarnai (Hitam dan Putih)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (puzzle[i][j] == 1) { 
                    blackCells++; 
                    filledCells++;
                } else if (puzzle[i][j] == 2) {
                    whiteCells++; 
                    filledCells++;
                }
            }
        }
    
        // Aturan 1 & 2: Cek konektivitas hitam dan putih memakai DFS
        boolean[][] visited = new boolean[size][size];
        int blackGroups = 0;
        int whiteGroups = 0;
    
        // Menentukan grup warna hitam dan putih serta menghitung kelompok
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!visited[i][j]) {
                    if (puzzle[i][j] == 1) {
                        blackGroups++;
                        dfsCount(i, j, 1, visited);
                    } else if (puzzle[i][j] == 2) {
                        whiteGroups++;
                        dfsCount(i, j, 2, visited);
                    }
                }
            }
        }
    
        // Penalti jika hitam dan putih tidak hanya membentuk satu grup
        if (blackGroups > 1) {
            fitness -= 5 * (blackGroups - 1);  // Menurunkan penalti untuk grup hitam
        }
        if (whiteGroups > 1) {
            fitness -= 5 * (whiteGroups - 1);  // Menurunkan penalti untuk grup putih
        }
    
        // Aturan 3: Cek pola 2x2 yang salah
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1; j++) {
                if (puzzle[i][j] == puzzle[i][j + 1] && puzzle[i][j] == puzzle[i + 1][j] && puzzle[i][j] == puzzle[i + 1][j + 1]) {
                    fitness -= 2;  // Mengurangi penalti untuk pola 2x2 yang salah (lebih ringan)
                }
            }
        }
    
        // Penalti keseimbangan hitam dan putih yang terlalu berbeda
        double balancePenalty = Math.abs(blackCells - whiteCells) * 0.5;  // Menurunkan bobot penalti
        fitness -= balancePenalty;
    
        // Bonus jika jumlah hitam dan putih mendekati sama
        if (blackCells == whiteCells) {
            fitness += 3;  // Meningkatkan fitness jika jumlah hitam dan putih setara
        }
    
        // Penyesuaian fitness berdasarkan kesesuaian solusi
        // Tambahkan penalti jika ada lebih banyak sel kosong
        double emptyPenalty = (totalCells - filledCells) * 0.3;
        fitness -= emptyPenalty;
    
        // Batasi fitness agar tidak menjadi negatif atau melebihi 100
        fitness = Math.max(0, Math.min(100, fitness));
    }
    
    

    // Fungsi tambahan untuk pencarian mendalam (DFS) menghitung kelompok yang terhubung
    private int dfsCount(int row, int col, int color, boolean[][] visited) {
        if (row < 0 || col < 0 || row >= size || col >= size || visited[row][col] || puzzle[row][col] != color) {
            return 0;  // keluar jika posisi tidak valid
        }

        visited[row][col] = true;
        int groupSize = 1;

        // Mengeksplorasi tetangga secara ortogonal
        groupSize += dfsCount(row - 1, col, color, visited);  // Atas
        groupSize += dfsCount(row + 1, col, color, visited);  // Bawah
        groupSize += dfsCount(row, col - 1, color, visited);  // Kiri
        groupSize += dfsCount(row, col + 1, color, visited);  // Kanan

        return groupSize;
    }

    // Mutasi individu
    public void mutate(Random gen) {
        int x, y;
        do {
            x = gen.nextInt(size);
            y = gen.nextInt(size);
        } while (initialPuzzle.isPresetCell(x, y)); // Hindari sel yang sudah ditentukan

        // Toggle warna (hitam <-> putih)
        puzzle[x][y] = (puzzle[x][y] == 1) ? 2 : 1;
        evaluateFitness();
    }

    // Crossover antara dua individu
    public Individual crossover(Individual other, Random gen) {
        Individual child = new Individual(initialPuzzle, gen);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!initialPuzzle.isPresetCell(i, j)) {
                    // Gabungkan kedua individu secara acak
                    child.puzzle[i][j] = gen.nextBoolean() ? this.puzzle[i][j] : other.puzzle[i][j];
                } else {
                    child.puzzle[i][j] = initialPuzzle.getPresetCellValue(i, j);
                }
            }
        }
        child.evaluateFitness();
        return child;
    }

    // Mendapatkan nilai fitness
    public double getFitness() {
        return Math.min(100, fitness);  // Membatasi nilai fitness maksimal hingga 100
    }

    // Menampilkan puzzle
    public void printPuzzle() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (puzzle[i][j] == 1) {
                    System.out.print("B ");
                } else if (puzzle[i][j] == 2) {
                    System.out.print("W ");
                } else {
                    System.out.print("_ ");
                }
            }
            System.out.println();
        }
    }
}