package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

public class GamePanel extends JPanel implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WIDTH=1100;
	public static final int HEIGHT=800;
	final int FPS=60;
	Thread gameThread;
	Board board=new Board();
	public static final int WHITE=0;
	public static final int BLACK=1;
	public int currentColor=WHITE;
	public static ArrayList<Piece> simPieces=new ArrayList<>();
	public ArrayList<Piece> pieces=new ArrayList<>();
	ArrayList<Piece> promoPieces=new ArrayList<>();
	Mouse mouse=new Mouse();
	Piece activeP=null;
	public static Piece castlingP;
	public Piece checkingP;
	public boolean canMove,validSquare,promotion,gameOver;
	
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		this.setBackground(Color.black);
		
		//Add all the pieces to the ArrayList
		setPieces();
		//testPromotion();
		copyPieces(pieces,simPieces);
		
		//Add mouse motion listener and mouse action listener
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
	}
	
	public void launchGame() {
		gameThread=new Thread(this);
		gameThread.start();
	}
	
	/*public void testPromotion() {
		pieces.add(new King(3,4,0));
	}*/
	
	public void setPieces() {
		pieces.add(new Pawn(0,1,1));
		pieces.add(new Pawn(1,1,1));
		pieces.add(new Pawn(2,1,1));
		pieces.add(new Pawn(3,1,1));
		pieces.add(new Pawn(4,1,1));
		pieces.add(new Pawn(5,1,1));
		pieces.add(new Pawn(6,1,1));
		pieces.add(new Pawn(7,1,1));
		pieces.add(new Rook(0,0,1));
		pieces.add(new Knight(1,0,1));
		pieces.add(new Bishop(2,0,1));
		pieces.add(new King(4,0,1));
		pieces.add(new Queen(3,0,1));
		pieces.add(new Bishop(5,0,1));
		pieces.add(new Knight(6,0,1));
		pieces.add(new Rook(7,0,1));
		
		//white pieces
		pieces.add(new Pawn(0,6,0));
		pieces.add(new Pawn(1,6,0));
		pieces.add(new Pawn(2,6,0));
		pieces.add(new Pawn(3,6,0));
		pieces.add(new Pawn(4,6,0));
		pieces.add(new Pawn(5,6,0));
		pieces.add(new Pawn(6,6,0));
		pieces.add(new Pawn(7,6,0));
		pieces.add(new Rook(0,7,0));
		pieces.add(new Knight(1,7,0));
		pieces.add(new Bishop(2,7,0));
		pieces.add(new King(4,7,0));
		pieces.add(new Queen(3,7,0));
		pieces.add(new Bishop(5,7,0));
		pieces.add(new Knight(6,7,0));
		pieces.add(new Rook(7,7,0));
	}
	
	public void testPromotion() {
		pieces.add(new Pawn(3,5,0));
		pieces.add(new Pawn(6,5,1));
	}
	
	
	//For adding elements from pieces ---> simPieces
	public void copyPieces(ArrayList<Piece>source,ArrayList<Piece>target) {
		target.clear();
		for(int i=0;i<source.size();i++) {
			target.add(source.get(i));
		}
	}
	
	@Override
	public void run() {
		//Game Loop
		
		double drawInterval=1000000000/FPS;
		double delta=0;
		long lastTime=System.nanoTime();
		long currentTime;
		while(gameThread!=null) {
			currentTime=System.nanoTime();
			delta+=(currentTime-lastTime)/drawInterval;
			lastTime=currentTime;
			if(delta>=1) {
				update();
				repaint();
				delta--;
			}
			
		}
	}
	
	public void update() {
		
		if(promotion) {
			promoting();
		}
		else {
			if(mouse.pressed) {
				//System.out.println("hello");
				if(activeP==null) {
					//System.out.println("hello");
					for(Piece p: simPieces) {
						/*Here we check the whether the piece's columns and rows are same as the mouse's as the x and y coordinates
						of the mouse may not be the same as that of the piece*/
						
						if(p.color==currentColor && p.col==mouse.x/Board.SQUARE_SIZE && p.row==mouse.y/Board.SQUARE_SIZE) {
							activeP=p;
						}
					}
				}else {
					simulate();
				}
			}
			
			//Mouse button released...
			if(mouse.pressed==false) {
				if(activeP!=null) {
					//System.out.println(validSquare);
					if(validSquare) {
						//simPieces ---> pieces
						copyPieces(simPieces,pieces);
						
						activeP.updatePosition();
						
						if(castlingP!=null) {
							castlingP.updatePosition();
						}
						
						if(isKingInCheck()) {
							
						}else {
							if(canPromote()) {
								promotion=true;
							}else {
								changePlayer(); 						
							}												
						}
					}else {
						//pieces ---> simPieces restore using the backup list
						copyPieces(pieces,simPieces);
						activeP.resetPosition();
						activeP=null;
					}
				}
			}
		}
		
	}
	
	public void simulate() {
		validSquare=false;
		canMove=false;
		
		if(castlingP!=null) {
			castlingP.col=castlingP.preCol;
			castlingP.x=castlingP.getX(castlingP.col);
			castlingP=null;
		}
		
		copyPieces(pieces,simPieces);
		
		
		activeP.x=mouse.x-Board.HALF_SQUARE_SIZE;
		activeP.y=mouse.y-Board.HALF_SQUARE_SIZE;
		
		activeP.row=activeP.getRow(activeP.y);
		activeP.col=activeP.getCol(activeP.x);
		
		if(activeP.canMove(activeP.col, activeP.row)) {
			
			canMove=true;
			if(activeP.hittingP!=null) {
				simPieces.remove(activeP.hittingP.getIndex());
			}
			
			checkCastling();
			
			if(isIllegal(activeP)==false && opponentCanCaptureKing()==false) {
				validSquare=true;				
			}
			
			//System.out.println("hello");
		}
	}
	
	
	private boolean opponentCanCaptureKing() {
		Piece king=getKing(false);
		for(Piece p:simPieces) {
			if(king.color!=p.color && p.canMove(king.col, king.row) && p!=king) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isKingInCheck() {
		Piece king =getKing(true);
		
		if(activeP.canMove(king.col, king.row)) {
			checkingP=activeP;
			return true;
		}else {
			checkingP=null;
		}
		return false;
	}
	
	private Piece getKing(boolean opponent) {
		Piece king=null;
		for(Piece p:simPieces) {
			if(opponent) {
				if(p.type==Type.KING && p.color!=currentColor) {
					king=p;
				}
			}else {
				if(p.type==Type.KING && p.color==currentColor) {
					king=p;
				}
			}
		}
		return king;
	}
	
	private void promoting() {
		if(mouse.pressed) {
			for(Piece p:promoPieces) {
				if(p.col==mouse.x/Board.SQUARE_SIZE && p.row==mouse.y/Board.SQUARE_SIZE) {
					switch(p.type) {
					case ROOK:simPieces.add(new Rook(activeP.col,activeP.row,currentColor));break;
					case QUEEN:simPieces.add(new Queen(activeP.col,activeP.row,currentColor));break;
					case KNIGHT:simPieces.add(new Knight(activeP.col,activeP.row,currentColor));break;
					case BISHOP:simPieces.add(new Bishop(activeP.col,activeP.row,currentColor));break;
					default: break;
					}
					simPieces.remove(activeP.getIndex());
					copyPieces(simPieces,pieces);
					activeP=null;
					promotion=false;
					changePlayer();
				}
			}
		}
		
	}


	private boolean isIllegal(Piece king) {
		if(king.type==Type.KING) {
			for(Piece p:simPieces) {
				if(p!=king && p.canMove(king.col,king.row) && p.color!=king.color) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean canPromote() {
		if(activeP.type==Type.PAWN) {
			if(activeP.color==BLACK && activeP.row==7 || activeP.color==WHITE && activeP.row==0) {
				promoPieces.clear();
				promoPieces.add(new Queen(9,2,currentColor));
				promoPieces.add(new Rook(9,3,currentColor));
				promoPieces.add(new Bishop(9,4,currentColor));
				promoPieces.add(new Knight(9,5,currentColor));
				return true;
			}
		}
		return false;
	}
	
	public void changePlayer() {
		if(currentColor==WHITE) {
			currentColor=BLACK;
			//Reset the twoStepped boolean every time the player changes ***
			for(Piece p:GamePanel.simPieces) {
				if(p.color==GamePanel.BLACK) {
					p.twoStepped=false;
				}
			}
			activeP=null;
		}else {
			currentColor=WHITE;
			for(Piece p:GamePanel.simPieces) {
				if(p.color==GamePanel.WHITE) {
					p.twoStepped=false;
				}
			}
			activeP=null;
		}
	}
	
	public void checkCastling() {
		if(castlingP!=null) {
			if(castlingP.col==0) {
				castlingP.col+=3;
			}
			else if(castlingP.col==7) {
				castlingP.col-=2;
			}
			castlingP.x=castlingP.getX(castlingP.col);
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2=(Graphics2D) g;
		
		//Draw the board
		board.draw(g2);
		
		//Draw the Pieces
		for(Piece p:simPieces) {
			p.draw(g2);
		}
		
		//Shows the highlighted rectangle only if it is a valid square
		if(canMove) {
			//Draw a opaque rectangle
			if(activeP!=null) {
				if(isIllegal(activeP) || opponentCanCaptureKing()) {
					g2.setColor(Color.GRAY);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
					activeP.draw(g2);
				}else {
					g2.setColor(Color.WHITE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
					activeP.draw(g2);
				}
				
			}
		}
		
		//Status Message
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Book-Antiqua",Font.PLAIN,40));
		g2.setColor(Color.WHITE);
		
		if(promotion) {
			g2.drawString("Promote to: ", 840, 150);
			for(Piece p:promoPieces) {
				g2.drawImage(p.image, p.getX(p.col), p.getY(p.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			}
		}else {
			if(currentColor==WHITE) {
				g2.drawString("White's Turn", 840, 550);
				if(checkingP!=null && checkingP.color!=WHITE) {
					g2.setColor(Color.red);
					g2.drawString("The King", 840, 650);	
					g2.drawString("is in Check!",840,700);
				}
			}else {
				g2.drawString("Black's Turn", 840, 250);
				if(checkingP!=null && checkingP.color!=BLACK) {
					g2.setColor(Color.red);
					g2.drawString("The King", 840, 650);	
					g2.drawString("is in Check!",840,700);
				}
			}			
		}
		
	}

}
