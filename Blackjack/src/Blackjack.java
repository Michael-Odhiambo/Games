
/**
 * Author: Michael Allan Odhiambo.
 * E-mail: michaelallanodhiambo@gmail.com.
 */

/**
 * In the previous version of the Blackjack game, the user can click on the "Hit",
 * "Stand", and "NewGame" buttons even when it doesn't make sense to do so. It would
 * be better if the buttons were disabled at the appropriate times. The "New Game"
 * button should be disabled when there is a game in progress. The "Hit" and "Stand"
 * buttons should be disabled when there is not a game in progress. The instance variable
 * gameInProgress tells whether or not a game is in progress, so you just have to make sure
 * that the buttons are properly enabled and disabled whenever this variable changes value.
 * I strongly advise writing a method that can be called every time it is necessary to set the
 * value of the gameInProgress variable. That method can take full responsibility for
 * enabling and disabling the buttons ( as long as it is used consistently ). Recall
 * that if bttn is a variable of type button, then bttn.setDisable( true ) disables the
 * button and bttn.setDisable( false ) enables the button.
 *
 * A second ( and more difficult ) improvement, make it possible for the user to place bets
 * on the Blackjack game. When the program starts, give the user $100. Add a TextField to the
 * strip of controls along the bottom of the panel. The user enters the bet in this TextField.
 * When the game begins, check the amount of the bet. You should do this when the game begins,
 * not when it ends, because several errors can occur: The contents of the TextField might not
 * be a legal number, the bet that the user places might be more money than the user has, or the
 * bet might be <= 0. You should detect these errors and show an error message instead of starting
 * the game. The user's bet should be an integral number of dollars.
 *
 * It would be a good idea to make the TextField uneditable while the game is in progress. If betInput
 * is the TextField, you can make it editable and uneditable by the user with the commands betInput.setEditable( true )
 * and betInput.setEditable( false ).
 *
 * In the drawBoard() method, you should include commands to display the amount of money that the user
 * has left.
 *
 * There is one other thing to think about: Ideally, the program should not start a new game when it
 * is first created. The user should have a chance to set a bet amount before the game starts. So, in
 * the start() method, you should not call doNewGame(). You might want to display a message such as
 * "Welcome to Blackjack" before the first game starts.
 */


import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.text.Font;

public class Blackjack extends Application {

    private Deck deck;  // A deck of cards to be used in the game.

    private BlackjackHand dealerHand;  // Hand containing the dealelr's cards.
    private BlackjackHand playerHand;  // Hand containing the user's cards.

    private Button hitButton, standButton, newGameButton;

    private TextField betInput;  // An input box for the user's bet amount.

    private String message;  // A message drawn on the canvas, which changes to reflect the state of the game.
    private boolean gameInProgress;  // Set to true when a game begins and to false when the game ends.

    private Canvas board;  // The canvas where the cards and messages are displayed.
    private Image cardImages;  // The image that contains all the cards in a deck.
    private int usersMoney = 100;  // How much money the user currently has.
    private int betAmount;  // The amount the user bet on the current game, when a game is in progress.

    public static void main( String[] args ) {
        launch( args );
    }

    /**
     * The start() method sets up the GUI and event handling.
     */
    public void start( Stage stage ) {

        cardImages = new Image( "cards.png" );
        board = new Canvas( 515, 415 );  // Space for 5 cards across and 2 cards down,
                                                      // with 20-pixel spaces between cards, plus space
                                                      // for messages.
        hitButton = new Button( "Hit!" );
        hitButton.setOnAction( e -> doHit() );
        standButton = new Button( "Stand!" );
        standButton.setOnAction( e -> doStand() );
        newGameButton = new Button( "New Game" );
        newGameButton.setOnAction( e -> doNewGame() );

        betInput = new TextField( "10" );
        betInput.setPrefColumnCount( 5 );

        HBox buttonBar = new HBox( 6, hitButton, standButton, newGameButton, new Label( "Your bet: " ), betInput );
        buttonBar.setStyle( "-fx-border-color: darkred; -fx-border-width: 3px 0 0 0;"
                + "-fx-padding: 8px; -fx-background-color:beige" );
        buttonBar.setAlignment( Pos.CENTER );

        BorderPane root = new BorderPane();
        root.setStyle( "-fx-border-color: darkred; -fx-border-width: 3px" );
        root.setCenter( board );
        root.setBottom( buttonBar );

        setGameInProgress( false );
        drawBoard();

        Scene scene = new Scene( root );
        stage.setScene( scene );
        stage.setTitle( "Blackjack" );
        stage.setResizable( false );
        stage.show();

    }

