import java.util.Scanner;

public class Tile {
    public boolean isEnterable;
    public boolean isOccupied;
    public int doorTo;

    public Tile(int id){
        this.isEnterable = true;
        this.isOccupied = false;
        this.doorTo = -1;
    }

    public Tile(Scanner scanner){
        String enterable = scanner.nextLine();
        this.isEnterable = enterable.equals("Y");
        this.doorTo = Integer.parseInt(scanner.nextLine());
        this.isOccupied = false;
    }
}
