package main;

import java.awt.AWTException;
import java.awt.Robot;

public class HumanRobot extends Robot
{
	private static double mouseSpeed=5;
	private int mouseX=0;
	private int mouseY=0;
	
	public HumanRobot() throws AWTException
	{
		super();
	}
	
	@Override
	public void mouseMove(int x,int y)
	{
		double mx=mouseX;
		double my=mouseY;
		
		double distance=getDistance(mouseX,mouseY,x,y);
		double dx=((x-mx)/distance)*mouseSpeed;
		double dy=((y-my)/distance)*mouseSpeed;
		
		int loopCount=(int)(distance/mouseSpeed);
		
		for(int i=0;i<loopCount;i++)
		{
			mx+=dx;
			my+=dy;
			
			super.mouseMove((int)mx,(int)my);
			try
			{
				Thread.sleep(1);
			}catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		super.mouseMove(x,y);
		mouseX=x;
		mouseY=y;
	}
	
	private double getDistance(double x1,double y1,double x2, double y2)
	{
		return Math.sqrt(((x2-x1)*(x2-x1))+((y2-y1)*(y2-y1)));
	}
}
