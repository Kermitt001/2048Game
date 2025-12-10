import java.util.Random;

public class Game2048 {
    private int[][] board;
    private Random random;
    private final int SIZE = 4;

    public Game2048() {
        board = new int[SIZE][SIZE];
        random = new Random();
        initGame();
    }

    public void initGame() {
        // Clear board
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }
        // Add two initial tiles
        addRandomTile();
        addRandomTile();
    }

    public void addRandomTile() {
        // Check if full first to avoid infinite loop
        boolean full = true;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    full = false;
                    break;
                }
            }
        }
        if (full) return;

        int row, col;
        do {
            row = random.nextInt(SIZE);
            col = random.nextInt(SIZE);
        } while (board[row][col] != 0);

        // 90% chance of 2, 10% chance of 4
        board[row][col] = (random.nextDouble() < 0.9) ? 2 : 4;
    }

    public void printBoard() {
        System.out.println("-----------------------------");
        for (int i = 0; i < SIZE; i++) {
            System.out.print("|");
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    System.out.printf("%6s", " ");
                } else {
                    System.out.printf("%6d", board[i][j]);
                }
                System.out.print("|");
            }
            System.out.println();
            System.out.println("-----------------------------");
        }
    }

    private int score = 0;

    public int[][] getBoard() {
        return board;
    }

    public int getScore() {
        return score;
    }

    private int[] slideMerge(int[] line) {
        int[] newLine = new int[SIZE];
        int insertIndex = 0;
        
        // Step 1: Slide non-zeros to the front/merge
        // ... (Previous logic was a bit messy looking at the comment, 
        // using the simpler clean implementation below)
        
        // Simpler implementation using ArrayList-like logic on array
        int[] temp = new int[SIZE];
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            if (line[i] != 0) temp[count++] = line[i];
        }
        
        // Merge
        int[] result = new int[SIZE];
        int resIndex = 0;
        for (int i = 0; i < count; i++) {
            if (i + 1 < count && temp[i] == temp[i + 1]) {
                int mergedVal = temp[i] * 2;
                result[resIndex++] = mergedVal;
                score += mergedVal; // Update score
                i++; // Skip next
            } else {
                result[resIndex++] = temp[i];
            }
        }
        return result;
    }

    public boolean moveLeft() {
        boolean changed = false;
        for (int i = 0; i < SIZE; i++) {
            int[] oldRow = board[i].clone();
            board[i] = slideMerge(board[i]);
            if (!java.util.Arrays.equals(oldRow, board[i])) changed = true;
        }
        return changed;
    }

    public boolean moveRight() {
        boolean changed = false;
        for (int i = 0; i < SIZE; i++) {
            // Reverse row, slideMerge, reverse back
            int[] row = new int[SIZE];
            for (int j = 0; j < SIZE; j++) row[j] = board[i][SIZE - 1 - j];
            
            int[] newRow = slideMerge(row);
            
            // Check change and copy back reversed
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != newRow[SIZE - 1 - j]) {
                    changed = true;
                    board[i][j] = newRow[SIZE - 1 - j];
                }
            }
        }
        return changed;
    }

    public boolean moveUp() {
        boolean changed = false;
        for (int j = 0; j < SIZE; j++) {
            int[] col = new int[SIZE];
            for (int i = 0; i < SIZE; i++) col[i] = board[i][j];
            
            int[] newCol = slideMerge(col);
            
            for (int i = 0; i < SIZE; i++) {
                if (board[i][j] != newCol[i]) {
                    changed = true;
                    board[i][j] = newCol[i];
                }
            }
        }
        return changed;
    }

    public boolean moveDown() {
        boolean changed = false;
        for (int j = 0; j < SIZE; j++) {
            int[] col = new int[SIZE];
            for (int i = 0; i < SIZE; i++) col[i] = board[SIZE - 1 - i][j]; // Reverse col
            
            int[] newCol = slideMerge(col);
            
            for (int i = 0; i < SIZE; i++) {
                if (board[i][j] != newCol[SIZE - 1 - i]) {
                    changed = true;
                    board[i][j] = newCol[SIZE - 1 - i];
                }
            }
        }
        return changed;
    }

    public boolean canMove() {
        // Check for empty cells
        for(int i=0; i<SIZE; i++)
            for(int j=0; j<SIZE; j++)
                if(board[i][j] == 0) return true;

        // Check for adjacent matches
        for(int i=0; i<SIZE; i++) {
            for(int j=0; j<SIZE; j++) {
                if (j < SIZE - 1 && board[i][j] == board[i][j+1]) return true; // Horizontal match
                if (i < SIZE - 1 && board[i][j] == board[i+1][j]) return true; // Vertical match
            }
        }
        return false;
    }

    public boolean hasWon() {
        for(int i=0; i<SIZE; i++)
            for(int j=0; j<SIZE; j++)
                if(board[i][j] == 2048) return true;
        return false;
    }

    public boolean isGameOver() {
        return !canMove();
    }
}
