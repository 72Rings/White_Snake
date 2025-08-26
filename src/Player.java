import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int chips;
    private List<Card> hand;
    private int foldCount;
    private boolean folded;
    private boolean eliminated;
    private boolean isBot;

    public Player(String name) {
        this(name, false);
    }

    public Player(String name, boolean isBot) {
        this.name = name;
        this.chips = 50;
        this.hand = new ArrayList<>();
        this.foldCount = 0;
        this.folded = false;
        this.eliminated = false;
        this.isBot = isBot;
    }

    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getFoldCount() {
        return foldCount;
    }

    public boolean isFolded() {
        return folded;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public boolean isBot() {
        return isBot;
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }

    public void clearHand() {
        hand.clear();
    }

    public void bet(int amount) {
        if (amount > chips) amount = chips;
        chips -= amount;
        if (chips < 0) chips = 0;
        folded = false; // betting means not folded
    }

    public void check() {
        // No chips removed, player stays in
        folded = false;
    }

    public void fold() {
        folded = true;
        foldCount++;
        if (foldCount >= 3) {
            eliminate();
        }
    }

    public void resetFold() {
        folded = false;
    }

    public void resetFoldCount() {
        foldCount = 0;
    }

    public void eliminate() {
        eliminated = true;
        chips = 0;
        folded = true;
    }

    public void addChips(int amount) {
        chips += amount;
    }

    public void passCard(Player target, Card card) {
        if (hand.remove(card)) {
            target.addCardToHand(card);
        }
    }

    // Improved bot decision logic
    public int decideBet(int minBet, int pot) {
        if (isBot) {
            // Simple bot: checks if possible, otherwise bets minimum
            if (minBet == 0) return 0;
            return Math.min(minBet, chips);
        }
        // For human, you would prompt for input in UI
        return minBet;
    }

    // Placeholder for scoring logic
    public int scoreHand() {
        // TODO: Implement full scoring based on game rules
        // For now, just count number of cards
        return hand.size();
    }

    public String displayStatus() {
        return String.format("%s: Chips=%d, Folds=%d, Folded=%b, Eliminated=%b", 
            name, chips, foldCount, folded, eliminated);
    }

    @Override
    public String toString() {
        return String.format("Player{name='%s', chips=%d, foldCount=%d, folded=%b, eliminated=%b, hand=%s}", 
            name, chips, foldCount, folded, eliminated, hand);
    }
}
