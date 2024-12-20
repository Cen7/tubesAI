import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Informasi Eksperimen
        // String hardwareSpecs = "Processor: Intel Core i5-9400F, RAM: 16GB, OS:
        // Windows 11";
        long startTime = System.currentTimeMillis();

        // Inisialisasi seed
        Random init = new Random(100);
        long seed = init.nextLong() % 1000; // Menghasilkan seed antara -1000 dan 999
        System.out.println("Seed yang digunakan: " + seed);
        // System.out.println("Spesifikasi Hardware: " + hardwareSpecs);

        // Periksa apakah file input diberikan melalui args
        if (args.length < 2) {
            System.out.println("Gunakan: java Main <file_puzzle> <file_params>");
            return;
        }

        String puzzleFile = args[0];
        String paramsFile = args[1];
        int successfulRuns = 0;
        int totalRuns = 100; // Ubah sesuai jumlah percobaan yang diinginkan

        Random globalRandom = new Random(seed); // Random dengan seed tetap

        Individual bestOverallSolution = null; // Menyimpan solusi terbaik dari semua percobaan

        for (int i = 0; i < totalRuns; i++) {
            try {
                // Baca Puzzle dan Parameter dari file
                Puzzle puzzle = FileReaderUtil.readPuzzleFromFile(puzzleFile);
                GeneticAlgorithm ga = FileReaderUtil.readParamsFromFile(paramsFile, globalRandom);

                // Eksekusi algoritma genetika
                Individual solution = ga.solve(puzzle);

                // Jika solusi valid ditemukan
                if (solution.getFitness() >= GeneticAlgorithm.FITNESS_THRESHOLD) {
                    successfulRuns++;
                    System.out.println("Solusi ditemukan pada percobaan ke-" + (i + 1));
                    solution.printPuzzle();
                }

                // Update solusi terbaik
                if (bestOverallSolution == null || solution.getFitness() > bestOverallSolution.getFitness()) {
                    bestOverallSolution = solution;
                }

            } catch (IOException e) {
                System.err.println("Gagal membaca file: " + e.getMessage());
                break; // Hentikan loop jika file tidak bisa dibaca
            }
        }

        // Hitung waktu eksekusi
        long endTime = System.currentTimeMillis();
        double totalExecutionTime = (endTime - startTime) / 1000.0; // Dalam detik

        // Hasil eksperimen
        System.out.println("===========================================");
        System.out.println("Hasil Eksperimen:");
        System.out.println("Total Percobaan: " + totalRuns);
        System.out.println("Percobaan Berhasil: " + successfulRuns);
        System.out.println("Persentase Keberhasilan: " + (successfulRuns * 100.0 / totalRuns) + "%");
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
