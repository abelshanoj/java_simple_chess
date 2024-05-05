package pieces;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece{

	public Bishop(int col, int row, int color) {
		super(col, row, color);
		
		type=Type.BISHOP;
		
		if(color==GamePanel.WHITE) {
			image=getImage("/piece/w-bishop.png");
		}else {
			image=getImage("/piece/b-bishop.png");
		}
	}
	
	public boolean canMove(int targetCol,int targetRow) {
		if(isWithinBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow)==false && isPieceOnDiagonalLine(targetCol,targetRow)==false) {
			if(Math.abs(targetRow-preRow)==Math.abs(targetCol-preCol)) {
				if(isValidSquare(targetCol,targetRow)) {
					return true;
				}
			}
		}
		return false;
	}

}
