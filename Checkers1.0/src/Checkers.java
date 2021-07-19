
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

/**
 * This panel lets two users play checkers against each other. Red always starts the game. If a player can jump an
 * opponent's piece, then the player must jump. When a player can make no more moves, the game ends.
 */
public class Checkers extends Application {

    public static void main( String[] args ) {
        launch( args );

    }

    public void start( Stage stage ) {

    }



}