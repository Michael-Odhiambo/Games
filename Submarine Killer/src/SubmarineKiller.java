
/**
 * Author: Michael Allan Odhiambo.
 * E-mail: michaelallanodhiambo@gmail.com
 */

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

/**
 * This program implements a simple arcade game in which the user tries to
 * blow up a "submarine" ( a black oval ) by dropping "depth charges" ( a red disk )
 * from a "boat" ( a blue roundRect ). The user moves the boat with the left- and right
 * arrow keys and drops the depth charge with the down-arrow key. The sub moves left
 * and right erratically along the bottom of the canvas.
 */

public class SubmarineKiller extends Application {

    private AnimationTimer timer;  // AnimationTimer that drives the animation.
    private final int width = 640, height = 480;  // Size of the canvas.

    // The boat, bomb and submarine are defined by nested classes.
    private Boat boat;
    private Bomb bomb;
    private Submarine sub;

    private Canvas canvas;  // The canvas where everything is drawn
    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start method creates the window content and configures event listening.
     */
    public void start( Stage stage ) {

        /* Create objects */
        boat = new Boat();
        sub = new Submarine();
        bomb = new Bomb();

        canvas = new Canvas( width, height );

        this.stage = stage;

        /* Set up the GUI */
        Pane root = new Pane( canvas );
        Scene scene = new Scene( root );
        stage.setScene( scene );
        stage.setTitle( "Submarine Killer version 1 -- Use arrow keys to play!" );
        stage.setResizable( false );

        /* Configure event listeners and animation.*/

        scene.setOnKeyPressed( evt -> {

            // The key listener responds to KeyPressed events on the canvas. Only the
            // left-, right-, and down-arrow keys have any effect. The left- and right-
            // arrow keys move the boat while down-arrow releases the bomb.
            KeyCode code = evt.getCode();  // Which key was pressed?
            if ( code == KeyCode.LEFT ) {
                // Move the boat left.
                boat.centerX -= 15;

            }
            else if ( code == KeyCode.RIGHT ) {
                // Move the boat to the right.
                boat.centerX += 15;

            }
            else if ( code == KeyCode.DOWN ) {
                // Start the bomb falling, if it is not already falling.
                if ( bomb.isFalling == false )
                    bomb.isFalling = true;
            }
        });

        stage.focusedProperty().addListener( ( obj, oldVal, newVal ) -> {

            // This listener turns the animation off when this program's window does
            // not have the input focus.
            if ( newVal ) {
                // The window has gained focus.
                timer.start();
            }
            else {
                timer.stop();

            }
            draw();  // Appearance changes depending on focus.
        });

        timer = new AnimationTimer() {
            // The handle method is called once per frame while the animation
            // is running. There should be about 60 frames per second.
            long previousFrameTime;

            public void handle( long time ) {

                if ( time - previousFrameTime > 0.95e9/60 ) {
                    // The test in the if is to guard against a bug that has shown
                    // up in some versions of JavaFX on some computers.  The bug allows
                    // the handle() method to be called many times more than the 60 times
                    // per second that is specified in the JavaFX documentation.  The
                    // test throttles the frame rate to 60 per second, in case JavaFX
                    // is not already doing that.  It should not be necessary.
                    boat.updateForNewFrame();
                    bomb.updateForNewFrame();
                    sub.updateForNewFrame();
                    draw();
                    previousFrameTime = time;
                }
            }
        };

        /* Show the window.*/
        stage.show();
        timer.start();
    }

    /**
     * The draw() method draws the current state of the game. It draws a gray or cyan border
     * around the canvas to indicate whether or not the window has the input focus. It draws the
     * boat, sub and bomb calling their respective draw() methods. This method is called in each
     * frame and when the window gains or loses focus.
     */
    public void draw() {

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill( Color.rgb(0, 200, 0) );  // Fill with green, erasing previous frame.
        g.fillRect( 0, 0, width, height );

        if ( stage.isFocused() ) {

            // Draw a CYAN border on the window when the window has focus.
            g.setStroke( Color.CYAN );

        }
        else {
            // Draw gray border and message when the window does not have focus.
            g.setFill( Color.BLACK );
            g.fillText( "(ANIMATION PAUSED)", 20, 30 );
            g.setStroke( Color.DARKGRAY );

        }
        g.setLineWidth(3);
        g.strokeRect( 1.5, 1.5, width-3, height-3 );  // Draw a 3-pixel border.

        boat.draw(g);  // The three objects draw themselves.
        sub.draw(g);
        bomb.draw(g);

    }

    /**
     * This nested class defines the boat.
     */
    private class Boat {

        int centerX, centerY;  // Current position of the center of the boat.

        // Constructor centers the boat horizontally, 80 pixels from top.
        Boat() {

            centerX = width/2;
            centerY = 80;
        }

