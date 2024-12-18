# Quivus

Quivus is a chess bot designed to provide a challenging opponent for players of all skill levels. Built with advanced algorithms and a graphical interface, Quivus combines strategic depth with an intuitive user experience.

## Features

- **5-Move Lookahead**: Quivus evaluates up to five moves ahead, considering potential counter-moves and responses.
- **Alpha-Beta Pruning**: Optimizes move selection by efficiently pruning unneeded branches in the decision tree, improving performance without sacrificing accuracy.
- **Java Swing UI**: Includes a clean, interactive graphical user interface (GUI) built with Java Swing for playing chess.
- **Puzzle Feature**: Generate random puzzles to practice and improve your gameplay.
- **Move Takeback**: Undo moves to experiment or recover from mistakes.

## Getting Started

### Prerequisites

- **Java Development Kit (JDK)** version 8 or higher
- A Java IDE such as IntelliJ IDEA or Eclipse (optional)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/KurbyDoo/Quivus.git
   ```
2. Navigate to the project directory:
   ```bash
   cd Quivus
   ```
3. Open the project in your preferred Java IDE.
4. Build and run the project using the IDE’s build tools or the following command:
   ```bash
   javac -d out src/**/*.java
   java -cp out Main
   ```

## Usage

- Launch the application to start a game of chess against Quivus.
- Use the graphical interface to make your moves.
- Quivus will respond with optimized moves calculated using its alpha-beta pruning algorithm.
- Use the following **controls** for additional features:

### Controls

- **Click and drag pieces** with the mouse to move them.
- **Right Arrow Key**: Make a random move for the current player.
- **Left Arrow Key**: Undo the previous move.
- **"<" and ">" Keys**: Faster counterparts to the above controls.
- **Up Arrow Key**: Make the "best" move at a depth of 5 (may take upwards of 60 seconds).
- **P Key**: Generate a random puzzle.
- **B Key**: Flip the board.
- **Spacebar**: Reset the board.

## Project Structure

```
/project-root
  ├── src/            # Source code
  ├── res/            # Resources (e.g., icons, images)
  ├── out/            # Compiled output
  ├── Chess V2.iml   # IntelliJ project file
```

## Contributing

Contributions are welcome! If you’d like to enhance Quivus, please:

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature/your-feature
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add your feature"
   ```
4. Push to the branch:
   ```bash
   git push origin feature/your-feature
   ```
5. Submit a pull request.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Acknowledgements

- chess.com for sounds and piece icons
- Java Swing developers for providing the foundation for the graphical user interface

## TODO

- **Add Transposition Tables**: Implement caching of previously evaluated board positions to avoid redundant computations.
- **Compress Piece Objects into Bitmasks**: Optimize piece representation using bitmasks for faster comparisons and memory efficiency.
- **Add Iterative Deepening**: Enhance move time control by using iterative deepening to progressively deepen the search within a fixed time limit.

