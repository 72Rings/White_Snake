import java.util.Collections;
import java.util.List;

public class DeckDemo {
    public static void main(String[] args) {
        List<Card> deck = DeckBuilder.buildDeck();

        System.out.println("Initial deck:");
        deck.forEach(System.out::println);

        Collections.shuffle(deck);

        System.out.println("\nShuffled deck:");
        deck.forEach(System.out::println);

        System.out.println("\nDeck size: " + deck.size());
    }
}
