import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameState {

    private Player[][] board;
    private Player currPlayer;
    private Player lastPlayer;
    private int[] lastPiece;

    public GameState() {
        board = new Player[7][6];
        for (Player[] players : board) {
            Arrays.fill(players, Player.EMPTY);
        }
        lastPiece = new int[]{-1, -1};
        currPlayer = Player.RED;
        lastPlayer = null;
    }
    public GameState(GameState gs) {
        board = gs.board.clone();
        for(int col = 0; col < board.length; col++) {
            board[col] = board[col].clone();
        }
        currPlayer = gs.currPlayer;
        lastPlayer = gs.lastPlayer;
        lastPiece = gs.lastPiece.clone();
    }

    public boolean addToTop(int column) {
        for (int row = 0; row < board[column].length; row++) {
            if (board[column][row] == Player.EMPTY) {
                board[column][row] = currPlayer;
                lastPiece[0] = column;
                lastPiece[1] = row;
                if (currPlayer == Player.RED) {
                    lastPlayer = Player.RED;
                    currPlayer = Player.BLUE;
                } else {
                    lastPlayer = Player.BLUE;
                    currPlayer = Player.RED;
                }
                return true;
            }
        }
        return false;
    }

    public boolean isWin() {

        if (currPlayer == Player.RED) {
            lastPlayer = Player.BLUE;
        } else {
            lastPlayer = Player.RED;
        }

        if (lastPiece[0] == -1) {
            return false;
        }

        //check horizontal
        int count = 0;
        for (Player[] players : board) {
            if (players[lastPiece[1]] == lastPlayer) {
                count++;
            } else {
                count = 0;
            }

            if (count == 4) {
                return true;
            }
        }
        //check vertical
        count = 0;
        for (int row  = 0; row < board[0].length; row++) {
            if (board[lastPiece[0]][row] == lastPlayer) {
                count++;
            } else {
                count = 0;
            }

            if (count == 4) {
                return true;
            }
        }
        //check / diagonal
        count = 1;
        int indexX = lastPiece[0];
        int indexY = lastPiece[1];
        try {
            while (count < 4) {
                indexX--;
                indexY--;
                if (board[indexX][indexY] == lastPlayer) {
                    count++;
                } else {
                    break;
                }
            }
        }
        catch (IndexOutOfBoundsException ignored) {}
        indexX = lastPiece[0];
        indexY = lastPiece[1];
        try {
            while (count < 4) {
                indexX++;
                indexY++;
                if (board[indexX][indexY] == lastPlayer) {
                    count++;
                } else {
                    break;
                }
            }
        }
        catch (IndexOutOfBoundsException ignored) {}

        if (count == 4) {
            return true;
        }

        //check \ diagonal
        count = 1;
        indexX = lastPiece[0];
        indexY = lastPiece[1];
        try {
            while (count < 4) {
                indexX--;
                indexY++;
                if (board[indexX][indexY] == lastPlayer) {
                    count++;
                } else {
                    break;
                }
            }
        }
        catch (IndexOutOfBoundsException ignored) {}
        indexX = lastPiece[0];
        indexY = lastPiece[1];
        try {
            while (count < 4) {
                indexX++;
                indexY--;
                if (board[indexX][indexY] == lastPlayer) {
                    count++;
                } else {
                    break;
                }
            }
        }
        catch (IndexOutOfBoundsException ignored) {}
        return count == 4;
    }

    public void printBoard() {
        for (int row = board[0].length - 1; row >= 0; row--) {
            System.out.print("|");
            for (int column = 0; column < board.length; column++) {
                Player piece = board[column][row];
                System.out.print(" ");
                if (piece == Player.RED) {
                    System.out.print("x");
                } else  if (piece == Player.BLUE) {
                    System.out.print("o");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println(" |");
        }
        System.out.println("  _ _ _ _ _ _ _ ");
        System.out.println("  1 2 3 4 5 6 7 ");
    }

    public Player getCurrPlayer() {
        return currPlayer;
    }

    public Player getLastPlayer() {
        return lastPlayer;
    }

    public double getStateValue() {

        //position values matrix
        /*
             9 11 13 15 13 11 9
            11 13 15 17 15 13 11
            13 15 17 19 17 15 13
            13 15 17 19 17 15 13
            11 13 15 17 15 13 11
             9 11 13 15 13 11 9
        */

        //flipped 90 degrees clockwise
        double[][] positionValues = {
                {9, 11, 13, 13, 11, 9},
                {11, 13, 15, 15, 13, 11},
                {13, 15, 17, 17, 15, 13},
                {15, 17, 19, 19, 17, 15},
                {13, 15, 17, 17, 15, 13},
                {11, 13, 15, 15, 13, 11},
                {9, 11, 13, 13, 11, 9}
        };

        final double POSITION_WEIGHT = 1;
        final double POSSIBLE_THREE_WEIGHT = 15;
        final double POSSIBLE_FOUR_WEIGHT = 35;

        //calculate value, RED/X will be positive, BLUE/O will be negative

        int total = 0;

        //add position values
        for (int row = 0; row < board[0].length; row++) {
            for (int column = 0; column < board.length; column++) {
                if (board[column][row] == Player.RED) {
                    total += positionValues[column][row] * POSITION_WEIGHT;
                } else if (board[column][row] == Player.BLUE) {
                    total -= positionValues[column][row] * POSITION_WEIGHT;
                }
            }
        }

        // add win value?
        if (isWin()) {
            if (lastPlayer == Player.RED) {
                total += 1000;
            } else {
                total -= 1000;
            }
        }

        Set<Integer[]> redPositionsThree = new HashSet<>();
        Set<Integer[]> redPositionsFour = new HashSet<>();

        Set<Integer[]> bluePositionsThree = new HashSet<>();
        Set<Integer[]> bluePositionsFour = new HashSet<>();

        //find every connection between two and three, find if there is a win/three available from those

        //horizontal
        for (int row = 0; row < board[0].length; row++) {
            for (int column = 0; column < board.length; column++) {
                if (board[column][row] == Player.EMPTY) {

                    //check red
                    int count = 0;

                    int columnIndex = column - 1;
                    try {
                        while (board[columnIndex][row] == Player.RED) {
                            count++;
                            columnIndex--;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}
                    columnIndex = column + 1;
                    try {
                        while (board[columnIndex][row] == Player.RED) {
                            count++;
                            columnIndex++;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}

                    if (count >= 3) {
                        total += POSSIBLE_FOUR_WEIGHT;
                        redPositionsFour.add(new Integer[]{column, row});
//                        System.out.println("Red 4 horizontal added from index [" + column + ", " + row + "]");
                    } else if (count == 2) {
                        total += POSSIBLE_THREE_WEIGHT;
                        redPositionsThree.add(new Integer[]{column, row});
//                        System.out.println("Red 3 horizontal added from index [" + column + ", " + row + "]");
                    }

                    //check blue

                    count = 0;

                    columnIndex = column - 1;
                    try {
                        while (board[columnIndex][row] == Player.BLUE) {
                            count++;
                            columnIndex--;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}
                    columnIndex = column + 1;
                    try {
                        while (board[columnIndex][row] == Player.BLUE) {
                            count++;
                            columnIndex++;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}

                    if (count >= 3) {
                        total -= POSSIBLE_FOUR_WEIGHT;
                        bluePositionsFour.add(new Integer[]{column, row});
//                        System.out.println("Blue 4 horizontal added from index [" + column + ", " + row + "]");
                    } else if (count == 2) {
//                        System.out.println("Blue 3 horizontal added from index [" + column + ", " + row + "]");
                        total -= POSSIBLE_THREE_WEIGHT;
                        bluePositionsThree.add(new Integer[]{column, row});
                    }

                }
            }
        }

        //vertical

        for (int column = 0; column < board.length; column++) {
            for (int row = 0; row < board[0].length; row++) {
                if (board[column][row] == Player.EMPTY) {

                    //check red
                    int count = 0;

                    int rowIndex = row - 1;
                    try {
                        while (board[column][rowIndex] == Player.RED) {
                            count++;
                            rowIndex--;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}
                    rowIndex = row + 1;
                    try {
                        while (board[column][rowIndex] == Player.RED) {
                            count++;
                            rowIndex++;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}

                    Integer[] currIndex = new Integer[]{column, row};

                    if (count >= 3 && !redPositionsFour.contains(currIndex)) {
                        total += POSSIBLE_FOUR_WEIGHT;
                        redPositionsFour.add(currIndex);
//                        System.out.println("Red 4 vertical added from index [" + column + ", " + row + "]");
                        if (redPositionsThree.contains(currIndex)) {
                            redPositionsThree.remove(new Integer[]{column, row});
                            total -= POSSIBLE_THREE_WEIGHT;
                        }
                    } else if (count == 2 && !redPositionsFour.contains(currIndex)
                            && !redPositionsThree.contains(currIndex)) {
//                        System.out.println("Red 3 vertical added from index [" + column + ", " + row + "]");
                        total += POSSIBLE_THREE_WEIGHT;
                        redPositionsThree.add(currIndex);
                    }

                    //check blue

                    count = 0;

                    rowIndex = row - 1;
                    try {
                        while (board[column][rowIndex] == Player.BLUE) {
                            count++;
                            rowIndex--;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}
                    rowIndex = row + 1;
                    try {
                        while (board[column][rowIndex] == Player.BLUE) {
                            count++;
                            rowIndex++;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}

                    currIndex = new Integer[]{column, row};

                    if (count >= 3 && !bluePositionsFour.contains(currIndex)) {
                        total -= POSSIBLE_FOUR_WEIGHT;
                        bluePositionsFour.add(currIndex);
//                        System.out.println("Blue 4 vertical added from index [" + column + ", " + row + "]");
                        if (bluePositionsThree.contains(currIndex)) {
                             bluePositionsThree.remove(currIndex);
                            total -= POSSIBLE_THREE_WEIGHT;
                        }
                    } else if (count == 2 && !bluePositionsFour.contains(currIndex)
                            && !bluePositionsThree.contains(currIndex)) {
//                        System.out.println("Blue 3 vertical added from index [" + column + ", " + row + "]");
                        total -= POSSIBLE_THREE_WEIGHT;
                        bluePositionsThree.add(currIndex);
                    }
                }
            }
        }

        // check / diagonal

        for (int column = 0; column < board.length; column++) {
            for (int row = 0; row < board[0].length; row++) {
                if (board[column][row] == Player.EMPTY) {

                    //check red
                    int count = 0;

                    int rowIndex = row - 1;
                    int colIndex = column - 1;
                    try {
                        while (board[colIndex][rowIndex] == Player.RED) {
                            count++;
                            rowIndex--;
                            colIndex--;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}
                    rowIndex = row + 1;
                    colIndex = column + 1;
                    try {
                        while (board[colIndex][rowIndex] == Player.RED) {
                            count++;
                            rowIndex++;
                            colIndex++;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}

                    Integer[] currIndex = new Integer[]{column, row};

                    if (count >= 3 && !redPositionsFour.contains(currIndex)) {
                        total += POSSIBLE_FOUR_WEIGHT;
                        redPositionsFour.add(currIndex);
//                        System.out.println("Red 4 / diagonal added from index [" + column + ", " + row + "]");
                        if (redPositionsThree.contains(currIndex)) {
                            redPositionsThree.remove(new Integer[]{column, row});
                            total -= POSSIBLE_THREE_WEIGHT;
                        }
                    } else if (count == 2 && !redPositionsFour.contains(currIndex)
                            && !redPositionsThree.contains(currIndex)) {
//                        System.out.println("Red 3 / diagonal added from index [" + column + ", " + row + "]");
                        total += POSSIBLE_THREE_WEIGHT;
                        redPositionsThree.add(currIndex);
                    }

                    //check blue

                    count = 0;

                    rowIndex = row - 1;
                    colIndex = column - 1;
                    try {
                        while (board[colIndex][rowIndex] == Player.BLUE) {
                            count++;
                            rowIndex--;
                            colIndex--;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}
                    rowIndex = row + 1;
                    colIndex = column + 1;
                    try {
                        while (board[colIndex][rowIndex] == Player.BLUE) {
                            count++;
                            rowIndex++;
                            colIndex++;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}

                    currIndex = new Integer[]{column, row};

                    if (count >= 3 && !bluePositionsFour.contains(currIndex)) {
                        total -= POSSIBLE_FOUR_WEIGHT;
                        bluePositionsFour.add(currIndex);
//                        System.out.println("Blue 4 / diagonal added from index [" + column + ", " + row + "]");
                        if (bluePositionsThree.contains(currIndex)) {
                            bluePositionsThree.remove(currIndex);
                            total -= POSSIBLE_THREE_WEIGHT;
                        }
                    } else if (count == 2 && !bluePositionsFour.contains(currIndex)
                            && !bluePositionsThree.contains(currIndex)) {
//                        System.out.println("Blue 3 / diagonal added from index [" + column + ", " + row + "]");
                        total -= POSSIBLE_THREE_WEIGHT;
                        bluePositionsThree.add(currIndex);
                    }
                }
            }
        }

        //check \ diagonal

        for (int column = 0; column < board.length; column++) {
            for (int row = 0; row < board[0].length; row++) {
                if (board[column][row] == Player.EMPTY) {

                    //check red
                    int count = 0;

                    int rowIndex = row - 1;
                    int colIndex = column + 1;
                    try {
                        while (board[colIndex][rowIndex] == Player.RED) {
                            count++;
                            rowIndex--;
                            colIndex++;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}
                    rowIndex = row + 1;
                    colIndex = column - 1;
                    try {
                        while (board[colIndex][rowIndex] == Player.RED) {
                            count++;
                            rowIndex++;
                            colIndex--;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}

                    Integer[] currIndex = new Integer[]{column, row};

                    if (count >= 3 && !redPositionsFour.contains(currIndex)) {
                        total += POSSIBLE_FOUR_WEIGHT;
                        redPositionsFour.add(currIndex);
//                        System.out.println("Red 4 \\ diagonal added from index [" + column + ", " + row + "]");
                        if (redPositionsThree.contains(currIndex)) {
                            redPositionsThree.remove(new Integer[]{column, row});
                            total -= POSSIBLE_THREE_WEIGHT;
                        }
                    } else if (count == 2 && !redPositionsFour.contains(currIndex)
                            && !redPositionsThree.contains(currIndex)) {
//                        System.out.println("Red 3 \\ diagonal added from index [" + column + ", " + row + "]");
                        total += POSSIBLE_THREE_WEIGHT;
                        redPositionsThree.add(currIndex);
                    }

                    //check blue

                    count = 0;

                    rowIndex = row - 1;
                    colIndex = column + 1;
                    try {
                        while (board[colIndex][rowIndex] == Player.BLUE) {
                            count++;
                            rowIndex--;
                            colIndex++;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}
                    rowIndex = row + 1;
                    colIndex = column - 1;
                    try {
                        while (board[colIndex][rowIndex] == Player.BLUE) {
                            count++;
                            rowIndex++;
                            colIndex--;
                        }
                    } catch(IndexOutOfBoundsException ignored) {}

                    currIndex = new Integer[]{column, row};

                    if (count >= 3 && !bluePositionsFour.contains(currIndex)) {
                        total -= POSSIBLE_FOUR_WEIGHT;
                        bluePositionsFour.add(currIndex);
//                        System.out.println("Blue 4 \\ diagonal added from index [" + column + ", " + row + "]");
                        if (bluePositionsThree.contains(currIndex)) {
                            bluePositionsThree.remove(currIndex);
                            total -= POSSIBLE_THREE_WEIGHT;
                        }
                    } else if (count == 2 && !bluePositionsFour.contains(currIndex)
                            && !bluePositionsThree.contains(currIndex)) {
//                        System.out.println("Blue 3 \\ diagonal added from index [" + column + ", " + row + "]");
                        total -= POSSIBLE_THREE_WEIGHT;
                        bluePositionsThree.add(currIndex);
                    }
                }
            }
        }

        return total;
    }
    public ArrayList<Integer> getNextMoves() {
        ArrayList<Integer> pos = new ArrayList<>();
        for (int column = 0; column < board.length; column++) {
            for (int row = 0; row < board[0].length; row++) {
                if (board[column][row] == Player.EMPTY) {
                    pos.add(column);
                    break;
                }
            }
        }
        return pos;
    }
    public ArrayList<GameState> generateNextStates() {
        ArrayList<Integer> nextMoves = getNextMoves();
        ArrayList<GameState> nextStates = new ArrayList<>(nextMoves.size());
        for (Integer move : nextMoves) {
            GameState nextState = new GameState(this);
            nextState.addToTop(move);
            nextStates.add(nextState);
        }
        return nextStates;
    }
    public static double minimax(GameState currState, int depth, double alpha, double beta, boolean maximizing) {
        if (depth == 0 || currState.isWin()) {
            return currState.getStateValue();
        }

        if (maximizing) {
            double maxEval = Integer.MIN_VALUE;
            for (GameState child : currState.generateNextStates()) {
                double eval = minimax(child, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            double minEval = Integer.MAX_VALUE;
            for (GameState child : currState.generateNextStates()) {
                double eval = minimax(child, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.max(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }
}
