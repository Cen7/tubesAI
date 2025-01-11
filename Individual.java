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
                // Hanya randomize sel kosong
                if (!initialPuzzle.isPresetCell(i, j)) {
                    // Randomize antara 1 (Black) dan 2 (White)
                    puzzle[i][j] = gen.nextInt(2) + 1;
                } else {
                    // Tetapkan sel yang sudah diatur
                    puzzle[i][j] = initialPuzzle.getPresetCellValue(i, j);
                }
            }
        }
    }

    // revisi perhitungan fitness individu
    private void evaluateFitness() {
        fitness = 100; // nilai fitness awal
        int totalCells = size * size;
        int filledCells = 0;
        int blackCells = 0;
        int whiteCells = 0;
    
        // loop untuk menghitung area yang terisi
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
    
        // aturan 1&2 cek konektivitas hitam & putihnya memakai array
        boolean[][] visited = new boolean[size][size];
        int blackGroups = 0;
        int whiteGroups = 0;
        int largestBlackGroup = 0;
        int largestWhiteGroup = 0;
    
        // menggunakan DFS untuk menghitung kelompok hitam dan putih serta ukuran terbesar
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!visited[i][j]) {
                    if (puzzle[i][j] == 1) { 
                        blackGroups++;
                        int groupSize = dfsCount(i, j, 1, visited);
                        largestBlackGroup = Math.max(largestBlackGroup, groupSize);
                    } else if (puzzle[i][j] == 2) {
                        whiteGroups++;
                        int groupSize = dfsCount(i, j, 2, visited);
                        largestWhiteGroup = Math.max(largestWhiteGroup, groupSize);
                    }
                }
            }
        }
    
        // jika grouping hitam / putih lebih dari satu artinya ada yg terputus
        // kurangi nilai fitnessnya
        if (blackGroups > 1) {
            fitness -= 20 * (blackGroups - 1);  
        }
        if (whiteGroups > 1) {
            fitness -= 20 * (whiteGroups - 1);  
        }
    
        // Menyesuaikan keseimbangan penalti dengan hanya menurunkan penalti pada distribusi hitam dan putih
        double balancePenalty = Math.abs(blackCells - whiteCells) * 1.5;  // Menurunkan penalti agar ada fleksibilitas
        fitness -= balancePenalty;
    
        // Menyempurnakan evaluasi agar fitness mendekati 100 untuk konfigurasi yang tepat
        fitness = Math.max(0, fitness);  // Mencegah fitness menjadi negatif
    }

    private int dfsCount(int x, int y, int color, boolean[][] visited) {
        if (x < 0 || y < 0 || x >= size || y >= size)
            return 0;
        if (visited[x][y] || puzzle[x][y] != color)
            return 0;

        visited[x][y] = true;
        int count = 1;
        count += dfsCount(x + 1, y, color, visited);
        count += dfsCount(x - 1, y, color, visited);
        count += dfsCount(x, y + 1, color, visited);
        count += dfsCount(x, y - 1, color, visited);
        return count;
    }

    // Method mutasi dengan random generator
    public void mutate(Random gen) {
        int x, y;
        do {
            x = gen.nextInt(size);
            y = gen.nextInt(size);
        } while (initialPuzzle.isPresetCell(x, y)); // Hindari mutasi sel yang sudah diatur

        // Toggle antara 1 (Black) dan 2 (White)
        puzzle[x][y] = (puzzle[x][y] == 1) ? 2 : 1;

        // Re-evaluasi fitness setelah mutasi
        evaluateFitness();
    }

    // Method crossover dengan random generator
    public Individual crossover(Individual other, Random gen) {
        Individual child = new Individual(initialPuzzle, gen);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!initialPuzzle.isPresetCell(i, j)) {
                    // Crossover dengan probabilitas 50%
                    child.puzzle[i][j] = gen.nextBoolean() ? this.puzzle[i][j] : other.puzzle[i][j];
                } else {
                    child.puzzle[i][j] = initialPuzzle.getPresetCellValue(i, j);
                }
            }
        }
        child.evaluateFitness();
        return child;
    }

    // return nilai fitness
    public double getFitness() {
        return Math.min(100, fitness);  // Membatasi nilai fitness maksimal hingga 100
    }

    // jangan dihapus, untuk method main panggilnya dari sini
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