package main;

import pieces.*;

import java.util.Random;
import java.util.ArrayList;

public class Bot {
    MoveGenerator mg;
    ArrayList<Move> movesList;
    Random r = new Random();
    ChessBoard currentBoard;
    int numberOfCalls = 0;

    // Piece values at each position
    int[][] pawnEval = {{ 0,  0,  0,  0,  0,  0,  0,  0},
                        {50, 50, 50, 50, 50, 50, 50, 50},
                        {10, 10, 20, 30, 30, 20, 10, 10},
                        {5,   5, 10, 20, 20, 10,  5,  5},
                        {0,   0,-10, 20, 20,-10,  0,  0},
                        {5, -5,-10,  15, 15,-10, -5,  5},
                        {5, 10, -15, -45,-45, -15, 10,  5},
                        {0,  0,  0,  0,  0,  0,  0,  0}};

    int[][] knightEval = {{-50,-20,-30,-30,-30,-30,-20,-50},
                        {-40,-20,  0,  0,  0,  0,-20,-40},
                        {-30,  0, 10, 15, 15, 10,  0,-30},
                        {-30,  5, 30, 30, 30, 30,  5,-30},
                        {-30,  0, 30, 50, 50, 30,  0,-30},
                        {-30,  5, 10, 15, 15, 10,  5,-30},
                        {-40,-20,  0,  5,  5,  0,-20,-40},
                        {-50,-40,-30,-30,-30,-30,-40,-50}};

    int[][] bishopEval = {{-20,-10,-10,-10,-10,-10,-10,-20},
                        {-10,  0,  0,  0,  0,  0,  0,-10},
                        {-10,  0,  5, 10, 10,  5,  0,-10},
                        {-10,  5,  5, 10, 10,  5,  5,-10},
                        {-10,  0, 55, 10, 10, 15,  0,-10},
                        {-10, 35, 10, 10, 10, 10, 10,-10},
                        {-10,  5,  0,  5,  5,  0,  5,-10},
                        {-20,-10,-70,-10,-10,-70,-10,-20}};

    int[][] rookEval = {{0,  0,  0,  0,  0,  0,  0,  0},
                        {5, 10, 10, 10, 10, 10, 10,  5},
                        {-5,  0,  0,  0,  0,  0,  0, -5},
                        {-5,  0,  0,  0,  0,  0,  0, -5},
                        {-5,  0,  0,  0,  0,  0,  0, -5},
                        {-5,  0,  0,  0,  0,  0,  0, -5},
                        {-5,  0,  0,  0,  0,  0,  0, -5},
                        {-30, -30, -30, 40, 5, 40, -30, -30}};

    int[][] queenEval = {{-20,-10,-10, -5, -5,-10,-10,-20},
                        {-10,  0,  0,  0,  0,  0,  0,-10},
                        {-10,  0,  5,  5,  5,  5,  0,-10},
                        {-5,  0,  5,  5,  5,  5,  0, -5},
                        {0,  0,  5,  5,  5,  5,  0, -5},
                        {-10,  5,  5,  5,  5,  5,  0,-10},
                        {-10,  0,  5,  0,  0,  0,  0,-10},
                        {-20,-10,-10, 30, 0,-10,-10,-20}};

    int[][] kingMidgameEval = {{-30,-40,-40,-50,-50,-40,-40,-30},
                            {-30,-40,-40,-50,-50,-40,-40,-30},
                            {-30,-40,-40,-50,-50,-40,-40,-30},
                            {-30,-40,-40,-50,-50,-40,-40,-30},
                            {-20,-30,-30,-40,-40,-30,-30,-20},
                            {-10,-20,-20,-20,-20,-20,-20,-10},
                            {20, -40, -40,-150,-150,-150, 20, 20},
                            {20, 20, 50, -100,  20,-100, 60, 20}};

    int[][] kingEndgameEval = {{-50,-40,-30,-20,-20,-30,-40,-50},
                                {-30,-20,-10,  0,  0,-10,-20,-30},
                                {-30,-10, 20, 30, 30, 20,-10,-30},
                                {-30,-10, 30, 40, 40, 30,-10,-30},
                                {-30,-10, 30, 40, 40, 30,-10,-30},
                                {-30,-10, 20, 30, 30, 20,-10,-30},
                                {-30,-30,  0,  0,  0,  0,-30,-30},
                                {-50,-30,-30,-30,-30,-30,-30,-50}};

