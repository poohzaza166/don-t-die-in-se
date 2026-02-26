public class Card {
    private final String name;   //  Card name
    private final int type;      // Card type (e.g: Weapon, Player, Room etc.)
    private final int id;        // Card ID


    // --- Constructor ---
    public Card(String name, int type, int id) {
        this.name = name;
        this.type = type;
        this.id = id;
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    // Equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Card other))
            return false;

        return this.id == other.id &&
                this.type == other.type &&
                this.name.equals(other.name);
    }


    // optional method for debug
    @Override
    public String toString() {
        return "Card{name='" + name + "', type=" + type + ", id=" + id + "}";
    }
}