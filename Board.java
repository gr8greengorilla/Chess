import java.util.*;
public class Board {
    public final static int BOARD_SIZE = 8;
    
    private Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE];
    private Scanner input = new Scanner(System.in);
    public Board() {
    }

    public void setup() {
        //fill the board up with pieces
        for (int i=0; i < 2; i ++) {
            setPiece(i*7, 0, new Rook(i));
            setPiece(i*7, 1, new Knight(i));
            setPiece(i*7, 2, new Bishop(i));
            setPiece(i*7, 4, new King(i));
            setPiece(i*7, 3, new Queen(i));
            setPiece(i*7, 5, new Bishop(i));
            setPiece(i*7, 6, new Knight(i));
            setPiece(i*7, 7, new Rook(i));
        }
        
        for (int i=0; i < BOARD_SIZE; i ++) {
            setPiece(1,i,new Pawn(0));
        }
        for (int i=0; i < BOARD_SIZE; i ++) {
            setPiece(6,i,new Pawn(1));
        }
    }

    public boolean hasLegalMoves(int turn) {
        for (int a = 0; a < BOARD_SIZE; a++) {
            for (int b = 0; b < BOARD_SIZE; b++) {
                if (hasPiece(a, b) && getPos(a,b).getColor() == turn) {
                    for (int x=0; x < BOARD_SIZE; x++) {
                        for (int y=0; y < BOARD_SIZE; y++) {
                            if (legalMove(a, b, x, y, turn)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // will return boolean if the square is attacked by a piece of the same color as inputted
    public boolean isAttacked(int x, int y, int color) {
        //get next piece above the cords
        int searchx;
        int searchy;
        Piece piece = null;
        for (int i = 0; i < 8; i++) {
            searchx = x;
            searchy = y;
            piece = null;
            if (i==0) {
                searchx = x + 1; // will check above
            } else if (i == 1) {
                searchx = x - 1; //checks below
            }  else if (i == 2) {
                searchy = y + 1; //checks right
            } else if (i==3) {
                searchy = y - 1; //checks left
            } else if (i==4) {
                searchy = y + 1; //checks up right diag
                searchx = x + 1;
            } else if (i==5) {
                searchy = y + 1; //checks down right diag
                searchx = x - 1;
            } else if (i==6) {
                searchy = y - 1; //checks up left diag
                searchx = x + 1;
            } else if (i==7) {
                searchy = y - 1; //checks down left diag
                searchx = x - 1;
            }
            

            //gets a piece in each direction
            while (searchx < BOARD_SIZE && searchx >= 0 && searchy >= 0 && searchy < BOARD_SIZE) {

                if (searchx == 6 && searchy == 6) {
 
                }
                if (hasPiece(searchx, searchy)) {
                    piece = getPos(searchx, searchy);
                    break;
                }

                //progress all the checks in the direction they need to go
                if (searchx - x > 0) {
                    searchx++;
                } else if (searchx - x < 0) {
                    searchx--;
                }
                if (searchy - y > 0) {
                    searchy++;
                } else if (searchy - y < 0) {
                    searchy--;
                }
            }

            //checks if a unblocked correctly colored rook or queen is on the horizontal
            if (piece != null && piece.getColor() == color) {
                if (i <= 3 && (piece.getType().equals("Rook") || piece.getType().equals("Queen"))) {
                    return true;
                } else if (i > 3 && (piece.getType().equals("Bishop") || piece.getType().equals("Queen"))) { //this checks if there is a correctly colored bishop or queen on the diag
                    return true;
                }
            }
        }

        //now we have to check if there is a pawn, knight, or king covering those squares.

        //check if there is a knight at an attacking square
        searchx = -1;
        searchy = -1;
        int b = 1;
        for (int a = 2; a >= 1; a--) {
            for (int i = 0; i < 4; i++) {
                searchx = a;
                searchy = b;
                if (i==1) {
                    searchx = -a;
                } else if (i == 2) {
                    searchy = -b;
                } else if (i==3) {
                    searchx = -a;
                    searchy = -b;
                }
                if (hasPiece(searchx + x, searchy + y) && getPos(searchx + x, searchy + y).getType().equals("Knight") && getPos(searchx + x, searchy + y).getColor() == color) {
                    return true;
                }
            }
            b++;
        }

        //check if there is a pawn in an attacking square. Have to take into account that different oclored pawns attack in different directions
        if (color == 1) {
            if (hasPiece(x-1, y+1) && getPos(x-1, y+1).getType().equals("Pawn") && getPos(x-1, y+1).getColor() == 1) {
                return true;
            } else if (hasPiece(x-1, y-1) && getPos(x-1, y-1).getType().equals("Pawn") && getPos(x-1, y-1).getColor() == 1) {
                return true;
            }
        } else if (color == 0) {
            if (hasPiece(x+1, y+1) && getPos(x+1, y+1).getType().equals("Pawn") && getPos(x+1, y+1).getColor() == 0) {
                return true;
            } else if (hasPiece(x+1, y-1) && getPos(x+1, y-1).getType().equals("Pawn") && getPos(x+1, y-1).getColor() == 0) {
                return true;
            }
        }

        //finally, check if a king is attacking the square
        for (searchx = -1; searchx <=1; searchx++) {
            for (searchy = -1; searchy <= 1; searchy++) {
                //make sure the king isnt on the square
                if (searchx != x && searchy != y) {
                    if (hasPiece(searchx, searchy) && getPos(searchx, searchy).getType().equals("King") && getPos(searchx, searchy).getColor() == color){
                        return true;
                    } 
                }
            }
        }
        //if none of those return true triggers, then the square is not attacked.
        return false;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void delPiece(int x, int y) {
        board[x][y] = null;
    }

    public void setPiece(int x, int y, Piece p) {
        board[x][y] = p;
        p.setX(x);
        p.setY(y);
    }

    public Piece getPos(int x, int y) {
        return board[x][y];
    }

    // will return true if the square the king of color is on is attacked by the opposite color
    public boolean isChecked(int color) {
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (hasPiece(x, y) && getPos(x, y).getType().equals("King") && getPos(x, y).getColor() == color) {
                    return isAttacked(x, y, 1 - color);
                }
            }
        }
        return false;
    }

    public void printAttackedSquares(int color) {
        for (int x = 0; x < BOARD_SIZE;x++) {
            
            for (int y = 0; y < BOARD_SIZE; y++) {
                //System.out.print(((isAttacked( 8-x, y, color)) ? "" : ""));
                System.out.print(((isAttacked(x, y, color)) ? "X " : "- "));
                //System.out.print(((hasPiece(x, y) && getPos(x, y).enPassentPossible()) ? "X " : "- "));
            }
            System.out.println();
        }
    }

    public boolean hasPiece(int x, int y) {
        if (x < BOARD_SIZE && x >= 0 && y < BOARD_SIZE && y >=0 ) {
            return board[x][y] != null;
        }
        return false;
    }

    public boolean castleQueenside(int color) {
        for (int i = 1; i < 4; i++) {
            if (hasPiece(color*7, i) || isAttacked(color*7, i, 1-color)) {
                return false;
            }
        }
        if (hasPiece(color*7, 0) && getPos(color*7, 0).getType().equals("Rook") && !getPos(color*7, 0).hasMoved() ||
                hasPiece(color*7, 4) && getPos(color*7, 4).getType().equals("King") && !getPos(color*7, 4).hasMoved()) {
                    movePiece(color*7, 4, color*7, 2, false);
                    movePiece(color*7, 0, color*7, 3, false);
                    return true;
        }
        return false;
    }

    public boolean castleKingside(int color) {
        for (int i = 5; i < 7; i++) {
            if (hasPiece(color*7, i) || isAttacked(color*7, i, 1-color)) {
                return false;
            }
        }
        if (hasPiece(color*7, 7) && getPos(color*7, 7).getType().equals("Rook") && !getPos(color*7, 7).hasMoved() ||
                hasPiece(color*7, 4) && getPos(color*7, 4).getType().equals("King") && !getPos(color*7, 4).hasMoved()) {
                    movePiece(color*7, 4, color*7, 6, false);
                    movePiece(color*7, 7, color*7, 5, false);
                    return true;
        }
        return false;
    }

    public void movePiece(int x, int y, int targetx, int targety, boolean verbose) {

        if (hasPiece(x,y) && getPos(x,y).getType().equals("Pawn") && (x - targetx == 1 || x - targetx == -1)) { //if the pawn did en passent, remove the other pawn
            if (hasPiece(x,targety) && getPos(x, targety).enPassentPossible() && targety - y == 1) {
                delPiece(x, targety);
            } else if (hasPiece(x,y-1) && getPos(x, y-1).enPassentPossible() && targety - y == -1) {
                delPiece(x, targety);
            }
        }

        for (int a = 0; a < BOARD_SIZE; a++) { //after moving, disable en passant for all opposite colored pawns
            for (int b = 0; b < BOARD_SIZE; b++) {
                if (hasPiece(a, b) && getPos(a,b).getType().equals("Pawn") && getPos(a,b).getColor() == 1 - getPos(x, y).getColor()) {
                   getPos(a, b).setPassent(false);
                }
            }
        }
        
        if (getPos(x,y).getType().equals("Pawn") && (x - targetx == 2 || x - targetx == -2) && targety == y) { //enable en passent if a pawn moves two squares
            getPos(x, y).setPassent(true);
        }

        if (verbose && getPos(x,y).getType().equals("Pawn") && ((getPos(x,y).getColor() == 0 && targetx == 7) || (getPos(x,y).getColor() == 1 && targetx == 0))) {
            System.out.println("Promote to Q,R,B, or N?");
            String a = input.nextLine().toUpperCase();
            if (a.equals("Q")) {
                setPiece(x, y, new Queen(getPos(x,y).getColor()));
            } else if (a.equals("R")) {
                setPiece(x, y, new Rook(getPos(x,y).getColor()));
            } else if (a.equals("B")) {
                setPiece(x, y, new Bishop(getPos(x,y).getColor()));
            } else if (a.equals("N")) {
                setPiece(x, y, new Knight(getPos(x,y).getColor()));
            }
        }

        

        setPiece(targetx, targety, getPos(x, y));
        getPos(x, y).setMoved(true);
        delPiece(x,y);

        
    }



    public void setEqual(Piece[][] x, Piece[][] y) {
        for (int i =0; i < BOARD_SIZE;i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (y[i][j] != null) {
                    x[i][j] = y[i][j].makeCopy();
                } else {
                    x[i][j] = null;
                }
                
            }
        }
    }



    public boolean legalMove(int x, int y, int targetx, int targety, int turn) {
        Piece[][] testboard=  new Piece[BOARD_SIZE][BOARD_SIZE];
        setEqual(testboard, board);
        //if the piece can move, move it...
        if (canMove(x, y, targetx, targety, turn)) {
            movePiece(x, y, targetx, targety, false);
        } else {
            return false;
        }
        //but if that move left your king in check, then return false
        if (isChecked(turn)) {
            setEqual(board, testboard);
            //movePiece(targetx,targety, x, y); //move the piece back before returning.
            return false;
        }
        setEqual(board, testboard);
        //movePiece(targetx,targety, x, y);
        return true;
    }

    //splits all the move checks into smaller comprehensible parts
    public boolean canMove(int x, int y, int targetx, int targety, int turn) {
        //Maks sure there is actually a piece there to move, and make sure its the player's piece and not their opponents
        if (!hasPiece(x, y) || getPos(x, y).getColor() != turn) {
            return false;
        }
        return getPos(x,y).canMove(this, targetx, targety);
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }

    //Prints out the board
    public String toString() {
        String output = "";

        

        //prints the pieces on board using their toString methods
        for (int i = 0; i < BOARD_SIZE; i++) {

            output += (8-i) + " ";

            for (int j = 0; j < BOARD_SIZE; j++) {
                //if there is no piece, add " - "
                if (hasPiece(i,j)) {
                    output += (getPos(i,j).getColor() == 1) ?  " " + getPos(i,j) + " " : " " + getPos(i,j) + "*";
                } else {
                    output += " - ";
                }
            }
            output += "\n";
        }

        //prints the numbers across top
        output += "  ";
        for (int i = 0; i < BOARD_SIZE; i++) {
            output += " " + "abcdefghijklmnopqrstuvwxyz".charAt(i) + " "; //add letters on side
        }
        output += "\n";

        return output;
    }
}
