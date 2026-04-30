import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests the player's Notepad get/set behaviour.
public class NotepadTest {

    @Test
    void general_notes_round_trip() {
        Notepad n = new Notepad();
        n.setGeneralNotes("colonel did it");
        assertEquals("colonel did it", n.getGeneralNotes());
    }

    @Test
    void column_notes_round_trip() {
        Notepad n = new Notepad();
        n.setColumnNotes(1, 2, "seen");
        assertEquals("seen", n.getColumnNotes(1, 2));
    }

    @Test
    void general_notes_start_empty() {
        Notepad n = new Notepad();
        assertEquals("", n.getGeneralNotes());
    }
}
