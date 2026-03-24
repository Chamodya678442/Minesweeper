public enum Difficulty {

    Easy(8, 10),
    Medium(10, 20),
    Hard(12, 30);

    private final int size;
    private final int mines;

    Difficulty(int size, int mines) {
        this.size = size;
        this.mines = mines;
    }

    public int getSize() {
        return size;
    }

    public int getMines() {
        return mines;
    }
}