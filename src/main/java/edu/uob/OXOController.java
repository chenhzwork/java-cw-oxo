package edu.uob;

import java.util.Scanner;

public class OXOController {
    OXOModel gameModel;

    boolean keepScan = true;

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
        if (gameModel.getCellOwner(rowNumber, colNumber) == null && keepScan) {
            gameModel.setCellOwner(rowNumber, colNumber, currPlayer);
            gameModel.switchPlayer(curPlayerNum);
        } else {
            System.out.println("Cannot drop.");
        }
        //whether row met the threshold
        int rowCounter = 0;
        int winThreshold = gameModel.getWinThreshold();
        for (int i = 0; i < gameModel.getNumberOfColumns(); i++) {
            if(gameModel.getCellOwner(rowNumber, i) == currPlayer){
                while(i < gameModel.getNumberOfColumns()){
                    if(gameModel.getCellOwner(rowNumber, i) == currPlayer) {
                        rowCounter++;
                    }
                    if(rowCounter == winThreshold){
                        gameModel.setWinner(currPlayer);
                        keepScan = false;
                    }
                    i++;
                }
            }
        }
        //whether column met the threshold
        int colCounter = 0;
        for (int j = 0; j < gameModel.getNumberOfRows(); j++) {
            if(gameModel.getCellOwner(j, colNumber) == currPlayer){
                while(j < gameModel.getNumberOfRows()){
                    if(gameModel.getCellOwner(j, colNumber) == currPlayer) {
                        colCounter++;
                    }
                    if(colCounter == winThreshold){
                        gameModel.setWinner(currPlayer);
                        keepScan = false;
                    }
                    j++;
                }
            }
        }
        // STOPSHIP: 15/02/2023 判断斜线
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
    }

}
