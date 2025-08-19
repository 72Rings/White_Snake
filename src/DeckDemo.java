import java.util.Collections;
import java.util.List;

public class DeckDemo {
    public static void main(String[] args) {
        DeckBuilder.DeckConfig config = new DeckBuilder.DeckConfig();
        List<Card> deck = DeckBuilder.buildDeck(config);

        System.out.println("Initial deck:");
        deck.forEach(System.out::println);

        Collections.shuffle(deck);

        System.out.println("\nShuffled deck:");
        deck.forEach(System.out::println);

        System.out.println("\nDeck size: " + deck.size());
    }
}