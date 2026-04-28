import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.Spinner;
import javafx.util.Duration;

public class Main extends Application {

    private Game game;
    private TextArea log;
    private GridPane boardGrid;

    //Declare private in the class and connect and use it!
    private Button rollBtn;
    private Button suggestBtn;
    private Button accuseBtn;
    private Button statusBtn;


    // Player character colors for better readability
    private Color[] playerColors = {
            Color.RED,       // Player 0
            Color.BLUE,      // Player 1
            Color.GREEN,     // Player 2
            Color.YELLOW,    // Player 3
            Color.PURPLE,    // Player 4
            Color.ORANGE     // Player 5
    };



    @Override
    public void start(Stage stage) {
        stage.setTitle("CLUEDO - Watson Games Simulation");
        stage.setScene(createStartScreen(stage));
        stage.show();
    }

    // Start screen
    public Scene createStartScreen(Stage stage) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(80));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label title = new Label("CLUEDO");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        Button startBtn = new Button("Start Game");
        Button settingsBtn = new Button("Settings");
        Button exitBtn = new Button("Exit");

        startBtn.setPrefWidth(200);
        settingsBtn.setPrefWidth(200);
        exitBtn.setPrefWidth(200);


        startBtn.setOnAction(e -> stage.setScene(initialGameSettingScreen(stage)));

        settingsBtn.setOnAction(e -> showSettingsPopup());
        exitBtn.setOnAction(e -> stage.close());

        root.getChildren().addAll(title, startBtn, settingsBtn, exitBtn);
        return new Scene(root, 800, 600);
    }
    public Scene initialGameSettingScreen(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #fafafa;");

        Label title = new Label("Game Setup");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        // Player Count
        Label playerLabel = new Label("Number of Players:");
        Spinner<Integer> playerCountSpinner = new Spinner<>(2,6,4);
        playerCountSpinner.setPrefWidth(150);

        Button startBtn = new Button("Start Game");
        startBtn.setPrefWidth(200);

        startBtn.setOnAction(e -> {
            stage.setScene(createMainGameScreen(stage, playerCountSpinner.getValue()));
        });

        root.getChildren().addAll(
                title,
                playerLabel,
                playerCountSpinner,
                startBtn
        );

        return new Scene(root, 600, 500);
    }



    // Main game screen
    public Scene createMainGameScreen(Stage stage, int playerCount) {
        game = new Game(playerCount);

        BorderPane root = new BorderPane();

        // Board
        boardGrid = new GridPane();
        boardGrid.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 20px;");
        updateBoard();
        root.setCenter(boardGrid);

        // Right Panel
        VBox controlPanel = createControlPanel();
        root.setRight(controlPanel);

        // Bottom Log
        log = new TextArea();
        log.setEditable(false);
        log.setPrefHeight(120);
        log.setText("Game Started...\n");
        root.setBottom(log);

        updateButtonState();
        if (game.brains[game.getTurn()] != null) {
            runAITurn();
        }
        return new Scene(root, 1024, 768);
    }

    // AI logic for AI turn
    private void runAITurn() {
        int current = game.getTurn();

        if (game.brains[current] == null) {
            return;
        }

        String aiName = game.getDisplayName(current);


        log.appendText(aiName + " is thinking...\n");

        int delay = 2000 + new java.util.Random().nextInt(2000);

        PauseTransition pause = new PauseTransition(Duration.millis(delay));
        pause.setOnFinished(e -> {

            if (game.getTurn() != current) {
                return;
            }

            int roll = game.rollAndMove();
            log.appendText(aiName + " rolled " + roll + "\n");
            updateBoard();

            if (game.getPlayers()[current].inRoom != -1 &&
                    game.brains[current].makeSuggestion(game.getPlayers(), game.getTiles(), current)) {

                String suggestion = game.doSuggestion();
                log.appendText(aiName + ": " + suggestion + "\n");
            }

            if (game.brains[current].makeAccusation(game.getPlayers(), game.getTiles(), current)) {
                String accusation = game.doAccusation();
                log.appendText(aiName + ": " + accusation + "\n");
                updateBoard();
            }

            updateButtonState();


            if (game.brains[game.getTurn()] != null) {
                runAITurn();
            }
        });

        pause.play();
    }



    private void updateBoard() {
        boardGrid.getChildren().clear();

        int width = game.getWidth();
        int height = game.getHeight();
        Tile[][] tiles = game.getTiles();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Rectangle tileView = new Rectangle(25, 25);
                Tile currentTile = tiles[x][y];

                if (!currentTile.isEnterable) {
                    tileView.setFill(Color.DARKSLATEGRAY);
                } else if (currentTile.isOccupied) {
                    tileView.setFill(Color.LIGHTGRAY);
                } else if (currentTile.doorTo != -1) {
                    tileView.setFill(Color.DARKORANGE);
                } else {
                    tileView.setFill(Color.LIGHTGOLDENRODYELLOW);
                }

                tileView.setStroke(Color.GRAY);
                tileView.setStrokeWidth(0.5);

                boardGrid.add(tileView, x, y);
            }
        }
        Player[] players = game.getPlayers();
        for (int i = 0; i < players.length; i++) {
            Player p = players[i];

            Rectangle piece = new Rectangle(24, 24);
            piece.setFill(playerColors[i]);
            piece.setStroke(Color.BLACK);
            piece.setStrokeWidth(1);

            boardGrid.add(piece, p.posX, p.posY);
        }
    }

    // control panel for main game screen
    private VBox createControlPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color: #e0e0e0;");
        panel.setPrefWidth(250);

        Label statusLabel = new Label("Current Status: Game in progress");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        //Declare private in the class and connect and use it!
        rollBtn = new Button("Roll Dice & Move");
        suggestBtn = new Button("Make Suggestion");
        accuseBtn = new Button("Make Accusation");
        statusBtn = new Button("My status");


        rollBtn.setMaxWidth(Double.MAX_VALUE);
        suggestBtn.setMaxWidth(Double.MAX_VALUE);
        accuseBtn.setMaxWidth(Double.MAX_VALUE);
        statusBtn.setMaxWidth(Double.MAX_VALUE);



        rollBtn.setOnAction(e -> {
            int currentPlayer = game.getTurn();
            String name = game.getDisplayName(currentPlayer);
            int roll = game.rollAndMove();
            log.appendText(name + " rolled " + roll + "\n");
            updateBoard();
            updateButtonState();
            //call AI turn logic if next turn is AI
            if (game.brains[game.getTurn()] != null) {
                runAITurn();
            }
        });

        suggestBtn.setOnAction(e -> {
            String result = game.doSuggestion();
            log.appendText(game.getDisplayName(game.getTurn()) + result + "\n");
            updateButtonState();
        });

        accuseBtn.setOnAction(e -> {
            String result = game.doAccusation();
            log.appendText(game.getDisplayName(game.getTurn()) + ": " + result + "\n");
            updateBoard();
            updateButtonState();
        });
        statusBtn.setOnAction(e -> showStatusPopup());
        panel.getChildren().addAll(statusLabel, rollBtn, suggestBtn, accuseBtn, statusBtn);
        return panel;
    }

    // Button State for inactivate button on AI's turn
    private void updateButtonState() {
        int current = game.getTurn();
        boolean isAI = (game.brains[current] != null);

        rollBtn.setDisable(isAI);
        suggestBtn.setDisable(isAI);
        accuseBtn.setDisable(isAI);
    }
    // popup box for checking status
    private void showStatusPopup() {
        Stage popup = new Stage();
        popup.setTitle("Your Status");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        int me = game.humanIndex;
        Player player = game.getPlayers()[me];

        Label title = new Label("Your Status");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label name = new Label("Character: " + player.name);
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label cardsTitle = new Label("Your Cards:");
        cardsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox cardList = new VBox(5);

        for (Card c : player.hand.getCards()) {
            Label cardLabel = new Label("• " + c.getName());
            cardList.getChildren().add(cardLabel);
        }

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> popup.close());

        root.getChildren().addAll(title, name, cardsTitle, cardList, closeBtn);

        popup.setScene(new Scene(root, 300, 350));
        popup.show();
    }


    // popup box for setting option
    private void showSettingsPopup() {
        Stage popup = new Stage();
        popup.setTitle("Settings");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> popup.close());

        root.getChildren().addAll(title, closeBtn);



        Scene scene = new Scene(root, 300, 200);
        popup.setScene(scene);
        popup.show();
    }

    // Game over screen
    public Scene createGameOverScreen(Stage stage, String winner) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(80));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #fafafa;");

        Label result = new Label("Winner: " + winner);
        result.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Button viewSolution = new Button("View Solution");
        Button mainMenu = new Button("Main Menu");
        Button playAgain = new Button("Play Again");

        viewSolution.setPrefWidth(200);
        viewSolution.setOnAction(e -> {
            Stage popup = new Stage();
            popup.setTitle("Solution");

            VBox box = new VBox(10);
            box.setPadding(new Insets(20));
            box.setAlignment(Pos.CENTER);

            box.getChildren().add(new Label("The Answer:"));
            for (Card c : game.getAnswer().getCards()) {
                box.getChildren().add(new Label("• " + c.getName()));
            }

            Button close = new Button("Close");
            close.setOnAction(ev -> popup.close());
            box.getChildren().add(close);

            popup.setScene(new Scene(box, 280, 240));
            popup.show();
        });

        mainMenu.setPrefWidth(200);
        playAgain.setPrefWidth(200);

        mainMenu.setOnAction(e -> stage.setScene(createStartScreen(stage)));
        playAgain.setOnAction(e -> stage.setScene(createMainGameScreen(stage, game.getPlayerCount())));

        root.getChildren().addAll(result, viewSolution, mainMenu, playAgain);

        return new Scene(root, 800, 600);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
