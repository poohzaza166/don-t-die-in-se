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

public class Main extends Application {

    private Game game;
    private GridPane boardGrid;

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

        //-----------------------------------
        //need to be implemented!!!!
        //-----------------------------------
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

        //-----------------------------------
        // need to be implemented!!!!
        //-----------------------------------

        Button startBtn = new Button("Start Game");
        startBtn.setPrefWidth(200);

        startBtn.setOnAction(e -> {
            //-----------------------------------
            // need to be implemented!!!!
            //-----------------------------------

            stage.setScene(createMainGameScreen(stage));
        });

        root.getChildren().addAll(
                title,
                playerLabel,
                // playerCountBox
                startBtn
        );

        return new Scene(root, 600, 500);
    }



    // Main game screen
    public Scene createMainGameScreen(Stage stage) {
        game = new Game();

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
        TextArea log = new TextArea();
        log.setEditable(false);
        log.setPrefHeight(120);
        log.setText("Game Started...\n");
        root.setBottom(log);

        return new Scene(root, 1024, 768);
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
                    tileView.setFill(Color.CRIMSON);
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
    }

    // control panel for main game screen
    private VBox createControlPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color: #e0e0e0;");
        panel.setPrefWidth(250);

        Label statusLabel = new Label("Current Status: Game in progress");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Button rollBtn = new Button("Roll Dice & Move");
        Button suggestBtn = new Button("Make Suggestion");
        Button accuseBtn = new Button("Make Accusation");

        rollBtn.setMaxWidth(Double.MAX_VALUE);
        suggestBtn.setMaxWidth(Double.MAX_VALUE);
        accuseBtn.setMaxWidth(Double.MAX_VALUE);

        //-----------------------------------
        //need to be implemented!!!!
        //-----------------------------------
        rollBtn.setOnAction(e -> {
            updateBoard();
        });

        suggestBtn.setOnAction(e -> {
        });

        accuseBtn.setOnAction(e -> {
        });

        panel.getChildren().addAll(statusLabel, rollBtn, suggestBtn, accuseBtn);
        return panel;
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


        //-----------------------------------
        //need to be implemented for setting option inside the box!!!!
        //-----------------------------------

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
        mainMenu.setPrefWidth(200);
        playAgain.setPrefWidth(200);

        mainMenu.setOnAction(e -> stage.setScene(createStartScreen(stage)));
        playAgain.setOnAction(e -> stage.setScene(createMainGameScreen(stage)));

        root.getChildren().addAll(result, viewSolution, mainMenu, playAgain);

        return new Scene(root, 800, 600);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
