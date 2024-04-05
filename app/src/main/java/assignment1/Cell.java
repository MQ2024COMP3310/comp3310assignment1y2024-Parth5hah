package assignment1;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Cell extends Rectangle{
    private static int size = 70;
    protected int col;
    protected int row;

    protected Color backbgroundColor;
    protected Color textColor; //should be opposite of piece colour later on
    protected char displayCharacter; //" " or "K" or "A-H" or "1-8"
    
    protected int cellType; // 0 = board, 1 = label
    protected boolean hasPiece;
    protected Color pieceColour;
    protected boolean isKingPiece;


    public Cell(){
        super(0,0,0,0);
        col = -1;
        row = -1;
        displayCharacter = ' ';
        backbgroundColor = Color.DARK_GRAY;
        textColor = Color.WHITE;
        cellType = 0; // 0 = board, 1 = label
        hasPiece = false;
        pieceColour = Color.white;
        isKingPiece = false;
    }

    public Cell(int columnIndex, int rowIndex, int inX, int inY, int cellRole, Color cellColour, String label){
        super(inX,inY,size,size);
        col = columnIndex;
        row = rowIndex;
        displayCharacter = label.charAt(0);
        backbgroundColor = cellColour;
        textColor = Color.WHITE;
        cellType = cellRole; // 0 = board, 1 = label
        hasPiece = false;
        pieceColour = Color.WHITE;
        isKingPiece = false;
    }

    void paint(Graphics g){
        if(cellType == 0) {
            // this is a board cell
            
            g.setColor(backbgroundColor);
            g.fillRect(x, y, size, size);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, size, size);

            //draw cell background colour
            
            //if has peice, draw the piece on top
            if(hasPiece == true){
                // draw the piece on it
                g.setColor(pieceColour);
                g.fillOval(x, y, size, size); // colour of piece
                g.setColor(Color.RED);
                g.drawOval(x, y, size, size); // red border around piece
                
                if(isKingPiece == true){
                    // put a K on top of the piece to indicate a king piece
                    g.setColor(Color.RED);
                    Font f = new Font("Arial", Font.PLAIN, 40);
                    FontMetrics metrics = g.getFontMetrics(f);
                    int drawXPos = x + ((size - metrics.stringWidth(""+displayCharacter))/2);
                    int drawYPos = y + ((size + metrics.getHeight())/2 - 10);
        
                    g.setFont(f); 
                    g.drawString("K", drawXPos-6, drawYPos);
                }
            }

        } else {
            // this is a label cell
            g.setColor(Color.BLACK);
            Font f = new Font("Arial", Font.PLAIN, 40);
            FontMetrics metrics = g.getFontMetrics(f);
            int drawXPos = x + ((size - metrics.stringWidth(""+displayCharacter))/2);
            int drawYPos = y + ((size + metrics.getHeight())/2 - 10);

            g.setFont(f); 
            g.drawString(""+displayCharacter, drawXPos, drawYPos);
        }

    }

    void reset(){
        // board cell neads to be cleared.
        // label piece does not need to be changed (risky if overwritten?)
        if(cellType == 0){
            hasPiece = false;
            isKingPiece = false;
        }
    }

    public boolean hasPiece(){
        return hasPiece;
    }

    public Color getPieceColor(){
        if(hasPiece == true){
            return pieceColour;
        }else{
            return null;
        }
    }

    public void setPiece(Color newPieceColour, boolean isAKingPiece){
        pieceColour = newPieceColour;
        hasPiece = true;
        isKingPiece = isAKingPiece;
    }

    public void removePiece(){
        reset();
    }

    public boolean getPieceKingStatus(){
        return isKingPiece;
    }

    public void makePieceKing(){
        isKingPiece = true;
    }

    

    public String getStoredCharacter(){
        return "" + displayCharacter;
    }

    @Override
    public String toString(){
        if(hasPiece == false){
            return Integer.toString(col) + Integer.toString(row) + ": Blank Square. ";
        }else{
            if(isKingPiece == true){
                return Integer.toString(col) + Integer.toString(row) + ": Square with KING piece of colour " + pieceColour.toString();
            }
            return Integer.toString(col) + Integer.toString(row) + ": Square with piece of colour " + pieceColour.toString();
        }
    }
    
}

    