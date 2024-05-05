package pieces;

import main.GamePanel;
import main.Type;

public class Queen extends Piece{

	public Queen(int col, int row, int color) {
		super(col, row, color);
		
		type= Type.QUEEN;
		
		if(color==GamePanel.WHITE) {
			image=getImage("/piece/w-queen.png");
		}else {
			image=getImage("/piece/b-queen.png");
		}
	}
	
	public boolean canMove(int targetCol,int targetRow) {
		if(isWithinBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow)==false) {
			if(Math.abs(targetRow-preRow)==Math.abs(targetCol-preCol) && isPieceOnDiagonalLine(targetCol,targetRow)==false) {
				if(isValidSquare(targetCol,targetRow)) {
					return true;
				}
			}
			else if((targetRow==preRow || targetCol==preCol) && isPieceOnStraightLine(targetCol,targetRow)==false) {
				if(isValidSquare(targetCol,targetRow)) {
					return true;
				}
			}
		}
		return false;
	}

}
