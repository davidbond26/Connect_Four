import java.util.*;

public class Main {
    public static void main(String[] args) {
        runGameVersusAI();
    }

    public static void runGameVersusAI() {
        Set<String> validInputs = new HashSet<>(List.of(new String[]{"1", "2", "3", "4", "5", "6", "7"}));
        GameState gs = new GameState();
        Scanner sc = new Scanner(System.in);

        System.out.println("Do you want to go first? (y/n)");
        String input = sc.nextLine();
        while (!(input.equalsIgnoreCase("y") || input.equalsIgnoreCase("n"))) {
            System.out.println("Invalid input");
            input = sc.nextLine();
        }


        gs.printBoard();
        if (input.equalsIgnoreCase("y")) {
            while (!gs.isWin()) {

                System.out.println("Player, enter valid column (1-7): ");
                String p1Input;
                while (true) {
                    p1Input = sc.nextLine();
                    if (validInputs.contains(p1Input)) {
                        if (gs.addToTop(Integer.parseInt(p1Input) - 1)) {
                            break;
                        } else {
                            System.out.println("That column is full");
                        }
                    } else {
                        System.out.println("Invalid input");
                    }
                }

                gs.printBoard();
                if (gs.isWin()) {
                    break;
                }

                System.out.println("AI Turn... ");
                ArrayList<Integer> nextMoves = gs.getNextMoves();
                ArrayList<GameState> nextStates = gs.generateNextStates();

                double minVal = Integer.MAX_VALUE;
                int col = -1;
                for (int i = 0; i < nextStates.size(); i++) {
                    double eval = GameState.minimax(nextStates.get(i), 6, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
                    if (eval < minVal) {
                        minVal = eval;
                        col = nextMoves.get(i);
                    }
                }

                gs.addToTop(col);
                System.out.println("AI placed in column " + (col + 1));
                gs.printBoard();

            }
            System.out.println(gs.getLastPlayer() + " Wins!!");
        } else {
            while (!gs.isWin()) {

                System.out.println("AI Turn... ");
                ArrayList<Integer> nextMoves = gs.getNextMoves();
                ArrayList<GameState> nextStates = gs.generateNextStates();

                double maxVal = Integer.MIN_VALUE;
                int col = -1;
                for (int i = 0; i < nextStates.size(); i++) {
                    double eval = GameState.minimax(nextStates.get(i), 6, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                    if (eval > maxVal) {
                        maxVal = eval;
                        col = nextMoves.get(i);
                    }
                }

                gs.addToTop(col);
                System.out.println("AI placed in column " + (col + 1));
                gs.printBoard();

                if (gs.isWin()) {
                    break;
                }

                System.out.println("Player, enter valid column (1-7): ");
                String p1Input;
                while (true) {
                    p1Input = sc.nextLine();
                    if (validInputs.contains(p1Input)) {
                        if (gs.addToTop(Integer.parseInt(p1Input) - 1)) {
                            break;
                        } else {
                            System.out.println("That column is full");
                        }
                    } else {
                        System.out.println("Invalid input");
                    }
                }
                gs.printBoard();

            }
            System.out.println(gs.getLastPlayer() + " Wins!!");
        }


    }

    public static void runTestGame() {
        Set<String> validInputs = new HashSet<>(List.of(new String[]{"1", "2", "3", "4", "5", "6", "7"}));
        GameState gs = new GameState();
        Scanner sc = new Scanner(System.in);

        gs.printBoard();
        System.out.println("State Value: " + gs.getStateValue());
        while (!gs.isWin()) {

            System.out.println("Player 1, enter valid column (1-7): ");
            String p1Input;
            while (true) {
                p1Input = sc.nextLine();
                if (validInputs.contains(p1Input)) {
                    if (gs.addToTop(Integer.parseInt(p1Input) - 1)) {
                        break;
                    } else {
                        System.out.println("That column is full");
                    }
                } else {
                    System.out.println("Invalid input");
                }
            }
            gs.printBoard();
//            System.out.println("State Value minimax: " + GameState.minimax(gs, 5, false));
            if (gs.isWin()) {
                break;
            }

            //player 2
            System.out.println("Player 2, enter valid column (1-7): ");
            String p2Input;
            while (true) {
                p2Input = sc.nextLine();
                if (validInputs.contains(p2Input)) {
                    if (gs.addToTop(Integer.parseInt(p2Input) - 1)) {
                        break;
                    } else {
                        System.out.println("That column is full");
                    }
                } else {
                    System.out.println("Invalid input");
                }
            }
            gs.printBoard();
//            System.out.println("State Value: " + gs.getStateValue());
//            System.out.println("State Value minimax: " + GameState.minimax(gs, 5, true));
        }
        System.out.println(gs.getLastPlayer() + " Wins!!");
    }
}