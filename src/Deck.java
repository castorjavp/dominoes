import java.io.IOException;
import java.util.*;

public class Deck {
    private Domino[] dominoes = new Domino[28];
    private Player[] players = new Player[4];
    private ArrayList<Domino> dominoesOnTable = new ArrayList<>();
    private Player playerWithTurn = new Player();
    private Player winner = new Player();
    private HashMap<Integer, Integer> numberFrequency = new HashMap<>();
    private int currentVal;
    private static boolean isDoubleSixOnTable = false;
    private static boolean gameOver = false;
    private static int plays = 0;

    public static void clearScreen(){
        System.out.print("\033[H\033[2J");
    }

    public static void sleep() throws IOException {
        System.in.read();
        
    }

    public void shuffleDominoes(){
        List<Domino> dominoList = Arrays.asList(this.dominoes);
        Collections.shuffle(dominoList);
        dominoList.toArray(this.dominoes);
    }

    public Deck() throws IOException {
        //Creating all 28 dominoes
        int i = 0;
        int topSide = 0;
        int bottomSide = 0;
        int j=0;
        while(i < 28){
            if(bottomSide == 7){
                j++;
                topSide++;
                bottomSide=j;
            }
            this.dominoes[i] = new Domino(topSide, bottomSide);
            bottomSide++;
            i++;
        }
        //Creating all 4 players
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        this.players[0] = new Player(scanner.nextLine());
        this.players[1] = new Player("Bot 1");
        this.players[2] = new Player("Bot 2");
        this.players[3] = new Player("Bot 3");

        this.shuffleDominoes();
        this.dealDominoes();
        System.out.println("Game started. Here are your dominoes: ");
        for(Domino domino : this.players[0].getHand()){
            System.out.println(domino + " ");
        }
        this.playerWithTurn = this.findPlayerWithDoubleSix();
        sleep();
        clearScreen();
    }

    public void dealDominoes(){
        this.players[0].getHand().addAll(Arrays.asList(this.dominoes).subList(0, 7));
        this.players[1].getHand().addAll(Arrays.asList(this.dominoes).subList(7, 14));
        this.players[2].getHand().addAll(Arrays.asList(this.dominoes).subList(14, 21));
        this.players[3].getHand().addAll(Arrays.asList(this.dominoes).subList(21, 28));
    }

    public Player findPlayerWithDoubleSix(){
        for(Player player: this.players){
            for(int i = 0; i < 7; i++){
                if(player.getHand().get(i).toString().equals("6-6")){
                    return player;
                }
            }
        }
        return new Player("error");//6-6 not found
    }

    public Player nextPlayer(Player playerWithTurn){
        int i = 0;
        for(Player player : this.players){
            if(player.getName().equals(playerWithTurn.getName())){
                if(i==3) {
                    return this.players[0];
                }else{
                    return this.players[i+1];
                }
            }
            i++;
        }
        return new Player("error");//If no next player found
    }

    public void printDominoesToPlay(Player player){
        System.out.println("Dominoes available: ");
        int i = 1;
        for (Domino domino : player.getHand()){
            System.out.println(i + ": " + domino);
            i++;
        }
        System.out.println("-1: Skip");
    }

    public boolean getGameOver(){
        return gameOver;
    }

    public Player[] getPlayers(){
        return this.players;
    }

    public Player getWinner(){
        return this.winner;
    }

    public void playDomino(Player player, Domino domino){
        this.dominoesOnTable.add(domino);
        player.getHand().remove(domino);//test
        System.out.println( player.getName() + " played: "  + domino + "\nThe table is now:\n");
        for(Domino dominoInTable : dominoesOnTable){
            System.out.print(dominoInTable + " ");
        }
        isDoubleSixOnTable = true;
        System.out.println();
    }

    public boolean canPlayDominoOnBothSides(Domino domino, int endRight, int endLeft){
        int count = 0;
        int right = domino.getBottomSide();
        int left = domino.getTopSide();
        if(domino.isDouble()){
            return false;
        }
        if(left == endLeft){
            count++;
        }
        if(left == endRight){
            count++;
        }
        if(right == endRight){
            count++;
        }
        if(right == endLeft){
            count++;
        }
        return count > 1;
    }