    /**
     * This method is called whenever the value of the gameInProgress property
     * has to be changed. In addition to setting the value of the gameInProgress
     * variable, it also enables and disables the buttons and text input box to
     * reflect the state of the game.
     * @param inProgress The new value of gameInProgress.
     */
    private void setGameInProgress( boolean inProgress ) {
        gameInProgress = inProgress;
        if ( gameInProgress ) {
            hitButton.setDisable( false );
            standButton.setDisable( false );
            newGameButton.setDisable( true );
            betInput.setEditable( false );
            hitButton.requestFocus();

        }
        else {
            hitButton.setDisable( true );
            standButton.setDisable( true );
            newGameButton.setDisable( false );
            betInput.setEditable( true );
            newGameButton.requestFocus();

        }
    }

    /**
     * Start a new game. Deal two cards to each player. The game might end right then if one
     * of the players has a blackjack. Otherwise, gameInProgress is set to true and the game
     * begins.
     */
    void doNewGame() {
        if ( gameInProgress ) {
            // If the current game is not over, it is an error to try to start a new game. Should
            // be impossible, since the New Game button is disabled when it is not legal to use
            // it.
            message = "You still have to finish this game!";
            drawBoard();
            return;
        }
        if ( usersMoney == 0 ) {
            // User is broke; give the user another $100
            usersMoney = 100;
        }
        try {
            // get the amount of the user's bet and check for errors.
            betAmount = Integer.parseInt( betInput.getText() );
        }
        catch ( NumberFormatException e ) {
            message = "Bet amount must be an integer!";
            betInput.requestFocus();
            betInput.selectAll();
            drawBoard();
            return;
        }
        if ( betAmount > usersMoney ) {
            message = "The bet amount can't be more than you have!";
            betInput.requestFocus();
            betInput.selectAll();
            drawBoard();
            return;
        }
        if ( betAmount <= 0 ) {
            message = "The bet has to be a positive number";
            betInput.requestFocus();
            betInput.selectAll();
            drawBoard();
            return;
        }

        deck = new Deck();  // Create the deck and hands to use for this game.
        dealerHand = new BlackjackHand();
        playerHand = new BlackjackHand();
        deck.shuffle();
        dealerHand.addCard( deck.dealCard() );
        dealerHand.addCard( deck.dealCard() );
        playerHand.addCard( deck.dealCard() );
        playerHand.addCard( deck.dealCard() );

        if ( dealerHand.getBlackjackValue() == 21 ) {
            message = "Sorry, you lose. Dealer has a Blackjack.";
            usersMoney = usersMoney - betAmount;
            setGameInProgress( false );

        }
        else if ( playerHand.getBlackjackValue() == 21 ) {
            message = "Congratulations, you have a Blackjack.";
            usersMoney = usersMoney + betAmount;
            setGameInProgress( false );
        }
        else {
            message = "You have " + playerHand.getBlackjackValue() + ". Hit or Stand?";
            setGameInProgress( true );
        }
        drawBoard();

    }

    /**
     * This method is called when the user clicks the "Hit!" button. First check that a game
     * is actually in progress. If not, give an error message and exit. Otherwise, give the
     * user a card. The game can end at this point if the user goes over 21 or if the user has
     * taken 5 cards without going over 21.
     */
    void doHit() {

        if ( gameInProgress == false ) {
            // Should be impossible, since the Hit button is disabled
            // when it is not legal to use it.
            message = "Click \"New Game\" to start a new game.";
            drawBoard();
            return;
        }
        playerHand.addCard( deck.dealCard() );

        if ( playerHand.getBlackjackValue() > 21 ) {
            setGameInProgress( false );
            usersMoney = usersMoney - betAmount;
            message = "You've busted! Sorry, you lose on: " + playerHand.getBlackjackValue();

        }
        else if ( playerHand.getCardCount() == 5 ) {
            setGameInProgress( false );
            usersMoney = usersMoney + betAmount;
            message = "You win by taking 5 cards without going over 21.";

        }
        else {
            message = "You have " + playerHand.getBlackjackValue() + ". Hit or Stand?";

        }
        drawBoard();

    }

