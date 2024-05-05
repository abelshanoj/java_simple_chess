package pieces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;
import main.Type;

public class Piece {
	public Type type;
	public BufferedImage image;
	public int x,y;
	public int col,row;
	public int preCol,preRow;
	public int color;
	public Piece hittingP;
	public boolean moved,twoStepped;
	
	public Piece(int col,int row,int color) {
		this.row=row;
		this.col=col;
		this.color=color;
		this.x=getX(col);
		this.y=getY(row);
		preCol=col;
		preRow=row;
	}
	
	public BufferedImage getImage(String imagePath) {
		BufferedImage image=null;
		try {
			image=ImageIO.read(getClass().getResourceAsStream(imagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;	
	}
	
	public int getRow(int y) {
		return (y+Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
	}
	
	public int getCol(int x) {
		return (x+Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
	}
	
	
	public int getX(int col) {
		return col*Board.SQUARE_SIZE;
	}
	
	public int getY(int row) {
		return row*Board.SQUARE_SIZE;
	}
	
	public boolean canMove(int targetCol,int targetRow) {
		return false;
	}
	
	public boolean isWithinBoard(int targetCol,int targetRow) {
		if(targetRow>=0 && targetRow<8 && targetCol>=0 && targetCol<8) {
			return true;
		}
		return false;
	}
	
	public boolean isSameSquare(int targetCol, int targetRow) {
		if(targetCol==preCol && targetRow==preRow) {
			return true;
		}
		return false;
	}
	
	
	//check method for verifying if there is some piece along the path
	public boolean isPieceOnStraightLine(int targetCol,int targetRow) {
		if(targetCol<preCol) {
			for(int c=preCol-1;c>targetCol;c--) {
				for(Piece p:GamePanel.simPieces) {
					if(p.col==c && p.row==targetRow) {
						hittingP=p;
						return true;
					}
				}
			}	
		}else {
			for(int c=preCol+1;c<targetCol;c++) {
				for(Piece p:GamePanel.simPieces) {
					if(p.col==c && p.row==targetRow) {
						return true;
					}
				}
			}
		}
		
		if(targetRow>preRow) {
			for(int r=preRow+1;r<targetRow;r++) {
				for(Piece p:GamePanel.simPieces) {
					if(p.row==r && p.col==targetCol) {
						hittingP=p;
						return true;
					}
				}
			}
		}else {
			for(int r=preRow-1;r>targetRow;r--) {
				for(Piece p:GamePanel.simPieces) {
					if(p.row==r && p.col==targetCol) {
						hittingP=p;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean isPieceOnDiagonalLine(int targetCol,int targetRow) {
		if(targetRow<preRow) {
			for(int c=preCol-1;c>targetCol;c--) {
				int diff=Math.abs(c-preCol);
				for(Piece p:GamePanel.simPieces) {
					if(p.col==c && p.row==preRow-diff) {
						hittingP=p;
						return true;
					}
				}
			}
			for(int c=preCol+1;c<targetCol;c++) {
				int diff=Math.abs(c-preCol);
				for(Piece p:GamePanel.simPieces) {
					if(p.col==c && p.row==preRow-diff) {
						hittingP=p;
						return true;
					}
				}
			}
		}
		if(targetRow>preRow){
			for(int c=preCol+1;c<targetCol;c++) {
				int diff=Math.abs(c-preCol);
				for(Piece p:GamePanel.simPieces) {
					if(p.col==c && p.row==preRow+diff) {
						hittingP=p;
						return true;
					}
				}
			}
			for(int c=preCol-1;c>targetCol;c--) {
				int diff=Math.abs(c-preCol);
				for(Piece p:GamePanel.simPieces) {
					if(p.col==c && p.row==preRow+diff) {
						hittingP=p;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void updatePosition() {
		//Check for En Passant
		if(type==Type.PAWN) {
			if(Math.abs(row-preRow)==2) {
				twoStepped=true;
			}
		}
		x=getX(col);
		y=getY(row);
		preCol=getCol(x);
		preRow=getRow(y);
		moved=true;
		//System.out.println("hello from king");
	}
	
	public void resetPosition() {
		col=preCol;
		row=preRow;
		x=getX(col);
		y=getY(row);
	}
	
	public Piece isHittingP(int targetCol,int targetRow) {
		for(Piece p:GamePanel.simPieces) {
			if(p.col==targetCol && p.row==targetRow && p!=this) {
				return p;
			}
		}
		return null;
	}
	
	public boolean isValidSquare(int targetCol,int targetRow) {
		hittingP=isHittingP(targetCol,targetRow);
		if(hittingP==null) {
			return true;
		}else {
			if(hittingP.color!=this.color) {
				return true;
			}else {
				hittingP=null;
			}
		}
		
		return false;
		
	}
	
	
	public void draw(Graphics2D g2) {
		g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
	}

	public int getIndex() {
		for(int i=0;i<GamePanel.simPieces.size();i++) {
			if(GamePanel.simPieces.get(i)==this) {
				return i;
			}
		}
		return 0;
	}
}