    public ArrayList<Domino> getDominoesAvailableToPlay(Player player){
        int right = this.dominoesOnTable.get(this.dominoesOnTable.size()-1).getBottomSide();
        int left = this.dominoesOnTable.get(0).getTopSide();

        ArrayList<Domino> dominoesAvailableToPlay = new ArrayList<>();
        for(Domino domino : player.getHand()){
            if(domino.getBottomSide() == right || domino.getBottomSide() == left || domino.getTopSide() == right || domino.getTopSide() == left){
                dominoesAvailableToPlay.add(domino);
            }
        }
        return dominoesAvailableToPlay;
    }

    public HashMap<Domino, Integer> calculateDominoScores(ArrayList<Domino> availableDominoes){
        HashMap<Domino , Integer> scores = new HashMap<>();
        HashMap<Integer, Integer> handFreq = new HashMap<>();
        int val;
        for(Domino domino : this.playerWithTurn.getHand()){
            if(domino.isDouble()){
                val = handFreq.getOrDefault(domino.getTopSide(), 0);
                handFreq.put(domino.getTopSide(), val+1);
            }
            else{
                val = handFreq.getOrDefault(domino.getTopSide(), 0);
                handFreq.put(domino.getTopSide(), val+1);
                val = handFreq.getOrDefault(domino.getBottomSide(), 0);
                handFreq.put(domino.getBottomSide(), val+1);
            }
        }
        for(Domino domino : availableDominoes){
            if(domino.isDouble()){
                scores.put(domino, Integer.MAX_VALUE);
            }
            else{
                int topScore = (handFreq.get(domino.getTopSide()));
                int bottomScore = (handFreq.get(domino.getBottomSide()));
                scores.put(domino, topScore + bottomScore);
            }
        }
        return scores;
    }

    public void addToFrequency(Domino domino){
        if(domino.isDouble()){
            this.currentVal = this.numberFrequency.getOrDefault(domino.getTopSide(), 0);
            this.numberFrequency.put(domino.getTopSide(), this.currentVal+1);
        }
        else{
            this.currentVal = this.numberFrequency.getOrDefault(domino.getTopSide(), 0);
            this.numberFrequency.put(domino.getTopSide(), this.currentVal+1);
            this.currentVal = this.numberFrequency.getOrDefault(domino.getBottomSide(), 0);
            this.numberFrequency.put(domino.getBottomSide(), this.currentVal+1);
        }
    }

    public boolean isGameBlocked(){
        ArrayList<Domino> player1Available = getDominoesAvailableToPlay(this.players[0]);
        ArrayList<Domino> player2Available = getDominoesAvailableToPlay(this.players[1]);
        ArrayList<Domino> player3Available = getDominoesAvailableToPlay(this.players[2]);
        ArrayList<Domino> player4Available = getDominoesAvailableToPlay(this.players[3]);
        return player1Available.size() == 0 && player2Available.size() == 0 && player3Available.size() == 0 && player4Available.size() == 0;
    }

    public int calculatePlayerPoints(Player player){
        int count = 0;
        for(Domino domino : player.getHand()){
            count += domino.getBottomSide();
            count += domino.getTopSide();
        }
        return count;
    }

