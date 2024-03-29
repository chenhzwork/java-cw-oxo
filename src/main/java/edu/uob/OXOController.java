package edu.uob;
import edu.uob.OXOMoveException.*;

public class OXOController {
    OXOModel gameModel;

    //boolean keepScan = true;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        if(gameModel.getNumberOfPlayers() < 2){
            System.out.println("The minimum player is 2.");
            return;
        }
        if(gameModel.isGameDrawn()){
            return;
        }
        if(gameModel.getWinner() != null){
            return;
        }
        int len = command.length();
        if (len != 2){
            throw new InvalidIdentifierLengthException(len);
        }
        new OXOModel(3, 3, 3);
        int curPlayerNum = gameModel.getCurrentPlayerNumber();
        gameModel.setCurrentPlayerNumber(curPlayerNum);
        int rowNumber = 0;
        if(command.charAt(0) >= 97 && command.charAt(0) <= 122) {
            rowNumber = command.charAt(0) - 97;
        }else if(command.charAt(0) >= 65 && command.charAt(0) <= 90){
            rowNumber = command.charAt(0) - 65;
        }
        int colNumber = command.charAt(1) - '1';
        OXOPlayer currPlayer = gameModel.getPlayerByNumber(curPlayerNum);

        if (command.charAt(0) < 65 || (command.charAt(0) > 90 && command.charAt(0) < 97) || command.charAt(0) > 122){
            throw new InvalidIdentifierCharacterException(RowOrColumn.ROW, command.charAt(0));
        }

        if (!Character.isDigit(command.charAt(1))){
            throw new InvalidIdentifierCharacterException(RowOrColumn.COLUMN, command.charAt(1));
        }

        if (rowNumber + 1 > gameModel.getNumberOfRows()){
            throw new OutsideCellRangeException(RowOrColumn.ROW, rowNumber);
        }

        if (colNumber + 1 > gameModel.getNumberOfColumns() || colNumber < 0){
            throw new OutsideCellRangeException(RowOrColumn.COLUMN, colNumber);
        }

        if (gameModel.getCellOwner(rowNumber, colNumber) != null){
            throw new CellAlreadyTakenException(rowNumber, colNumber);
        }

        if (gameModel.getCellOwner(rowNumber, colNumber) == null
                && gameModel.getWinner() == null) {
            gameModel.setCellOwner(rowNumber, colNumber, currPlayer);
            gameModel.switchPlayer(curPlayerNum);
        }
        if(gameModel.getNumberOfColumns() == 1 && gameModel.getNumberOfRows() == 1){
            gameModel.setGameDrawn();
            return;
        }
        if(detectWinner() != null){
            gameModel.setWinner(detectWinner());
            return;
        }
        if(gameModel.detectDraw()){
            gameModel.setGameDrawn();
        }
    }



    private boolean horizontalWin(int rowNumber, OXOPlayer currPlayer, int winThreshold){
        int rowCounter = 0;
        for (int i = 0; i < gameModel.getNumberOfColumns(); i++) {
            if(gameModel.getCellOwner(rowNumber, i) == currPlayer){
                for(int cursor = 0; cursor < gameModel.getNumberOfColumns(); cursor++){
                    if(gameModel.getCellOwner(rowNumber, cursor) != currPlayer){
                        rowCounter = 0;
                    }
                    if(gameModel.getCellOwner(rowNumber, cursor) == currPlayer) {
                        rowCounter++;
                    }
                    if(rowCounter == winThreshold){
                        return true;
                    }
                }
                rowCounter = 0;
            }
        }
        return false;
    }

    private boolean verticalWin(int colNumber, OXOPlayer currPlayer, int winThreshold){
        int colCounter = 0;
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            if(gameModel.getCellOwner(i, colNumber) == currPlayer){
                for(int cursor = 0; cursor < gameModel.getNumberOfRows(); cursor++){
                    if(gameModel.getCellOwner(cursor, colNumber) != currPlayer){
                        colCounter = 0;
                    }
                    if(gameModel.getCellOwner(cursor, colNumber) == currPlayer) {
                        colCounter++;
                    }
                    if(colCounter == winThreshold){
                        return true;
                    }
                }
                colCounter = 0;
            }
        }
        return false;
    }

    private boolean diagWin(OXOPlayer currPlayer, int winThreshold){
        int diagCounter = 0;
        boolean rgt_flag = true;
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                if(gameModel.getCellOwner(i, j) == currPlayer){
                    int minNum_rgt = Math.min(gameModel.getNumberOfRows() - i, gameModel.getNumberOfColumns() - j);
                    if(minNum_rgt < winThreshold){
                        rgt_flag = false;
                    }
                    for (int k = 0; k < minNum_rgt && rgt_flag; k++) {
                        if(gameModel.getCellOwner(i + k, j + k) != currPlayer){
                            diagCounter = 0;
                        }
                        if(gameModel.getCellOwner(i + k, j + k) == currPlayer){
                            diagCounter++;
                        }
                        if(diagCounter == winThreshold){
                            return true;
                        }
                    }
                    diagCounter = 0;
                    boolean lft_flag = true;
                    int minNum_lft = Math.min(gameModel.getNumberOfRows() - i - 1, j);
                    if(minNum_lft < winThreshold - 1){
                        lft_flag = false;
                    }
                    for (int l = 0; l <= minNum_lft && lft_flag; l++) {
                        if(gameModel.getCellOwner(i + l, j - l) != currPlayer){
                            diagCounter = 0;
                        }
                        if(gameModel.getCellOwner(i + l, j - l) == currPlayer){
                            diagCounter++;
                        }
                        if(diagCounter == winThreshold){
                            return  true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void addRow() {
        if(gameModel.detectDraw() && gameModel.getNumberOfRows() == 9 && gameModel.getNumberOfColumns() == 9){
            return;
        }
        if(gameModel.getNumberOfRows() > 8){
            System.out.println("The maximum size of the grid is 9x9.");
            return;
        }
        if(gameModel.getWinner() == null){
            gameModel.setNotDrawn();
        }
        gameModel.addRow();
    }

    public void addColumn() {
        if(gameModel.detectDraw() && gameModel.getNumberOfRows() == 9 && gameModel.getNumberOfColumns() == 9){
            return;
        }
        if(gameModel.getNumberOfColumns() > 8){
            System.out.println("The maximum size of the grid is 9x9.");
            return;
        }
        if(gameModel.getWinner() == null){
            gameModel.setNotDrawn();
        }
        gameModel.addColumn();
    }

    public void removeRow() {
        for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
            if(gameModel.getCellOwner(gameModel.getNumberOfRows() - 1, j) != null){
                System.out.println("Cannot remove the row because it is not blank.");
                return;
            }
        }
        if(gameModel.getNumberOfRows() < 2){
            return;
        }
        gameModel.removeRow();
    }

    public void removeColumn() {
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            if(gameModel.getCellOwner(i, gameModel.getNumberOfColumns() - 1) != null){
                System.out.println("Cannot remove the column because it is not blank.");
                return;
            }
        }
        if(gameModel.getNumberOfColumns() < 2){
            return;
        }
        gameModel.removeColumn();
    }

    public void increaseWinThreshold() {
        gameModel.setWinThreshold(gameModel.getWinThreshold() + 1);
        System.out.println("The win threshold is: " + gameModel.getWinThreshold());
    }

    public void decreaseWinThreshold() {
        if(gameModel.isGameStart() && gameModel.getWinner() == null){
            System.out.println("The game has started, cannot decrease win threshold.");
            return;
        }
        if(gameModel.detectDraw()){
            return;
        }
        if(gameModel.getWinThreshold() <= 3){
            System.out.println("The minimum win threshold is 3.");
            return;
        }
        gameModel.setWinThreshold(gameModel.getWinThreshold() - 1);
        System.out.println("The win threshold is: " + gameModel.getWinThreshold());
    }

    public void reset() {
        for(int i = 0; i < gameModel.getNumberOfRows(); i++){
            for(int j = 0; j < gameModel.getNumberOfColumns(); j++){
                gameModel.setCellOwner(i, j, null);
            }
        }
        if(gameModel.getWinner() != null){
            gameModel.setWinner(null);
        }
        if(gameModel.isGameDrawn()){
            gameModel.setNotDrawn();
        }
        gameModel.setCurrentPlayerNumber(0);
    }
    public OXOPlayer detectWinner(){
        OXOPlayer player;
        int winThreshold = gameModel.getWinThreshold();
        for (int i = 0; i < gameModel.getNumberOfPlayers(); i++) {
            player = gameModel.getPlayerByNumber(i);
            for (int j = 0; j < gameModel.getNumberOfRows(); j++) {
                for (int k = 0; k < gameModel.getNumberOfColumns(); k++) {
                    if(horizontalWin(j, player, winThreshold) ||
                            verticalWin(k, player, winThreshold) ||
                            diagWin(player, winThreshold)){
                        return player;
                    }
                }
            }
        }
        return null;
    }
}

