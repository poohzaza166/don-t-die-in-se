import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests for Hand: holding cards and matching suggestions.
public class HandTest {

    private final Card scarlett = new Card("Miss Scarlett", 0, 0);
    private final Card rope     = new Card("Rope", 1, 3);
    private final Card kitchen  = new Card("Kitchen", 2, 0);

    @Test
    void empty_hand_has_no_cards() {
        Hand h = new Hand();
        assertTrue(h.getCards().isEmpty());
    }

    @Test
    void add_card_grows_the_hand() {
        Hand h = new Hand();
        h.addCard(scarlett);
        assertEquals(1, h.getCards().size());
    }

    @Test
    void hasCards_returns_only_matches() {
        Hand h = new Hand(new Card[] { scarlett, rope });
        Card[] result = h.hasCards(new Card[] { scarlett, kitchen });
        assertEquals(1, result.length);
        assertEquals(scarlett, result[0]);
    }

    @Test
    void hasCards_returns_empty_when_nothing_matches() {
        Hand h = new Hand(new Card[] { scarlett });
        Card[] result = h.hasCards(new Card[] { rope, kitchen });
        assertEquals(0, result.length);
    }

    @Test
    void hasAllCards_true_when_all_present() {
        Hand h = new Hand(new Card[] { scarlett, rope, kitchen });
        assertTrue(h.hasAllCards(new Card[] { scarlett, rope, kitchen }));
    }

    @Test
    void hasAllCards_false_when_one_missing() {
        Hand h = new Hand(new Card[] { scarlett, rope });
        assertFalse(h.hasAllCards(new Card[] { scarlett, rope, kitchen }));
    }
}
