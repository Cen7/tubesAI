import java.io.*;
import java.util.*;

public class FileReaderUtil {
    // Membaca puzzle dari file dengan format B, W, _
    public static Puzzle readPuzzleFromFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        // Baca baris pertama untuk mendapatkan ukuran, kesulitan, dan ID
        String[] firstLine = br.readLine().split(" ");
        int size = Integer.parseInt(firstLine[0]);
        String difficulty = firstLine[1];
        int id = Integer.parseInt(firstLine[2]);
        

        // Buat papan puzzle
        int[][] board = new int[size][size];

        // Baca puzzle baris per baris
        for (int i = 0; i < size; i++) {
            String[] row = br.readLine().trim().split(" ");

            // Pastikan jumlah kolom sesuai
            if (row.length != size) {
                throw new IllegalArgumentException(
                        "Jumlah kolom tidak sesuai dengan ukuran puzzle di baris " + (i + 1));
            }

            for (int j = 0; j < size; j++) {
                String cellValue = row[j];
                switch (cellValue) {
                    case "B":
                        board[i][j] = 1; // Black
                        break;
                    case "W":
                        board[i][j] = 2; // White
                        break;
                    case "_":
                        board[i][j] = 0; // Blank
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid cell value: " + cellValue + " di baris " + (i + 1));
                }
            }
        }

        br.close();
        return new Puzzle(size, difficulty, id, board);
    }
    
    public static GeneticAlgorithm readParamsFromFile(String fileName, Random random) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
    
        int populationSize = Integer.parseInt(br.readLine().split(":")[1].trim());
        int generations = Integer.parseInt(br.readLine().split(":")[1].trim());
        double mutationRate = Double.parseDouble(br.readLine().split(":")[1].trim());
        
        br.close();
        return new GeneticAlgorithm(populationSize, generations, mutationRate, random);
    }
}