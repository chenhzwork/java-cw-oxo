package edu.uob;

import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ExampleControllerTests {
  private OXOModel model;
  private OXOController controller;

  // Make a new "standard" (3x3) board before running each test case (i.e. this method runs before every `@Test` method)
  // In order to test boards of different sizes, winning thresholds or number of players, create a separate test file (without this method in it !)
  @BeforeEach
  void setup() {
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }

  // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
  void sendCommandToController(String command) {
      // Try to send a command to the server - call will time out if it takes too long (in case the server enters an infinite loop)
      // Note: this is ugly code and includes syntax that you haven't encountered yet
      String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
      assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
  }

  // Test simple move taking and cell claiming functionality
  @Test
  void testBasicMoveTaking() {
    // Find out which player is going to make the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a move
    sendCommandToController("a1");
    // Check that A1 (cell [0,0] on the board) is now "owned" by the first player
    String failedTestComment = "Cell a1 wasn't claimed by the first player";
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), failedTestComment);
  }

  // Test out basic win detection
  @Test
  void testBasicWin() {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }
@Test
  void testWinDrawHappensTogether(){
    sendCommandToController("c1");
    sendCommandToController("c2");
    sendCommandToController("c3");
    sendCommandToController("b1");
    sendCommandToController("b2");
    sendCommandToController("b3");
    sendCommandToController("a2");
    sendCommandToController("a1");
    sendCommandToController("a3");
    assertEquals('X', model.getWinner().getPlayingLetter());
    assertFalse(model.isGameDrawn());
  }
  @Test
  void remove3x3RowToDraw(){
    sendCommandToController("a1");
    sendCommandToController("a2");
    sendCommandToController("a3");
    sendCommandToController("b1");
    sendCommandToController("b2");
    sendCommandToController("b3");
    assertFalse(model.isGameDrawn());
    controller.removeRow();
    assertSame(3, model.getNumberOfRows(), "Cannot remove row, it leads to draw.");
    assertFalse(model.isGameDrawn());
  }
  @Test
  void oneCellAndThreshold(){
    controller.decreaseWinThreshold();
    assertSame(3, model.getWinThreshold(), "The minimum win threshold is 3.");
    controller.removeRow();
    controller.removeRow();
    controller.removeColumn();
    controller.removeColumn();
    sendCommandToController("a1");
    assertTrue(model.isGameDrawn());
    controller.reset();
    assertFalse(model.isGameDrawn());
  }
  @Test
  void testChangeSizeAfterWin() {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
    controller.removeRow();
    assertEquals(2, model.getNumberOfRows(), "Failed to remove row.");
    controller.addColumn();
    assertEquals(4, model.getNumberOfColumns(), "Failed to add column.");
    controller.removeColumn();
    assertEquals(3, model.getNumberOfColumns(), "Failed to remove column.");
    controller.removeColumn();
    assertEquals(3, model.getNumberOfColumns(), "Failed to remove column.");
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
  }
  @Test
  void testVerticalWin_Cap() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("A1"); // First player
    sendCommandToController("A2"); // Second player
    sendCommandToController("B1"); // First player
    sendCommandToController("B2"); // Second player
    sendCommandToController("C1"); // First player
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
    sendCommandToController("A3");
    assertNull(model.getCellOwner(0, 2), "The game has already won.");
  }
  @Test
  void testRightDiagWinAndAdds() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("c3"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("b2"); // First player
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
    controller.addRow();
    assertEquals(4, model.getNumberOfRows(), "Add row failed.");
    controller.addColumn();
    assertEquals(4, model.getNumberOfColumns(), "Add column failed.");
    controller.increaseWinThreshold();
    assertEquals(4, model.getWinThreshold(), "The win threshold cannot be changed.");
    sendCommandToController("a4");
    assertNull(model.getCellOwner(0, 3), "Game has won, cannot drop anymore.");
  }
  @Test
  void testLeftDiagWinAndRemoves() {
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    sendCommandToController("a3"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("b2"); // First player
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
    controller.removeRow();
    assertEquals(3, model.getNumberOfRows(), "The grid cannot be changed.");
    controller.removeColumn();
    assertEquals(3, model.getNumberOfColumns(), "The grid cannot be changed.");
    controller.decreaseWinThreshold();
    assertEquals(3, model.getWinThreshold(), "The win threshold cannot be changed.");
  }
  @Test
  void testBeforeStartAdds(){
    //based on 3x3 grid
    controller.addRow();
    assertEquals(4, model.getNumberOfRows(), "Add row failed.");
    controller.addColumn();
    assertEquals(4, model.getNumberOfColumns(), "Add column failed.");
    controller.increaseWinThreshold();
    assertEquals(4, model.getWinThreshold(), "Add win threshold failed.");
    sendCommandToController("a1");
    assertSame(model.getCellOwner(0, 0), model.getPlayerByNumber(0), "Add owner failed.");
    controller.addRow();
    assertEquals(5, model.getNumberOfRows(), "Add row failed.");
    controller.addColumn();
    assertEquals(5, model.getNumberOfColumns(), "Add column failed.");
    controller.increaseWinThreshold();
    assertEquals(5, model.getWinThreshold(), "Add win threshold failed.");
    controller.decreaseWinThreshold();
    assertEquals(5, model.getWinThreshold(), "Win threshold cannot be decreased after start game.");
  }

  @Test
  void testBeforeStartRemoves(){
    controller.removeRow();
    assertEquals(2, model.getNumberOfRows(), "Remove row failed.");
    controller.removeColumn();
    assertEquals(2, model.getNumberOfColumns(), "Remove column failed.");
    controller.decreaseWinThreshold();
    assertEquals(3, model.getWinThreshold(), "The minimum win threshold is 3.");
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.decreaseWinThreshold();
    assertEquals(4, model.getWinThreshold(), "Change win threshold failed.");
    sendCommandToController("a1");
    controller.removeRow();
    assertEquals(1, model.getNumberOfRows(), "Remove row failed.");
    controller.removeColumn();
    assertEquals(2, model.getNumberOfColumns(), "Cannot remove column here.");
    //Test the minimum grid is 1x1
    controller.removeRow();
    assertEquals(1, model.getNumberOfRows(), "Cannot remove row here.");
    //Test after game start, the win threshold cannot be decreased
    controller.decreaseWinThreshold();
    assertEquals(4, model.getWinThreshold(), "Decrease win threshold failed.");
  }
  @Test
  void testRemoveTakenCell(){
    controller.addRow();
    controller.addColumn();
    sendCommandToController("b4");
    sendCommandToController("d3");
    controller.removeRow();
    controller.removeColumn();
    assertEquals(4, model.getNumberOfRows(), "Should not remove a not blank row.");
    assertEquals(4, model.getNumberOfColumns(), "Should not remove a not blank column.");
  }
  // Example of how to test for the throwing of exceptions
  @Test
  void testInvalidIdentifierException(){
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command `abc123`";
    // The next lines is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("abc123"), failedTestComment);
    assertThrows(InvalidIdentifierLengthException.class, ()-> controller.handleIncomingCommand("aa1"));
    assertThrows(InvalidIdentifierLengthException.class, ()-> controller.handleIncomingCommand("1"));
    assertThrows(InvalidIdentifierLengthException.class, ()-> controller.handleIncomingCommand("a"));
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("d1"));   //row out range
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("a4"));   //col out range
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("b0"));
    sendCommandToController("a1");
    assertThrows(CellAlreadyTakenException.class, ()-> controller.handleIncomingCommand("a1"));
    assertThrows(InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("@2"));
    assertThrows(InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("66"));
    assertThrows(InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("a%"));
  }

  @Test
  void test_draw(){
    String failedTestComment = "Game is not draw, test failed.";
    sendCommandToController("a1");
    sendCommandToController("a2");
    sendCommandToController("a3");
    sendCommandToController("b2");
    sendCommandToController("b1");
    sendCommandToController("b3");
    sendCommandToController("c2");
    sendCommandToController("c1");
    sendCommandToController("c3");
    assertTrue(model.isGameDrawn(), failedTestComment);
    //Test whether the grid size can be changed after draw
    controller.removeRow();
    assertEquals(3, model.getNumberOfRows(), "The grid cannot be changed.");
    controller.removeColumn();
    assertEquals(3, model.getNumberOfColumns(), "The grid cannot be changed.");
    controller.decreaseWinThreshold();
    assertEquals(3, model.getWinThreshold(), "Increase win threshold failed.");
    controller.addRow();
    assertEquals(4, model.getNumberOfRows(), "Add row failed.");
    controller.addColumn();
    assertEquals(4, model.getNumberOfColumns(), "Add column failed.");
    controller.increaseWinThreshold();
    assertEquals(4, model.getWinThreshold(), "Increase win threshold failed.");
  }

  void multiPlayerSetup() {
    model = new OXOModel(5, 5, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    model.addPlayer(new OXOPlayer('T'));
    controller = new OXOController(model);
  }
  @Test
  void testMultiPlayerWin(){
    multiPlayerSetup();
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer thirdMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber() + 2);
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a5"); // Third player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("b4"); // Third player
    sendCommandToController("c1"); // First player
    sendCommandToController("d1"); // Second player
    sendCommandToController("c3"); // Third player
    String failedTestComment = "Winner was expected to be " + thirdMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(thirdMovingPlayer, model.getWinner(), failedTestComment);
    controller.reset();
    String failResetPlayer = "The current player after reset should be 0.";
    assertEquals(0, model.getCurrentPlayerNumber(), failResetPlayer);
  }

  void the9x9Setup() {
    model = new OXOModel(9, 9, 100);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }

  @Test
  void testAfter9x9Draw(){
    String failedTestComment = "Test failed, the game should remain draw.";
    the9x9Setup();
    controller.addRow();
    assertEquals(9, model.getNumberOfRows(), "The max size of the grid is 9x9.");
    controller.addColumn();
    assertEquals(9, model.getNumberOfColumns(), "The max size of the grid is 9x9.");
    for (char i = 'a'; i < 'j'; i++) {
      for (int j = 1; j < 10; j++) {
        sendCommandToController("" + i + j);
      }
    }
    assertTrue(model.isGameDrawn(), "Failed to set a draw board.");
    controller.addColumn();
    controller.addRow();
    assertTrue (model.isGameDrawn(), failedTestComment);
  }

  void singlePlayerSetup() {
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    controller = new OXOController(model);
  }
  @Test
  void testSinglePlayer(){
    String failedTestComment = "Test failed, the minimum player is 2.";
    singlePlayerSetup();
    sendCommandToController("a1");
    assertNull(model.getCellOwner(0, 0), failedTestComment);
  }

  void setUpRectangle(){
    model = new OXOModel(3, 4, 4);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }
  @Test
  void testRec(){
    setUpRectangle();
    sendCommandToController("a1");
    sendCommandToController("b1");
    sendCommandToController("a2");
    sendCommandToController("b2");
    sendCommandToController("a3");
    sendCommandToController("b3");
    sendCommandToController("a4");
    assertSame(model.getWinner(), model.getPlayerByNumber(0), "Failed to the rectangle.");
  }

  void setUpRectangle2(){
    model = new OXOModel(4, 3, 4);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }
  @Test
  void testRec2(){
    setUpRectangle2();
    sendCommandToController("a1");
    sendCommandToController("a2");
    sendCommandToController("b1");
    sendCommandToController("b2");
    sendCommandToController("c1");
    sendCommandToController("c2");
    sendCommandToController("d1");
    assertSame(model.getWinner(), model.getPlayerByNumber(0), "Failed to the rectangle.");
  }

  void setUpRectangle3(){
    model = new OXOModel(4, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }
  @Test
  void testRec3(){
    setUpRectangle3();
    sendCommandToController("a1");
    sendCommandToController("a2");
    sendCommandToController("b2");
    sendCommandToController("b1");
    sendCommandToController("c3");
    assertSame(model.getWinner(), model.getPlayerByNumber(0), "Failed to the rectangle.");
  }

  void setUpRectangle4(){
    model = new OXOModel(3, 4, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }
  @Test
  void testRec4(){
    setUpRectangle4();
    sendCommandToController("c4");
    sendCommandToController("a3");
    sendCommandToController("b3");
    sendCommandToController("b1");
    sendCommandToController("a2");
    assertSame(model.getWinner(), model.getPlayerByNumber(0), "Failed to the rectangle.");
  }
}
