public class Puzzle {
    private int[][] board; // Papan puzzle
    private int size;      // Ukuran puzzle
    private String difficulty; // Tingkat kesulitan
    private int id;        // ID Puzzle

    // Konstruktor
    public Puzzle(int size, String difficulty, int id, int[][] board) {
        this.size = size;
        this.difficulty = difficulty;
        this.id = id;
        this.board = board;
    }

    // Getter untuk papan puzzle
    public int[][] getBoard() {
        return board;
    }

    public int getSize() {
        return size;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getId() {
        return id;
    }

    // Tambahan method untuk membantu dalam evaluasi fitness
    public boolean isPresetCell(int row, int col) {
        return board[row][col] != 0;  // 0 adalah sel kosong
    }

    public int getPresetCellValue(int row, int col) {
        return board[row][col];
    }
}