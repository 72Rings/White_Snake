import java.util.Collections;
import java.util.List;

public class DeckDemo {
    // Test function for Player class
    public static void testPlayer() {
        Player p = new Player("Alice");
        System.out.println("Created player: " + p);

        // Add chips and remove chips
        p.addChips(10);
        p.bet(5); // Use bet instead of removeChips
        System.out.println("After chip changes: " + p);

        // Fold and reset fold count
        p.fold();
        p.fold();
        System.out.println("After folding twice: " + p);
        p.resetFoldCount();
        System.out.println("After reset fold count: " + p);

        // Add cards to hand
        p.addCardToHand(new Card("Member", 1));
        p.addCardToHand(new Card("Noble", 2));
        System.out.println("After adding cards: " + p);
    }

    public static void main(String[] args) {
        DeckBuilder.DeckConfig config = new DeckBuilder.DeckConfig();
        List<Card> deck = DeckBuilder.buildDeck(config);

        System.out.println("Initial deck:");
        deck.forEach(System.out::println);

        Collections.shuffle(deck);

        System.out.println("\nShuffled deck:");
        deck.forEach(System.out::println);

        System.out.println("\nDeck size: " + deck.size());

        // Run Player test
        System.out.println("\n--- Player Test ---");
        testPlayer();
    }
}