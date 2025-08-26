import java.util.ArrayList;
import java.util.List;

public class Pot {
    public int amount;
    public List<Player> eligiblePlayers;

    public Pot(int amount) {
        this.amount = amount;
        this.eligiblePlayers = new ArrayList<>();
    }

    public void addPlayer(Player p) {
        if (!eligiblePlayers.contains(p)) {
            eligiblePlayers.add(p);
        }
    }
}