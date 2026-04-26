import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    // main window (full screen)
    @Override
    public void start(Stage stage) {
        Label label = new Label("This is the panel which the content will be show");
        StackPane root = new StackPane(label);

        // window object
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("CLUEDO");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    // launch methods
    public static void main(String[] args) {
        launch(args);
    }
}
