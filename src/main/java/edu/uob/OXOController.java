package edu.uob;

import java.util.Scanner;

public class OXOController {
    OXOModel gameModel;

    //boolean keepScan = true;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        new OXOModel(3, 3, 3);
        int curPlayerNum = gameModel.getCurrentPlayerNumber();
        gameModel.setCurrentPlayerNumber(curPlayerNum);
        int rowNumber = 0;
        if(command.charAt(0) >= 97 && command.charAt(0) <= 122) {
            rowNumber = command.charAt(0) - 97;
        }else if(command.charAt(0) >= 65 && command.charAt(0) <= 90){
            rowNumber = command.charAt(0) - 65;
        }
        int colNumber = command.charAt(1) - 49;
        OXOPlayer currPlayer = gameModel.getPlayerByNumber(curPlayerNum);
        if (gameModel.getCellOwner(rowNumber, colNumber) == null && !gameModel.isGameDrawn()
            && gameModel.getWinner() == null) {
            gameModel.setCellOwner(rowNumber, colNumber, currPlayer);
            gameModel.switchPlayer(curPlayerNum);
        } else {
            System.out.println("Cannot drop.");
        }

        int winThreshold = gameModel.getWinThreshold();
        if(horizontalWin(rowNumber, currPlayer, winThreshold) ||
            verticalWin(colNumber, currPlayer, winThreshold) ||
            diagWin(rowNumber, colNumber, currPlayer, winThreshold)){
            gameModel.setWinner(currPlayer);
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

    private boolean diagWin(int rowNumber, int colNumber, OXOPlayer currPlayer, int winThreshold){
        int diagCounter = 0;
        int smalSide = Math.min(gameModel.getNumberOfColumns(), gameModel.getNumberOfRows());
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                if(gameModel.getCellOwner(i, j) == currPlayer){
                    int smalNum = Math.max(i, j);
                    for (int k = 1; k < smalSide - smalNum; k++) {
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
                }
            }
        }
        return false;
    }

    public void addRow() {
        gameModel.addRow();
    }
    public void removeRow() {
        gameModel.removeRow();
    }
    public void addColumn() {
        gameModel.addColumn();
    }
    public void removeColumn() {
        gameModel.removeColumn();
    }
    public void increaseWinThreshold() {}
    public void decreaseWinThreshold() {}
    public void reset() {
        for(int i = 0; i < gameModel.getNumberOfRows(); i++){
            for(int j = 0; j < gameModel.getNumberOfColumns(); j++){
                gameModel.setCellOwner(i, j, null);
            }
        }
        if(gameModel.getWinner() != null){
            gameModel.setWinner(null);
        }
    }

}