    /**
     * Creates a new instance of a bot with the board to evaluate's move generator
     * <p>
     * @param mg valid move generator
     */
    public Bot(MoveGenerator mg) {
        this.mg = mg;
    }

    /**
     * Returns the best move at the given depth based on evaluation
     * <p>
     * @param evalBoard valid board
     * @param colourToMove valid colour
     * @param depth is > 0 and is of reasonable size
     * @return the best move
     */
    public Move generateEvalMove(ChessBoard evalBoard, int colourToMove, int depth) {
        numberOfCalls = 0;
        System.out.printf("----------- depth = %d -----------\n", depth);
        double initTime = System.nanoTime();

        currentBoard = evalBoard;
        movesList = evalBoard.moveList;
        int bestEval = (colourToMove == Definition.WHITE ? -8888888: 8888888);
        Move bestMove = null;

        if (movesList.size() == 0) {
            if (colourToMove == Definition.WHITE) {
                System.out.printf("WHITE CHECKMATED on turn %d\n", currentBoard.getTurnCounter());

            } else {
                System.out.printf("BLACK CHECKMATED on turn %d\n", currentBoard.getTurnCounter());

            }

            return null;
        }
        int alpha = -9999999;
        int beta = 9999999;


        for (Move m: movesList) {
            int boardEval = 0;
            simulateMove(m);
//            boardEval = -alphaBeta(depth - 1, -beta, -alpha);
            boardEval = -alphaBeta(depth - 1, -beta, -alpha);

            currentBoard.undoLastMove();
          if (boardEval > alpha || bestMove == null) {
//            if (boardEval > (alpha + (5 - (r.nextInt(10)))) || bestMove == null) { // Add variation to the moves
                    bestMove = m;
                    alpha = boardEval;
                }
        }

        System.out.printf("Best move is: %s\n", bestMove.getText());
        System.out.printf("Eval is: %d for %d\n", alpha, colourToMove);
        System.out.printf("Took %f to search depth of %d\n", (System.nanoTime() - initTime) / 1_000_000_000, depth);
        System.out.printf("Number of calls = %d\n", numberOfCalls);
        System.out.println("----------------------------------");
        return bestMove;
    }

    /**
     * Removes branches that are guaranteed to be worse than other options
     * <p>
     * @param depth cannot be negative
     * @param alpha is the best score
     * @param beta is the worse score
     * @return score of the branch
     */
    private int alphaBeta(int depth, int alpha, int beta) {
        if (depth == 0) {
            return searchCaptures(alpha, beta); // Makes the bot hate trading
//            return evaluateBoard();
        }

        ArrayList<Move> moves = currentBoard.moveList;
        if (moves.size() == 0) {
            if (currentBoard.checkInCheck()) {
                return -99999999;
            }

            return 0;
        }

        for (Move m: moves) {
            simulateMove(m);
            int eval = (-alphaBeta(depth - 1, -beta, -alpha));
            currentBoard.undoLastMove();
            if (eval >= beta) {
                return beta;
            }

            if (eval > alpha) {
                alpha = eval;
            }

        }
        return alpha;
    }

    /**
     * Used to continue to evaluate all captures to avoid hanging pieces when max depth is reached
     * <p>
     * @param alpha max score
     * @param beta min score
     * @return eval after all captures
     */
    int searchCaptures(int alpha, int beta) {
        int eval = evaluateBoard();
        if (eval == 7777777 || eval == -7777777) return 7777777;
        if (eval >= beta) return beta;
        if (eval > alpha) alpha = eval;


        ArrayList<Move> captures = mg.captures;

        for (Move m: captures) {
            simulateMove(m);
            eval = -searchCaptures(-beta, -alpha);
            currentBoard.undoLastMove();

            if (eval >= beta) {
                return beta;
            }
            if (eval > alpha) alpha = eval;
        }

        return alpha;
    }

