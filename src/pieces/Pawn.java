package pieces;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece{

	public Pawn(int col, int row, int color) {
		super(col, row, color);	
		type=Type.PAWN;
		if(color==GamePanel.WHITE) {
			image=getImage("/piece/w-pawn.png");
		}else {
			image=getImage("/piece/b-pawn.png");
		}
	}
	
	public boolean canMove(int targetCol,int targetRow) {
		int moveValue;
		if(color==GamePanel.WHITE) {
			moveValue=-1;
		}else {
			moveValue=1;
		}
		
		hittingP = isHittingP(targetCol,targetRow);
		
		if(targetRow==preRow+moveValue && targetCol==preCol && hittingP==null) {
			return true;
		}
		
		if(targetRow==preRow+moveValue*2 && targetCol==preCol && hittingP==null
				&& isPieceOnStraightLine(targetCol,targetRow)==false && moved==false) {
			return true;
		}
		
		//capture the pieces
		if(Math.abs(targetCol-preCol)==1 && targetRow==preRow+moveValue && hittingP!=null && hittingP.color!=color) {
			return true;
		}
		
		//En Passant
		if(Math.abs(targetCol-preCol)==1 && targetRow==preRow+moveValue) {
			for(Piece p:GamePanel.simPieces) {
				if(p.row==preRow && p.col==targetCol && p.twoStepped) {
					hittingP=p;
					return true;
				}
			}
		}
		
		return false;
	}
}
	

