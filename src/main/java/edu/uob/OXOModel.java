package edu.uob;

import java.util.ArrayList;

public class OXOModel {
    private ArrayList<ArrayList<OXOPlayer>> cells;
    //private OXOPlayer[] players;
    private ArrayList<OXOPlayer> players;
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
        players = new ArrayList<>();
    }

    public void addRow(){
        if(isGameDrawn() && getNumberOfRows() == 9 && getNumberOfColumns() == 9){
            return;
        }
        if(getNumberOfRows() > 8){
            System.out.println("The maximum size of the grid is 9x9.");
            return;
        }
        ArrayList<OXOPlayer> row = new ArrayList<>();
        for (int i = 0; i < getNumberOfColumns(); i++) {
            row.add(null);
        }
        cells.add(row);
    }

    public void addColumn(){
        if(isGameDrawn() && getNumberOfRows() == 9 && getNumberOfColumns() == 9){
            return;
        }
        if(getNumberOfColumns() > 8){
            System.out.println("The maximum size of the grid is 9x9.");
            return;
        }
        for (int j = 0; j < getNumberOfRows(); j++) {
            cells.get(j).add(null);
        }
    }

    public void removeRow(){
        int row = getNumberOfRows();
        for (int j = 0; j < getNumberOfColumns(); j++) {
            if(getCellOwner(getNumberOfRows() - 1, j) != null){
                System.out.println("Cannot remove the row because it is not blank.");
                return;
            }
        }
        if(row < 2){
            return;
        }
        if(!isGameDrawn()) {
            cells.remove(row - 1);
            if(isGameDrawn()){
                setGameDrawn();
            }
        }
    }

    public void removeColumn(){
        int clm = getNumberOfColumns();
        for (int i = 0; i < getNumberOfRows(); i++) {
            if(getCellOwner(i, getNumberOfColumns() - 1) != null){
                System.out.println("Cannot remove the column because it is not blank.");
                return;
            }
        }
        if(clm < 2){
            return;
        }
        int row = getNumberOfRows();
        if(!isGameDrawn()) {
            for (int i = 0; i < row; i++) {
                cells.get(i).remove(clm - 1);
            }
            if(isGameDrawn()){
                setGameDrawn();
            }
        }
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public void addPlayer(OXOPlayer player) {
        if(!isGameDrawn() && getWinner() == null){
            players.add(player);
        }
    }

    public void switchPlayer(int curPlayerNum){
        for(int i = 0; i < getNumberOfPlayers(); i++){
            if(i + 1 >= getNumberOfPlayers()){
                currentPlayerNumber = 0;
                break;
            }
            if(i == curPlayerNum){
                currentPlayerNumber = i + 1;
                break;
            }
        }
    }

    public OXOPlayer getPlayerByNumber(int number) {
        return players.get(number);
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

    public void setNotDrawn(){
        gameDrawn = false;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }
}
