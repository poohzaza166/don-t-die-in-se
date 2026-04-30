import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests the AI player. Uses small fixtures so results are predictable.
public class RandomBrainTest {

    private Card[] suspects() {
        return new Card[] { new Card("Scarlett", 0, 0), new Card("Mustard", 0, 1) };
    }

    private Card[] weapons() {
        return new Card[] { new Card("Rope", 1, 0), new Card("Dagger", 1, 1) };
    }

    private Card[] roomCards() {
        return new Card[] { new Card("Kitchen", 2, 0), new Card("Library", 2, 1) };
    }

    private Room[] rooms() {
        return new Room[] { new Room(0), new Room(1) };
    }

    @Test
    void cardsSuggestion_returns_three_cards() {
        RandomBrain b = new RandomBrain(suspects(), weapons(), roomCards(), rooms());
        Player[] players = { new Player(0, 0, "p") };
        players[0].inRoom = 0;
        Tile[][] tiles = new Tile[5][5];

        Card[] s = b.cardsSuggestion(players, tiles, 0);
        assertEquals(3, s.length);
    }

    @Test
    void cardsSuggestion_room_card_matches_current_room() {
        RandomBrain b = new RandomBrain(suspects(), weapons(), roomCards(), rooms());
        Player[] players = { new Player(0, 0, "p") };
        players[0].inRoom = 1;
        Tile[][] tiles = new Tile[5][5];

        Card[] s = b.cardsSuggestion(players, tiles, 0);
        assertEquals("Library", s[2].getName());
    }

    @Test
    void cardsAccusation_returns_three_cards() {
        RandomBrain b = new RandomBrain(suspects(), weapons(), roomCards(), rooms());
        Player[] players = { new Player(0, 0, "p") };
        Tile[][] tiles = new Tile[5][5];

        Card[] a = b.cardsAccusation(players, tiles, 0);
        assertEquals(3, a.length);
    }

    @Test
    void selectCard_returns_valid_index() {
        RandomBrain b = new RandomBrain(suspects(), weapons(), roomCards(), rooms());
        Player[] players = { new Player(0, 0, "p") };
        Tile[][] tiles = new Tile[5][5];
        Card[] choices = { new Card("a", 0, 0), new Card("b", 0, 1) };

        int idx = b.selectCard(players, tiles, 0, choices);
        assertTrue(idx >= 0 && idx < choices.length);
    }

    @Test
    void moveTo_on_open_board_stays_within_dice_roll() {
        // Build a 5x5 fully open board with no walls or doors.
        Tile[][] tiles = new Tile[5][5];
        for (int x = 0; x < 5; x++)
            for (int y = 0; y < 5; y++)
                tiles[x][y] = new Tile(0);

        RandomBrain b = new RandomBrain(suspects(), weapons(), roomCards(), rooms());
        Player[] players = { new Player(2, 2, "p") };

        // Run many times because RandomBrain picks at random.
        for (int i = 0; i < 50; i++) {
            int[] dest = b.moveTo(players, tiles, 0, 3);
            if (dest[0] == -1) continue; // door entry
            int distance = Math.abs(dest[0] - 2) + Math.abs(dest[1] - 2);
            assertTrue(distance <= 3, "moved further than dice roll allows");
        }
    }
}
