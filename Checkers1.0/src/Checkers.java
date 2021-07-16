
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
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

    /**
     * A canvas on which a checker board is drawn, defined by a static nested
     * subclass. Much of the game logic is defined in this class.
     */
    CheckersBoard board;

    private Button newGameButton;  // Button for starting a new game.
    private Button resignButton;  // Button that a player can use to end the game by resigning.

    private Label message;  // Label for displaying messages to the user.

    /**
     * The constructor creates the Board ( which in turn creates and manages the buttons and message label ), adds all
     * the components, and sets the bounds of the components. A null layout is used. ( This is the only thing that is
     * done in the main Checkers class. )
     */
    public void start( Stage stage ) {

        /* Create the label that will show messages. */
        message = new Label( "Click \"New Game\" to begin." );
        message.setTextFill( Color.rgb( 100, 255, 100 ) );  // Light green.
        message.setFont( Font.font( null, FontWeight.BOLD, 18 ) );

        /**
         * Create the buttons and the board. The buttons MUST be created first, since they are used in the CheckerBoard
         * constructor!
         */
        newGameButton = new Button( "New Game" );
        resignButton = new Button( "Resign" );

        board = new CheckerBoard();  // A subclass of Canvas, defined below.
        board.draw();  // Draws the content of the checkerboard.

        /**
         * Set up ActionEvent handlers for the buttons and a MousePressed handler for the board. The handlers call
         * instance methods in the board object.
         */
        newGameButton.setOnAction( e -> board.doNewGame() );
        resignButton.setOnAction( e -> board.doResign() );
        board.setOnMousePressed( e -> board.mousePressed(e) );

        /* Set the location of each child by calling its relocate() method. */
        board.relocate( 20, 20 );
        newGameButton.relocate( 370, 120 );
        resignButton.relocate( 370, 200 );
        message.relocate( 20, 370 );

        /**
         * Set the sizes of the buttons. For this to have an effect, make the buttons "unmanaged". If they are managed,
         * the Pane will set their sizes.
         */
        resignButton.setManaged( false );
        resignButton.resize( 100, 30 );
        newGameButton.setManaged( false );
        newGameButton.resize( 100, 30 );

        /**
         * Create the Pane and give it a preferred size. If the preferred size were not set, the unmanaged buttons would
         * not be included in the Pane's computed preffered size.
         */
        Pane root = new Pane();

        root.setPrefWidth( 500 );
        root.setPrefHeight( 420 );

        /**
         * Add the child nodes to the Pane and set up the rest of the GUI.
         */
        root.getChildren().addAll( board, newGameButton, resignButton, message );
        root.setStyle( "-fx-background-color: darkgreen; -fx-border-color: darkred; -fx-border-width:3" );

        Scene scene = new Scene( root );
        stage.setScene( scene );
        stage.setResizable( false );
        stage.setTitle( "Checkers 1.0" );
        stage.show();

    }  // end start().


    // ------------------------------------------ Nested Subclasses. ---------------------------------------


    /**
     * A CheckersMove object represents a move in the game of Checkers. It holds the row and column of the piece that is
     * to be moved and the row and the column of the square to which it is to be moved. ( This class makes no guarantee
     * that the move is legal. )
     */
    private static class CheckersMove {

        int fromRow, fromCol;  // Position of piece to be moved.
        int toRow, toCol;  // Square it is to move to.

        CheckersMove( int r1, int c1, int r2, int c2 ) {
            // Constructor. Just set the values of the instance variables.
            fromRow = r1;
            fromCol = c1;
            toRow = r2;
            toCol = c2;

        }
        boolean isJump() {
            // Text whether this move is a jump. It is assumed that the move is legal. In a jump,
            // the piece moves two rows. ( In a regular move, it only moves on row. )
            return ( fromRow - toRow == 2 || fromRow - toRow == -2 );

        }

    }

    /**
     * This canvas displays a 320-by-320 checkerboard pattern with a 2-pixel dark red border. The canvas will be exactly
     * 324-by-324 pixels. This class contains methods that are called in response to a mouse click on the canvas and in
     * response to clicks on the New Game and Resign buttons. Note that the "New Game" and "Resign" buttons must be
     * created before the Board constructor is called, since the constructor references the buttons ( in the call to
     * doNewGame() ).
     */
    private class CheckersBoard extends Canvas {

        /**
         * The data for the checkers board is kept here. This board
         * is also responsible for generating lists of legal moves.
         */
        CheckersData board;

        boolean gameInProgress;  // Is a game currently in progress?

        /* The next three variables are valid only when the game is in progress. */

        /**
         * Whose turn is it now? The possible values are CheckersData.RED and CheckersData.BLACK.
         */
        int currentPlayer;

        /**
         * If the current player has selected a piece to move, these give the row and column containing
         * that piece. If no piece is yet selected, the selectedRow is -1.
         */
        int selectedRow, selectedCol;

        /**
         * An array containing the legal moves for the current player.
         */
        CheckersMove[] legalMoves;

        /**
         * Constructor. Creates a CheckersData to represent the contents of the checkerboard,
         * and calls doNewGame to start the first game.
         */
        CheckersBoard() {
            super( 324, 324 );  // canvas is 324-by-324 pixels.
            board = new CheckersData();
            doNewGame();

        }

        /**
         * Start a new game. This method is called when the Board is first created and when the "New Game" button
         * is clicked. Event handling is set up in the start() method in the main class.
         */
        void doNewGame() {
            if ( gameInProgress ) {
                // This should not be possible, but it doesn't hurt to check.
                message.setText( "Finish the current game first!" );
                return;

            }
            board.setUpGame();  // Set up the pieces.
            currentPlayer = CheckersData.RED;  // RED moves first.
            legalMoves = board.getLegalMoves( CheckersData.RED );  // Get RED's legal moves.
            selectedRow = -1;  // RED has not yet selected a piece to move.
            message.setText( "Red: Make your move." );
            gameInProgress = true;
            newGameButton.setDisable( true );
            resignButton.setDisable( false );
            drawBoard();

        }

        /**
         * Current player resigns. Game ends. Opponent wins. This method is called when the user clicks
         * the "Resign" button. Event handling is set up in the start() method in the main class.
         */
        void doResign() {

            if ( !gameInProgress ) {
                // Should be impossible.
                message.setText( "There's no game in progress!!!" );
                return;
            }
            if ( currentPlayer == CheckersData.RED )
                gameOver( "RED resigns. BLACK wins." );
            else
                gameOver( "BLACK resigns. RED wins." );

        }

        /**
         * The game ends. The parameter, str, is displayed as a message to the user. The states of the buttons are
         * adjusted so players can start a new game. This method is called when the game ends at any point in this
         * class.
         */
        void gameOver( String str ) {
            message.setText( str );
            newGameButton.setDisable( false );
            resignButton.setDisable( true );
            gameInProgress = false;

        }

        /**
         * This is called by mousePressed() when a player clicks on the square in the specified row and col. It has
         * already been checked that a game is, in fact in progress.
         */
        void doClickSquare( int row, int col ) {
            /**
             * If the player clicked on one of the pieces that the player can move, mark this row and col as selected
             * and return. ( This might change a previous selection. ) Reset the message, in case it was previously
             * displaying an error message.
             */

            for ( int i = 0; i < legalMoves.length; i++ )
                if ( legalMoves[i].)
        }


    }



}
