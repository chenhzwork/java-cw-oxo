package edu.uob;

import java.util.ArrayList;

public class OXOModel {
    private ArrayList<ArrayList<OXOPlayer>> cells;
    private OXOPlayer[] players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        cells = new ArrayList<>();
        for(int i = 0; i < numberOfRows; i++){
            cells.add(new ArrayList<>());
            for(int j = 0; j < numberOfColumns; j++){
                cells.get(i).add(null);
            }
        }
        players = new OXOPlayer[2];
    }

    public void addRow(){
        ArrayList<OXOPlayer> row = new ArrayList<>();
        for (int i = 0; i < getNumberOfColumns(); i++) {
            row.add(null);
        }
        cells.add(row);
    }

    public void addColumn(){
        for (int i = 0; i < getNumberOfRows(); i++) {
            cells.get(i).add(null);
        }
    }

    public void removeRow(){
        int clm = getNumberOfColumns();
        int row = getNumberOfRows();
        if(row <= winThreshold){
            System.out.println("Cannot remove the row.");
            return;
        }
        cells.remove(clm);
    }

    public void removeColumn(){
        int clm = getNumberOfColumns();
        int row = getNumberOfRows();
        if(clm <= winThreshold){
            System.out.println("Cannot remove the column.");
            return;
        }
        for (int j = 0; j < row; j++) {
            cells.get(j).remove(row);
        }
    }

    public int getNumberOfPlayers() {
        return players.length;
    }

    public void addPlayer(OXOPlayer player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                return;
            }
        }
    }

    public int switchPlayer(int curPlayerNum){
        for(int i = 0; i < players.length; i++){
            if(i + 1 >= players.length){
                currentPlayerNumber = 0;
                break;
            }
            if(i == curPlayerNum){
                currentPlayerNumber = i + 1;
                break;
            }
        }
        return currentPlayerNumber;
    }

    public OXOPlayer getPlayerByNumber(int number) {
        return players[number];
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        return cells.size();
    }

    public int getNumberOfColumns() {
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells.get(rowNumber).set(colNumber, player);
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn() {
        gameDrawn = true;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

}
