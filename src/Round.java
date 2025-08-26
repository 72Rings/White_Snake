import java.util.*;
import java.util.function.BiFunction;
public class Round {
    private final List<Player> players;
    private final List<Card> deck;
    private final List<Pot> pots;
    private int highestBet;
    private final int startingPlayerIndex;

    public Round(List<Player> players, List<Card> deck, int pot) {
        this.players = players;
        this.deck = deck;
        this.pots = new ArrayList<>();
        Pot mainPot = new Pot(0);
        for (Player p : players) {
            if (!p.isEliminated()) mainPot.addPlayer(p);
        }
        this.pots.add(mainPot);
        this.highestBet = 0;
        this.startingPlayerIndex = new Random().nextInt(Math.max(1, players.size()));
    }

    // Primary play method that accepts external Scanner (so GameDemo can control it)
    public void play(Scanner scanner) {
        // Prepare round
        for (Player p : players) {
            p.clearHand();
            p.resetFold();
        }

        // If deck is low (not enough cards for full hands), rebuild and shuffle
        if (deck == null || deck.size() < players.size() * 5) {
            DeckBuilder.DeckConfig cfg = new DeckBuilder.DeckConfig();
            List<Card> fresh = DeckBuilder.buildDeck(cfg);
            deck.clear();
            deck.addAll(fresh);
        }
        Collections.shuffle(deck);
        // Deal 2 cards to each active player
        for (Player p : players) {
            if (!p.isEliminated()) {
                for (int i = 0; i < 2 && !deck.isEmpty(); i++) p.addCardToHand(deck.remove(0));
            }
        }

        displayGameState(null);

        final int n = players.size();

        // We'll reuse bets/allIn per betting "street"; initialize for first street
        int[] bets = new int[n];
        boolean[] allIn = new boolean[n];
        Arrays.fill(bets, 0);
        Arrays.fill(allIn, false);

        // Helper: run one betting round (mutates bets/allIn/highestBet)
        BiFunction<Integer, Integer, Void> runBetting = (startIdx, dummy) -> {
            int lastRaiserIdx = -1;
            while (true) {
                // If this is an opening cycle (no bets placed yet), allow players to open.
                boolean openingRound = (highestBet == 0);
                // If everyone active has matched highestBet (or is folded/eliminated/all-in),
                // don't iterate asking everyone to "check" — prompt last raiser once instead.
                boolean allMatched = true;
                if (!openingRound) {
                    for (int i = 0; i < n; i++) {
                        Player p = players.get(i);
                        if (p.isEliminated() || p.isFolded() || allIn[i]) continue;
                        if (bets[i] != highestBet) { allMatched = false; break; }
                    }
                } else {
                    allMatched = false; // opening round forces prompts below
                }
                if (allMatched) {
                    if (lastRaiserIdx == -1) break;
                    Player last = players.get(lastRaiserIdx);
                    if (last.isEliminated() || last.isFolded() || allIn[lastRaiserIdx]) break;
                    int toCallForLast = highestBet - bets[lastRaiserIdx];
                    int maxAvailable = last.getChips();
                    int finalDecision = 0;
                    if (last.isBot()) {
                        finalDecision = botBetLogic(last, toCallForLast, maxAvailable);
                    } else {
                        displayGameState(last);
                        System.out.printf("Last raiser (%s), final chance. To call/raise: %d chips. Enter bet amount (number), or 'fold' to fold (or 'quit' to exit): ", last.getName(), toCallForLast);
                        String input = scanner.next();
                        if (input.equalsIgnoreCase("quit")) {
                            System.out.println("Game stopped by user."); System.exit(0);
                        }
                        if (input.equalsIgnoreCase("fold")) { last.fold(); System.out.println(last.getName() + " folds."); break; }
                        try { finalDecision = Integer.parseInt(input.trim()); } catch (NumberFormatException ex) { finalDecision = toCallForLast; }
                    }
                    if (finalDecision > toCallForLast) {
                        int contribution = Math.min(finalDecision, maxAvailable);
                        if (contribution > 0) {
                            last.bet(contribution);
                            bets[lastRaiserIdx] += contribution;
                            if (last.getChips() == 0) allIn[lastRaiserIdx] = true;
                            addToPots(last, contribution);
                            highestBet = bets[lastRaiserIdx];
                            lastRaiserIdx = lastRaiserIdx;
                            // someone raised -> continue loop so others can respond
                            continue;
                        }
                    } else {
                        System.out.println(last.getName() + " checks.");
                        break;
                    }
                }

                boolean someoneRaisedThisCycle = false;

                // Only prompt players who still need to act (toCall > 0)
                for (int offset = 0; offset < n; offset++) {
                    int idx = (startIdx + offset) % n;
                    Player p = players.get(idx);
                    if (p.isEliminated() || p.isFolded() || allIn[idx]) continue;

                    int toCall = highestBet - bets[idx];
                    // if openingRound==true we prompt even when toCall==0 so players can open/check
                    if (toCall == 0 && !openingRound) continue; // already matched, don't ask to "check"

                    int maxAvailable = p.getChips();
                    int chosenBet = 0;
                    if (p.isBot()) {
                        chosenBet = botBetLogic(p, toCall, maxAvailable);
                    } else {
                        displayGameState(p);
                        System.out.printf("Your turn, %s! To call: %d chips. Enter bet amount (number), or 'fold' to fold (or 'quit' to exit): ", p.getName(), toCall);
                        String input = scanner.next();
                        if (input.equalsIgnoreCase("quit")) { System.out.println("Game stopped by user."); System.exit(0); }
                        if (input.equalsIgnoreCase("fold")) { p.fold(); System.out.println(p.getName() + " folds."); continue; }
                        try { chosenBet = Integer.parseInt(input.trim()); } catch (NumberFormatException ex) { chosenBet = toCall; }
                    }

                    // If chosenBet < toCall and not an all-in intent -> fold
                    if (chosenBet < toCall && !(chosenBet > 0 && chosenBet >= maxAvailable)) {
                        p.fold();
                        System.out.println(p.getName() + " folds (did not match highest bet).");
                        continue;
                    }

                    int contribution = Math.min(chosenBet, maxAvailable);
                    if (contribution > 0) {
                        p.bet(contribution);
                        bets[idx] += contribution;
                        if (p.getChips() == 0) allIn[idx] = true;
                        addToPots(p, contribution);
                        System.out.println(p.getName() + " bets " + contribution + " chips.");
                    } else {
                        System.out.println(p.getName() + " checks.");
                    }

                    if (bets[idx] > highestBet) {
                        highestBet = bets[idx];
                        lastRaiserIdx = idx;
                        someoneRaisedThisCycle = true;
                    }

                    displayGameState(p);
                }

                if (!someoneRaisedThisCycle) break;
            } // end while betting
            return null;
        };

        // INITIAL betting round
        runBetting.apply(startingPlayerIndex, 0);

        // THREE draw+bet cycles: draw one card then betting, except after last draw (no betting)
        for (int drawRound = 1; drawRound <= 3; drawRound++) {
            // Draw one card for each active player
            for (Player p : players) {
                if (p.isEliminated() || p.isFolded()) continue;
                if (!deck.isEmpty()) p.addCardToHand(deck.remove(0));
            }
            System.out.println("\nHands after drawing (partial):");
            for (Player p : players) {
                if (!p.isBot()) System.out.println(p.getName() + "'s hand: " + p.getHand());
            }

            if (drawRound < 3) {
                // reset street bets for next betting round
                Arrays.fill(bets, 0);
                highestBet = 0;
                // players who are all-in remain allIn; folded/eliminated remain so
                runBetting.apply(startingPlayerIndex, 0);
            }
        }

        // Final reveal (scoring/pot distribution TODO)
        System.out.println("\nFinal hands:");
        for (Player p : players) {
            System.out.println(p.getName() + "'s hand: " + p.getHand());
        }

        // End-round housekeeping: reset folded flag for next round; foldCount reset only if they participated
        for (Player p : players) {
            if (!p.isFolded() && !p.isEliminated()) p.resetFoldCount();
            p.resetFold();
        }
    }

