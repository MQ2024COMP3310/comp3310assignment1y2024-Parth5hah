package assignment1;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.function.Consumer;



public class Grid implements Iterable<Cell>{
    Cell[][] cells;
    boolean gameFinished;
    SQLiteConnectionManager movesDatabaseConnection;
    
    public Grid(int rows, int cols, SQLiteConnectionManager sqlConn){
        cells = new Cell[rows + 1][cols + 1];
        //board cells + row and col for lables.
        
        //create the board and label cells
        for (int i = 0; i < cells.length -1; i++) {
            int x = 0;
            int y = 0;
            for (int j = 0; j < cells[i].length -1; j++) {
                x = 10 + 80 * i;
                y = 10 + 80 * j;
                if( (i + j) % 2 == 0){
                    cells[i][j] = new Cell(i,j,y,x, 0, Color.LIGHT_GRAY," ");
                }
                else{
                    cells[i][j] = new Cell(i,j,y,x, 0, Color.DARK_GRAY, " ");
                }
            }
            //add row label on the end
            y = 10 + 80 * cols;
            cells[i][cols] = new Cell(i,cols,y,x, 1, Color.WHITE, Integer.toString(8-i)); 
        }
        
        //now draw the row of letters for the bottom row (A thorugh H letters)
        for(int l = 0; l < cells[rows].length; l++){
            int x = 10 + 80 * rows;
            int y = 10 + 80 * l;
            if(l <8){
                cells[rows][l] = new Cell(rows,l,y,x, 1, Color.WHITE, Character.toString ((char) l + 65)); 
            }else{
                // no label for the bottom right corner.
                cells[rows][l] = new Cell(rows,l,y,x, 1, Color.WHITE, " "); 
            }
        }

        gameFinished = false;
        movesDatabaseConnection = sqlConn;
    }

