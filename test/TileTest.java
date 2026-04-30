import org.junit.jupiter.api.Test;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

// Tests Tile parsing of "Y/N" and door IDs.
public class TileTest {

    @Test
    void Y_means_enterable() {
        Tile t = new Tile(new Scanner("Y\n-1\n"));
        assertTrue(t.isEnterable);
    }

    @Test
    void N_means_blocked() {
        Tile t = new Tile(new Scanner("N\n-1\n"));
        assertFalse(t.isEnterable);
    }

    @Test
    void doorTo_is_parsed() {
        Tile t = new Tile(new Scanner("Y\n3\n"));
        assertEquals(3, t.doorTo);
    }

    @Test
    void new_tile_starts_unoccupied() {
        Tile t = new Tile(new Scanner("Y\n-1\n"));
        assertFalse(t.isOccupied);
    }

    @Test
    void int_constructor_makes_open_tile() {
        Tile t = new Tile(0);
        assertTrue(t.isEnterable);
        assertFalse(t.isOccupied);
        assertEquals(-1, t.doorTo);
    }
}
