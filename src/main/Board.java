package main;

import pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;

public class Board extends JPanel implements Runnable {
    final double BILLION = 1000000000;
    final double THOUSAND = 1000;

    public static int TILE_SIZE = 100;
    final int BOARD_SIZE = 8;
//    final int SCREEN_SIZE = TILE_SIZE * (BOARD_SIZE + 1);
    int SCREEN_WIDTH = TILE_SIZE * (BOARD_SIZE + 6);
    int SCREEN_HEIGHT = TILE_SIZE * (BOARD_SIZE + 1);

    int BOARD_X_OFFSET = (SCREEN_WIDTH - (BOARD_SIZE * TILE_SIZE)) / 2 - TILE_SIZE;
    int BOARD_Y_OFFSET = (SCREEN_HEIGHT - (BOARD_SIZE * TILE_SIZE)) / 2;


    public boolean boardFlipped = false;

    ChessBoard mainBoard = new ChessBoard();
//    ChessBoard mainBoard = new ChessBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/ w - - 0 0");
//    ChessBoard mainBoard = new ChessBoard("1rbr2k1/1p3p2/1R1p4/4p2R/2B1P3/2P3P1/P6P/6K1/ w - - 0 0");

    //TODO Fix broken positions
//    ChessBoard mainBoard = new ChessBoard("1r1r2k1/1p3p2/1R2b3/4p2R/2P4P/1B4P1/P7/6K1/ b - - 0 0");
//    ChessBoard mainBoard = new ChessBoard("1r3k2/1p5P/1R3p2/4p1R1/2P5/1B1r2P1/P7/6K1/ w - - 0 0");
//    ChessBoard mainBoard = new ChessBoard("8/1p3k1P/1R3p2/4p2R/B1P5/3r2P1/P7/6K1/ w - - 0 0");
//    ChessBoard mainBoard = new ChessBoard("Q7/pp5p/2kN4/2pp4/6b1/7P/PPPB4/2KR2NR/ b - - 0 0");
//    ChessBoard mainBoard = new ChessBoard("8/p6p/3N4/1Q1p4/2p2B2/2P4P/P1P5/3K2NR/ b - - 0 0");
//    ChessBoard mainBoard = new ChessBoard("8/p6p/3N4/1Q1p4/2p2B2/2k4P/PPP5/3K2NR/ w - - 0 0");
//    ChessBoard mainBoard = new ChessBoard("rnb1k2r/pppp1ppp/7n/1Q1B1q2/7P/2P1b3/PP1B1P1K/RN4NR/ b - - 0 0");
//    ChessBoard mainBoard = new ChessBoard("8/pp5p/1p6/6PP/1k6/6K1/P4P2/8/ w - - 0 0");


    //TODO Test puzzles
//    ChessBoard mainBoard = new ChessBoard("r5rk/5p1p/5R2/4B3/8/8/7P/7K/ w - - 0 0"); // SOL 1.Ra6 f6 2.Bxf6 Rg7 3.Rxa8#
//    ChessBoard mainBoard = new ChessBoard("3r4/pR2N3/2pkb3/5p2/8/2B5/qP3PPP/4R1K1 w - - 1 0"); // SOL Be5+ Kc5 2. Rc1+ Bc4 3. b4#
//    ChessBoard mainBoard = new ChessBoard("1B1K1R2/6P1/4B3/p3pNp1/Rb3k1p/2Pp1p2/3P1N2/8 w - - 1 0"); // SOL Be5+ Kc5 2. Rc1+ Bc4 3. b4#

    //TODO checkmate patterns
//    ChessBoard mainBoard = new ChessBoard("8/1k6/8/8/6R1/7R/8/6K1 w - - 0 1"); // SOL Be5+ Kc5 2. Rc1+ Bc4 3. b4#
//    ChessBoard mainBoard = new ChessBoard("8/8/6k1/8/8/8/K7/3q4/ b - - 0 0"); // SOL Be5+ Kc5 2. Rc1+ Bc4 3. b4#
//    ChessBoard mainBoard = new ChessBoard("k7/8/8/8/8/8/5PRK/7R w - - 0 6"); // SOL Be5+ Kc5 2. Rc1+ Bc4 3. b4#

    KeyHandler keyHandler = new KeyHandler();
    MouseHandler mouseHandler = new MouseHandler();
    Bot bot = new Bot(mainBoard.moveGenerator);

