package edu.uob;
import edu.uob.OXOMoveException.*;
import java.util.Scanner;


public class OXOController {
    OXOModel gameModel;

    //boolean keepScan = true;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        if(gameModel.isGameDrawn()){
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
        int colNumber = 0;
        if(command.charAt(1) > 48 && command.charAt(1) < 58){
            colNumber = command.charAt(1) - 49;
        }
        OXOPlayer currPlayer = gameModel.getPlayerByNumber(curPlayerNum);


        if (rowNumber + 1 > gameModel.getNumberOfRows()){
            throw new OutsideCellRangeException(RowOrColumn.ROW, rowNumber);
        }

        if (colNumber + 1 > gameModel.getNumberOfColumns()){
            throw new OutsideCellRangeException(RowOrColumn.COLUMN, colNumber);
        }

        if (gameModel.getCellOwner(rowNumber, colNumber) != null){
            throw new CellAlreadyTakenException(rowNumber, colNumber);
        }

        if (command.charAt(0) < 65 || (command.charAt(0) > 73 && command.charAt(0) < 96) || command.charAt(0) > 105){
            throw new InvalidIdentifierCharacterException(RowOrColumn.ROW, command.charAt(0));
        }

        if (!Character.isDigit(command.charAt(1))){
            throw new InvalidIdentifierCharacterException(RowOrColumn.COLUMN, command.charAt(1));
        }

        if (gameModel.getCellOwner(rowNumber, colNumber) == null && !gameModel.isGameDrawn()
                && gameModel.getWinner() == null) {
            gameModel.setCellOwner(rowNumber, colNumber, currPlayer);
            gameModel.switchPlayer(curPlayerNum);
        }

        int winThreshold = gameModel.getWinThreshold();
        if(horizontalWin(rowNumber, currPlayer, winThreshold) ||
            verticalWin(colNumber, currPlayer, winThreshold) ||
            diagWin(currPlayer, winThreshold)){
            gameModel.setWinner(currPlayer);
        }

        if(isDraw()){
            gameModel.setGameDrawn();
        }
    }

    private boolean isDraw(){
        int drawCounter = 0;
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                if(gameModel.getCellOwner(i, j) == null){
                    drawCounter++;
                }
            }
        }
        if(drawCounter == 0){
            return true;
        }
        return false;
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
        for (int j = 0; j < gameModel.getNumberOfRows(); j++) {
            if(gameModel.getCellOwner(j, colNumber) == currPlayer){
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
        if(!gameModel.isGameDrawn() && gameModel.getWinner() == null){
            gameModel.addRow();
        }
    }
    public void removeRow() {
        if(!gameModel.isGameDrawn() && gameModel.getWinner() == null) {
            gameModel.removeRow();
        }
    }
    public void addColumn() {
        if(!gameModel.isGameDrawn() && gameModel.getWinner() == null){
            gameModel.addColumn();
        }
    }
    public void removeColumn() {
        if(!gameModel.isGameDrawn() && gameModel.getWinner() == null) {
            gameModel.removeColumn();
        }
    }
    public void increaseWinThreshold() {
        if(!isGameStart()){
            gameModel.setWinThreshold(gameModel.getWinThreshold() + 1);
            System.out.println("The new win threshold is: " + gameModel.getWinThreshold());
        }else{
            System.out.println("Ooops, game has begun!");
        }
    }
    public void decreaseWinThreshold() {
        if(gameModel.getWinThreshold() < 2){
            System.out.println("Cannot decrease anymore!");
            return;
        }
        if(!isGameStart()){
            gameModel.setWinThreshold(gameModel.getWinThreshold() - 1);
            System.out.println("The new win threshold is: " + gameModel.getWinThreshold());
        }else{
            System.out.println("Ooops, game has begun!");
        }
    }

    public void addPlayer(){
        char c = 65;
        for (int i = 0; i < gameModel.getNumberOfPlayers(); i++) {
            if(c == gameModel.players[i])
        }
        OXOPlayer oxoPlayer = new OXOPlayer();
        gameModel.addPlayer();
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
    }

    public boolean isGameStart(){
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                if(gameModel.getCellOwner(i, j) != null){
                    return true;
                }
            }
        }
        return false;
    }

}