    /**
     * This method is called when the user clicks the "Stand!" button. Check whether
     * a game is actually in progress. If it is, the game ends. The dealer takes cards
     * until either the dealer has 5 cards or more than 16 points. Then the winner of
     * the game is determined.
     */
    void doStand() {
        if ( gameInProgress == false ) {
            // Should be impossible, since the Stand button is disabled when
            // it is not legal to use it.
            message = "Click \"New Game\" to start a new game.";
            drawBoard();
            return;

        }
        setGameInProgress( false );

        while ( dealerHand.getBlackjackValue() <= 16 && dealerHand.getCardCount() < 5 )
            dealerHand.addCard( deck.dealCard() );
        if ( dealerHand.getBlackjackValue() > 21 ) {
            usersMoney = usersMoney + betAmount;
            message = "Congratulations, You win! Dealer has busted with " + dealerHand.getBlackjackValue() + ".";

        }
        else if ( dealerHand.getCardCount() == 5 ) {
            usersMoney = usersMoney - betAmount;
            message = "Sorry, you lose. Dealer took 5 cards without going over 21.";

        }
        else if ( dealerHand.getBlackjackValue() > playerHand.getBlackjackValue() ) {
            usersMoney = usersMoney - betAmount;
            message = "Sorry, you lose, " + dealerHand.getBlackjackValue() + " to " + playerHand.getBlackjackValue() + ".";

        }
        else if ( dealerHand.getBlackjackValue() == playerHand.getBlackjackValue() ) {
            usersMoney = usersMoney - betAmount;
            message = "Sorry, you lose. Dealer wins on a tie.";

        }
        else {
            usersMoney = usersMoney + betAmount;
            message = "You win, " + playerHand.getBlackjackValue() + " to " + dealerHand.getBlackjackValue() + "!";

        }
        drawBoard();
    }

    /**
     * The drawBoard() method shows the messages at the bottom of the canvas,
     * and it draws all of the dealt cards spread out across the canvas. If the
     * first game has not started, it shows a welcome message instead of the cards.
     */
    public void drawBoard() {

        GraphicsContext g = board.getGraphicsContext2D();
        g.setFill( Color.DARKGREEN );
        g.fillRect( 0, 0, board.getWidth(), board.getHeight() );

        g.setFont( Font.font(16) );

        // Draw a message telling how much money the user has.
        g.setFill( Color.YELLOW );
        if ( usersMoney > 0 ) {
            g.fillText( "You have $ " + usersMoney, 20, board.getHeight() - 45 );
        }
        else {
            g.fillText( "YOU ARE BROKE! ( I will give you another $100. )", 20, board.getHeight() - 45 );
            usersMoney = 100;

        }

        g.setFill( Color.rgb( 220, 255, 220 ) );

        if ( dealerHand == null ) {
            // The first game has not yet started.
            // Draw a welcome message and return.
            g.setFont( Font.font( 30 ) );
            g.fillText( "        Welcome to Blackjack!\n        Place your bet and \n        click \"New Game\".", 40, 80 );

            g.setFont( Font.font( 15 ) );
            g.fillText( message, 20, board.getHeight() - 20 );

            return;
        }

        // Draw the message at the bottom of the canvas.
        g.fillText( message, 20, board.getHeight() - 20 );

        // Draw labels for the two sets of cards.
        g.fillText( "Dealer's Cards: ", 20, 27 );
        g.fillText( "Your Cards: ", 20, 190 );

        /**
         * Draw the dealer's cards. Draw first card face down if the game is still
         * in progress, It will be revealed when the game ends.
         */
        if ( gameInProgress )
            drawCard( g, null, 20, 40 );
        else
            drawCard( g, dealerHand.getCard(0), 20, 40 );

        for ( int i = 1; i < dealerHand.getCardCount(); i++ )
            drawCard( g, dealerHand.getCard(i), 20 + i * 99, 40 );

        // Draw the user's cards.
        for ( int i = 0; i < playerHand.getCardCount(); i++ )
            drawCard( g, playerHand.getCard(i), 20 + i * 99, 206 );
        
    }

    /**
     * Draws a card with top-left corner at ( x, y ). If card is null, then a face-down
     * card is drawn. The card images are from the file cards.png; this program will fail
     * without it.
     */
    private void drawCard( GraphicsContext g, Card card, int x, int y ) {
        int cardRow, cardCol;
        if ( card == null ) {
            cardRow = 4;  // Row and column of a face-down card.
            cardCol = 2;

        }
        else {
            cardRow = 3 - card.getSuit();
            cardCol = card.getValue() - 1;

        }
        double sx, sy;  // top-left corner of source rectangle for card in cardImages.
        sx = 79 * cardCol;
        sy = 123 * cardRow;
        g.drawImage( cardImages, sx, sy, 79, 123, x, y, 79, 123 );

    }
}



