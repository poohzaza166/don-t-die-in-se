import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Player[] players;
    private Weapon[] weapons;
    private Room[] rooms;
    private Tile[][] tiles;
    private Hand answer;
    private int turn;
    private int turnState;
    public Brain[] brains;
    private Deck[] decks;
    private final Random rng = new Random();
    private int width;
    private int height;
    private final int requestedPlayerCount;
    public int humanIndex;


    public int getPlayerCount() {
        return players.length;
    }

    public Hand getAnswer() {
        return answer;
    }

    public Game(int playerCount) {
        this.requestedPlayerCount = playerCount;
        this.turn = 0;
        this.turnState = 0;
        try {
            initialiseGame();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load save.txt", e);
        }
    }

    private void initialiseGame() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("save.txt"));
        br.mark(10_000_000);

        int deckStart = Integer.parseInt(br.readLine());
        int roomStart = Integer.parseInt(br.readLine());
        int tileStart = Integer.parseInt(br.readLine());

        br.reset();
        skipLines(br, deckStart - 1);
        int deckCount = Integer.parseInt(br.readLine());
        decks = new Deck[deckCount];
        for (int i = 0; i < deckCount; i++) {
            decks[i] = new Deck(i, br);
        }

        br.reset();
        skipLines(br, roomStart - 1);
        int roomCount = Integer.parseInt(br.readLine());
        rooms = new Room[roomCount];
        for (int i = 0; i < roomCount; i++) {
            rooms[i] = new Room(i, br);
        }

        Scanner sc = new Scanner(new FileReader("save.txt"));
        for (int i = 1; i < tileStart; i++) sc.nextLine();

        this.width = Integer.parseInt(sc.nextLine());
        this.height = Integer.parseInt(sc.nextLine());
        tiles = new Tile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(sc);
            }
        }
        sc.close();
        br.close();

        Card[] answerCards = new Card[deckCount];
        for (int i = 0; i < deckCount; i++) {
            decks[i].shuffle();
            answerCards[i] = decks[i].drawCard();
        }
        answer = new Hand(answerCards);

        int playerCount = requestedPlayerCount;
        players = new Player[playerCount];
        if (brains == null) brains = new Brain[playerCount];

        List<String> nameList = new ArrayList<>(List.of(
                "Miss Scarlett",
                "Col Mustard",
                "Mrs White",
                "Rev Green",
                "Mrs Peacock",
                "Prof Plum"));


        Collections.shuffle(nameList);

        int[][] spawns = {{0, 0}, {width - 1, 0}, {0, height - 1}, {width - 1, height - 1}, {width / 2, 0}, {width / 2, height - 1}};

        for (int i = 0; i < playerCount; i++) {
            String assignedName = nameList.get(i);
            players[i] = new Player(spawns[i][0], spawns[i][1], assignedName);
            tiles[spawns[i][0]][spawns[i][1]].isOccupied = true;
        }

        List<Card> remaining = new ArrayList<>();
        for (Deck d : decks) {
            Card c;
            while ((c = d.drawCard()) != null) {
                remaining.add(c);
            }
        }
        Collections.shuffle(remaining);
        for (int i = 0; i < remaining.size(); i++) {
            players[i % playerCount].hand.addCard(remaining.get(i));
        }

        weapons = new Weapon[0];

        Card[] suspectCards = decks[0].getContents();
        Card[] weaponCards = decks[1].getContents();
        Card[] roomCardArr = decks[2].getContents();
        humanIndex = new Random().nextInt(playerCount);


        for (int i = 0; i < playerCount; i++) {
            if (i == humanIndex) {
                brains[i] = null;
            } else {
                brains[i] = new RandomBrain(suspectCards, weaponCards, roomCardArr, rooms);
            }
        }

    }
    // label player and AI on the game log
    public String getDisplayName(int playerIndex) {
        if (brains[playerIndex] == null) {
            return players[playerIndex].getName() + " (player)";
        }

        int aiNumber = 0;
        for (int i = 0; i <= playerIndex; i++) {
            if (brains[i] != null) {
                aiNumber++;
            }
        }

        return "AI " + aiNumber;
    }


    private void skipLines(BufferedReader br, int n) throws IOException {
        for (int i = 0; i < n; i++) br.readLine();
    }

    public void playGame() {
        while (true) {
            int alive = 0;
            int lastAlive = -1;
            for (int i = 0; i < players.length; i++) {
                if (!players[i].hasGuessed) {
                    alive++;
                    lastAlive = i;
                }
            }
            if (alive <= 1) {
                System.out.println("Player " + lastAlive + " wins by default.");
                return;
            }
            if (!players[turn].hasGuessed) {
                if (playTurn(turn)) return;
            }
            turn = (turn + 1) % players.length;
        }
    }

    private boolean playTurn(int p) {
        Player player = players[p];
        int oldX = player.posX, oldY = player.posY, oldInRoom = player.inRoom;

        movementPhase(p);

        player.prevPosX = oldX;
        player.prevPosY = oldY;
        player.prevInRoom = oldInRoom;

        boolean uncontested = false;
        if (player.inRoom != -1 && brains[p] != null && brains[p].makeSuggestion(players, tiles, p)) {
            Card[] suggestion = brains[p].cardsSuggestion(players, tiles, p);
            uncontested = handleSuggestion(p, suggestion);
        }

        if (uncontested && brains[p] != null && brains[p].makeAccusation(players, tiles, p)) {
            Card[] accusation = brains[p].cardsAccusation(players, tiles, p);
            if (handleAccusation(p, accusation)) {
                System.out.println("Player " + p + " (" + player.getName() + ") wins!");
                return true;
            }
            player.hasGuessed = true;
            for (int i = 0; i < players.length; i++) {
                if (brains[i] != null) {
                    brains[i].playerAccusationFail(players, tiles, i, p, accusation);
                }
            }
        }

        return false;
    }

    private void movementPhase(int p) {
        Player player = players[p];

        if (player.inRoom != -1 && rooms[player.inRoom].getWarp() != -1) {
            if (brains[p] != null && brains[p].useWarp(players, tiles, p)) {
                int target = rooms[player.inRoom].getWarp();
                rooms[player.inRoom].clear(player.posX, player.posY);
                int[] pos = rooms[target].occupy();
                player.posX = pos[0];
                player.posY = pos[1];
                player.inRoom = target;
                notifyAll((b, i) -> b.playerMoveInRoom(players, tiles, i, p));
                return;
            }
        }

        int roll = (rng.nextInt(6) + 1) + (rng.nextInt(6) + 1);
        if (brains[p] == null) return;
        int[] target = brains[p].moveTo(players, tiles, p, roll);

        if (target[0] == -1) {
            int roomId = target[1];
            if (player.inRoom != -1) {
                rooms[player.inRoom].clear(player.posX, player.posY);
            } else {
                tiles[player.posX][player.posY].isOccupied = false;
            }
            int[] pos = rooms[roomId].occupy();
            player.posX = pos[0];
            player.posY = pos[1];
            player.inRoom = roomId;
            notifyAll((b, i) -> b.playerMoveInRoom(players, tiles, i, p));
        } else {
            int newX = target[0], newY = target[1];
            boolean wasInRoom = player.inRoom != -1;
            if (wasInRoom) {
                rooms[player.inRoom].clear(player.posX, player.posY);
                player.inRoom = -1;
            } else {
                tiles[player.posX][player.posY].isOccupied = false;
            }
            tiles[newX][newY].isOccupied = true;
            player.posX = newX;
            player.posY = newY;
            if (wasInRoom) {
                notifyAll((b, i) -> b.playerMoveOutRoom(players, tiles, i, p));
            }
            notifyAll((b, i) -> b.playerMove(players, tiles, i, p));
        }
    }

    private boolean movementLegal(int oldX, int oldY, int newX, int newY) {
        if (newX < 0 || newY < 0 || newX >= tiles.length || newY >= tiles[0].length) return false;
        int dx = Math.abs(newX - oldX);
        int dy = Math.abs(newY - oldY);
        if (dx + dy != 1) return false;
        if (!tiles[newX][newY].isEnterable) return false;
        if (tiles[newX][newY].isOccupied) return false;
        return true;
    }

    private boolean handleSuggestion(int playerId, Card[] suggestion) {
        int n = players.length;
        for (int offset = 1; offset < n; offset++) {
            int targetId = (playerId + offset) % n;
            Player target = players[targetId];
            Card[] matched = target.hand.hasCards(suggestion);
            if (matched.length > 0) {
                int idx = brains[targetId].selectCard(players, tiles, targetId, matched);
                Card shown = matched[idx];
                brains[playerId].playerShows(players, tiles, playerId, targetId, shown);
                brains[targetId].playerShown(players, tiles, targetId, playerId, shown);
                final int contestedBy = targetId;
                notifyAll((b, i) -> b.playerSuggestion(players, tiles, i, playerId, contestedBy, suggestion));
                return false;
            }
        }
        notifyAll((b, i) -> b.playerSuggestion(players, tiles, i, playerId, -1, suggestion));
        return true;
    }

    private boolean handleAccusation(int playerId, Card[] accusation) {
        if (accusation.length != decks.length) return false;
        return answer.hasAllCards(accusation);
    }

    private interface BrainAction {
        void apply(Brain b, int playerId);
    }

    private void notifyAll(BrainAction action) {
        for (int i = 0; i < brains.length; i++) {
            if (brains[i] != null) action.apply(brains[i], i);
        }
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTurn() {
        return turn;
    }

    public int rollAndMove() {
        int roll = (rng.nextInt(6) + 1) + (rng.nextInt(6) + 1);
        movementPhase(turn);
        do{
            turn = (turn + 1) % players.length;
        } while (players[turn].hasGuessed);
        return roll;
    }

    public String doSuggestion() {
        Player p = players[turn];
        if (p.inRoom ==-1) return p.getName() + ": not in a room";
        Card[] suggestion = brains[turn].cardsSuggestion(players, tiles, turn);
        boolean uncontested = handleSuggestion(turn, suggestion);
        return p.getName() + " suggests: "
        + suggestion[0].getName() + ", "
        + suggestion[1].getName() + ", "
        + suggestion[2].getName()
        + (uncontested ? " (uncontested!)" : " (contested)");
    }

    public String doAccusation() {
        Player p = players[turn];
        Card[] accusation = brains[turn].cardsAccusation(players, tiles, turn);
        boolean correct = handleAccusation(turn, accusation);
        String result = p.getName() + " accuses: "
            + accusation[0].getName() + ", "
            + accusation[1].getName() + ", "
            + accusation[2].getName();
        if (correct) {
            return result + " — CORRECT! " + p.getName() + " wins!";
        } else {
            p.hasGuessed = true;
            for (int i = 0; i < players.length; i++) {
                if (brains[i] != null) {
                    brains[i].playerAccusationFail(players, tiles, i, turn, accusation);
                }
            }
            return result + " — WRONG! " + p.getName() + " is eliminated!";
        }
    }
}
