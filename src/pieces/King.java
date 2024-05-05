package pieces;

import main.GamePanel;
import main.Type;

public class King extends Piece{

	public King(int col, int row, int color) {
		super(col, row, color);
		
		type=Type.KING;
		
		if(color==GamePanel.WHITE) {
			image=getImage("/piece/w-king.png");
		}else {
			image=getImage("/piece/b-king.png");
		}
		
	}

	@Override
	public boolean canMove(int targetCol,int targetRow) {
		//Movement
		if(isWithinBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow)==false) {
			if(Math.abs(targetCol-preCol)+Math.abs(targetRow-preRow)==1 ||
					Math.abs(targetCol-preCol)*Math.abs(targetRow-preRow)==1) {
				if(isValidSquare(targetCol,targetRow)) {					
					return true;
				}
			}
		}
		
		//Castling
		if(moved==false) {
			//right castling
			if(targetCol==preCol+2 && targetRow==preRow && isPieceOnStraightLine(targetCol,targetRow)==false
					&& isHittingP(targetCol,targetRow)==null) {
				for(Piece p:GamePanel.simPieces) {
					if(p.moved==false && p.row==preRow && p.col==preCol+3) {
						GamePanel.castlingP=p;
						//System.out.println("hello");
						return true;
					}
				}
			}
			
			//left castling
			if(targetCol==preCol-2 && targetRow==preRow && isPieceOnStraightLine(targetCol,targetRow)==false
					&& isHittingP(targetCol,targetRow)==null) {
				for(Piece p:GamePanel.simPieces) {
					Piece[] pe=new Piece[2];
					if(p.col==preCol-3 && p.row==preRow) {
						pe[0]=p;
					}
					if(p.col==preCol-4 && p.row==preRow) {
						pe[1]=p;
					}
					
					if(pe[0]==null && pe[1]!=null && pe[1].moved==false) {
						GamePanel.castlingP=pe[1];
						return true;
					}
				}
				
			}
		}
		return false;
	}

}
