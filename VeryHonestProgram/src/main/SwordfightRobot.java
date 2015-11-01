package main;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SwordfightRobot implements PPRobot
{
	private boolean run=true;
	private Rectangle boardRect;
	private SwordfightPair currentDrop;
	private SwordfightPair nextDrop;
	private Robot robot;
	
	private BufferedImage bi_topLeft;
	private BufferedImage bi_bottomRight;
	private Coordinate topLeft;
	private Coordinate bottomRight;
	
	public SwordfightRobot(Robot robot)
	{
		this.robot=robot;
	}
	
	//this also changes the angle of the pair to the optimal angle
	private int searchDrop(SwordfightBoard board,SwordfightPair pair)
	{
		byte bestAngle=0;
		byte bestColumn=0;
		double bestScore=0;
		//all the columns
		for(byte u=0;u<6;u++)
		{
			for(byte i=0;i<4;i++)
			{
				if((u==0 && i==2) || (u==5 && i==0))
					continue;
				pair.setAngle(i);
				board.dropPair(pair,u);
				double score=board.getScore();
				if(score>bestScore)
				{
					bestScore=score;
					bestAngle=i;
					bestColumn=u;
				}
				board.undoLast();
			}
		}
		pair.setAngle(bestAngle);
		board.dropPair(pair,bestColumn);
		return bestColumn;
	}

	private boolean getBounds()
	{
		BufferedImage screen=null;
		try
		{
			screen=ImageIO.read(this.getClass().getResource("/images/sfboard.png"));
		}catch(IOException e)
		{
			e.printStackTrace();
		} //robot.createScreenCapture(new Rectangle(0,0,1366,768));
		
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
	
	@Override
	public void init()
	{
		try
		{
			bi_topLeft=ImageIO.read(this.getClass().getResource("/images/sf_topleft.png"));
			bi_bottomRight=ImageIO.read(this.getClass().getResource("/images/sf_bottomright.png"));
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public GameResult playGame()
	{
		run = true;
		getBounds();
		boardRect = new Rectangle(topLeft.x,topLeft.y,bottomRight.x,bottomRight.y);
		while(run)
		{
			BufferedImage screen;
			BufferedImage subImage=null;
			try
			{
				screen=ImageIO.read(this.getClass().getResource("/images/sfboard.png"));
				subImage=Main.subImage(screen,boardRect);
			}catch(IOException e)
			{
				e.printStackTrace();
				try
				{
					Thread.sleep(1000);
					continue;
				}catch(InterruptedException e1)
				{
					e1.printStackTrace();
				}
				continue;
			}
			
			SwordfightBoard sfBoard=new SwordfightBoard(subImage);
			currentDrop=new SwordfightPair(2,3);
			
			int column=searchDrop(sfBoard,currentDrop);
			
			int distance=column-3;
			int rotation=currentDrop.getAngle()-1;
			if(rotation<0)
				rotation=3;
			
			while(rotation>0)
			{
				robot.keyPress(40);
				rotation--;
				try
				{
					Thread.sleep(100);
				}catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			while(distance>0)
			{
				robot.keyPress(37);
				distance--;
				try
				{
					Thread.sleep(100);
				}catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			while(distance<0)
			{
				robot.keyPress(39);
				distance++;
				try
				{
					Thread.sleep(100);
				}catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			run=false;
		}
		return GameResult.ABORTED;
	}

	@Override
	public void abortGame()
	{
		run = false;
	}

	@Override
	public void setScreen(Coordinate topLeft,Coordinate bottomRight)
	{
		this.boardRect=new Rectangle(topLeft.x,topLeft.y,bottomRight.x,bottomRight.y);
	}
	
}