    // Simple add-to-pot: add amount to main pot and ensure player is eligible
    private void addToPots(Player p, int amount) {
        if (amount <= 0) return;
        Pot main = pots.get(0);
        main.amount += amount;
        main.addPlayer(p);
    }

    private void displayGameState(Player currentPlayer) {
        System.out.println("\n--- Game State ---");
        System.out.println("Total Pot: " + getTotalPot());
        for (Pot pot : pots) {
            System.out.print("Pot (" + pot.amount + "): ");
            for (Player ep : pot.eligiblePlayers) System.out.print(ep.getName() + " ");
            System.out.println();
        }
        for (Player p : players) {
            System.out.println(p.displayStatus());
        }
        if (currentPlayer != null && !currentPlayer.isBot()) {
            System.out.println(currentPlayer.getName() + "'s hand: " + currentPlayer.getHand());
        }
    }

    public int getTotalPot() {
        int s = 0;
        for (Pot pot : pots) s += pot.amount;
        return s;
    }

    private int botBetLogic(Player bot, int toCall, int maxBet) {
        // simple: if must call and it's > half chips -> fold (0)
        if (toCall > 0) {
            if (toCall >= maxBet) return maxBet;         // all-in
            if (toCall > maxBet / 2) return 0;          // fold
            return toCall;                              // call
        } else {
            return Math.min(5, maxBet);                 // open small bet
        }
    }
}
