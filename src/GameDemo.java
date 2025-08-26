import java.util.ArrayList;
import java.util.List;

public class GameDemo {
    public static void main(String[] args) {
        // Create player names: first is human, rest are bots
        List<String> playerNames = new ArrayList<>();
        playerNames.add("You");
        playerNames.add("Bot1");
        playerNames.add("Bot2");

        // Initialize the game
        Game game = new Game(playerNames);

        // Start the game loop
        game.start();

        // After game ends, print final chip counts and status
        System.out.println("\n--- Final Results ---");
        for (Player p : game.getPlayers()) {
            System.out.println(p.displayStatus());
        }
    }
}
