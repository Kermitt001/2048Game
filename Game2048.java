import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game2048 {
    private int[][] board;
    private Random random;
    private final int SIZE = 4;
    private int score = 0;
    
    // Animation tracking
    public static class TileAnimation {
        public int r1, c1; // From
        public int r2, c2; // To
        public int value;
        public boolean merged;

        public TileAnimation(int r1, int c1, int r2, int c2, int value, boolean merged) {
            this.r1 = r1; this.c1 = c1;
            this.r2 = r2; this.c2 = c2;
            this.value = value;
            this.merged = merged;
        }
    }
    
    private List<TileAnimation> lastAnimations = new ArrayList<>();

    public Game2048() {
        board = new int[SIZE][SIZE];
        random = new Random();
        initGame();
    }

    public void initGame() {
        score = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }
        addRandomTile();
        addRandomTile();
    }
    
    public List<TileAnimation> getLastAnimations() {
        return lastAnimations;
    }

    public void addRandomTile() {
        List<Integer> empty = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) empty.add(i * SIZE + j);
            }
        }
        
        if (empty.isEmpty()) return;
        
        int idx = empty.get(random.nextInt(empty.size()));
        int row = idx / SIZE;
        int col = idx % SIZE;
        
        board[row][col] = (random.nextDouble() < 0.9) ? 2 : 4;
    }

    // Helper to track moves within a single line (row/col)
    private static class LineMove {
        int oldIndex;
        int newIndex;
        int val;
        boolean merged;
        LineMove(int old, int newIdx, int v, boolean m) {
            oldIndex = old; newIndex = newIdx; val = v; merged = m;
        }
    }
    
    private static class MoveResult {
        int[] newLine;
        List<LineMove> moves;
        int scoreAdded;
    }

    private MoveResult slideMerge(int[] line) {
        MoveResult res = new MoveResult();
        res.newLine = new int[SIZE];
        res.moves = new ArrayList<>();
        res.scoreAdded = 0;
        
        // Track non-zero tiles: value and original index
        List<LineMove> temp = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            if (line[i] != 0) {
                temp.add(new LineMove(i, -1, line[i], false));
            }
        }
        
        int insertIndex = 0;
        for (int i = 0; i < temp.size(); i++) {
            LineMove current = temp.get(i);
            
            if (i + 1 < temp.size()) {
                LineMove next = temp.get(i + 1);
                if (current.val == next.val) {
                    // Merge
                    int newVal = current.val * 2;
                    res.newLine[insertIndex] = newVal;
                    res.scoreAdded += newVal;
                    
                    // Add animations for both merging tiles
                    res.moves.add(new LineMove(current.oldIndex, insertIndex, current.val, false)); // Slide
                    res.moves.add(new LineMove(next.oldIndex, insertIndex, next.val, true));   // Slide & Merge
                    
                    insertIndex++;
                    i++; // Skip next
                    continue;
                }
            }
            // No merge
            res.newLine[insertIndex] = current.val;
            res.moves.add(new LineMove(current.oldIndex, insertIndex, current.val, false));
            insertIndex++;
        }
        
        return res;
    }

    public boolean moveLeft() {
        lastAnimations.clear();
        boolean changed = false;
        for (int i = 0; i < SIZE; i++) {
            int[] oldRow = board[i].clone();
            MoveResult res = slideMerge(oldRow);
            
            board[i] = res.newLine;
            score += res.scoreAdded;
            
            // Map line moves to board coordinates
            for (LineMove lm : res.moves) {
                lastAnimations.add(new TileAnimation(i, lm.oldIndex, i, lm.newIndex, lm.val, lm.merged));
            }
            
            if (!java.util.Arrays.equals(oldRow, board[i])) changed = true;
        }
        return changed;
    }

    public boolean moveRight() {
        lastAnimations.clear();
        boolean changed = false;
        for (int i = 0; i < SIZE; i++) {
            int[] row = new int[SIZE];
            // Get reversed
            for (int j = 0; j < SIZE; j++) row[j] = board[i][SIZE - 1 - j];
            
            MoveResult res = slideMerge(row);
            
            int[] newRow = new int[SIZE];
            for(int j=0; j<SIZE; j++) newRow[j] = res.newLine[j]; // Copy
            
            boolean rowChanged = false;
            // Write back reversed
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != newRow[SIZE - 1 - j]) {
                    rowChanged = true;
                    board[i][j] = newRow[SIZE - 1 - j];
                }
            }
            if (rowChanged) changed = true;
            score += res.scoreAdded;
            
            // Map moves. index k in 'row' corresponds to real column (SIZE-1-k).
            for (LineMove lm : res.moves) {
                int r1 = i, c1 = SIZE - 1 - lm.oldIndex;
                int r2 = i, c2 = SIZE - 1 - lm.newIndex;
                lastAnimations.add(new TileAnimation(r1, c1, r2, c2, lm.val, lm.merged));
            }
        }
        return changed;
    }

    public boolean moveUp() {
        lastAnimations.clear();
        boolean changed = false;
        for (int j = 0; j < SIZE; j++) {
            int[] col = new int[SIZE];
            for (int i = 0; i < SIZE; i++) col[i] = board[i][j];
            
            MoveResult res = slideMerge(col);
            
            for (int i = 0; i < SIZE; i++) {
                if (board[i][j] != res.newLine[i]) {
                    changed = true;
                    board[i][j] = res.newLine[i];
                }
            }
            score += res.scoreAdded;
            
            for (LineMove lm : res.moves) {
                lastAnimations.add(new TileAnimation(lm.oldIndex, j, lm.newIndex, j, lm.val, lm.merged));
            }
        }
        return changed;
    }

    public boolean moveDown() {
        lastAnimations.clear();
        boolean changed = false;
        for (int j = 0; j < SIZE; j++) {
            int[] col = new int[SIZE];
            // Get reversed
            for (int i = 0; i < SIZE; i++) col[i] = board[SIZE - 1 - i][j];
            
            MoveResult res = slideMerge(col);
            
            boolean colChanged = false;
            for (int i = 0; i < SIZE; i++) {
                if (board[i][j] != res.newLine[SIZE - 1 - i]) {
                    colChanged = true;
                    board[i][j] = res.newLine[SIZE - 1 - i];
                }
            }
            if (colChanged) changed = true;
            score += res.scoreAdded;
            
            // Map moves: index k corresponds to real row (SIZE-1-k)
            for (LineMove lm : res.moves) {
                int r1 = SIZE - 1 - lm.oldIndex, c1 = j;
                int r2 = SIZE - 1 - lm.newIndex, c2 = j;
                lastAnimations.add(new TileAnimation(r1, c1, r2, c2, lm.val, lm.merged));
            }
        }
        return changed;
    }

    public int[][] getBoard() { return board; }
    public int getScore() { return score; }

    public boolean canMove() {
        for(int i=0; i<SIZE; i++)
            for(int j=0; j<SIZE; j++)
                if(board[i][j] == 0) return true;
        for(int i=0; i<SIZE; i++) {
            for(int j=0; j<SIZE; j++) {
                if (j < SIZE - 1 && board[i][j] == board[i][j+1]) return true;
                if (i < SIZE - 1 && board[i][j] == board[i+1][j]) return true;
            }
        }
        return false;
    }

    public boolean hasWon() {
        for(int i=0; i<SIZE; i++) for(int j=0; j<SIZE; j++) if(board[i][j] == 2048) return true;
        return false;
    }

    public boolean isGameOver() { return !canMove(); }
    public void printBoard() {
       // Optional console print, removing to keep file clean or keep if needed.
       // Keeping simple.
    }
}
