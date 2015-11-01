package main;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;


public class BilgeRobot
{
	private Robot robot;
	private Coordinate topLeft,bottomRight;
	
	private int tileWidth;
	private int tileHeight;
	
	public boolean run=true;
	
	public BilgeRobot(Robot robot,Coordinate topLeft,Coordinate bottomRight)
	{
		this.robot=robot;
		this.topLeft=topLeft;
		this.bottomRight=bottomRight;
	}
	
	public void stop()
	{
		run=false;
	}
	
	public BilgeBoard getBilgeBoard()
	{
		int[][]board=new int[6][12]; //TODO kan byttes med byte?
		
		BufferedImage subScreen;
		BufferedImage screen=robot.createScreenCapture(new Rectangle(0,0,1366,768));
		/*BufferedImage screen=null;
		try
		{
			screen=ImageIO.read(this.getClass().getResource("/images/screen.png"));
		}catch(IOException e2)
		{
			e2.printStackTrace();
		}*/
		subScreen=Main.subImage(screen,new Rectangle(topLeft.x,topLeft.y,bottomRight.x,bottomRight.y));
		
		/*try
		{
			ImageIO.write(subScreen,"PNG",new File("C:\\Users\\Ulrich\\Pictures\\test.png"));
		}catch(IOException e1)
		{
			e1.printStackTrace();
		}*/
			
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
	
	public void run()
	{
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
			
			Coordinate toClick=topSearchBoard(bilgeBoard,3);
			if(toClick==null)
				continue;
			
			System.out.println(toClick.x+"  "+toClick.y);
			int x=((toClick.x*tileWidth)+(tileWidth))+topLeft.x;
			int y=((toClick.y*tileHeight)+(tileHeight/2))+topLeft.y;
			
			System.out.println(x+"  "+y);
			
			robot.mouseMove(x,y);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}
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
}
