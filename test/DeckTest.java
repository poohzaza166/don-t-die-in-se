import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

// Tests Deck loading from save.txt and basic draw behaviour.
public class DeckTest {

    // The Deck constructor relies on the BufferedReader being positioned the way Game
    // leaves it during initialiseGame. We mimic that setup here, then build decks in order.
    private Deck loadDeck(int id) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("save.txt"));
        br.mark(10_000_000);
        // Read the 3 header lines, then reset and skip to the deck section header.
        int deckStart = Integer.parseInt(br.readLine());
        br.readLine();
        br.readLine();
        br.reset();
        for (int i = 0; i < deckStart - 1; i++) br.readLine();
        br.readLine(); // consume the deck count line
        Deck d = null;
        for (int i = 0; i <= id; i++) d = new Deck(i, br);
        return d;
    }

    @Test
    void suspect_deck_loads_six_cards() throws Exception {
        Deck d = loadDeck(0);
        assertEquals(6, d.getContents().length);
    }

    @Test
    void shuffle_keeps_same_cards() throws Exception {
        Deck d = loadDeck(0);
        Set<String> before = new HashSet<>();
        for (Card c : d.getContents()) before.add(c.getName());
        d.shuffle();
        Set<String> after = new HashSet<>();
        for (Card c : d.getContents()) after.add(c.getName());
        assertEquals(before, after);
    }

    @Test
    void drawCard_returns_one_then_null_when_empty() throws Exception {
        Deck d = loadDeck(0);
        int total = d.getContents().length;
        for (int i = 0; i < total; i++) assertNotNull(d.drawCard());
        assertNull(d.drawCard());
    }

    @Test
    void remainder_shrinks_as_cards_drawn() throws Exception {
        Deck d = loadDeck(0);
        int start = d.remainder();
        d.drawCard();
        assertEquals(start - 1, d.remainder());
    }
}
