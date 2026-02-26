import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;

public class Deck {

    private Card[] contents;  // An array which contains all the cards
    private int index;        // Card drow pointer



    // Constructor
    public Deck(int id, BufferedReader br) throws IOException {
        // 1. Read the staring point of deck
        int deckStart = Integer.parseInt(br.readLine());

        // Move to deckStart line
        moveToLine(br, deckStart);

        // Read the number of types of card
        int deckTypes = Integer.parseInt(br.readLine());

        // Read the starting position of a list of cards for each deck type
        int[] pointers = new int[deckTypes];
        for (int i = 0; i < deckTypes; i++) {
            pointers[i] = Integer.parseInt(br.readLine());
        }

        // Go to the card list start position of the deck ID player want
        moveToLine(br, pointers[id]);

        // Read the number of cards
        int cardCount = Integer.parseInt(br.readLine());

        // Create Card Array
        contents = new Card[cardCount];

        // Create a Card Instance by Reading the Card Name
        for (int i = 0; i < cardCount; i++) {
            String name = br.readLine();
            contents[i] = new Card(name, id, i);
        }
        index = 0;
    }

    // Helper methods for constructor for line based read
    private void moveToLine(BufferedReader br, int target) throws IOException {
        br.reset();
        for (int i = 1; i < target; i++) {
            br.readLine();
        }
    }

    // Card shuffle method
    public void shuffle() {
        List<Card> list = Arrays.asList(contents);
        Collections.shuffle(list);
        contents = list.toArray(new Card[0]);
        index = 0;
    }

    // Card draw method
    public Card drawCard() {
        if (index >= contents.length)
            return null;
        else {
            return contents[index++];
        }
    }

    // Return remaining cards
    public int remainder() {
        return contents.length - index;
    }

}