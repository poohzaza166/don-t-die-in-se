import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests the Player initial state.
public class PlayerTest {

    @Test
    void new_player_starts_at_given_position() {
        Player p = new Player(2, 5, "Mrs White");
        assertEquals(2, p.posX);
        assertEquals(5, p.posY);
    }

    @Test
    void new_player_is_not_in_a_room() {
        Player p = new Player(0, 0, "Rev Green");
        assertEquals(-1, p.inRoom);
    }

    @Test
    void new_player_is_not_eliminated() {
        Player p = new Player(0, 0, "Col Mustard");
        assertFalse(p.hasGuessed);
    }

    @Test
    void new_player_has_empty_hand_and_notepad() {
        Player p = new Player(0, 0, "Prof Plum");
        assertNotNull(p.hand);
        assertTrue(p.hand.getCards().isEmpty());
        assertNotNull(p.notes);
    }

    @Test
    void getName_returns_constructor_name() {
        Player p = new Player(0, 0, "Miss Scarlett");
        assertEquals("Miss Scarlett", p.getName());
    }
}
