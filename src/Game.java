import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private List<Card> deck;
    private int roundNumber;

    public Game(List<String> playerNames) {
        this.players = new ArrayList<>();
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        DeckBuilder.DeckConfig config = new DeckBuilder.DeckConfig();
        this.deck = DeckBuilder.buildDeck(config);
        this.roundNumber = 1;
    }

    public void start() {
        System.out.println("Starting White Snake game!");
        while (!isGameOver()) {
            System.out.println("\n--- Round " + roundNumber + " ---");
            Round round = new Round(players, deck, 0); // Pot is now managed by Round
            round.play();
            // TODO: After round, distribute chips from pots to winners
            // TODO: Announce round winner(s)
            roundNumber++;
        }
        System.out.println("Game over!");
        // TODO: Announce overall winner(s)
    }

    private boolean isGameOver() {
        // Example: game ends after 10 rounds or one player has all chips
        if (roundNumber > 10) return true;
        int activePlayers = 0;
        for (Player p : players) {
            if (p.getChips() > 0 && !p.isEliminated()) activePlayers++;
        }
        return activePlayers <= 1;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
