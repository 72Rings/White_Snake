import java.util.ArrayList;
import java.util.List;

public class DeckBuilder {

    public static class DeckConfig {
        public int memberCount = 13;
        public int nobleCount = 10;
        public int governorCount = 7;
        public int emperorCount = 4;
        public int whiteSnakeCount = 5;
        public int theyCount = 2;
        public int davisCount = 1;
    }

    private DeckBuilder() {
        // Utility class
    }

    public static List<Card> buildDeck(DeckConfig config) {
        List<Card> deck = new ArrayList<>();

        addCards(deck, "Member", config.memberCount, 1);
        addCards(deck, "Noble", config.nobleCount, 1);
        addCards(deck, "Governor", config.governorCount, 1);
        addCards(deck, "Emperor", config.emperorCount, 1);
        addCards(deck, "White Snake", config.whiteSnakeCount, 1);

        addCards(deck, "They", config.theyCount, 9);

        for (int i = 0; i < config.davisCount; i++) {
            deck.add(new Card("The Davis", Card.INFINITY));
        }

        return deck;
    }

    // startNumber lets you control the starting number for "They" cards
    private static void addCards(List<Card> deck, String type, int count, int startNumber) {
        for (int i = 0; i < count; i++) {
            deck.add(new Card(type, startNumber + i));
        }
    }
}
