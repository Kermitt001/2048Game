2048 Game - User Manual

Overview
2048 is a single-player puzzle game played on a 4x4 grid. The objective is to slide numbered tiles on a grid to combine them to create a tile with the number 2048.

How It Works
- The Board: A 4x4 grid initiated with two tiles (values 2 or 4).
- Movement: You can move tiles Up, Down, Left, or Right.
- Sliding: When you move, all tiles slide as far as possible in that direction until they hit the border or another tile.
- Merging: If two tiles of the same number collide while moving, they merge into a single tile with the total value of the two tiles that collided (e.g., 2+2=4, 4+4=8).
- New Tiles: After every valid move, a new tile (2 or 4) appears in a random empty spot.

Game End
- Victory: You win when a tile with value 2048 appears on the board.
- Game Over: You lose if the board is full and no legal moves (no adjacent tiles with the same value) are possible.

How to Play

1. Prerequisites
Ensure you have Java installed on your system.

2. Launching the Game
Open your terminal or command prompt, navigate to the game folder, and run:

# Compile (if needed)
javac Main.java Game2048.java GamePanel.java

# Run
java Main

3. Controls
The game is played using the keyboard:
- Up Arrow / W: Move Up
- Down Arrow / S: Move Down
- Left Arrow / A: Move Left
- Right Arrow / D: Move Right

The game interface will update to show your score and the state of the board. Good luck!
