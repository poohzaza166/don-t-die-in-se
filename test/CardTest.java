import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests for the Card value object.
public class CardTest {

    @Test
    void getters_return_constructor_values() {
        Card c = new Card("Rope", 1, 3);
        assertEquals("Rope", c.getName());
        assertEquals(1, c.getType());
        assertEquals(3, c.getId());
    }

    @Test
    void same_values_are_equal() {
        Card a = new Card("Rope", 1, 3);
        Card b = new Card("Rope", 1, 3);
        assertEquals(a, b);
    }

    @Test
    void different_id_makes_them_unequal() {
        Card a = new Card("Rope", 1, 3);
        Card b = new Card("Rope", 1, 4);
        assertNotEquals(a, b);
    }

    @Test
    void different_name_makes_them_unequal() {
        Card a = new Card("Rope", 1, 3);
        Card b = new Card("Dagger", 1, 3);
        assertNotEquals(a, b);
    }
}
