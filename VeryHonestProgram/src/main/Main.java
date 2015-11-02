package main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main
{
	Robot robot;
	BufferedImage screen;
	
	GUI gui=new GUI(this);
	
	BilgeRobot bilgeRobot;
	SwordfightRobot swordfightRobot;
	
	GameMode gameMode = GameMode.SWORDFIGHT;
	
	public void loadImages()
	{
		try
		{
			screen=ImageIO.read(this.getClass().getResource("/images/screen.png"));
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void init()
	{
		gui.init();
		try
		{
			robot=new HumanRobot();
		}catch(AWTException e)
		{
			e.printStackTrace();
		}
		loadImages();
		
		bilgeRobot = new BilgeRobot(robot);
		swordfightRobot = new SwordfightRobot(robot);
		
		bilgeRobot.init();
		swordfightRobot.init();
	}

	public static Coordinate searchFor(BufferedImage img1,BufferedImage img2)
	{
		int[][]img2Bytes=new int[img2.getWidth()][img2.getHeight()];

		for(int i=0;i<img2Bytes.length;i++)
		{
			for(int u=0;u<img2Bytes[i].length;u++)
				img2Bytes[i][u]=img2.getRGB(i,u);
		}
		
		for(int i=0;i<img1.getWidth();i++)
		{
			for(int u=0;u<img1.getHeight();u++)
			{
				boolean matches=true;
				int xCount=0;
				int yCount=0;
				while(matches)
				{
					int x=i+xCount;
					int y=u+yCount;
					
					int rgbval=img1.getRGB(x,y);
					//System.out.println(rgbval+"  "+img2Bytes[xCount][yCount]);
					if(rgbval != img2Bytes[xCount][yCount])
					{
						matches=false;
						break;
					}
					
					xCount++;
					
					if(xCount>=img2.getWidth())
					{
						xCount=0;
						yCount++;
						if(yCount>=img2.getHeight())
							return new Coordinate(x-img2.getWidth()+1,y-img2.getHeight()+1);
					}
				}
			}
		}
		
		return null;
	}
	
	public void run()
	{
		gui.run();
	}
	
	public void startSearching()
	{
		if(robot==null)
			return;
		
		screen=robot.createScreenCapture(new Rectangle(0,0,1366,768));
		
		try
		{
			screen=ImageIO.read(this.getClass().getResource("/images/sfboard.png"));
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
				
		GameResult result=GameResult.NONE;
		
		switch(gameMode)
		{
		case BILGING:
			result = bilgeRobot.playGame();
			break;
		case SWORDFIGHT:
			result = swordfightRobot.playGame();
			break;
		default:
			break;
		}
		
		System.out.println(result);
	}
	
	public void stopSearching()
	{
		switch(gameMode)
		{
		case BILGING:
			bilgeRobot.abortGame();
			break;
		case SWORDFIGHT:
			swordfightRobot.abortGame();
			break;
		default:
			break;
		}
	}
	
	public static BufferedImage subImage(BufferedImage img,Rectangle rect)
	{
		BufferedImage toReturn=new BufferedImage(rect.width,rect.height,BufferedImage.TYPE_3BYTE_BGR);
		
		if(rect.x+rect.width > img.getWidth() || rect.x < 0 || rect.y+rect.height > img.getHeight() || rect.y < 0)
			throw new IllegalArgumentException("Illegal bounds on image");
		
		for(int i=0;i<rect.width;i++)
		{
			for(int u=0;u<rect.height;u++)
			{
				toReturn.setRGB(i,u,img.getRGB(i+rect.x,u+rect.y));
			}
		}
		
		return toReturn;
	}
	
	public static void main(String[] args)
	{
		//v=vanlig, s=solid(samlet blokk), g=grå, b=breaker
		//blå
		int bv=(new Color(8,111,196)).getRGB();
		int bs=(new Color(0,74,140).getRGB());
		int br=(new Color(17,56,93)).getRGB();
		int bg=(new Color(139,137,139)).getRGB();
		//System.out.println(bv);
		//System.out.println(bs);
		//System.out.println(br);
		//grønn
		int gv=(new Color(15,241,0)).getRGB();
		int gs=(new Color(0,180,0)).getRGB();
		int gr=(new Color(18,230,0)).getRGB();
		int gg=(new Color(179,177,178)).getRGB();
		//System.out.println(gv);
		//System.out.println(gs);
		//System.out.println(gr);
		//rød
		int rv=(new Color(255,39,0)).getRGB();
		int rs=(new Color(73,0,0)).getRGB();
		int rr=(new Color(0,0,0)).getRGB();
		int rg=(new Color(193,190,193)).getRGB();
		//System.out.println(rv);
		//System.out.println(rs);
		//System.out.println(rr);
		//gul
		int yv=(new Color(118,106,0)).getRGB();
		int ys=(new Color(78,70,0)).getRGB();
		int yr=(new Color(255,255,0)).getRGB();
		int yg=(new Color(121,119,121)).getRGB();
		//System.out.println(yv);
		//System.out.println(ys);
		//System.out.println(yr);
		//aqua
		int yv1=(new Color(1,189,146)).getRGB();
		int ys1=(new Color(2,165,115)).getRGB();
		int yr1=(new Color(5,158,109)).getRGB();
		int yg1=(new Color(161,159,161)).getRGB();
		//System.out.println(yv1);
		//System.out.println(ys1);
		//System.out.println(yr1);
		//hard blank
		int hh=(new Color(182,183,189)).getRGB();
		
		//System.out.println(hh);
		
		Main m=new Main();
		m.init();
		m.run();
	}
}
