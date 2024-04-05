package assignment1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
// import java.util.Scanner;
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;



public class Board {
    Grid grid;
    SQLiteConnectionManager movesDatabaseConnection; // to import, store, and print moves.
    Prompt keyboardInputDisplay; // the black display a the bottom of the gui
    int move = 0;

    public Board(){
        
        keyboardInputDisplay = new Prompt(10,750);

        movesDatabaseConnection = new SQLiteConnectionManager("checkersGames.db");
        int setupStage = 0;

        movesDatabaseConnection.createNewDatabase("checkersGames.db");
        if (movesDatabaseConnection.checkIfConnectionDefined())
        {
            System.out.println("Game created and connected.");
            if(movesDatabaseConnection.createTables()) 
            {
                System.out.println("Game structures in place.");
                setupStage = 1;
            }
        }

        grid = new Grid(8,8, movesDatabaseConnection);
        setNewGame();
        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            
            System.out.println("Processing resources/data.txt");
            boolean validMovesSoFar = true;
            while ((line = br.readLine()) != null && validMovesSoFar) {
                System.out.println(line);
                if(move == 0){
                    if(!grid.processMoveRequest(Color.WHITE,line)){ 
                        validMovesSoFar = false;
                    }else{
                        move = 1 - move;
                    }
                }else{
                    if(!grid.processMoveRequest(Color.BLACK,line)) 
                    {
                        validMovesSoFar = false;
                    }else{
                        move = 1 - move;
                    }
                }
            
            }
            
            setupStage = 2;
        }catch(IOException e)
        {
            System.out.println(e.getMessage());
        }

    }

    public void resetBoard(){
        grid.reset();

    }

    void paint(Graphics g){
        grid.paint(g);
        keyboardInputDisplay.paint(g);
    }    

    public void setNewGame(){
        grid.reset();
        keyboardInputDisplay.reset();

        for(int row = 7; row > 4; row--){
            int column = 0 + (1-(row%2));
            for(;column < 8; column += 2){
                grid.addPiece(row, column, Color.WHITE, false);
            }
        }

        for(int row = 0; row < 3; row++){
            int column = 0 + ((row+1)%2);
            for(;column < 8; column += 2){
                grid.addPiece(row, column, Color.BLACK, false);
            }
        }

    }

    public boolean addMoveToGrid(Color colorToMove, String instruction){
        return grid.processMoveRequest(colorToMove, instruction);
    }

    public void keyPressed(KeyEvent e){
        System.out.println("Key Pressed! " + e.getKeyCode());

        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            grid.keyPressedEnter();
            boolean validInstruction = false;
            if(move == 0){
                validInstruction = grid.processMoveRequest(Color.WHITE, keyboardInputDisplay.getPromptText());
            }else{
                validInstruction = grid.processMoveRequest(Color.BLACK, keyboardInputDisplay.getPromptText());
            }
            keyboardInputDisplay.reset();
            if(validInstruction){
                move = 1 - move;
            }
            System.out.println("Enter Key Pressed");
            
            if(grid.countNumPieces(Color.WHITE)<=0 || grid.countNumPieces(Color.BLACK)<=0 ){
                System.out.println("Winner! Now exiting ...");
                System.exit(0);
            }
        }
        
        if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
            grid.keyPressedBackspace();
            keyboardInputDisplay.removeLastLetter();
            System.out.println("Backspace Key");
        }
        
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            grid.keyPressedEscape();
            System.out.println("Escape Key");
        }

        if( (e.getKeyCode()>= KeyEvent.VK_A && e.getKeyCode() <= KeyEvent.VK_Z) || 
            (e.getKeyCode()>= KeyEvent.VK_0 && e.getKeyCode() <= KeyEvent.VK_9) || 
            (e.getKeyCode()== KeyEvent.VK_SPACE)){
            grid.keyPressedLetter(e.getKeyChar());
            keyboardInputDisplay.addLetter((char) e.getKeyCode() );
            System.out.println("Character Key sent to prompt");
        }

    }

}
