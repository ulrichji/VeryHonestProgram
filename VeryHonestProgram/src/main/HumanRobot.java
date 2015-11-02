package main;

import java.awt.AWTException;
import java.awt.Robot;

public class HumanRobot extends Robot
{
	private static double mouseSpeed;
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
		mouseSpeed=0.35+(int)(Math.random())+distance*0.001;
		double dx=((x-mx)/distance)*mouseSpeed;
		double dy=((y-my)/distance)*mouseSpeed;
		
		int loopCount=(int)(distance/mouseSpeed);
		
		for(int i=0, bue=0;i<loopCount;i++)
		{
			if(i<loopCount/4)
			{
				bue++;
				mx+=dx+0.001*bue;
				my+=dy-0.001*bue;
			}
			else if(i<loopCount/2 && i>=loopCount/4)
			{
				bue--;
				mx+=dx+0.001*bue;
				my+=dy-0.001*bue;
			}
			else if(i<3*loopCount/4 && i>=loopCount/2)
			{
				bue++;
				mx+=dx-0.001*bue;
				my+=dy+0.001*bue;
			}
			else
				{
				bue--;
				mx+=dx-0.001*bue;
				my+=dy+0.001*bue;
				}
			
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