        void updateForNewFrame() {

            // Makes sure the boat has not moved off the screen.
            if ( centerX < 0 )
                centerX = 0;
            else if ( centerX > width )
                centerX = width;

        }

        // Draws the boat at its current location.
        void draw(GraphicsContext g ) {
            g.setFill( Color.BLUE );
            g.fillRoundRect( centerX - 40, centerY - 20, 80, 40, 20, 20 );

        }
    }

    /**
     * This nested class defines the bomb.
     */
    private class Bomb {

        int centerX, centerY;  // Current position of the center of the bomb.
        boolean isFalling;  // If true, the bomb is falling; if false, it is attached to the boat.

        // Constructor creates a bomb that is initially attached to a boat.
        Bomb() {

            isFalling = false;

        }

        void updateForNewFrame() {

            // If bomb is falling, take appropriate action.
            if ( isFalling ) {
                if ( centerY > height ) {
                    // Bomb has missed the submarine. It is returned to its initial state,
                    // with isFalling equal to false.
                    isFalling = false;

                }
                else if ( Math.abs( centerX - sub.centerX ) <= 36 &&
                Math.abs( centerY - sub.centerY ) <= 21 ) {
                    // Bomb has hit the submarine. The submarine enters the "isExploding" state.
                    sub.isExploding = true;
                    sub.explosionFrameNumber = 1;
                    isFalling = false;  // Bomb reappears on the boat.
                }
                else {
                    // If the bomb has not fallen off the canvas or hit the sub, then it is moved down
                    // 6 pixels.
                    centerY += 6;

                }
            }
        }

        // Draw the bomb.
        void draw( GraphicsContext g ) {

            // If not falling, set centerX and centerY to show the bomb on the bottom of the boat.
            if ( !isFalling ) {
                centerX = boat.centerX;
                centerY = boat.centerY + 23;
            }
            g.setFill( Color.RED );
            g.fillOval( centerX - 8, centerY - 8, 16, 16 );

        }
    }


    /**
     * This nested class defines the sub.
     */
    private class Submarine {

        int centerX, centerY;  // Current position of the center of the sub.
        boolean isMovingLeft;  // Tells whether the sub is moving left or right.
        boolean isExploding;  // Set to true when the sub is hit by the bomb.
        int explosionFrameNumber;  // If the sub is exploding, this is the number of frames since the explosion started.

        // Create the sub at a random x-coordinate, 40 pixels from the bottom.
        Submarine() {

            centerX = (int) (width * Math.random());
            centerY = height - 40;
            isExploding = false;
            isMovingLeft = (Math.random() < 0.5);

        }

        void updateForNewFrame() {

            // Move sub or increase explosionFrameNumber.
            if (isExploding) {
                // If the sub is exploding, add 1 to explosionFrameNumber. When the number reaches
                // 25, the explosion ends and the sub reappears in a random position.
                explosionFrameNumber++;
                if (explosionFrameNumber == 25) {
                    centerX = (int) (width * Math.random());
                    centerY = height - 40;
                    isExploding = false;
                    isMovingLeft = (Math.random() < 0.5);

                }
            } else { // Move the sub.
                if (Math.random() < 0.02) {
                    // In one frame out of every 50, on average, the sub reverses its direction of motion.
                    isMovingLeft = !isMovingLeft;
                }
                if (isMovingLeft) {
                    // Move the sub 3 pixels to the left. If it moves off the left edge of the canvas,
                    // move it back to the left edge and start it moving to the right.
                    centerX -= 3;
                    if (centerX <= 0) {
                        centerX = 0;
                        isMovingLeft = false;
                    }
                } else {
                    // Move the sub 3 pixels to the right. If it moves off the right edge of the canvas,
                    // move it back to the right edge and start it moving to the left.
                    centerX += 3;
                    if (centerX > width) {
                        centerX = width;
                        isMovingLeft = true;
                    }
                }
            }
        }

        void draw( GraphicsContext g ) {
            // Draw the sub, and if it is exploding, the explosion.
            g.setFill( Color.BLACK );
            g.fillOval( centerX - 30, centerY - 15, 60, 30 );

            if ( isExploding ) {
                // Draw an "explosion" that grows in size as the number of frames since the
                // start of the explosion increases.
                g.setFill( Color.YELLOW );
                g.fillOval( centerX - 3*explosionFrameNumber, centerY - 1.5*explosionFrameNumber,
                        6*explosionFrameNumber, 3*explosionFrameNumber );
                g.setFill( Color.RED );
                g.fillOval( centerX - 1.5*explosionFrameNumber, centerY - explosionFrameNumber/2,
                        3*explosionFrameNumber, explosionFrameNumber );

            }
        }


    }
}
