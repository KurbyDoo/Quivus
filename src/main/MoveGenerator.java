package main;

import pieces.Piece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MoveGenerator {
    public ArrayList<Move> movesList = new ArrayList<>();
    public ArrayList<Move> captures = new ArrayList<>();

    private Piece[][] gameBoard;
    private boolean[][] blockCheckSquares;
    public boolean[][] attackedSquares;
    private ArrayList<Piece> pinnedPieces = new ArrayList<>();
    private Move lastMove;
    private int colourToMove;
    private Piece currentPlayerKing;
    private boolean inCheck = false;
    public boolean doubleCheck = false;
    public ChessBoard board;

    /**
     * Generates the list of valid moves given the board
     * <p>
     * @param mainBoard board has pieces
     * @param genQuiet generate only captures
     * @return list of valid moves
     */
    public ArrayList<Move> generateMoves(ChessBoard mainBoard, boolean genQuiet) {
        board = mainBoard;
        HashMap<Integer, HashMap<Integer, ArrayList<Piece>>>pieces = mainBoard.pieces;
        Piece[][] gameBoard = mainBoard.getBoard();
        Move lastMove = mainBoard.pastMoves.get(mainBoard.pastMoves.size() - 1);
        int colourToMove = mainBoard.currentColour;


        double startTime = System.nanoTime();
        movesList = new ArrayList<>();
        captures = new ArrayList<>();

        attackedSquares = new boolean[8][8];
        blockCheckSquares = new boolean[8][8];
        inCheck = false;
        doubleCheck = false;
        for (Piece e: pinnedPieces) {
            e.setPinned(false);
        }
        pinnedPieces = new ArrayList<>();

        try {
            this.currentPlayerKing = pieces.get(colourToMove).get(Definition.KING).get(0);

        } catch (Exception e) {
            System.out.println("last 4 moves");
            board.printFenString();
            board.undoLastMove();
            board.printFenString();
            board.undoLastMove();
            board.printFenString();
            board.undoLastMove();
            board.printFenString();
            e.printStackTrace();
        }
        this.colourToMove = colourToMove;

//        System.out.println("-----------");
//        System.out.println("Generating Moves for: " + colourToMove);

        this.gameBoard = gameBoard;
        HashMap<Integer, ArrayList<Piece>> friendlyPieceMap = pieces.get(colourToMove);
        HashMap<Integer, ArrayList<Piece>> enemyPieceMap = pieces.get(Definition.ReverseColour(colourToMove));
        this.lastMove = lastMove;

        for (Piece p: enemyPieceMap.get(Definition.QUEEN)) generateQueenAttacks(p.getX(), p.getY());
        for (Piece p: enemyPieceMap.get(Definition.BISHOP)) generateBishopAttacks(p.getX(), p.getY());
        for (Piece p: enemyPieceMap.get(Definition.KNIGHT)) generateKnightAttacks(p.getX(), p.getY());
        for (Piece p: enemyPieceMap.get(Definition.PAWN)) generatePawnAttacks(p.getX(), p.getY(), p.getPromoted());
        for (Piece p: enemyPieceMap.get(Definition.ROOK)) generateRookAttacks(p.getX(), p.getY());
        for (Piece p: enemyPieceMap.get(Definition.KING)) generateKingAttacks(p.getX(), p.getY());

        int numberOfChecks = findChecks();
        if (numberOfChecks > 0) inCheck = true;
        if (numberOfChecks > 1) {
            doubleCheck = true;
            generateKingMoves(currentPlayerKing.getX(), currentPlayerKing.getY());
            captures.addAll(movesList);
            return captures;
        }

        for (Piece p: friendlyPieceMap.get(Definition.QUEEN)) generateQueenMoves(p.getX(), p.getY());
        for (Piece p: friendlyPieceMap.get(Definition.BISHOP)) generateBishopMoves(p.getX(), p.getY());
        for (Piece p: friendlyPieceMap.get(Definition.KNIGHT)) generateKnightMoves(p.getX(), p.getY());
        for (Piece p: friendlyPieceMap.get(Definition.PAWN)) generatePawnMoves(p.getX(), p.getY(), p.getPromoted());
        for (Piece p: friendlyPieceMap.get(Definition.ROOK)) generateRookMoves(p.getX(), p.getY());
        for (Piece p: friendlyPieceMap.get(Definition.KING)) generateKingMoves(p.getX(), p.getY());



//        System.out.printf("Total of %d moves generated\n", movesList.size());
//        System.out.printf("Took %f to generate moves\n", (System.nanoTime() - startTime) / 1_000_000_000);
//        System.out.println("-----------");
//        if (genQuiet) {
//            return captures;
//        }
        ArrayList<Move> allMoves = new ArrayList<>();
        allMoves.addAll(captures);
        allMoves.addAll(movesList);

//        sortMoves(allMoves);
        return allMoves;
    }

    /**
     * Returns the number of checks on the current king
     * <p>
     * @return the number of checks
     */
    private int findChecks() {
        int numberOfChecks = 0;

        numberOfChecks += findSlidingChecks();
        numberOfChecks += findKnightChecks();

        return numberOfChecks;
    }


    /**
     * Returns number of checks by knights
     * <p>
     * @return number of knight checks
     */
    private int findKnightChecks() {
        int numberOfChecks = 0;

        if (checkChecksInDirection(1, 2, 1)) numberOfChecks++;
        if (checkChecksInDirection(1, -2, 1)) numberOfChecks++;
        if (checkChecksInDirection(-1, 2, 1)) numberOfChecks++;
        if (checkChecksInDirection(-1, -2, 1)) numberOfChecks++;
        if (checkChecksInDirection(2, 1, 1)) numberOfChecks++;
        if (checkChecksInDirection(2, -1, 1)) numberOfChecks++;
        if (checkChecksInDirection(-2, 1, 1)) numberOfChecks++;
        if (checkChecksInDirection(-2, -1, 1)) numberOfChecks++;

        return numberOfChecks;
    }

    /**
     * Returns number of diagonal, vertical, and horizontal checks
     * <p>
     * @return number of checks
     */
    private int findSlidingChecks() {
        int numberOfChecks = 0;

        // Horizontal/Vertical Attacks
        if (checkChecksInDirection(1, 0, 8)) numberOfChecks++;
        if (checkChecksInDirection(-1, 0, 8)) numberOfChecks++;
        if (checkChecksInDirection(0, 1, 8)) numberOfChecks++;
        if (checkChecksInDirection(0, -1, 8)) numberOfChecks++;

        // Diagonal Attacks
        if (checkChecksInDirection(1, 1, 8)) numberOfChecks++;
        if (checkChecksInDirection(-1, 1, 8)) numberOfChecks++;
        if (checkChecksInDirection(1, -1, 8)) numberOfChecks++;
        if (checkChecksInDirection(-1, -1, 8)) numberOfChecks++;
        return numberOfChecks;
    }

    /**
     * Checks all squares for checks from the king in a direction given the offset
     * Counters number of attacks and sets pieces to pinned
     * <p>
     * @param xOffset x offset
     * @param yOffset y offset
     * @param timesToRepeat not negative
     * @return if the king is in check
     */
    private boolean checkChecksInDirection(int xOffset, int yOffset, int timesToRepeat) {
        int piecesEncountered = 0;
        int newX = currentPlayerKing.getX() + xOffset, newY = currentPlayerKing.getY() + yOffset;
        Piece pinnedPiece = null;
        ArrayList<Coordinate> checkedSquares = new ArrayList<>();
        for (int i = 1; ChessBoard.InBounds(newX, newY) && i <= timesToRepeat; i++) {
            newX = currentPlayerKing.getX() + xOffset * i;
            newY = currentPlayerKing.getY() + yOffset * i;
            checkedSquares.add(new Coordinate(newX, newY));

            if (!ChessBoard.InBounds(newX, newY)) return false;
            if (gameBoard[newX][newY] == null) continue;
            piecesEncountered++;

            if (gameBoard[newX][newY].isSameColour(colourToMove)) {
                pinnedPiece = gameBoard[newX][newY];
                if (piecesEncountered != 1) return false;

            } else if (pinnedPiece != null) {
                Piece attackingPiece = gameBoard[newX][newY];
                boolean pinning = false;

                if (attackingPiece.getID() == Definition.QUEEN && ((Math.abs(xOffset) == Math.abs(yOffset)) || Math.min(Math.abs(xOffset), Math.abs(yOffset)) == 0)) pinning = true;
                else if (attackingPiece.getID() == Definition.PAWN && attackingPiece.getPromoted() && ((Math.abs(xOffset) == Math.abs(yOffset)) || Math.min(Math.abs(xOffset), Math.abs(yOffset)) == 0)) pinning = true;
                else if (attackingPiece.getID() == Definition.BISHOP && Math.abs(xOffset) == Math.abs(yOffset)) pinning = true;
                else if (attackingPiece.getID() == Definition.ROOK && Math.min(Math.abs(xOffset), Math.abs(yOffset)) == 0) pinning = true;

                if (pinning) {
                    pinnedPiece.setPinned(true);
                    pinnedPieces.add(pinnedPiece);
                }

                return false;

            } else {
                Piece attackingPiece = gameBoard[newX][newY];
                boolean attacking = false;
                if (attackingPiece.getID() == Definition.QUEEN && ((Math.abs(xOffset) == Math.abs(yOffset)) || Math.min(Math.abs(xOffset), Math.abs(yOffset)) == 0)) attacking = true;
                else if (attackingPiece.getID() == Definition.PAWN && attackingPiece.getPromoted() && ((Math.abs(xOffset) == Math.abs(yOffset)) || Math.min(Math.abs(xOffset), Math.abs(yOffset)) == 0)) attacking = true;
                else if (attackingPiece.getID() == Definition.BISHOP && Math.abs(xOffset) == Math.abs(yOffset)) attacking = true;
                else if (attackingPiece.getID() == Definition.ROOK &&  Math.min(Math.abs(xOffset), Math.abs(yOffset)) == 0) attacking = true;
                else if (attackingPiece.getID() == Definition.KNIGHT && Math.abs(xOffset) + Math.abs(yOffset) == 3) attacking = true;
                else if (attackingPiece.getID() == Definition.PAWN && (Math.abs(xOffset * i) == 1 && (yOffset * i) == (colourToMove == Definition.WHITE ? -1 : 1))) attacking = true;

                if (attacking) {
                    for (Coordinate c: checkedSquares) {
                        blockCheckSquares[c.getX()][c.getY()] = true;
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }


    /**
     * Generates attacks for queen
     * @param x is in bounds
     * @param y is in bounds
     */
    private void generateQueenAttacks(int x, int y) {
        generateRookAttacks(x, y);
        generateBishopAttacks(x, y);
    }

    /**
     * Generates attacks for Rook
     * @param x is in bounds
     * @param y is in bounds
     */
    private void generateRookAttacks(int x, int y) {
        generateAttacksInDirection(x, y, 1, 0, 8);
        generateAttacksInDirection(x, y, -1, 0, 8);
        generateAttacksInDirection(x, y, 0, 1, 8);
        generateAttacksInDirection(x, y, 0, -1, 8);
    }

    /**
     * Generates attacks for Bishop
     * @param x is in bounds
     * @param y is in bounds
     */
    private void generateBishopAttacks(int x, int y) {
        generateAttacksInDirection(x, y, 1, 1, 8);
        generateAttacksInDirection(x, y, 1, -1, 8);
        generateAttacksInDirection(x, y, -1, 1, 8);
        generateAttacksInDirection(x, y, -1, -1, 8);
    }

    /**
     * Generates attacks for Knight
     * @param x is in bounds
     * @param y is in bounds
     */
    private void generateKnightAttacks(int x, int y) {
        generateAttacksInDirection(x, y, 1, 2, 1);
        generateAttacksInDirection(x, y, -1, 2, 1);
        generateAttacksInDirection(x, y, 1, -2, 1);
        generateAttacksInDirection(x, y, -1, -2, 1);
        generateAttacksInDirection(x, y, 2, 1, 1);
        generateAttacksInDirection(x, y, -2, 1, 1);
        generateAttacksInDirection(x, y, 2, -1, 1);
        generateAttacksInDirection(x, y, -2, -1, 1);
    }

    /**
     * Generates attacks for King
     * @param x is in bounds
     * @param y is in bounds
     */
    private void generateKingAttacks(int x, int y) {
        for (int xOffset = -1; xOffset < 2; xOffset++) {
            for (int yOffset = -1; yOffset < 2; yOffset++) {
                if (xOffset == 0 && yOffset == 0) continue;
                generateAttacksInDirection(x, y, xOffset, yOffset, 1);
            }
        }
    }

    /**
     * Generates attacks for pawns
     * <p>
     * @param x is in bounds
     * @param y is in bounds
     * @param promoted if the current pawn is promoted
     */
    private void generatePawnAttacks(int x, int y, boolean promoted) {
        if (promoted) {
            generateQueenAttacks(x, y);
            return;
        }

        if (colourToMove == Definition.BLACK) {
            generateAttacksInDirection(x, y, 1, -1, 1);
            generateAttacksInDirection(x, y, -1, -1, 1);
        } else {
            generateAttacksInDirection(x, y, 1, 1, 1);
            generateAttacksInDirection(x, y, -1, 1, 1);
        }

    }


    /**
     * Generates all attacked squares given a directional offset and the number of times to repeat in a direction
     * @param startX is in bounds
     * @param startY is in bounds
     * @param xDirection x offset
     * @param yDirection y offset
     * @param timesToRepeat is not negative
     */
    private void generateAttacksInDirection(int startX, int startY, int xDirection, int yDirection, int timesToRepeat) {
        int newX, newY;
        for (int i = 1; i <= timesToRepeat; i++) {
            newX = startX + (xDirection * i);
            newY = startY + (yDirection * i);
            if (!ChessBoard.InBounds(newX , newY)) return;
//            if (!attackedSquares[newX][newY]) dangerSquares++;
            attackedSquares[newX][newY] = true;
            if (gameBoard[newX][newY] != null && gameBoard[newX][newY].getColourID() != colourToMove + Definition.KING) return;
        }
    }

    /**
     * Generates king moves and castling
     * Cannot move into attacked squares
     * <p>
     * @param x is in bounds
     * @param y is in bounds
     */
    private void generateKingMoves(int x, int y) {
        for (int xOffset = -1; xOffset < 2; xOffset++) {
            for (int yOffset = -1; yOffset < 2; yOffset++) {
                if (xOffset == 0 && yOffset == 0) continue;
                if (!ChessBoard.InBounds(x + xOffset, y + yOffset)) continue;
                if (attackedSquares[x + xOffset][y + yOffset]) continue;

                if (gameBoard[x + xOffset][y + yOffset] == null) movesList.add(new Move(x, y, x + xOffset, y + yOffset, Definition.MOVEMENT));
                else if (!gameBoard[x + xOffset][y + yOffset].isSameColour(colourToMove)) captures.add(new Move(x, y, x + xOffset, y + yOffset, Definition.CAPTURE));
            }
        }

        int backRank = colourToMove == Definition.WHITE ? 7 : 0;
        if (gameBoard[x][y].numberOfTimesMoved == 0 && gameBoard[0][y] != null && gameBoard[0][y].numberOfTimesMoved == 0) {
            if (gameBoard[x - 1][backRank] == null && gameBoard[x - 2][backRank] == null && gameBoard[x - 3][backRank] == null) {
                if (!attackedSquares[x - 1][backRank] && !attackedSquares[x - 2][backRank] && !inCheck) {
                    movesList.add(new Move(x, y, x - 2, y, Definition.CASTLE_LEFT));
                }
            }
        }

        if (gameBoard[x][y].numberOfTimesMoved == 0 && gameBoard[7][y] != null && gameBoard[7][y].numberOfTimesMoved == 0) {
            if (gameBoard[x + 1][backRank] == null && gameBoard[x + 2][backRank] == null) {
                if (!attackedSquares[x + 1][backRank] && !attackedSquares[x + 2][backRank] && !inCheck) {
                    movesList.add(new Move(x, y, x + 2, y, Definition.CASTLE_RIGHT));
                }
            }
        }
    }

    /**
     * Generates special pawn moves such as en passant
     * <p>
     * @param x is in bounds
     * @param y is in bounds
     * @param promoted is pawn promoted
     */
    private void generatePawnMoves(int x, int y, boolean promoted) {
        if (promoted) {
            generateQueenMoves(x, y);
            return;
        }

        int direction = colourToMove == Definition.WHITE ? - 1: 1;
        int startRank = colourToMove == Definition.WHITE ? 6 : 1;
        if (gameBoard[x][y].isPinned()) {
            Coordinate slope = findSlopeFromKing(x, y);
            if (Math.abs(slope.getX()) == 0) {
                generatePawnMovesInDirection(x, y, 0, -(slope.getY()/Math.abs(slope.getY())), 1);
                return;

            } else if (Math.abs(slope.getX()) == Math.abs(slope.getY()) && ((colourToMove == Definition.WHITE && slope.getY() > 0) || (colourToMove == Definition.BLACK && slope.getY() < 0))) {
                generatePawnMovesInDirection(x, y, -(slope.getX()/Math.abs(slope.getX())), -(slope.getY()/Math.abs(slope.getX())), 1);
            }
            return;
        }

        if (y == startRank) {
            generatePawnMovesInDirection(x, y, 0, direction, 2);
        } else {
            generatePawnMovesInDirection(x, y, 0, direction, 1);
        }

        generatePawnMovesInDirection(x, y, 1, direction, 1);
        generatePawnMovesInDirection(x, y, -1, direction, 1);
        checkEnPassant(x, y);
    }

    /**
     * Generates valid knight moves
     * <p>
     * @param x is in bounds
     * @param y is in bounds
     */
    private void generateKnightMoves(int x, int y) {
        if (gameBoard[x][y].isPinned()) return;
        generateMovesInDirection(x, y, 1, 2, 1);
        generateMovesInDirection(x, y, -1, 2, 1);
        generateMovesInDirection(x, y, 1, -2, 1);
        generateMovesInDirection(x, y, -1, -2, 1);
        generateMovesInDirection(x, y, 2, 1, 1);
        generateMovesInDirection(x, y, -2, 1, 1);
        generateMovesInDirection(x, y, 2, -1, 1);
        generateMovesInDirection(x, y, -2, -1, 1);
    }

    /**
     * Generates valid queen moves
     * <p>
     * @param x is in bounds
     * @param y is in bounds
     */
    private void generateQueenMoves(int x, int y) {
        generateRookMoves(x, y);
        generateBishopMoves(x, y);
    }

    /**
     * Generates valid rook moves
     * <p>
     * @param x is in bounds
     * @param y is in bounds
     */
    private void generateRookMoves(int x, int y) {
        if (gameBoard[x][y].isPinned()) {
            Coordinate slope = findSlopeFromKing(x, y);
            if (Math.abs(slope.getX()) == Math.abs(slope.getY())) return;
            int xDirection = slope.getX() == 0 ? 0 : 1;
            int yDirection = slope.getY() == 0 ? 0 : 1;
//            System.out.printf("Pinned in direction = %s\n", slope.getText());
            generateMovesInDirection(x, y, xDirection, yDirection, 8);
            generateMovesInDirection(x, y, -xDirection, -yDirection, 8);

        } else {
            generateMovesInDirection(x, y, 1,0, 8);
            generateMovesInDirection(x, y, -1,0, 8);
            generateMovesInDirection(x, y, 0,1, 8);
            generateMovesInDirection(x, y, -0,-1, 8);
        }
    }

    /**
     * Generates valid bishop moves
     * <p>
     * @param x is in bounds
     * @param y is in bounds
     */
    private void generateBishopMoves(int x, int y) {
        if (gameBoard[x][y].isPinned()) {
            Coordinate slope = findSlopeFromKing(x, y);
            if (Math.abs(slope.getX()) != Math.abs(slope.getY())) return;
            generateMovesInDirection(x, y, slope.getX()/Math.abs(slope.getX()), slope.getY()/Math.abs(slope.getY()), 8);
            generateMovesInDirection(x, y, -slope.getX()/Math.abs(slope.getX()), -slope.getY()/Math.abs(slope.getY()), 8);

        } else {
            generateMovesInDirection(x, y, 1,1, 8);
            generateMovesInDirection(x, y, -1,1, 8);
            generateMovesInDirection(x, y, 1,-1, 8);
            generateMovesInDirection(x, y, -1,-1, 8);
        }
    }

    /**
     * Checks for pawn en passant
     * <p>
     * @param x is in bounds
     * @param y is in bounds
     */
    private void checkEnPassant(int x, int y) {
        int startRank = colourToMove == Definition.WHITE ? 3 : 4;
        int midRank = colourToMove == Definition.WHITE ? 2 : 5;
        int backRank = colourToMove == Definition.WHITE ? 1 : 6;

        lastMove = board.pastMoves.get(board.pastMoves.size() - 1);


        if (lastMove == null) return;
        if (y != startRank) return;
        if (lastMove.getStartCoordinates().getY() != backRank || lastMove.getEndCoordinates().getY() != startRank) return;
        if (board.getBoardAt(lastMove.getEndCoordinates().getX(), lastMove.getEndCoordinates().getY()).getColourID() != Definition.ReverseColour(colourToMove) + Definition.PAWN) return;
        if (x != lastMove.getEndCoordinates().getX() - 1 && x != lastMove.getEndCoordinates().getX() + 1) return;
        movesList.add(new Move(x, y, lastMove.getEndCoordinates().getX(), midRank, Definition.ENPASSANT));

    }

    /**
     * Generates all moves in a direction given an offset
     * <p>
     * @param startX is in bounds
     * @param startY is in bounds
     * @param xDirection x offset
     * @param yDirection y offset
     * @param timesToRepeat is not negative
     */
    private void generateMovesInDirection(int startX, int startY, int xDirection, int yDirection, int timesToRepeat) {
        int newX, newY;
        boolean occupied;
        for (int i = 1; i <= timesToRepeat; i++) {
            newX = startX + (xDirection * i);
            newY = startY + (yDirection * i);
            occupied = true;
            if (!ChessBoard.InBounds(newX , newY)) return;
            if (gameBoard[newX][newY] == null) occupied = false;
            else if (gameBoard[newX][newY].getColour() == colourToMove) return;
            if (inCheck && !blockCheckSquares[newX][newY]) {
                if (occupied) return;
                continue;
            }
            if (occupied) {
                captures.add(new Move(startX, startY, newX, newY, Definition.CAPTURE));
            } else {
                movesList.add(new Move(startX, startY, newX, newY, Definition.MOVEMENT));
            }
            if (occupied) return;
        }
    }

    /**
     * Generates valid pawn moves
     * <p>
     * @param startX is in bounds
     * @param startY is in bounds
     * @param xDirection x offset
     * @param yDirection y offset
     * @param timesToRepeat is not negative
     */
    private void generatePawnMovesInDirection(int startX, int startY, int xDirection, int yDirection, int timesToRepeat) {
        int newX, newY;
        boolean occupied;
        for (int i = 1; i <= timesToRepeat; i++) {
            newX = startX + (xDirection * i);
            newY = startY + (yDirection * i);
            occupied = true;
            if (!ChessBoard.InBounds(newX , newY)) return;
            if (gameBoard[newX][newY] == null) occupied = false;
            else if (gameBoard[newX][newY].getColour() == colourToMove) return;
            if ((xDirection == 0 && occupied || (Math.abs(xDirection) == 1 && !occupied))) return;
            if (inCheck && !blockCheckSquares[newX][newY]) continue;
            if (newY == (colourToMove == Definition.WHITE ? 0 : 7)) {
                movesList.add(new Move(startX, startY, newX, newY, occupied ? Definition.PROMOTE_CAPTURE : Definition.PROMOTE_MOVEMENT));

            } else {
                if (occupied) {
                    captures.add(new Move(startX, startY, newX, newY, Definition.CAPTURE));

                } else {
                    movesList.add(new Move(startX, startY, newX, newY, Definition.MOVEMENT));

                }
            }
        }
    }

    /**
     * Returns if the king is in check
     * <p>
     * @return if the king is in check
     */
    public boolean getInCheck() {
        return inCheck;
    }
    private Coordinate findSlopeFromKing(int x, int y) {
        int xDiff = currentPlayerKing.getX() - x, yDiff = currentPlayerKing.getY() - y;
        return new Coordinate(xDiff, yDiff);
    }

    /**
     * Custom comparator to sort moves by move value
     */
    public class SortMoves implements Comparator<Move> {
        @Override
        public int compare(Move o1, Move o2) {
            return o2.value - o1.value;
        }
    }

    /**
     * Unused
     * Sorts the moves according to what is considered the "best" moves
     * <p>
     * @param toSort is not empty
     */
    public void sortMoves(ArrayList<Move> toSort) {
        for (Move m: toSort) {
            System.out.println("value = " + m.value);
        }
        for (Move m: toSort) {
            int score = 0;

            Piece startSquare = gameBoard[m.getStartCoordinates().getX()][m.getStartCoordinates().getY()];
            Piece endSquare = gameBoard[m.getEndCoordinates().getX()][m.getEndCoordinates().getY()];

            if (endSquare != null) {
                score = 10 * (Math.abs(startSquare.getValue()) - Math.abs(endSquare.getValue()));
            }

            if (startSquare.getID() == Definition.PAWN) {
                if (m.getType() == Definition.PROMOTE_MOVEMENT || m.getType() == Definition.PROMOTE_CAPTURE) {
                    score += 900;
                }

            } else {
                if (attackedSquares[m.getEndCoordinates().getX()][m.getEndCoordinates().getY()]) {
                    score -= startSquare.getValue();
                }
            }

            m.value = score;
        }

        toSort.sort(new SortMoves());

        for (Move m: toSort) {
            System.out.println("sorted value = " + m.value);
        }
    }
}
