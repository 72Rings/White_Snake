import java.util.ArrayList;
import java.util.List;

public class DeckBuilder {

    private DeckBuilder() {
        // Utility class
    }

    public static List<Card> buildDeck() {
        List<Card> deck = new ArrayList<>();

        addCards(deck, "Member", 13);
        addCards(deck, "Noble", 10);
        addCards(deck, "Governor", 7);
        addCards(deck, "Emperor", 4);
        addCards(deck, "White Snake", 5);

        deck.add(new Card("They", 9));
        deck.add(new Card("They", 10));
        deck.add(new Card("The Davis", Card.INFINITY));

        return deck;
    }

    private static void addCards(List<Card> deck, String type, int maxNumber) {
        for (int i = 1; i <= maxNumber; i++) {
            deck.add(new Card(type, i));
        }
    }
}
