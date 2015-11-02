package main;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


public class BilgeRobot implements PPRobot
{
	private Robot robot;
	private Coordinate topLeft,bottomRight;
	
	private int tileWidth;
	private int tileHeight;
	
	public boolean run=true;
	
	private BufferedImage bi_topLeft;
	private BufferedImage bi_bottomRight;
	
	public BilgeRobot(Robot robot)
	{
		this.robot=robot;
	}
	
	public void stop()
	{
		run=false;
	}
	
	private boolean getBounds()
	{
		BufferedImage screen = robot.createScreenCapture(new Rectangle(0,0,1366,768));
		
		topLeft=Main.searchFor(screen,bi_topLeft);
		bottomRight=Main.searchFor(screen,bi_bottomRight);
				
		if(topLeft == null || bottomRight == null)
			return false;
		
		bottomRight.x=bottomRight.x-topLeft.x-bi_bottomRight.getWidth();//FIXME Why subtract width of image?
		bottomRight.y=bottomRight.y-topLeft.y-bi_bottomRight.getHeight();
		topLeft.x=topLeft.x+bi_topLeft.getWidth();
		topLeft.y=topLeft.y+bi_topLeft.getHeight();
		
		return true;
	}
	
	public BilgeBoard getBilgeBoard()
	{
		int[][]board=new int[6][12]; //TODO kan byttes med byte?
		
		BufferedImage subScreen;
		BufferedImage screen=robot.createScreenCapture(new Rectangle(0,0,1366,768));
		
		subScreen=Main.subImage(screen,new Rectangle(topLeft.x,topLeft.y,bottomRight.x,bottomRight.y));
			
		tileWidth=subScreen.getWidth()/board.length;
		tileHeight=subScreen.getHeight()/board[0].length;

		for(int i=0;i<board.length;i++)
		{
			for(int u=0;u<board[i].length;u++)
			{
				//System.out.println(((i*tileWidth)+(tileWidth/2)+1)+"  "+((u*tileHeight)+(tileHeight/2))+"  halla");
				int dot=subScreen.getRGB((i*tileWidth)+(tileWidth/2),(u*tileHeight)+(tileHeight/2));
				//System.out.println(i+"  "+u+"  "+dot);
				board[i][u]=BilgeBoard.getType(dot);
			}
		}
		
		return new BilgeBoard(board);
	}
	
	private int searchBoard(BilgeBoard board,int depth,int turn)
	{
		if(depth==0)
			return 0;
		
		int maxScore=0;
		
		for(int i=0;i<board.getWidth()-1;i++)
		{
			for(int u=0;u<board.getHeight();u++)
			{
				BilgeBoard tempBoard=new BilgeBoard(board);
				tempBoard.swap(i,u);
				int score=tempBoard.evalBoard();
				score+=searchBoard(tempBoard,depth-1,turn+1);
				score/=turn;
				if(score>maxScore)
					maxScore=score;
			}
		}
		return maxScore;
	}
	
	public Coordinate topSearchBoard(BilgeBoard board,int depth)
	{
		if(depth==0)
			return new Coordinate(0,0);
		
		Coordinate toReturn=null;
		
		int maxScore=0;
		
		for(int i=0;i<board.getWidth()-1;i++)
		{
			for(int u=0;u<board.getHeight();u++)
			{
				BilgeBoard tempBoard=new BilgeBoard(board);
				tempBoard.swap(i,u);
				int score=tempBoard.evalBoard();
				score+=searchBoard(tempBoard,depth-1,2);
				if(score>maxScore)
				{
					maxScore=score;
					toReturn=new Coordinate(i,u);
					//System.out.println("Here: "+i+"  "+u);
				}
			}
		}
		return toReturn;
	}

	@Override
	public void init()
	{
		try
		{
			bi_topLeft=ImageIO.read(this.getClass().getResource("/images/b_topleft.png"));
			bi_bottomRight=ImageIO.read(this.getClass().getResource("/images/b_bottomright.png"));
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public GameResult playGame()
	{
		run=true;
		getBounds();
		while(run)
		{
			try
			{
				Thread.sleep(200);
			}catch(InterruptedException e1)
			{
				e1.printStackTrace();
			}
			
			
			BilgeBoard bilgeBoard=null;
			BilgeBoard tempBoard=null;
			long lastTime=0;
			do
			{
				long startTime=System.currentTimeMillis();
				bilgeBoard=getBilgeBoard();
				try
				{
					long timeToSleep=200-(System.currentTimeMillis()-startTime)-lastTime;
					if(timeToSleep>0)
						Thread.sleep(timeToSleep);
				}catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				startTime=System.currentTimeMillis();
				tempBoard=getBilgeBoard();
				lastTime=System.currentTimeMillis()-startTime;
			}while(! bilgeBoard.isLike(tempBoard));
			
			final Coordinate toClick=topSearchBoard(bilgeBoard,3);
			if(toClick==null)
			{
				//TODO find reason here. Duty report? Mission done?
				continue;
			}
			
			Runnable r = new Runnable(){
				int x=((toClick.x*tileWidth)+(tileWidth))+topLeft.x+(int)(Math.random()*40-20);
				int y=((toClick.y*tileHeight)+(tileHeight/2))+topLeft.y+(int)(Math.random()*20-10);
				public void run(){
					robot.delay(150);
					x=3*tileWidth+topLeft.x+(int)(Math.random()*100-50);
					y=6*tileHeight+topLeft.y+(int)(Math.random()*200-100);
					robot.mouseMove(x,y);
				}
				};
			
			int x=((toClick.x*tileWidth)+(tileWidth))+topLeft.x+(int)(Math.random()*40-20);
			int y=((toClick.y*tileHeight)+(tileHeight/2))+topLeft.y+(int)(Math.random()*20-10);		
			System.out.println(x+"  "+y);
			robot.mouseMove(x,y);
			robot.delay(100+(int)Math.random()*20-10);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.delay((int) (5 + (int)(Math.random())));
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.delay(150+(int)Math.random()*20-10);
			Thread th = new Thread(r);
			th.start();
		}
		return GameResult.ABORTED;
	}

	@Override
	public void abortGame()
	{
		run=false;
	}

	@Override
	public void setScreen(Coordinate topLeft,Coordinate bottomRight)
	{
		this.topLeft=topLeft;
		this.bottomRight=bottomRight;
	}
}
