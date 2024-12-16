import java.util.Random;

public class Individual {
    private int[][] puzzle; // Representasi solusi
    private Puzzle initialPuzzle; // Puzzle awal dengan sel yang sudah ditentukan
    private int size;       // Ukuran puzzle
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

    private void evaluateFitness() {
        fitness = 0;
        
        // Periksa aturan larangan 2x2
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1; j++) {
                int color = puzzle[i][j];
                if (color != 0 && color == puzzle[i][j + 1] 
                              && color == puzzle[i + 1][j] 
                              && color == puzzle[i + 1][j + 1]) {
                    // Penalti jika ada blok 2x2 dengan warna seragam
                    fitness -= 10;
                }
            }
        }
    
        // Periksa konektivitas warna
        boolean[][] visited = new boolean[size][size];
        int blackCount = 0, whiteCount = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!visited[i][j]) {
                    int color = puzzle[i][j];
                    if (color != 0) {
                        int count = dfsCount(i, j, color, visited);
                        if (color == 1) blackCount += count;
                        else if (color == 2) whiteCount += count;
                    }
                }
            }
        }
        
        // Jika semua hitam/putih terhubung, tambahkan fitness
        if (blackCount > 0) fitness += blackCount;
        if (whiteCount > 0) fitness += whiteCount;
    }
    private int dfsCount(int x, int y, int color, boolean[][] visited) {
        if (x < 0 || y < 0 || x >= size || y >= size) return 0;
        if (visited[x][y] || puzzle[x][y] != color) return 0;
    
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
                    child.puzzle[i][j] = gen.nextBoolean() ? 
                        this.puzzle[i][j] : other.puzzle[i][j];
                } else {
                    child.puzzle[i][j] = initialPuzzle.getPresetCellValue(i, j);
                }
            }
        }

        child.evaluateFitness();
        return child;
    }

    // Method lainnya tetap sama
    public double getFitness() {
        return fitness;
    }

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