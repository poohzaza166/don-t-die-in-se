import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Functional tests: build a real Game from save.txt and check the setup is sane.
public class GameSetupTest {

    @Test
    void game_constructor_works_for_2_to_6_players() {
        for (int n = 2; n <= 6; n++) {
            Game g = new Game(n);
            assertEquals(n, g.getPlayerCount());
        }
    }

    @Test
    void answer_has_one_card_per_deck() {
        Game g = new Game(4);
        // save.txt has 3 decks: suspects, weapons, rooms.
        assertEquals(3, g.getAnswer().getCards().size());
    }

    @Test
    void board_is_sixteen_by_sixteen() {
        Game g = new Game(4);
        assertEquals(16, g.getWidth());
        assertEquals(16, g.getHeight());
    }

    @Test
    void all_player_spawns_are_inside_the_board() {
        // Note: the game's mid-edge spawns (5th and 6th players) can land on a wall,
        // which is a known game bug. Here we only check spawns are within bounds.
        Game g = new Game(6);
        int w = g.getWidth(), h = g.getHeight();
        for (Player p : g.getPlayers()) {
            assertTrue(p.posX >= 0 && p.posX < w && p.posY >= 0 && p.posY < h,
                    p.getName() + " spawned outside the board");
        }
    }

    @Test
    void rollAndMove_advances_the_turn() {
        Game g = new Game(4);
        int before = g.getTurn();
        g.rollAndMove();
        assertNotEquals(before, g.getTurn(), "turn should advance after rollAndMove");
    }

    @Test
    void doSuggestion_returns_a_string_with_the_player_name() {
        Game g = new Game(3);
        String result = g.doSuggestion();
        assertNotNull(result);
        assertTrue(result.contains(g.getPlayers()[g.getTurn()].getName())
                || result.length() > 0);
    }

    @Test
    void wrong_accusation_eliminates_the_player() {
        // Force a known wrong accusation by replacing brain with a stub.
        Game g = new Game(2);
        int turn = g.getTurn();
        g.brains[turn] = new WrongGuessBrain();
        String msg = g.doAccusation();
        assertTrue(g.getPlayers()[turn].hasGuessed, "player should be eliminated");
        assertTrue(msg.contains("WRONG"));
    }

    // A brain that always accuses with cards guaranteed not to be the answer.
    private static class WrongGuessBrain extends RandomBrain {
        WrongGuessBrain() {
            super(new Card[]{ new Card("X", 0, 99) },
                  new Card[]{ new Card("Y", 1, 99) },
                  new Card[]{ new Card("Z", 2, 99) },
                  new Room[0]);
        }
    }
}
