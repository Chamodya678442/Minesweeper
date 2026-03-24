import java.util.Random;

public class GameBoard {

    private int size;
    private int mines;
    private Cell[][] board;

    public GameBoard(Difficulty difficulty) {
        this.size = difficulty.getSize();
        this.mines = difficulty.getMines();

        board = new Cell[size][size];
        initializeBoard();
        placeMines();
        calculateAdjacents();
    }

    private void initializeBoard() {
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                board[r][c] = new Cell();
    }

    private void placeMines() {
        Random rand = new Random();
        int count = 0;

        while (count < mines) {
            int r = rand.nextInt(size);
            int c = rand.nextInt(size);

            if (!board[r][c].isMine()) {
                board[r][c].setMine(true);
                count++;
            }
        }
    }

    private void calculateAdjacents() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {

                if (!board[r][c].isMine()) {
                    board[r][c].setAdjacentMines(
                            countAdjacent(r, c));
                }
            }
        }
    }

    private int countAdjacent(int row, int col) {
        int count = 0;

        for (int r = row - 1; r <= row + 1; r++)
            for (int c = col - 1; c <= col + 1; c++)
                if (r >= 0 && r < size && c >= 0 && c < size)
                    if (board[r][c].isMine())
                        count++;

        return count;
    }

    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    public int getSize() {
        return size;
    }

    public int getMineCount() {
        return mines;
    }
}