    public void next() throws IOException {
        Scanner scanner = new Scanner(System.in);
        if(plays > 2) {
            if (this.isGameBlocked()) {
                gameOver = true;
                int[] playerPoints = new int[4];
                playerPoints[0] = this.calculatePlayerPoints(this.players[0]);
                playerPoints[1] = this.calculatePlayerPoints(this.players[1]);
                playerPoints[2] = this.calculatePlayerPoints(this.players[2]);
                playerPoints[3] = this.calculatePlayerPoints(this.players[3]);
                int minPoints = Math.min(Math.min(Math.min(playerPoints[3], playerPoints[2]), playerPoints[1]), playerPoints[0]);
                int winner = -1;
                System.out.println("The game is blocked. These are each player's points:\n" + this.players[0].getName() + ": " + playerPoints[0] + "\n" + this.players[1].getName() + ": " + playerPoints[1] + "\n" + this.players[2].getName() + ": " + playerPoints[2] + "\n" + this.players[3].getName() + ": " + playerPoints[3] + "\n");
                int numberOfPlayersWithMinPoints = 0;
                for (int i = 0; i < 4; i++) {
                    if (playerPoints[i] == minPoints) {
                        numberOfPlayersWithMinPoints++;
                        winner = i;
                    }
                }
                if (numberOfPlayersWithMinPoints > 1) {
                    System.out.println("There is a tie! Game Over");
                } else {
                    System.out.println(this.players[winner].getName().toUpperCase() + " WON!! Game Over");
                }
                return;
            }
        }

        if(!isDoubleSixOnTable && this.playerWithTurn.getName().equals(this.players[0].getName())){
            System.out.println("It is your turn.");
            this.printDominoesToPlay(this.playerWithTurn);
            System.out.print("Which domino would you like to play: ");
            int dominoPicked = scanner.nextInt();
            dominoPicked--;
            while(dominoPicked <= -1 || dominoPicked > this.players[0].getHand().size()-1){
                System.out.print("Invalid number, please try again\nWhich domino would you like to play: ");
                dominoPicked = scanner.nextInt();
                dominoPicked--;
            }
            while(this.players[0].findIndexOfDoubleSix() != dominoPicked){
                System.out.print("Warning! You must start a game with 6-6\nWhich domino would you like to play: ");
                dominoPicked = scanner.nextInt();
                dominoPicked--;
            }
            Domino dominoPlayed = this.playerWithTurn.getHand().get(dominoPicked);
            this.addToFrequency(dominoPlayed);
            this.dominoesOnTable.add(dominoPlayed);
            this.playerWithTurn.getHand().remove(dominoPicked);
            System.out.println("You played: " + dominoPlayed);
            System.out.print("The table is now:\n");
            for(Domino domino: dominoesOnTable){
                System.out.print(domino + " ");
            }
            isDoubleSixOnTable = true;
            System.out.println();
            sleep();
            clearScreen();
        }
        else if(!isDoubleSixOnTable && !(this.playerWithTurn.getName().equals(this.players[0].getName()))){
            System.out.println("It is " + this.playerWithTurn.getName() + "'s turn.");
            Domino dominoPlayed = this.playerWithTurn.getHand().get(this.playerWithTurn.findIndexOfDoubleSix());
            this.addToFrequency(dominoPlayed);
            this.dominoesOnTable.add(dominoPlayed);
            this.playerWithTurn.getHand().remove(dominoPlayed);
            System.out.println(this.playerWithTurn.getName() + " played " + dominoPlayed);
            System.out.print("The table is now:\n");
            for(Domino domino: dominoesOnTable){
                System.out.print(domino + " ");
            }
            isDoubleSixOnTable = true;
            System.out.println();
            sleep();
            clearScreen();
        }
        else if(isDoubleSixOnTable && this.playerWithTurn.getName().equals(this.players[0].getName())){
            ArrayList<Domino> dominoesAvailableToPlay = this.getDominoesAvailableToPlay(this.playerWithTurn);
            System.out.println("It is your turn.");
            this.printDominoesToPlay(playerWithTurn);
            System.out.print("Which domino would you like to play: ");
            int dominoPicked = scanner.nextInt();
            dominoPicked--;
            if(dominoesAvailableToPlay.size() == 0){
                while(dominoPicked != -2){
                    System.out.print("You can not play that domino.\nWhich domino would you like to play: ");
                    dominoPicked = scanner.nextInt();
                    dominoPicked--;
                }
                System.out.println("You skipped turn");
                System.out.print("The table is now:\n");
                for (Domino domino : dominoesOnTable) {
                    System.out.print(domino + " ");
                }
            }
            else{
                while(dominoPicked <= -1 || dominoPicked > this.players[0].getHand().size()-1 || !dominoesAvailableToPlay.contains(this.players[0].getHand().get(dominoPicked))){
                    if(dominoPicked == -2){
                        System.out.print("You can not skip.\nWhich domino would you like to play: ");
                    }
                    else{
                        System.out.print("You can not play that domino.\nWhich domino would you like to play: ");
                    }
                    dominoPicked = scanner.nextInt();
                    dominoPicked--;
                }
                Domino dominoPlayed = this.playerWithTurn.getHand().get(dominoPicked);
                int endRight = this.dominoesOnTable.get(this.dominoesOnTable.size()-1).getBottomSide();
                int endLeft = this.dominoesOnTable.get(0).getTopSide();
                if(plays > 1 && this.canPlayDominoOnBothSides(dominoPlayed, endRight, endLeft)){
                    System.out.print("You can play this domino on both ends. Which side would you like to play? 1 for left, 2 for right: ");
                    int input = scanner.nextInt();
                    while(input != 1 && input != 2){
                        System.out.print("You can play this domino on both ends. Which side would you like to play? 1 for left, 2 for right: ");
                        input = scanner.nextInt();
                    }
                    if(input == 1){
                        if(endLeft == dominoPlayed.getTopSide()){
                            dominoPlayed.reverseDomino();
                        }
                        this.dominoesOnTable.add(0, dominoPlayed);
                    }
                    else {
                        if(endRight == dominoPlayed.getBottomSide()){
                            dominoPlayed.reverseDomino();
                        }
                        this.dominoesOnTable.add(dominoPlayed);
                    }
                }
                else {
                    if (endRight == dominoPlayed.getTopSide()) {
                        this.dominoesOnTable.add(dominoPlayed);
                    } else if (endRight == dominoPlayed.getBottomSide()) {
                        dominoPlayed.reverseDomino();
                        this.dominoesOnTable.add(dominoPlayed);
                    } else if (endLeft == dominoPlayed.getTopSide()) {
                        dominoPlayed.reverseDomino();
                        this.dominoesOnTable.add(0, dominoPlayed);
                    } else if (endLeft == dominoPlayed.getBottomSide()) {
                        this.dominoesOnTable.add(0, dominoPlayed);
                    }
                }
                this.addToFrequency(dominoPlayed);
                this.playerWithTurn.getHand().remove(dominoPicked);
                System.out.println("You played: " + dominoPlayed);
                System.out.print("The table is now:\n");
                for (Domino domino : dominoesOnTable) {
                    System.out.print(domino + " ");
                }
            }
            System.out.println();
            sleep();
            clearScreen();
        }
        else if(isDoubleSixOnTable && !(this.playerWithTurn.getName().equals(this.players[0].getName()))) {
            System.out.println("It is " + this.playerWithTurn.getName() + "'s turn.");
            ArrayList<Domino> availableDominoes = this.getDominoesAvailableToPlay(this.playerWithTurn);
            if (availableDominoes.size() == 0) {
                System.out.println(this.playerWithTurn.getName() + " skipped turn.");
                System.out.print("The table is now:\n");
                for (Domino domino : dominoesOnTable) {
                    System.out.print(domino + " ");
                }
            } else {
                HashMap<Domino, Integer> scores = calculateDominoScores(availableDominoes);

                Domino dominoPlayed = null;
                int maxValue = Integer.MIN_VALUE;
                for (Map.Entry<Domino, Integer> entry : scores.entrySet()) {
                    if (entry.getValue() > maxValue) {
                        maxValue = entry.getValue();
                        dominoPlayed = entry.getKey();
                    }
                }


                int endRight = this.dominoesOnTable.get(this.dominoesOnTable.size() - 1).getBottomSide();
                int endLeft = this.dominoesOnTable.get(0).getTopSide();
                assert dominoPlayed != null;
                this.addToFrequency(dominoPlayed);
                if (endRight == dominoPlayed.getBottomSide()) {
                    dominoPlayed.reverseDomino();
                    this.dominoesOnTable.add(dominoPlayed);
                }
                else if (endRight == dominoPlayed.getTopSide()) {
                    this.dominoesOnTable.add(dominoPlayed);
                }
                else if (endLeft == dominoPlayed.getTopSide()) {
                    dominoPlayed.reverseDomino();
                    this.dominoesOnTable.add(0, dominoPlayed);
                }
                else if (endLeft == dominoPlayed.getBottomSide()) {
                    this.dominoesOnTable.add(0, dominoPlayed);
                }
                this.playerWithTurn.getHand().remove(dominoPlayed);
                System.out.println(playerWithTurn.getName() + " played: " + dominoPlayed);
                System.out.print("The table is now:\n");
                for (Domino domino : this.dominoesOnTable) {
                    System.out.print(domino + " ");
                }
            }
            System.out.println();
            sleep();
            clearScreen();
        }
        plays++;
        if(this.playerWithTurn.getHand().size() == 0){
            gameOver = true;
            winner = this.playerWithTurn;
            System.out.println(this.playerWithTurn.getName().toUpperCase() + " WON!! Game Over");
        }
        this.playerWithTurn = this.nextPlayer(this.playerWithTurn);
    }
}
