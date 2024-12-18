package main;

import pieces.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ChessBoard {
    private int turnCounter = 0;
    final int TILE_SIZE = 75;
    public MoveGenerator moveGenerator = new MoveGenerator();
    public final ArrayList<Move> pastMoves = new ArrayList<>();
    private final ArrayList<Piece> pastPieces = new ArrayList<>();
    private Piece[][] board;
    public ArrayList<Move> moveList;
    public HashMap<Integer, HashMap<Integer, ArrayList<Piece>>> pieces;

    public int currentColour;
    public int boardValue = 0;
    public int numberOfPieces = 0;

    /**
     * Creates a new instance of a board string with the default starting location
     */
    public ChessBoard() {
        board = new Piece[8][8];
        pieces = new HashMap<>();
        pieces.put(Definition.WHITE, new HashMap<>());
        pieces.put(Definition.BLACK, new HashMap<>());

        for (int i = 0; i < 6; i++) {
            pieces.get(Definition.WHITE).put(i, new ArrayList<Piece>());
            pieces.get(Definition.BLACK).put(i, new ArrayList<Piece>());
        }

        createPiece(7, 7, Definition.WHITE, Definition.ROOK);
        createPiece(0, 7, Definition.WHITE, Definition.ROOK);
        createPiece(0, 0, Definition.BLACK, Definition.ROOK);
        createPiece(7, 0, Definition.BLACK, Definition.ROOK);

        createPiece(2, 7, Definition.WHITE, Definition.BISHOP);
        createPiece(5, 7, Definition.WHITE, Definition.BISHOP);
        createPiece(2, 0, Definition.BLACK, Definition.BISHOP);
        createPiece(5, 0, Definition.BLACK, Definition.BISHOP);

        createPiece(1, 7, Definition.WHITE, Definition.KNIGHT);
        createPiece(6, 7, Definition.WHITE, Definition.KNIGHT);
        createPiece(1, 0, Definition.BLACK, Definition.KNIGHT);
        createPiece(6, 0, Definition.BLACK, Definition.KNIGHT);

        createPiece(3, 7, Definition.WHITE, Definition.QUEEN);
        createPiece(3, 0, Definition.BLACK, Definition.QUEEN);
        createPiece(4, 7, Definition.WHITE, Definition.KING);
        createPiece(4, 0, Definition.BLACK, Definition.KING);

        for (int i = 0; i < 8; i++) {
            createPiece(i, 6, Definition.WHITE, Definition.PAWN);
            createPiece(i, 1, Definition.BLACK, Definition.PAWN);
        }

        currentColour = Definition.WHITE;
        pastMoves.add(null);
        moveList = moveGenerator.generateMoves(this, false);
    }

    /**
     * Creates a new board with a starting location dictated by the fen string
     * <p>
     * @param fen valid string in Forsyth–Edwards Notation
     */
    public ChessBoard(String fen) {

        //FIXME ADD REST OF FEN STRING
        board = new Piece[8][8];
        pieces = new HashMap<Integer, HashMap<Integer, ArrayList<Piece>>>();
        pieces.put(Definition.WHITE, new HashMap<Integer, ArrayList<Piece>>());
        pieces.put(Definition.BLACK, new HashMap<Integer, ArrayList<Piece>>());

        for (int i = 0; i < 6; i++) {
            pieces.get(Definition.WHITE).put(i, new ArrayList<Piece>());
            pieces.get(Definition.BLACK).put(i, new ArrayList<Piece>());
        }

        String[] fenSplit = fen.split(" ");
        String[] boardFen = fenSplit[0].split("/");

        for (int y = 0; y < 8; y++) {
            String current = boardFen[y];
            int x = 0;
            for (int i = 0; i < current.length(); i++) {
                if (Character.isDigit(current.charAt(i))) {
                    for (int z = 0; z < Integer.parseInt(String.valueOf(current.charAt(i))); z++) {
                        board[x][y] = null;
                        x++;
                    }
                } else {
                    int tempColour = Character.isUpperCase(current.charAt(i)) ? Definition.WHITE : Definition.BLACK;
                    switch (current.toLowerCase().charAt(i)) {
                        case 'p': createPiece(x, y, tempColour, Definition.PAWN); break;
                        case 'n': createPiece(x, y, tempColour, Definition.KNIGHT); break;
                        case 'b': createPiece(x, y, tempColour, Definition.BISHOP); break;
                        case 'r': createPiece(x, y, tempColour, Definition.ROOK); break;
                        case 'q': createPiece(x, y, tempColour, Definition.QUEEN); break;
                        case 'k': createPiece(x, y, tempColour, Definition.KING); break;

                    }
                    x++;
                }
            }
        }

        if (fenSplit[1].equals("w")) {
            currentColour = Definition.WHITE;
            turnCounter = 0;
        } else {
            currentColour = Definition.BLACK;
            turnCounter = 1;
        }

        if (fenSplit[2].equals("-")) {
            pieces.get(Definition.WHITE).get(Definition.KING).get(0).numberOfTimesMoved++;
            pieces.get(Definition.BLACK).get(Definition.KING).get(0).numberOfTimesMoved++;

        }
        pastMoves.add(null);
        moveList = moveGenerator.generateMoves(this, false);
    }

    /**
     * Undos the last board move
     */
    public void undoLastMove() {
        double startTime = System.nanoTime();
        if (pastMoves.size() == 1) return;

        Move lastMove = pastMoves.get(pastMoves.size() - 1);
        int startX = lastMove.getEndCoordinates().getX();
        int startY = lastMove.getEndCoordinates().getY();
        int endX = lastMove.getStartCoordinates().getX();
        int endY = lastMove.getStartCoordinates().getY();
        int lastType = lastMove.getType();

        Piece pieceToMove = board[startX][startY];
        if (pieceToMove == null) {
            printBoard();
            System.out.println("BOARD STATE");
            System.out.printf("trying to access piece at (%d, %d)\n", startX, startY);
            System.out.printf("Last move = %s\n", lastMove.getText());
            printFenString();
        }

            // TODO clean up
        assert pieceToMove != null;
        undoMovePiece(lastMove, pieceToMove, lastType);
            // TO HERE

        turnCounter--;
        currentColour = turnCounter % 2 == 0 ? Definition.WHITE : Definition.BLACK;
        moveList = moveGenerator.generateMoves(this, false);
    }

    /**
     * Undo's a pieces movement given the move
     * <p>
     * @param pastMove past move is a valid move
     * @param pieceMoved piece moved was the last piece moved
     * @param pastType past move type is a valid type
     */
    private void undoMovePiece(Move pastMove, Piece pieceMoved, int pastType) {
        pieceMoved.numberOfTimesMoved--;
        pastMoves.remove(pastMoves.size() - 1);
        if (pastType != Definition.MOVEMENT && pastType != Definition.CAPTURE) {
            undoSpecialMove(pastMove, pastType);
        }

        board[pastMove.getEndCoordinates().getX()][pastMove.getEndCoordinates().getY()] = null;

        if (pastType == Definition.CAPTURE || pastType == Definition.PROMOTE_CAPTURE) {
            Piece toReplace = pastPieces.get(pastPieces.size() - 1);
            pastPieces.remove(pastPieces.size() - 1);
            createPiece(toReplace);
        }

        board[pastMove.getStartCoordinates().getX()][pastMove.getStartCoordinates().getY()] = pieceMoved;

        pieceMoved.updateBoardCoordinates(pastMove.getStartCoordinates());

    }

    /**
     * Undo's a special move such as castling, en passant, promotion
     * <p>
     * @param lastMove last move is a valid move
     * @param lastMoveType last move type
     */
    private void undoSpecialMove(Move lastMove, int lastMoveType) {
        if (lastMoveType == Definition.ENPASSANT) {
            Piece passedPiece = pastPieces.get(pastPieces.size() - 1);
            pastPieces.remove(pastPieces.size() - 1);
            board[lastMove.getEndCoordinates().getX()][lastMove.getStartCoordinates().getY()] = passedPiece;


        } else if (lastMoveType == Definition.CASTLE_RIGHT) {
            int rookX = 5, rookY = currentColour == Definition.BLACK ? 7 : 0;
            Move lastLastMove = pastMoves.get(pastMoves.size() - 1);
            undoMovePiece(lastLastMove, board[rookX][rookY], Definition.MOVEMENT);

        } else if (lastMoveType == Definition.CASTLE_LEFT) {
            int rookX = 3, rookY = currentColour == Definition.BLACK ? 7 : 0;
            Move lastLastMove = pastMoves.get(pastMoves.size() - 1);
            undoMovePiece(lastLastMove, board[rookX][rookY], Definition.MOVEMENT);

        } else if (lastMoveType == Definition.PROMOTE_MOVEMENT || lastMoveType == Definition.PROMOTE_CAPTURE) {
            getBoardAt(lastMove.getEndCoordinates().getX(), lastMove.getEndCoordinates().getY()).undoPromote();

        } else {
            throw new IllegalStateException("Unknown Move Type: " + lastMoveType);
        }
    }

    /**
     * Alternate method of moving a piece using x and y coordinates instead of moves
     * <p>
     * @param startX in bounds
     * @param startY in bounds
     * @param endX in bounds
     * @param endY in bounds
     * @param moveType is valid move type
     * @return last move done
     */
    public Move processPieceMove(int startX, int startY, int endX, int endY, int moveType) {
        return processPieceMove(new Move(startX, startY, endX, endY, moveType));
    }

    public Move processPieceMove(Move m) {

//        printBoard();
//        System.out.printf("Moving %s from %s -> %s, type = %d\n", board[m.getStartCoordinates().getX()][m.getStartCoordinates().getY()].getColourID(), newMove.getStartCoordinates().getText(), newMove.getEndCoordinates().getText(), m.getType());

        Piece pieceToMove = board[m.getStartCoordinates().getX()][m.getStartCoordinates().getY()];
        movePiece(m, pieceToMove, m.getType());

        turnCounter++;
        currentColour = turnCounter % 2 == 0 ? Definition.WHITE : Definition.BLACK;
        pastMoves.add(m);

        moveList = moveGenerator.generateMoves(this, false);

        return pastMoves.get(pastMoves.size() - 1);
    }

    /**
     * Highlights the valids moves from a given square
     * <p>
     * @param square in bounds
     * @param gridToHighlight all values equal to 0
     */
    public void getMovesAt(Coordinate square, int[][] gridToHighlight) {
        if (board[square.getX()][square.getY()] != null) System.out.printf("selected %s at %s pinned = %s)\n",
                Definition.ConvertIdToString(board[square.getX()][square.getY()].getColourID()), square.getText(), board[square.getX()][square.getY()].isPinned());

        for (Move move: moveList) {
            if (Objects.equals(move.getStartCoordinates().getText(), square.getText())) {
                gridToHighlight[move.getEndCoordinates().getX()][move.getEndCoordinates().getY()] = move.getType();
            }
        }
    }

    /**
     * Processes moving pieces within the grid
     * <p>
     * @param move is valid move
     * @param pieceToMove is valid piece and is not null
     * @param moveType is valid move type
     */
    private void movePiece(Move move, Piece pieceToMove, int moveType) {
         pieceToMove.numberOfTimesMoved++;
        if (moveType != Definition.MOVEMENT && moveType != Definition.CAPTURE) {
            processSpecialMove(move, moveType);
        }

        board[move.getStartCoordinates().getX()][move.getStartCoordinates().getY()] = null;

        Piece toRemove = board[move.getEndCoordinates().getX()][move.getEndCoordinates().getY()];
        if (toRemove != null) {
           removePiece(toRemove);
        }

        board[move.getEndCoordinates().getX()][move.getEndCoordinates().getY()] = pieceToMove;

        pieceToMove.updateBoardCoordinates(move.getEndCoordinates());
    }

    /**
     * Handles special piece movement castling, en passant, promotion
     * <p>
     * @param move is a valid move
     * @param moveType is a special move
     */
    public void processSpecialMove(Move move, int moveType) {
        if (moveType == Definition.ENPASSANT) {
            Piece passedPiece = board[move.getEndCoordinates().getX()][move.getStartCoordinates().getY()];
            board[move.getEndCoordinates().getX()][move.getStartCoordinates().getY()] = null;
            removePiece(passedPiece);

        } else if (moveType == Definition.CASTLE_RIGHT) {
            int rookX = 7, rookY = currentColour == Definition.WHITE ? 7 : 0;
            Move rookMove = new Move(rookX, rookY, rookX - 2, rookY, Definition.MOVEMENT);
            pastMoves.add(rookMove);
            movePiece(rookMove, board[rookX][rookY], Definition.MOVEMENT);

        } else if (moveType == Definition.CASTLE_LEFT) {
            int rookX = 0, rookY = currentColour == Definition.WHITE ? 7 : 0;
            Move rookMove = new Move(rookX, rookY, rookX + 3, rookY, Definition.MOVEMENT);
            pastMoves.add(rookMove);
            movePiece(rookMove, board[rookX][rookY], Definition.MOVEMENT);

        } else if (moveType == Definition.PROMOTE_MOVEMENT || moveType == Definition.PROMOTE_CAPTURE) {
            getBoardAt(move.getStartCoordinates().getX(), move.getStartCoordinates().getY()).promote();


        } else {
            throw new IllegalStateException("Unknown Move Type: " + moveType);
        }
    }

    /**
     * Creates a pieces at the given location and adds it to the board
     * <p>
     * @param x in bounds
     * @param y in bounds
     * @param colour is valid colour
     * @param piece is valid piece type
     */
    private void createPiece(int x, int y, int colour, int piece) {
        Piece newPiece;
        switch (colour + piece) {
            case Definition.WHITE + Definition.PAWN: newPiece =  new Pawn(x, y, Definition.WHITE, TILE_SIZE); boardValue += 1; break;
            case Definition.BLACK + Definition.PAWN: newPiece = new Pawn(x, y, Definition.BLACK, TILE_SIZE); boardValue -= 1; break;
            case Definition.WHITE + Definition.ROOK: newPiece = new Rook(x, y, Definition.WHITE, TILE_SIZE); boardValue += 5; break;
            case Definition.BLACK + Definition.ROOK: newPiece = new Rook(x, y, Definition.BLACK, TILE_SIZE); boardValue -= 5; break;
            case Definition.WHITE + Definition.BISHOP: newPiece = new Bishop(x, y, Definition.WHITE, TILE_SIZE); boardValue += 3; break;
            case Definition.BLACK + Definition.BISHOP: newPiece = new Bishop(x, y, Definition.BLACK, TILE_SIZE); boardValue -= 3; break;
            case Definition.WHITE + Definition.KNIGHT: newPiece = new Knight(x, y, Definition.WHITE, TILE_SIZE); boardValue += 3; break;
            case Definition.BLACK + Definition.KNIGHT: newPiece = new Knight(x, y, Definition.BLACK, TILE_SIZE); boardValue -= 3; break;
            case Definition.WHITE + Definition.QUEEN: newPiece = new Queen(x, y, Definition.WHITE, TILE_SIZE); boardValue += 8; break;
            case Definition.BLACK + Definition.QUEEN: newPiece = new Queen(x, y, Definition.BLACK, TILE_SIZE); boardValue -= 8; break;
            case Definition.WHITE + Definition.KING: newPiece = new King(x, y, Definition.WHITE, TILE_SIZE); boardValue += 999999; break;
            case Definition.BLACK + Definition.KING: newPiece = new King(x, y, Definition.BLACK, TILE_SIZE); boardValue -= 999999; break;
            default: throw new IllegalStateException("Unexpected value");
        }

        pieces.get(colour).get(piece).add(newPiece);

        board[x][y] = newPiece;
        numberOfPieces++;

    }

    /**
     * Alternate way of adding a piece to a board
     * <p>
     * @param piece piece is valid and not equal to null
     */
    public void createPiece(Piece piece) {
        pieces.get(piece.getColour()).get(piece.getID()).add(piece);
        board[piece.getX()][piece.getY()] = piece;
        numberOfPieces++;

    }

    /**
     * Removes a piece from the grid
     * <p>
     * @param toRemove is a valid piece on the board
     */
    private void removePiece(Piece toRemove) {
        numberOfPieces--;
        pastPieces.add(toRemove);
        pieces.get(toRemove.getColour()).get(toRemove.getID()).remove(toRemove);
    }

    /**
     * Generates a fen string of the current position and outputs to the console
     */
    public void printFenString() {
        int count = 0;
        StringBuilder fen = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[x][y] == null) {
                    count += 1;
                } else {
                    if (count != 0) {
                        fen.append(count);
                        count = 0;
                    }

                    if (board[x][y].getPromoted()) fen.append((board[x][y].getColour() == Definition.WHITE ? "Q" : "q"));
                    else fen.append(board[x][y].getStringId());

                }
            }
            if (count != 0) {
                fen.append(count);
                count = 0;
            }

            fen.append("/");
        }

        fen.append(" ");
        if (currentColour == Definition.WHITE) {
            fen.append("w");
        } else {
            fen.append("b");
        }

        fen.append(" - - 0 0");
        System.out.println(fen);
    }

    /**
     * Prints the board position to the console
     */
    public void printBoard() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[x][y] != null) {
                    System.out.printf("%10s ", Definition.ConvertIdToString(board[x][y].getColourID()));
                } else {
                    System.out.printf("%10s ", "None");
                }
            }
            System.out.println();
        }
    }

    /**
     * Gets the value at a specific position
     * <p>
     * @param x in bounds
     * @param y in bounds
     * @return piece at location or null
     */
    public Piece getBoardAt(int x, int y) {
        return board[x][y];
    }

    /**
     * Returns the board array
     * <p>
     * @return the board array
     */
    public Piece[][] getBoard() {
        return board;
    }

    /**
     * Returns whether the given coordinates are in bounds
     * <p>
     * @param x board size matches bounds
     * @param y board size matches bounds
     * @return if x and y are in bounds
     */
    public static boolean InBounds(int x, int y) {
        return (x < 8 && x >= 0 && y < 8 && y >= 0);
    }

    /**
     * Returns the number of turns passed
     * @return turn counter
     */
    public int getTurnCounter() {
        return turnCounter;
    }

    /**
     * Returns whether the king is in check
     * @return if king in check
     */
    public boolean checkInCheck() {
        return moveGenerator.getInCheck();
    }
}
