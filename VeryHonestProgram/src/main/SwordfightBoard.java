package main;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SwordfightBoard
{
	byte[][]board;
	byte[][]lastBoard;
	public SwordfightBoard()
	{
		board=new byte[6][13];
	}
	
	public SwordfightBoard(BufferedImage boardImage)
	{
		board=new byte[6][13];
		int tileWidth=boardImage.getWidth()/board.length;
		int tileHeight=boardImage.getHeight()/board[0].length;
		
		for(int i=0;i<board.length;i++)
		{
			for(int u=0;u<board[i].length;u++)
			{
				int dot=boardImage.getRGB((i*tileWidth)+14,(u*tileHeight)+7);
				board[i][u]=getType(dot);
				//System.out.println(i+","+u+": "+((i*tileWidth)+14)+" "+((u*tileHeight)+7)+"  "+dot);
			}
		}
	}
	
	public String toString()
	{
		String string="";
		
		for(int i=0;i<board[0].length;i++)
		{
			for(int u=0;u<board.length;u++)
			{
				if(board[u][i]>=0)
					string+=" "+board[u][i];
				else
					string+=board[u][i];
			}
			string+="\n";
		}
		return string;
	}
	
	public void undoLast()
	{
		byte[][]preBoard=new byte[board.length][board[0].length];
		
		for(int i=0;i<board.length;i++)
		{
			for(int u=0;u<board[i].length;u++)
			{
				preBoard[i][u]=board[i][u];
			}
		}
		board=lastBoard;
		lastBoard=preBoard;
	}
	
	public boolean dropPair(SwordfightPair pair,int column)
	{
		byte[][]preBoard=new byte[board.length][board[0].length];
		
		for(int i=0;i<board.length;i++)
		{
			for(int u=0;u<board[i].length;u++)
			{
				preBoard[i][u]=board[i][u];
			}
		}
		if(pair.getAngle()==1 || pair.getAngle()==3)
		{
			int top=getTop(column);
			if(top<0)
				return false;
			
			if(pair.getAngle()==1)
			{
				board[column][top]=pair.getRight();
				board[column][top-1]=pair.getLeft();
			}
			else
			{
				board[column][top]=pair.getLeft();
				board[column][top-1]=pair.getRight();
			}
		}
		else
		{
			int leftColumn=column;
			int rightColumn=0;
			
			if(pair.getAngle()==0)
				rightColumn=leftColumn+1;
			else
				rightColumn=leftColumn-1;
			
			board[leftColumn][getTop(leftColumn)]=pair.getLeft();
			board[rightColumn][getTop(rightColumn)]=pair.getRight();
			
		}
		lastBoard=preBoard;
		return true;
	}
	
	private Rectangle expandArea(boolean[][]visited,int color,int x,int y)
	{
		Rectangle rect=new Rectangle(x,y,1,1);
		Rectangle maxRect=new Rectangle(x,y,1,1);
		
		Rectangle tempRect=new Rectangle(rect);
		
		//the rectangles currentl visited that we dont want to visit again
		LinkedList<Rectangle>testedRects=new LinkedList<Rectangle>();
		//a queue of the rectangles we want to test.
		LinkedList<Rectangle>toTest=new LinkedList<Rectangle>();
		
		//add inital value
		toTest.push(new Rectangle(tempRect));
		while(!toTest.isEmpty())
		{
			tempRect=toTest.poll();
			//is the rectangle valid
			boolean isValidRect=true;
			//loop through all the squares covered by the rectangle
			for(int i=0;i<tempRect.getWidth();i++)
			{
				for(int u=0;u<tempRect.getHeight();u++)
				{
					int tempX=(int)(tempRect.getX()+i);
					int tempY=(int)(tempRect.getY()+u);
					
					//has the square been visited or is the square a different color?
					//if so, this is invalid and should be thrown
					if(tempX<0 || tempX>5 || tempY<0 || tempY>12 || visited[tempX][tempY]==true || board[tempX][tempY]!=color)
					{
						isValidRect=false;
						break;
					}
				}
				if(!isValidRect)
					break;
			}
			//the rect is not valid and we should take out the next item from the stack
			if(!isValidRect)
				continue;
			
			for(Rectangle testedRect:testedRects)
			{
				//the rectangle is already checked and the next item in the queue should be checked.
				if(testedRect.hashCode()==tempRect.hashCode())
					continue;
			}
			
			//at this point the rectangle should be valid
			
			//the area of the current rectangle
			int area=(int)(tempRect.getWidth()*tempRect.getHeight());
			
			//strips are not considered blocks
			if(tempRect.getHeight()<=1 || tempRect.getWidth()<=1)
				area=0;
			
			//Check if current rectangle is the largest.
			if(area>(int)(maxRect.getWidth()*maxRect.getHeight()))
				maxRect=tempRect;

			
			//Now the rectangle should be expanded and added to the queue
			
			Rectangle left=new Rectangle(tempRect);
			left.x--;
			left.width++;
			
			Rectangle right=new Rectangle(tempRect);
			right.width++;
			
			Rectangle up=new Rectangle(tempRect);
			up.height++;
			
			Rectangle down=new Rectangle(tempRect);
			down.y--;
			down.height++;
			
			//now add these values to the queue
			toTest.add(left);
			toTest.add(right);
			toTest.add(up);
			toTest.add(down);
		}
		return maxRect;
	}
	//returns a list of all rectangles reachable from the given coordinates and visitedmatrix
	public ArrayList<Rectangle> searchRects(boolean[][]visited,int color,int x,int y)
	{
		Queue<Coordinate> toSearch=new LinkedList<Coordinate>();
		toSearch.add(new Coordinate(x,y));
		
		ArrayList<Rectangle>rects=new ArrayList<Rectangle>();
		
		while(!toSearch.isEmpty())
		{
			Coordinate coordinate=toSearch.poll();
			if(visited[coordinate.x][coordinate.y])
				continue;
			
			//get the largest rectangle at the current coordinate
			Rectangle largestRectangle=expandArea(visited,color,coordinate.x,coordinate.y);
			//add the rectangle to the result set
			rects.add(largestRectangle);
			//Mark the area covered by the rectangle as visited
			for(int i=0;i<largestRectangle.getWidth();i++)
			{
				for(int u=0;u<largestRectangle.getHeight();u++)
					visited[(int)(largestRectangle.getX()+i)][(int)(largestRectangle.getY()+u)]=true;
			}
			
			//search the area around this rectangle to see if it is possible to increase the area
			//search to the left
			if(largestRectangle.getX()>0)
			{
				int tempX=(int)(largestRectangle.getX()-1);
				for(int u=0;u<largestRectangle.getHeight();u++)
				{
					int tempY=(int)(largestRectangle.getY()+u);
					//check that the y coordinate is inside the board. Must be greater than 0
					if(tempY>12)
						continue;
					
					//if it is the same color
					if(board[tempX][tempY]==color)
					{
						//add it to the queue
						toSearch.add(new Coordinate(tempX,tempY));
					}
				}
			}
			//search to the right
			if(largestRectangle.getX()+largestRectangle.getWidth()<6)
			{
				int tempX=(int)(largestRectangle.getX()+largestRectangle.getWidth());
				for(int u=0;u<largestRectangle.getHeight();u++)
				{
					int tempY=(int)(largestRectangle.getY()+u);
					//check that the y coordinate is inside the board. Must be greater than 0
					if(tempY>12)
						continue;
					
					//if it is the same color
					if(board[tempX][tempY]==color)
					{
						//add it to the queue
						toSearch.add(new Coordinate(tempX,tempY));
					}
				}
			}
			//check up
			if(largestRectangle.getY()+largestRectangle.getHeight()<13)
			{
				int tempY=(int)(largestRectangle.getY()+largestRectangle.getHeight());
				for(int i=0;i<largestRectangle.getWidth();i++)
				{
					int tempX=(int)(largestRectangle.getX()+i);
					//check that the y coordinate is inside the board. Must be greater than 0
					if(tempX>5)
						continue;
					
					//if it is the same color
					if(board[tempX][tempY]==color)
					{
						//add it to the queue
						toSearch.add(new Coordinate(tempX,tempY));
					}
				}
			}
			//check down
			if(largestRectangle.getY()>0)
			{
				int tempY=(int)(largestRectangle.getY()-1);
				for(int i=0;i<largestRectangle.getWidth();i++)
				{
					int tempX=(int)(largestRectangle.getX()+i);
					//check that the y coordinate is inside the board. Must be greater than 0
					if(tempX>5)
						continue;
					
					//if it is the same color
					if(board[tempX][tempY]==color)
					{
						//add it to the queue
						toSearch.add(new Coordinate(tempX,tempY));
					}
				}
			}
			
			//System.out.println(largestRectangle.getWidth()+"  "+largestRectangle.getHeight());
		}
		return rects;
	}
	
	public double getScore()
	{
		boolean[][]visited=new boolean[6][13];		
		ArrayList<ArrayList<Rectangle>>allRects=new ArrayList<ArrayList<Rectangle>>();
		for(int i=0;i<visited.length;i++)
		{
			for(int u=0;u<visited[i].length;u++)
			{
				//TODO find upper bound for valid fields
				if(!visited[i][u] && board[i][u]>0)
				{
					allRects.add(searchRects(visited,board[i][u],i,u));
				}
			}
		}
		
		int largestGroup=0;
		for(ArrayList<Rectangle>rectangleGroup:allRects)
		{
			int groupSize=0;
			for(Rectangle rect:rectangleGroup)
				groupSize+=rect.getHeight()*rect.getWidth();
			
			if(groupSize>largestGroup)
				largestGroup=groupSize;
		}
		
		double score=0;
		int i=0;
		for(ArrayList<Rectangle>rectangleGroup:allRects)
		{
			int groupSize=0;
			int singlesCount=0;
			ArrayList<Integer>groups=new ArrayList<Integer>();
			for(Rectangle rect:rectangleGroup)
			{
				int area=(int)(rect.getHeight()*rect.getWidth());
				if(area>1)
					groups.add(area);
				else
					singlesCount++;
				
				groupSize+=area;
			}
			
			score+=singlesCount;
			
			for(int u=0;u<groups.size();u++)
			{
				//formula for largest group
				if(i==largestGroup)
					score+=2*groups.get(u)*getYValue(groups.get(u));
				
				//formula for large groups
				else if(groupSize>8)
					score+=2*groups.get(u)*getYValue(groups.get(u));
				
				//formula for small groups
				else
					score+=groups.get(u)*getYValue(groups.get(u));
			}
			
			//increment counter
			i++;
		}
		
		return score;
	}
	
	private double getYValue(double a)
	{
		return 0.9+((a*a)+a+1)/((20*a)+5);
	}

	public int getTop(int column)
	{
		int x=column;
		int y=0;
		while(y<13)
		{
			if(board[x][y] != 0)
			{
				return y-1;
			}
			y++;
		}
		return 0;
	}
	
	public static byte getType(int color)
	{
		switch(color)
		{
		case -16224316: //blue regular
			return 1;
		case -16758132: //blue solid
			return 1;
		case -15648675: //blue breaker
			return -1;
		case -15732480: //green regular
			return 2;
		case -16731136: //green solid
			return 2; 
		case -15538688: //green breaker
			return -2;
		case -55552: //red regular
			return 3;
		case -11993088: //red solid
			return 3;
		case -2550784: //red solid
			return 3;
		case -16777216: //red breaker
			return -3;
		case -9016832: //yellow regular
			return 4;
		case -11647488: //yellow solid
			return 4;
		case -256: //yellow breaker
			return -4;
		case -16663150: //aqua regular
			return 5;
		case -16603789: //aqua solid
			return 5;
		case -16408979:
			return -5;
		case -4802627: //unknown gray
			return 6;
		default:
			return 0;
		}
	}
}
