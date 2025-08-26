import java.util.*;
public class Round {
    private List<Player> players;
    private List<Card> deck;
    private List<Pot> pots;
    private int highestBet;
    private int startingPlayerIndex;

    public Round(List<Player> players, List<Card> deck, int pot) {
        this.players = players;
        this.deck = deck;
        this.pots = new ArrayList<>();
        // Initial main pot
        Pot mainPot = new Pot(0);
        for (Player p : players) {
            if (!p.isEliminated()) mainPot.addPlayer(p);
        }
        this.pots.add(mainPot);
        this.highestBet = 0;
        this.startingPlayerIndex = new Random().nextInt(players.size());
    }

    public void play() {
        for (Player p : players) {
            p.clearHand();
            p.resetFold();
        }

        Collections.shuffle(deck);
        for (Player p : players) {
            if (!p.isEliminated()) {
                for (int i = 0; i < 2; i++) {
                    if (!deck.isEmpty()) {
                        p.addCardToHand(deck.remove(0));
                    }
                }
            }
        }

        displayGameState(null);

        Scanner scanner = new Scanner(System.in);
        boolean bettingActive = true;
        int[] bets = new int[players.size()];
        boolean[] allIn = new boolean[players.size()];
        Arrays.fill(allIn, false);

        while (bettingActive) {
            bettingActive = false;
            for (int i = 0; i < players.size(); i++) {
                int idx = (startingPlayerIndex + i) % players.size();
                Player p = players.get(idx);
                if (p.isEliminated() || p.isFolded() || allIn[idx]) continue;

                int toCall = highestBet - bets[idx];
                int maxBet = p.getChips();

                int bet = 0;
                if (p.isBot()) {
                    bet = botBetLogic(p, toCall, maxBet);
                } else {
                    displayGameState(p);
                    System.out.printf("Your turn, %s! To call: %d chips. Enter bet amount (number), or 'fold' to fold: ", p.getName(), toCall);
                    String input = scanner.next();
                    if (input.equalsIgnoreCase("fold")) {
                        p.fold();
                        System.out.println("You folded.");
                        continue;
                    }
                    try {
                        bet = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        bet = toCall;
                    }
                }

                if (bet < toCall) {
                    p.fold();
                    System.out.println(p.getName() + " folds (did not match highest bet).");
                } else if (bet >= maxBet) {
                    // All-in
                    bet = maxBet;
                    p.bet(bet);
                    bets[idx] += bet;
                    allIn[idx] = true;
                    System.out.println(p.getName() + " goes all-in with " + bet + " chips.");
                    addToPots(p, bets[idx]);
                    if (bet > highestBet) {
                        highestBet = bet;
                        bettingActive = true;
                    }
                } else {
                    p.bet(bet);
                    bets[idx] += bet;
                    System.out.println(p.getName() + " bets " + bet + " chips.");
                    addToPots(p, bets[idx]);
                    if (bet > highestBet) {
                        highestBet = bet;
                        bettingActive = true;
                    }
                }
                displayGameState(p);
            }
        }

        // Draw 3 more cards for each active player
        for (Player p : players) {
            if (p.isEliminated() || p.isFolded()) continue;
            for (int i = 0; i < 3; i++) {
                if (!deck.isEmpty()) {
                    p.addCardToHand(deck.remove(0));
                }
            }
        }

        System.out.println("\nHands after drawing:");
        for (Player p : players) {
            if (!p.isBot()) {
                System.out.println(p.getName() + "'s hand: " + p.getHand());
            }
        }

        // TODO: White Snake passing, scoring, pot distribution

        for (Player p : players) {
            if (!p.isFolded() && !p.isEliminated()) {
                p.resetFoldCount();
            }
            p.resetFold();
        }
    }

    // Add player bets to pots, create side pots if needed
    private void addToPots(Player p, int totalBet) {
        int remainingBet = totalBet;
        for (Pot pot : pots) {
            if (pot.eligiblePlayers.contains(p)) {
                int minBet = Integer.MAX_VALUE;
                for (Player ep : pot.eligiblePlayers) {
                    minBet = Math.min(minBet, ep.getChips() + ep.getChips()); // chips already bet + chips left
                }
                int add = Math.min(remainingBet, minBet);
                pot.amount += add;
                remainingBet -= add;
                if (remainingBet <= 0) break;
            }
        }
        if (remainingBet > 0) {
            Pot sidePot = new Pot(remainingBet);
            sidePot.addPlayer(p);
            pots.add(sidePot);
        }
    }

    public int getTotalPot() {
        int sum = 0;
        for (Pot pot : pots) sum += pot.amount;
        return sum;
    }

    private void displayGameState(Player currentPlayer) {
        System.out.println("\n--- Game State ---");
        System.out.println("Total Pot: " + getTotalPot());
        for (Pot pot : pots) {
            System.out.print("Pot (" + pot.amount + "): ");
            for (Player p : pot.eligiblePlayers) {
                System.out.print(p.getName() + " ");
            }
            System.out.println();
        }
        for (Player p : players) {
            System.out.println(p.displayStatus());
        }
        if (currentPlayer != null && !currentPlayer.isBot()) {
            System.out.println(currentPlayer.getName() + "'s hand: " + currentPlayer.getHand());
        }
    }

    // Improved bot betting logic for all-in
    private int botBetLogic(Player bot, int toCall, int maxBet) {
        if (toCall >= maxBet) return maxBet; // bot goes all-in if can't call
        if (toCall == 0) return Math.min(5, maxBet); // bot starts with a small bet
        if (toCall > maxBet / 2) return 0; // bot folds if call is too high
        return toCall; // bot matches the bet
    }
}