    /**
     * Evaluates the amount of material on the board and the position of each piece
     * <p>
     * @return the value of the board
     */
    public int evaluateBoard() {
        numberOfCalls++;
        double endgameWeight = 1 - currentBoard.numberOfPieces/ (double) 32;

        int eval = 0;

        if (currentBoard.checkInCheck() && currentBoard.moveList.size() == 0) {
            return (currentBoard.currentColour == Definition.WHITE ? -1 : 1) * 7777777;
        } else if (currentBoard.moveList.size() == 0) {
            return 0;
        }

        if (currentBoard.checkInCheck()) eval += 25 * (currentBoard.currentColour == Definition.WHITE ? -1 : 1);
        if (currentBoard.moveGenerator.doubleCheck) eval += 50 * (currentBoard.currentColour == Definition.WHITE ? -1 : 1);

        for (Piece p: currentBoard.pieces.get(Definition.WHITE).get(Definition.PAWN)) {
            eval += 100 * (p.getPromoted() ? 9 : 1);
            if (p.getPromoted()) {
                eval += queenEval[p.getY()][p.getX()];

            } else {
                eval += pawnEval[p.getY()][p.getX()];

            }
        }
        for (Piece p: currentBoard.pieces.get(Definition.BLACK).get(Definition.PAWN)) {
            eval -= 100 * (p.getPromoted() ? 9 : 1);
            if (p.getPromoted()) {
                eval -= queenEval[7 - p.getY()][p.getX()];

            } else {
                eval -= pawnEval[7 - p.getY()][p.getX()];

            }
        }

        for (Piece p: currentBoard.pieces.get(Definition.WHITE).get(Definition.KNIGHT)) {
            eval += 300;
            eval += knightEval[p.getY()][p.getX()];
        }

        for (Piece p: currentBoard.pieces.get(Definition.BLACK).get(Definition.KNIGHT)) {
            eval -= 300;
            eval -= knightEval[7 - p.getY()][p.getX()];
        }

        for (Piece p: currentBoard.pieces.get(Definition.WHITE).get(Definition.BISHOP)) {
            eval += 340;
            eval += bishopEval[p.getY()][p.getX()];
        }

        for (Piece p: currentBoard.pieces.get(Definition.BLACK).get(Definition.BISHOP)) {
            eval -= 340;
            eval -= bishopEval[7 - p.getY()][p.getX()];
        }

        for (Piece p: currentBoard.pieces.get(Definition.WHITE).get(Definition.ROOK)) {
            eval += 500;
            eval += rookEval[p.getY()][p.getX()];
        }

        for (Piece p: currentBoard.pieces.get(Definition.BLACK).get(Definition.ROOK)) {
            eval -= 500;
            eval -= rookEval[7 - p.getY()][p.getX()];
        }

        for (Piece p: currentBoard.pieces.get(Definition.WHITE).get(Definition.QUEEN)) {
            eval += 900;
            eval += queenEval[p.getY()][p.getX()];
        }

        for (Piece p: currentBoard.pieces.get(Definition.BLACK).get(Definition.QUEEN)) {
            eval -= 900;
            eval -= queenEval[7 - p.getY()][p.getX()];
        }

        for (Piece p: currentBoard.pieces.get(Definition.WHITE).get(Definition.KING)) {
            eval += kingMidgameEval[p.getY()][p.getX()] * (1 - endgameWeight);
            eval += kingEndgameEval[p.getY()][p.getX()] * (endgameWeight);
        }

        for (Piece p: currentBoard.pieces.get(Definition.BLACK).get(Definition.KING)) {
            eval -= kingMidgameEval[7 - p.getY()][p.getX()] * (1 - endgameWeight);
            eval -= kingEndgameEval[7 - p.getY()][p.getX()] * (endgameWeight);

        }

//        System.out.printf("Eval found is %d\n", eval);
        if (Math.abs(eval) > 10000) currentBoard.printBoard();
        return eval * (currentBoard.currentColour == Definition.WHITE ? 1 : -1);
    }

    /**
     * Simulates a move on the current board
     * <p>
     * @param moveToProcess is a valid move
     */
    public void simulateMove(Move moveToProcess) {
        currentBoard.processPieceMove(moveToProcess);
    }


    /**
     * Simulates a random move from the current moves list
     * <p>
     * @param board board to simulate has valid moves
     */
    public void makeRandomMove(ChessBoard board) {
        double initTime = System.nanoTime();
        System.out.printf("Took %f to finish initilizing\n", (System.nanoTime() - initTime) / 1_000_000_000);

        movesList = board.moveList;

        if (movesList.size() == 0)  {
            if (board.checkInCheck()) System.out.println("CHECKMATE");
            else System.out.println("STALEMATE");
            return;
        }
        Move moveToMake = movesList.get(r.nextInt(movesList.size()));
        Coordinate moveStart = moveToMake.getStartCoordinates();
        Coordinate moveEnd = moveToMake.getEndCoordinates();
        board.processPieceMove(moveStart.getX(), moveStart.getY(), moveEnd.getX(), moveEnd.getY(), moveToMake.getType());

    }
}
