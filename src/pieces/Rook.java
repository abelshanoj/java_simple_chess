package pieces;

import main.GamePanel;
import main.Type;

public class Rook extends Piece{

	public Rook(int col, int row, int color) {
		super(col, row, color);
		
		type=Type.ROOK;
		
		if(color==GamePanel.WHITE) {
			image=getImage("/piece/w-rook.png");
		}else {
			image=getImage("/piece/b-rook.png");
		}
	}
	
	public boolean canMove(int targetCol,int targetRow) {
		if(isWithinBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow)==false && isPieceOnStraightLine(targetCol,targetRow)==false) {
			if(targetRow==preRow || targetCol==preCol) {
				if(isValidSquare(targetCol,targetRow)) {
					return true;
				}
			}
		}
		return false;
	}

	

}
