package main;

public class Pattern
{
	private int[][]pattern;
	private Coordinate clickPos;
	
	public Pattern(int[][]pattern,Coordinate clickPos)
	{
		this.pattern=pattern;
		this.clickPos=clickPos;
	}
	
	public int[][]getPattern()
	{
		return pattern;
	}
	public Coordinate getClickPos()
	{
		return clickPos;
	}
	
	public void printPattern()
	{
		for(int i=0;i<pattern[0].length;i++)
		{
			for(int u=0;u<pattern.length;u++)
			{
				System.out.print(pattern[u][i]+" ");
			}
			System.out.println();
		}
	}
}
