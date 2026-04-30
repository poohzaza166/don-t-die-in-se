import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.FileReader;
import static org.junit.jupiter.api.Assertions.*;

// Tests Room occupy / clear logic and warp loading.
public class RoomTest {

    private Room loadRoom(int id) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("save.txt"));
        br.mark(10_000_000);
        return new Room(id, br);
    }

    @Test
    void empty_constructor_has_no_warp() {
        Room r = new Room(0);
        assertEquals(-1, r.getWarp());
    }

    @Test
    void occupy_returns_a_position() throws Exception {
        Room r = loadRoom(0);
        int[] pos = r.occupy();
        assertNotNull(pos);
        assertEquals(2, pos.length);
    }

    @Test
    void clear_frees_a_slot_for_reuse() throws Exception {
        Room r = loadRoom(0);
        int[] first = r.occupy();
        r.clear(first[0], first[1]);
        // After clearing, the same slot should be reusable as the first free one.
        int[] again = r.occupy();
        assertArrayEquals(first, again);
    }

    @Test
    void room_loads_at_least_one_entrance() throws Exception {
        Room r = loadRoom(0);
        assertTrue(r.getEntrancesX().length > 0);
    }
}