    SoundHandler moveSound = new SoundHandler("move1_trimmed.wav", "move2_trimmed.wav");
    SoundHandler takeSound = new SoundHandler("capture1.wav", "capture2.wav");
    SoundHandler checkSound = new SoundHandler("check1.wav", "check2.wav");
    SoundHandler castleSound = new SoundHandler("castle1.wav", "castle2.wav");
    SoundHandler gameStart = new SoundHandler("game-start.wav");
    SoundHandler gameEnd = new SoundHandler("game-over.wav");


    Thread mainThread;
    Piece heldPiece;
    Coordinate selectedSquare;
    Boolean leftClickDown = false;
    Boolean keysDown = false;
    int lastKeyPressed;
    final int FPS = 200;
    int[][] highlightSquares = new int[8][8];
    Move bestMove = null;
    int keyDelay = 0;

    public Board() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        this.setFocusable(true);

        gameStart.play();
    }

    /**
     * Creates a new board instance with a random fen string picked from the mate_in_two.txt puzzles
     * <p>
     * Pre: File "mate_in_two.txt" exists
     * Post: New puzzle position is generated
     */
    public void generatePuzzle() {
        Random random = new Random();
        int line = random.nextInt(200) * 5  + 8;
        try {
            InputStream fenPuzzle = getClass().getResourceAsStream("/mate_in_two.txt");
            assert fenPuzzle != null;
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(fenPuzzle));
            for (int i = 0; i < line; i++) {
                fileReader.readLine();
            }
            String data = fileReader.readLine();
            System.out.println(data);
            mainBoard = new ChessBoard(data);

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void startThread() {
        mainThread = new Thread(this);
        mainThread.start();
    }

    /**
     * Runs the main game and updates frames
     */
    @Override
    public void run() {
        double drawInterval = BILLION / FPS;
        double delta1 = 0;

        long lastTime = System.nanoTime();
        long currentTime;
        long timePassed = 0;
        int framePassed = 0;

        while (mainThread != null) {
            currentTime = System.nanoTime();
            timePassed += (currentTime - lastTime);
            delta1 += (currentTime - lastTime) / drawInterval;

            lastTime = currentTime;

            if (delta1 >= 1) {
                updateMouseActions();
                updateKeys();
                checkResize();
                repaint();
                delta1--;
                keyDelay--;
                framePassed++;
            }

            if (timePassed >= BILLION) {
//                System.out.println("FPS: " + framePassed);
                framePassed = 0;
                timePassed = 0;
            }
        }
    }

    public void checkResize() {
        if (SCREEN_WIDTH != this.getWidth() || SCREEN_HEIGHT != this.getHeight()) {
//            System.out.println("resizing");
            TILE_SIZE = Math.max(Math.min(this.getWidth() / 14, this.getHeight() / 9), 1);
//            System.out.printf("New tile size = %d", TILE_SIZE);
            SCREEN_WIDTH = this.getWidth();
            SCREEN_HEIGHT = this.getHeight();

            BOARD_X_OFFSET = (SCREEN_WIDTH - (BOARD_SIZE * TILE_SIZE)) / 2 - TILE_SIZE;
            BOARD_Y_OFFSET = (SCREEN_HEIGHT - (BOARD_SIZE * TILE_SIZE)) / 2;

            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    if (mainBoard.getBoardAt(x, y) != null) {
                        Piece curPiece = mainBoard.getBoardAt(x, y);
                        curPiece.updateDisplayCoordinates(x * TILE_SIZE, y * TILE_SIZE);

                    }
                }
            }
        }
    }

    /**
     * Picks a random move from the list of valid moves and simulates a move action
     * <p>
     * Pre: List of moves is not empty
     * Post: A random move is simulated
     */
    public void makeRandomBotMove() {
        unhighlightGrid();
        System.out.println("------------------------------------------------");
        System.out.println("it is turn: " + mainBoard.getTurnCounter());
        System.out.println("it is turn: " + mainBoard.currentColour);

        double totalTime = System.nanoTime();
        bot.makeRandomMove(mainBoard);
        Move lastMove = mainBoard.pastMoves.get(mainBoard.pastMoves.size() - 1);
        if (mainBoard.moveList.size() == 0) {
            gameEnd.play();
            return;
        }
        if (mainBoard.checkInCheck()) checkSound.play();
        else if (lastMove.getType() == Definition.MOVEMENT || lastMove.getType() == Definition.PROMOTE_MOVEMENT) moveSound.play();
        else if (lastMove.getType() == Definition.CAPTURE || lastMove.getType() == Definition.ENPASSANT || lastMove.getType() == Definition.PROMOTE_CAPTURE) takeSound.play();
        else if (lastMove.getType() == Definition.CASTLE_RIGHT || lastMove.getType() == Definition.CASTLE_LEFT) castleSound.play();
        System.out.printf("Took %f to generate all moves\n", (System.nanoTime() - totalTime) / 1_000_000_000);
        System.out.println("------------------------------------------------");
    }

    /**
     * Processes interactions with the key handler and the main board
     * Pre: Keyhandler is not null
     * Post: Key actions execute the desired methods
     */
    public void updateKeys() {
        if (keyDelay > 0) return;
        // PRINT BOARD
        if (keyHandler.checkPressed(KeyEvent.VK_D) && !keysDown && lastKeyPressed == 0) {
            printBoard();
            mainBoard.printFenString();
            bot.evaluateBoard();
            keysDown = true;
            lastKeyPressed = KeyEvent.VK_D;
        }

        if (!keyHandler.checkPressed(KeyEvent.VK_D) && keysDown && lastKeyPressed == KeyEvent.VK_D) {
            keysDown = false;
            lastKeyPressed = 0;
        }

        // GENERATE RANDOM MOVE
        if (keyHandler.checkPressed(KeyEvent.VK_RIGHT) && !keysDown && lastKeyPressed == 0) {
            makeRandomBotMove();
            keysDown = true;
            lastKeyPressed = KeyEvent.VK_RIGHT;
        }

        if (!keyHandler.checkPressed(KeyEvent.VK_RIGHT) && keysDown && lastKeyPressed == KeyEvent.VK_RIGHT) {
            keysDown = false;
            lastKeyPressed = 0;
        }

        // UNDO LAST MOVE
        if (keyHandler.checkPressed(KeyEvent.VK_LEFT) && !keysDown && lastKeyPressed == 0) {
            unhighlightGrid();
            mainBoard.undoLastMove();
            keysDown = true;
            lastKeyPressed = KeyEvent.VK_LEFT;
        }

        if (!keyHandler.checkPressed(KeyEvent.VK_LEFT) && keysDown && lastKeyPressed == KeyEvent.VK_LEFT) {
            keysDown = false;
            lastKeyPressed = 0;
        }

        // EVAL MOVE
        if (keyHandler.checkPressed(KeyEvent.VK_UP) && !keysDown && lastKeyPressed == 0) {
            bestMove = bot.generateEvalMove(mainBoard, mainBoard.currentColour, 5); // MAX DEPTH
            if (bestMove != null) {
                Coordinate moveStart = bestMove.getStartCoordinates();
                Coordinate moveEnd = bestMove.getEndCoordinates();
                mainBoard.processPieceMove(moveStart.getX(), moveStart.getY(), moveEnd.getX(), moveEnd.getY(), bestMove.getType());
                Move lastMove = mainBoard.pastMoves.get(mainBoard.pastMoves.size() - 1);
                if (mainBoard.moveList.size() == 0) {
                    gameEnd.play();
                    return;
                }
                if (mainBoard.checkInCheck()) checkSound.play();
                else if (lastMove.getType() == Definition.MOVEMENT || lastMove.getType() == Definition.PROMOTE_MOVEMENT) moveSound.play();
                else if (lastMove.getType() == Definition.CAPTURE || lastMove.getType() == Definition.ENPASSANT || lastMove.getType() == Definition.PROMOTE_CAPTURE) takeSound.play();
                else if (lastMove.getType() == Definition.CASTLE_RIGHT || lastMove.getType() == Definition.CASTLE_LEFT) castleSound.play();

            }
            keysDown = true;
            lastKeyPressed = KeyEvent.VK_UP;
        }

        if (!keyHandler.checkPressed(KeyEvent.VK_UP) && keysDown && lastKeyPressed == KeyEvent.VK_UP) {
            keysDown = false;
            lastKeyPressed = 0;
        }

        // GENERATE PUZZLE
        if (keyHandler.checkPressed(KeyEvent.VK_P) && !keysDown && lastKeyPressed == 0) {
            unhighlightGrid();
            generatePuzzle();
            keysDown = true;
            lastKeyPressed = KeyEvent.VK_P;
        }

        if (!keyHandler.checkPressed(KeyEvent.VK_P) && keysDown && lastKeyPressed == KeyEvent.VK_P) {
            keysDown = false;
            lastKeyPressed = 0;
        }

        // FLIP BOARD
        if (keyHandler.checkPressed(KeyEvent.VK_B) && !keysDown && lastKeyPressed == 0) {
            boardFlipped = !boardFlipped;
            keysDown = true;
            lastKeyPressed = KeyEvent.VK_B;
        }

        if (!keyHandler.checkPressed(KeyEvent.VK_B) && keysDown && lastKeyPressed == KeyEvent.VK_B) {
            keysDown = false;
            lastKeyPressed = 0;
        }

        // RESET BOARD
        if (keyHandler.checkPressed(KeyEvent.VK_SPACE) && !keysDown && lastKeyPressed == 0) {
            mainBoard = new ChessBoard();
            keysDown = true;
            lastKeyPressed = KeyEvent.VK_SPACE;
        }

        if (!keyHandler.checkPressed(KeyEvent.VK_SPACE) && keysDown && lastKeyPressed == KeyEvent.VK_SPACE) {
            keysDown = false;
            lastKeyPressed = 0;
        }

        // SPEED FORWARD
        if (keyHandler.checkPressed(KeyEvent.VK_PERIOD) && !keysDown && lastKeyPressed == 0) {
            keyDelay = 10;
            makeRandomBotMove();
        }

        // SPEED BACKWARDS
        if (keyHandler.checkPressed(KeyEvent.VK_COMMA) && !keysDown && lastKeyPressed == 0) {
            keyDelay = 10;
            mainBoard.undoLastMove();
        }
    }

    /**
     * Processes mouse input and output interactions with the main board
     * <p>
     * Pre: The mouse is within the frame
     * Post: Mouse actions execute the desired functions
     */
    public void updateMouseActions() {
        int mouseX = mouseHandler.getX() - BOARD_X_OFFSET, mouseY = mouseHandler.getY() - BOARD_Y_OFFSET;

        if (boardFlipped) {
            mouseX =  (TILE_SIZE * BOARD_SIZE) - mouseX;
            mouseY =  (TILE_SIZE * BOARD_SIZE) - mouseY;
        }

        int boardX = mouseX / TILE_SIZE, boardY = mouseY / TILE_SIZE;

        if (!ChessBoard.InBounds(boardX, boardY)) return;
        if (mouseHandler.leftPressed() && !leftClickDown) {
            leftClickDown = true;
            if (highlightSquares[boardX][boardY] != 0) {
                proccessMouseMove(selectedSquare.getX(), selectedSquare.getY(), boardX, boardY);

            } else {
                selectedSquare = new Coordinate(boardX, boardY);

                heldPiece = mainBoard.getBoardAt(boardX, boardY);

                selectPiece(selectedSquare);

            }

        }

        if (heldPiece != null) {
            heldPiece.updateDisplayCoordinates(mouseX - (TILE_SIZE / 2), mouseY - (TILE_SIZE / 2));
        }

        if (!mouseHandler.leftPressed() && leftClickDown) {
            leftClickDown = false;

            if (heldPiece != null && ChessBoard.InBounds(boardX, boardY)) {
                if (highlightSquares[boardX][boardY] != 0) {
                    proccessMouseMove(selectedSquare.getX(), selectedSquare.getY(), boardX, boardY);
                }
                else heldPiece.updateBoardCoordinates(selectedSquare);

                heldPiece = null;
            }
        }
    }

    /**
     * Makes a move given the start and end coordinates
     * <p>
     * Pre: A valid move exists between these coordiates
     * Post: The main board is updated and the current player switches
     *
     * @param startX the starting x location of a move
     * @param startY the starting y location of a move
     * @param endX the ending x location of a move
     * @param endY the ending y location of a move
     */
    public void proccessMouseMove(int startX, int startY, int endX, int endY) {
        System.out.printf("highlighted squares = %d\n", highlightSquares[startX][startY]);
        Move move = mainBoard.processPieceMove(startX, startY, endX, endY, highlightSquares[endX][endY]);
        unhighlightGrid();

        double startTime = System.nanoTime();
        if (mainBoard.moveList.size() == 0) {
            gameEnd.play();
            return;
        }
        if (mainBoard.checkInCheck()) checkSound.play();
        else if (move.getType() == Definition.MOVEMENT || move.getType() == Definition.PROMOTE_MOVEMENT) moveSound.play();
        else if (move.getType() == Definition.CAPTURE || move.getType() == Definition.ENPASSANT || move.getType() == Definition.PROMOTE_CAPTURE) takeSound.play();
        else if (move.getType() == Definition.CASTLE_RIGHT || move.getType() == Definition.CASTLE_LEFT) castleSound.play();
        System.out.printf("Took %f to process audio\n", (System.nanoTime() - startTime) / 1_000_000_000);
    }

    /**
     * Unhighlights the highlighted squares on the board
     * <p>
     * Pre: Highlighted squares has values != 0
     * Post: All values are set to 0
     */
    public void unhighlightGrid() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                highlightSquares[x][y] = 0;
            }
        }
        bestMove = null;
    }

    /**
     * Makes a piece at the given coordinates the active piece
     * <p>
     * Pre: A piece exists at the given coordinates
     * Post: Selected piece is no longer equal to null
     * @param pieceCoordinates The coordinates of the piece to select
     */
    public void selectPiece(Coordinate pieceCoordinates) {
        unhighlightGrid();
        mainBoard.getMovesAt(pieceCoordinates, highlightSquares);
    }

    /**
     * Outputs the board position to the console and the current highlights
     */
    public void printBoard() {
        mainBoard.printBoard();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                System.out.printf("%10s ", highlightSquares[x][y]);
            }
            System.out.println();
        }
    }

    /**
     * Activates the draw method for all visual elements of the board
     * <p>
     * @param g2d inherinted from the paint component method
     */
    public void draw(Graphics2D g2d) {
        drawGrid(g2d);
        drawPieces(g2d);
        drawText(g2d);
    }

    /**
     * Draws the background grid times and the highlighted squares
     * <p>
     * @param g2d inherited from paint component
     */
    public void drawGrid(Graphics2D g2d) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if ((x + y) % 2 == 0) g2d.setColor(new Color(144, 169, 183));
                else g2d.setColor(new Color(82, 91, 118));
                if (boardFlipped) {
                    g2d.fillRect(((7 - x) * TILE_SIZE) +  + BOARD_X_OFFSET, ((7 - y) * TILE_SIZE) + BOARD_Y_OFFSET, TILE_SIZE, TILE_SIZE);

                } else {
                    g2d.fillRect((x * TILE_SIZE) + BOARD_X_OFFSET, (y * TILE_SIZE) + BOARD_Y_OFFSET, TILE_SIZE, TILE_SIZE);
                }

                if (highlightSquares[x][y] == 0) g2d.setColor(new Color(0, 0, 0, 0));
                else if (highlightSquares[x][y] == Definition.MOVEMENT) g2d.setColor(new Color(0, 255, 0, 70));
                else if (highlightSquares[x][y] == Definition.CAPTURE) g2d.setColor(new Color(255, 0, 0, 70));
                else g2d.setColor(new Color(0, 0, 255, 70));
                if (boardFlipped) {
                    g2d.fillRect(((7 - x) * TILE_SIZE) + BOARD_X_OFFSET, ((7 - y) * TILE_SIZE) + BOARD_Y_OFFSET, TILE_SIZE, TILE_SIZE);

                } else {
                    g2d.fillRect((x * TILE_SIZE) + BOARD_X_OFFSET, (y * TILE_SIZE) + BOARD_Y_OFFSET, TILE_SIZE, TILE_SIZE);
                }

            }
        }

        if (bestMove != null) {
            g2d.setColor(new Color(255, 0, 255, 80));
            if (boardFlipped) {
                g2d.fillRect(((7 - bestMove.getStartCoordinates().getX()) * TILE_SIZE) + BOARD_X_OFFSET, ((7 - bestMove.getStartCoordinates().getY()) * TILE_SIZE) + BOARD_Y_OFFSET, TILE_SIZE, TILE_SIZE);
                g2d.fillRect(((7 - bestMove.getEndCoordinates().getX()) * TILE_SIZE) + BOARD_X_OFFSET, ((7 - bestMove.getEndCoordinates().getY()) * TILE_SIZE) + BOARD_Y_OFFSET, TILE_SIZE, TILE_SIZE);

            } else {
                g2d.fillRect((bestMove.getStartCoordinates().getX() * TILE_SIZE) + BOARD_X_OFFSET, (bestMove.getStartCoordinates().getY() * TILE_SIZE) + BOARD_Y_OFFSET, TILE_SIZE, TILE_SIZE);
                g2d.fillRect((bestMove.getEndCoordinates().getX() * TILE_SIZE) + BOARD_X_OFFSET, (bestMove.getEndCoordinates().getY() * TILE_SIZE) + BOARD_Y_OFFSET, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    /**
     * Loops through all the pieces and draws them to the screen
     * <p>
     * @param g2d inherited from paint componenet
     */
    public void drawPieces(Graphics2D g2d) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (mainBoard.getBoardAt(x, y) != null) {
                    if (boardFlipped) {
                        mainBoard.getBoardAt(x, y).drawReversed(g2d, BOARD_X_OFFSET, BOARD_Y_OFFSET);

                    } else {
                        mainBoard.getBoardAt(x, y).draw(g2d, BOARD_X_OFFSET, BOARD_Y_OFFSET);
                    }
                }
            }
        }
        if (heldPiece != null) {
            if (boardFlipped) {
                heldPiece.drawReversed(g2d, BOARD_X_OFFSET, BOARD_Y_OFFSET);

            } else {
                heldPiece.draw(g2d, BOARD_X_OFFSET, BOARD_Y_OFFSET);

            }
        }
    }

    /**
     * Draws the instruction and displays checkmate
     * <p>
     * @param g2d inherited from paint component
     */
    public void drawText(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255));
        g2d.setFont(new Font("Calibri", Font.BOLD, TILE_SIZE));
        int textXOffset = (int) (SCREEN_WIDTH - (TILE_SIZE * 2 + BOARD_X_OFFSET * 0.9));
        int textYOffset = BOARD_Y_OFFSET + (int)(TILE_SIZE * 0.3);

        if (mainBoard.moveList.size() == 0) {
            if (mainBoard.checkInCheck()) {
                g2d.drawString("CHECKMATE", SCREEN_WIDTH / 2 - (int)(TILE_SIZE * 3.6) , SCREEN_HEIGHT / 2);

            } else {
                g2d.drawString("STALEMATE", SCREEN_WIDTH / 2 - (int)(TILE_SIZE * 3.6) , SCREEN_HEIGHT / 2);

            }
        }
        g2d.setFont(new Font("Calibri", Font.BOLD, TILE_SIZE / 2));


        g2d.drawString("Controls: ", textXOffset , textYOffset);
        g2d.setFont(new Font("Calibri", Font.BOLD, TILE_SIZE / 4));

        g2d.drawString("Click and drag pieces with", textXOffset , textYOffset + (TILE_SIZE) / 3);
        g2d.drawString("the mouse to move pieces", textXOffset , textYOffset + (TILE_SIZE * 2) / 3);

        g2d.drawString("Use the right arrow key to make a", textXOffset , textYOffset + (TILE_SIZE * 4) / 3);
        g2d.drawString("random move for the current player", textXOffset , textYOffset + (TILE_SIZE * 5) / 3);

        g2d.drawString("Use the left arrow key to ", textXOffset , textYOffset + (TILE_SIZE * 7) / 3);
        g2d.drawString("undo the previous move", textXOffset , textYOffset + (TILE_SIZE * 8) / 3);

        g2d.drawString("The \",\" and \".\" keys (\"<\", \">\")", textXOffset , textYOffset + (TILE_SIZE * 10) / 3);
        g2d.drawString("are the faster counterparts ", textXOffset , textYOffset + (TILE_SIZE * 11) / 3);
        g2d.drawString("of the above controls", textXOffset , textYOffset + (TILE_SIZE * 12) / 3);

        g2d.drawString("Use the up arrow key to make", textXOffset , textYOffset + (TILE_SIZE * 14) / 3);
        g2d.drawString("the \"best\" move at a depth of 5", textXOffset , textYOffset + (TILE_SIZE * 15) / 3);
        g2d.drawString("(may take upwards of 60 seconds)", textXOffset , textYOffset + (TILE_SIZE * 16) / 3);

        g2d.drawString("Press the P key to generate", textXOffset , textYOffset + (TILE_SIZE * 18) / 3);
        g2d.drawString("a random puzzle", textXOffset , textYOffset + (TILE_SIZE * 19) / 3);

        g2d.drawString("Press the B key to flip the board", textXOffset , textYOffset + (TILE_SIZE * 21) / 3);

        g2d.drawString("Press spacebar to reset the board", textXOffset , textYOffset + (TILE_SIZE * 23) / 3);
    }

    /**
     * Inherited from JFrame
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        draw(g2d);

        g2d.dispose();
    }
}
