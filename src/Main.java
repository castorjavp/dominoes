import java.io.IOException;

public class Main{
    public static void main(String[] args) throws IOException {
        Deck myDeck = new Deck();
        while(!myDeck.getGameOver()) {
            myDeck.next();
        }
        Player winner = myDeck.getWinner();
    }
}