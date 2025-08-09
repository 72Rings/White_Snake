public class Card {
    public static final int INFINITY = Integer.MAX_VALUE;

    private final String type;
    private final int number;

    public Card(String type, int number) {
        this.type = type;
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        if (number == INFINITY) {
            return type + " \u221E"; // Infinity symbol
        }
        return type + " " + number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return number == card.number && type.equals(card.type);
    }

    @Override
    public int hashCode() {
        return 31 * type.hashCode() + number;
    }
}
