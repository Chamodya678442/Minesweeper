import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MineSweeperFrame extends JFrame {

    private GameBoard board;
    private JButton[][] buttons;

    private JLabel timerLabel;
    private JLabel mineLabel;
    private JLabel statusLabel;

    private Timer timer;
    private int seconds;
    private int flagsPlaced;

    private boolean gameOver;

    private Difficulty currentDifficulty = Difficulty.Easy;

    // Base colors
    private final Color bgColor = new Color(230, 230, 230);
    private final Color revealedColor = new Color(200, 200, 200);
    private final Color mineColor = new Color(255, 100, 100);

    public MineSweeperFrame() {
        setTitle("Mini Mine Sweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        createTopPanelWithTitle();
        startNewGame(currentDifficulty);

        setLocationRelativeTo(null);
        setResizable(true);
        setVisible(true);
    }

    private void createTopPanelWithTitle() {
        // Main top container
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(bgColor);

        // Title label
        JLabel titleLabel = new JLabel("...Mini Mine Sweeper...", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); 

        // Original top panel (timer, mines, status)
        JPanel topPanel = new JPanel();
        topPanel.setBackground(bgColor);

        timerLabel = new JLabel(" Time: 0");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        mineLabel = new JLabel(" Mines: 0");
        mineLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mineLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        statusLabel = new JLabel("Playing");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JButton restartButton = new JButton("Restart");
        restartButton.setFocusPainted(false);
        restartButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        restartButton.addActionListener(e -> startNewGame(currentDifficulty));

 
        JLabel levelLabel = new JLabel("Select Level:");
        levelLabel.setFont(new Font("Arial", Font.BOLD, 16));
        levelLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));

        JComboBox<Difficulty> difficultyBox = new JComboBox<>(Difficulty.values());
        difficultyBox.setFont(new Font("Arial", Font.BOLD, 16));
        difficultyBox.addActionListener(e -> {
            currentDifficulty = (Difficulty) difficultyBox.getSelectedItem();
            startNewGame(currentDifficulty);
        });
    

        // Add components to top panel
        topPanel.add(timerLabel);
        topPanel.add(mineLabel);
        topPanel.add(statusLabel);
        topPanel.add(restartButton);

       
        topPanel.add(levelLabel);          
        topPanel.add(difficultyBox);      

       
        topContainer.add(titleLabel, BorderLayout.NORTH);
        topContainer.add(topPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
    }

    private void startNewGame(Difficulty difficulty) {
        if (timer != null) timer.stop();

        getContentPane().removeAll();
        createTopPanelWithTitle();

        board = new GameBoard(difficulty);
        int size = board.getSize();

        buttons = new JButton[size][size];
        JPanel gridPanel = new JPanel(new GridLayout(size, size, 5, 5));
        gridPanel.setBackground(Color.DARK_GRAY);

        flagsPlaced = 0;
        seconds = 0;
        gameOver = false;

        statusLabel.setText("Playing");
        statusLabel.setForeground(Color.BLUE);

        mineLabel.setText("Mines: " + board.getMineCount());
        timerLabel.setText("Time: 0");

        timer = new Timer(1000, e -> {
            seconds++;
            timerLabel.setText("Time: " + seconds);
        });
        timer.start();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                JButton button = new JButton();
                button.setFont(new Font("Arial", Font.BOLD, 28));
                button.setFocusPainted(false);
                button.setBackground(bgColor);
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                button.setPreferredSize(new Dimension(80, 80));

                buttons[r][c] = button;

                int row = r;
                int col = c;

                button.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (gameOver) return;
                        if (SwingUtilities.isLeftMouseButton(e)) revealCell(row, col);
                        else if (SwingUtilities.isRightMouseButton(e)) toggleFlag(row, col);
                    }

                    public void mouseEntered(MouseEvent e) {
                        if (!buttons[row][col].isEnabled()) return;
                        buttons[row][col].setBackground(new Color(220, 220, 220));
                    }

                    public void mouseExited(MouseEvent e) {
                        if (!buttons[row][col].isEnabled()) return;
                        buttons[row][col].setBackground(bgColor);
                    }
                });

                gridPanel.add(button);
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(900, 900));
        setMinimumSize(new Dimension(700, 700));
        pack();
        revalidate();
        repaint();
    }

    private void revealCell(int r, int c) {
        Cell cell = board.getCell(r, c);

        if (cell.isRevealed() || cell.isFlagged()) return;

        cell.setRevealed(true);
        JButton button = buttons[r][c];
        button.setEnabled(false);

        if (cell.isMine()) {
            button.setText("M");
            button.setBackground(mineColor);
            showAllMines();
            timer.stop();
            gameOver = true;
            statusLabel.setText("Game Over");
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "💥 Game Over!");
            return;
        }

        int count = cell.getAdjacentMines();
        if (count > 0) {
            button.setText(String.valueOf(count));
            setNumberColor(button, count);
            button.setBackground(getCellColor(count));
        } else {
            button.setBackground(revealedColor);
            for (int i = r - 1; i <= r + 1; i++)
                for (int j = c - 1; j <= c + 1; j++)
                    if (i >= 0 && i < board.getSize() && j >= 0 && j < board.getSize())
                        revealCell(i, j);
        }

        checkWin();
    }

    private void toggleFlag(int r, int c) {
        Cell cell = board.getCell(r, c);
        if (cell.isRevealed()) return;

        cell.setFlagged(!cell.isFlagged());

        if (cell.isFlagged()) {
            buttons[r][c].setText("F");
            flagsPlaced++;
        } else {
            buttons[r][c].setText("");
            flagsPlaced--;
        }

        mineLabel.setText(" Mines: " + (board.getMineCount() - flagsPlaced));
    }

    private void showAllMines() {
        for (int r = 0; r < board.getSize(); r++)
            for (int c = 0; c < board.getSize(); c++)
                if (board.getCell(r, c).isMine())
                    buttons[r][c].setText("M");
    }

    private void checkWin() {
        for (int r = 0; r < board.getSize(); r++)
            for (int c = 0; c < board.getSize(); c++) {
                Cell cell = board.getCell(r, c);
                if (!cell.isMine() && !cell.isRevealed()) return;
            }

        timer.stop();
        gameOver = true;
        statusLabel.setText(" You Win!");
        statusLabel.setForeground(new Color(0, 128, 0));
        JOptionPane.showMessageDialog(this, "🏆 You Win!");
    }

    // Number color
    private void setNumberColor(JButton button, int number) {
        switch (number) {
            case 1 -> button.setForeground(Color.BLUE);
            case 2 -> button.setForeground(new Color(0, 128, 0));
            case 3 -> button.setForeground(Color.RED);
            case 4 -> button.setForeground(new Color(128, 0, 128));
            case 5 -> button.setForeground(new Color(255, 140, 0));
            case 6 -> button.setForeground(Color.CYAN);
            default -> button.setForeground(Color.BLACK);
        }
    }

    // Custom background based on adjacent mine count
    private Color getCellColor(int count) {
        return switch (count) {
            case 1 -> new Color(200, 230, 255); 
            case 2 -> new Color(200, 255, 200); 
            case 3 -> new Color(255, 200, 200); 
            case 4 -> new Color(230, 200, 255); 
            case 5 -> new Color(255, 220, 180); 
            case 6 -> new Color(180, 255, 255); 
            case 7 -> new Color(220, 220, 220); 
            case 8 -> new Color(200, 200, 200); 
            default -> revealedColor; 
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MineSweeperFrame::new);
    }
}