    public int countNumPieces(Color colourToCheck){
        int count = 0;
        for (int i = 0; i < cells.length -1; i++) {
            for (int j = 0; j < cells[i].length -1; j++) {
                if(cells[i][j].hasPiece()){
                    if( cells[i][j].getPieceColor() == colourToCheck){
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public void addPiece(int row, int column, Color theColourToCreate, boolean createAsKingPiece){
        cells[row][column].setPiece(theColourToCreate, createAsKingPiece);
    }


    public boolean processMoveRequest(Color colorOfMover, String instruction){

        System.out.println("Instruction: " + instruction);

        String [] arguments = instruction.split(" ");

        if(arguments.length != 3){
            System.out.println("arg length is NOT 3");
            return false;
        }

        System.out.println("arg length is 3");
        int fromRow = getRow(arguments[1]);
        int fromCol = getCol(arguments[1]);
        int toRow = getRow(arguments[2]);
        int toCol = getCol(arguments[2]);

        if(fromRow<0 || fromRow > 8 || fromCol<0 || fromCol > 8 || toRow<0 || toRow > 8 || toCol<0 || toCol > 8){
            return false;
        }

        //can now assume to and from locations are on the board.

        if(arguments[0].equals("MOVE")){
            System.out.println("MOVE recognised");
            Color pieceColor = cells[fromRow][fromCol].getPieceColor();

            if(pieceColor != null && cells[fromRow][fromCol].hasPiece() && pieceColor == colorOfMover){
                //check to position
                if(! cells[toRow][toCol].hasPiece()){ // need to check validity of move (localisation not a take)
                    if(cells[fromRow][fromCol].getPieceKingStatus()){
                        //check if not a valid king move.
                        System.out.println("KING PEICE MOVE");
                        if(! (Math.abs(fromRow - toRow) == 1) ||! (Math.abs(fromCol - toCol) == 1)){
                            return false;
                        }
                    }else{
                        //check direction as piece is not a king
                        // also check if make it a king
                        System.out.println("NOT A KING PIECE MOVE");
                        if(colorOfMover == Color.BLACK && !( (fromRow - toRow == -1) && (fromCol - toCol == 1 || fromCol - toCol == -1 ))){
                            return false;
                        }

                        if(colorOfMover == Color.WHITE && !( (fromRow - toRow == 1) && (fromCol - toCol == 1 || fromCol - toCol == -1 ))){
                            return false;
                        }
                    }
                    cells[toRow][toCol].setPiece(colorOfMover,cells[fromRow][fromCol].getPieceKingStatus());
                    cells[fromRow][fromCol].reset();
                    
                    //make king?
                    if((toRow == 7 && colorOfMover == Color.BLACK) || (toRow == 0 && colorOfMover == Color.WHITE)){
                        cells[toRow][toCol].makePieceKing();
                    }

                    movesDatabaseConnection.addValidMove(instruction);
                    return true;
                } 
                return false;
            }

        }else if(arguments[0].equals("TAKE")){
            System.out.println("TAKE recognised");

            int midRow = (int) ((fromRow + toRow) / 2);
            int midCol = (int) ((fromCol + toCol) / 2);
            Color pieceColor = cells[fromRow][fromCol].getPieceColor();


            if(pieceColor != null && cells[fromRow][fromCol].hasPiece() && pieceColor == colorOfMover){
                //check to position
                if(! cells[toRow][toCol].hasPiece()){ // need to check validity of move (localisation not a take)
                    //check if move is ok
                    if(cells[fromRow][fromCol].getPieceKingStatus()){
                        //check if not a valid king move.
                        if(! (Math.abs(fromRow - toRow) == 2) ||! (Math.abs(fromCol - toCol) == 2)){
                            return false;
                        }
                    }else{
                        //check direction
                        // also check if make it a king
                        if(colorOfMover == Color.BLACK && !( (fromRow - toRow == -2) && (fromCol - toCol == 2 || fromCol - toCol == -2 ))){
                            return false;
                        }

                        if(colorOfMover == Color.WHITE && !( (fromRow - toRow == 2) && (fromCol - toCol == 2 || fromCol - toCol == -2 ))){
                            return false;
                        }
                    }

                    //make sure a piece exists of the opposite colour in the middle
                    if( !(cells[midRow][midCol].hasPiece()) || cells[midRow][midCol].getPieceColor() == colorOfMover ){
                        return false;
                    }

                    cells[toRow][toCol].setPiece(colorOfMover,cells[fromRow][fromCol].getPieceKingStatus());
                    cells[midRow][midCol].reset(); // take the piece in the middle
                    cells[fromRow][fromCol].reset();
                    //make king
                    if((toRow == 7 && colorOfMover == Color.BLACK) || (toRow == 0 && colorOfMover == Color.WHITE)){
                        cells[toRow][toCol].makePieceKing();
                    }
                    
                    movesDatabaseConnection.addValidMove(instruction);
                    return true;
                } 
                return false;
            }
            
            return false;

        }else{
            //not a MOVE or TAKE command
            return false;
        }

        //in theory, should never get here, but just in case.
        return false;
    }

    public int getRow(String s){
        if(s.length() == 2){
            int row = -1; 
            if(Character.isDigit(s.charAt(1))){
                row = s.charAt(1) - '0';
                row = 8 - row; // as the indexes are inverted from top left.
                if(row > 7 || row < 0){
                    row = -1;
                }
            }
            return row;
        }else{
            return -1;
        }
    }

    public int getCol(String s){
        if(s.length() == 2){
            int col = -1; 
            if(Character.isAlphabetic(s.charAt(0))){
                col = s.charAt(0) - 'A';
                if(col > 7 || col < 0){
                    col = -1;
                }
            }
            return col;
        }else{
            return -1;
        }
    }


    public void paint(Graphics g) {
        doToEachCell((Cell c) -> c.paint(g));
    }

    public void reset(){
        // goes to a blank board.
        // Load a game from DB as well?
        
        gameFinished = false;
        doToEachCell((Cell c) -> c.reset());
        
    }

    /**
     * Takes a cell consumer (i.e. a function that has a single `Cell` argument and
     * returns `void`) and applies that consumer to each cell in the grid.
     *
     * @param func The `Cell` to `void` function to apply at each spot.
     */
    public void doToEachCell(Consumer<Cell> func) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                func.accept(cells[i][j]);
            }
        }
    }

	@Override
	public Iterator<Cell> iterator() {
		return new CellIterator(cells);
	}

    void keyPressedBackspace(){

    }

    void keyPressedEscape(){
        System.exit(0);
    }

    void keyPressedEnter(){

    }

    void keyPressedLetter(char letter){
        if(!gameFinished){
            System.out.println("grid keypress received letter: " + letter);
        }
        if(letter == 'P' || letter == 'p'){
            printGameMoves();
        }
    }

    void printGameMoves(){
        System.out.println("Requested to print moves");
        String sql = "SELECT moveNumber, instruction from moves ORDER BY moveNumber";
        movesDatabaseConnection.printMoves(sql);
    }

}
