import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Informasi Eksperimen
        long startTime = System.currentTimeMillis();

        // Seed tetap untuk eksperimen yang dapat direproduksi
        long seed = -762; // Seed yang digunakan untuk seluruh random number
        Random globalRandom = new Random(seed); // Random dengan seed tetap

        // Cetak seed yang digunakan
        System.out.println("Seed yang digunakan: " + seed);

        // Periksa apakah file input diberikan melalui args
        if (args.length < 2) {
            System.out.println("Gunakan: java Main <file_puzzle> <file_params>");
            return;
        }

        String puzzleFile = args[0];
        String paramsFile = args[1];
        int totalRuns = 300; // Jumlah percobaan yang diinginkan

        Individual bestOverallSolution = null; // Menyimpan solusi terbaik dari semua percobaan
        double totalFitness = 0; // Akumulator nilai fitness total

        // Baca parameter GA di luar loop untuk ditampilkan
        GeneticAlgorithm ga = null;
        Puzzle puzzle = null;
        try {
            // Baca puzzle terlebih dahulu untuk mendapatkan ID
            puzzle = FileReaderUtil.readPuzzleFromFile(puzzleFile);
            System.out.println("Puzzle ID : " + puzzle.getId());
            System.out.println("Difficulity : " + puzzle.getDifficulty());
            ga = FileReaderUtil.readParamsFromFile(paramsFile, globalRandom);
            // Print parameter GA
            System.out.println("Population Size : " + ga.getPopulationSize());
            System.out.println("Generation : " + ga.getGenerations());
            System.out.println("Mutation Rate : " + ga.getMutationRate());
        } catch (IOException e) {
            System.err.println("Gagal membaca file parameter: " + e.getMessage());
            return;
        }

        for (int i = 0; i < totalRuns; i++) {
            try {
                // Baca Puzzle dari file
                puzzle = FileReaderUtil.readPuzzleFromFile(puzzleFile);

                // Eksekusi algoritma genetika
                Individual solution = ga.solve(puzzle);

                // Tambahkan nilai fitness ke total
                totalFitness += solution.getFitness();

                // Update solusi terbaik
                if (bestOverallSolution == null || solution.getFitness() > bestOverallSolution.getFitness()) {
                    bestOverallSolution = solution;
                }
            } catch (IOException e) {
                System.err.println("Gagal membaca file: " + e.getMessage());
                break;
            }
        }

        // [Rest of the code remains the same...]
        // Hitung waktu eksekusi
        long endTime = System.currentTimeMillis();
        double totalExecutionTime = (endTime - startTime) / 1000.0; // Dalam detik

        // Hitung persentase keberhasilan berdasarkan solusi terbaik
        double successRate = 0;
        if (bestOverallSolution != null) {
            double fitness = bestOverallSolution.getFitness();
            double minFitness = -200.0; // Nilai minimum fitness
            double maxFitness = 100.0; // Nilai maksimum fitness

            // Normalisasi nilai fitness ke skala 0-100
            successRate = ((fitness - minFitness) / (maxFitness - minFitness)) * 100.0;

            // Pastikan nilai berada di rentang 0-100
            successRate = Math.max(0, Math.min(successRate, 100));
        }

        // Hasil eksperimen
        System.out.println("===========================================");
        System.out.println("Hasil Eksperimen:");
        System.out.println("Total Percobaan: " + totalRuns);
        System.out.println("Rata-rata Fitness: " + (totalFitness / totalRuns));
        System.out.println("Persentase Keberhasilan: " + successRate + "%");
        System.out.println("Waktu Eksekusi: " + totalExecutionTime + " detik");

        // Tampilkan solusi terbaik yang ditemukan
        if (bestOverallSolution != null) {
            System.out.println("===========================================");
            System.out.println("Solusi Terbaik yang Ditemukan:");
            bestOverallSolution.printPuzzle();
            System.out.println("Fitness: " + bestOverallSolution.getFitness());
        } else {
            System.out.println("Tidak ada solusi yang ditemukan.");
        }
    }
}