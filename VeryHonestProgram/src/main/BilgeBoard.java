package main;


public class BilgeBoard
{	
	private int[][]board;
	private int width;
	private int height;
	
	public BilgeBoard()
	{
		board=new int[6][12];
		this.width=6;
		this.height=6;
	}
	
	public BilgeBoard(int[][]otherBoard)
	{
		board=new int[6][12];
		this.width=6;
		this.height=12;
		for(int i=0;i<6;i++)
		{
			for(int u=0;u<12;u++)
				board[i][u]=otherBoard[i][u];
		}
	}
	
	public BilgeBoard(BilgeBoard otherBoard)
	{
		board=new int[6][12];
		this.width=6;
		this.height=12;
		for(int i=0;i<6;i++)
		{
			for(int u=0;u<12;u++)
				board[i][u]=otherBoard.board[i][u];
		}
	}

	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	private int[][]copyBoard(int[][]board)
	{
		int[][]copy=new int[board.length][board[0].length];
		
		for(int i=0;i<board.length;i++)
		{
			for(int u=0;u<board[i].length;u++)
			{
				copy[i][u]=board[i][u];
			}
		}
		
		return copy;
	}
	
	public int evalBoard()
	{
		//printBoard(board);
		//threes, fours and fives
		int[]counts={0,0,0};
		//the board to put the removed squares in
		int[][]changeBoard=copyBoard(board);
		
		boolean found=false;
		//search for all the horizontal patterns
		for(int i=0;i<12;i++)
		{
			for(int u=0;u<4;u++)
			{
				int x=u;
				int y=i;
				int type=board[x][y];
				
				if(type==0)
					break;
				
				while(++x<=5 && board[x][y]==type);
				int len=x-u;
				if(len >= 3 && len <= 5)
				{
					counts[len-3]++;
					for(int j=0;j<len;j++)
						changeBoard[u+j][i]=0;
					
					u+=len-1;
				}
				if(len>=3)
					found=true;
			}
		}
		
		//search for all the vertical patterns
		for(int i=0;i<6;i++)
		{
			for(int u=0;u<10;u++)
			{
				int x=i;
				int y=u;
				int type=board[x][y];
				
				if(type <= 0 || type > 7)
					break;
				
				while(++y<=11 && board[x][y]==type);
				int len=y-u;
				if(len >= 3 && len <= 5)
				{
					counts[len-3]++;
					for(int j=0;j<len;j++)
						changeBoard[i][u+j]=0;
					
					u+=len-1;
				}
				if(len>=3)
					found=true;
			}
		}
		
		//printBoard(changeBoard);
		//System.out.println();
		if(found)
		{
			for(int i=0;i<changeBoard.length;i++)
			{
				for(int u=0;u<changeBoard[i].length;u++)
				{
					int count=0;
					while(u+count < 12 && changeBoard[i][u+count]==0)
						count++;
					
					for(int j=u;j<12;j++)
					{
						if(j+count<12)
							changeBoard[i][j]=changeBoard[i][j+count];
						else
							changeBoard[i][j]=0;
					}
				}
			}
		}
		//printBoard(changeBoard);
		//System.out.println("=========================================");
		
		board=changeBoard;
		
		//if it removes pieces find if there is any new ones.
		if(found)
			evalBoard();
		
		return getScore(counts);
	}
	
	public int getScore(int[]scores)
	{
		int hash=(scores[0]);
		hash+=(scores[1]*10);
		hash+=(scores[2]*100);
		
		switch(hash)
		{
		case 0:
			return 0;
		case 1:
			return 30;
		case 10:
			return 50;
		case 100:
			return 70;
		case 2:
			return 120;
		case 11:
			return 160;
		case 101:
			return 200;
		case 20:
			return 200;
		case 110:
			return 240;
		case 200:
			return 280;
		case 3:
			return 270;
		case 12:
			return 330;
		case 102:
			return 390;
		case 111:
			return 450;
		case 201:
			return 510;
		case 4:
			return 540;
		case 13:
			return 580;
		case 103:
			return 900;
		case 22:
			return 630;
		case 112:
			return 950;
		case 202:
			return 1000;
		}
		
		return 0;
	}
	
	public void swap(int x,int y)
	{
		if(board[x][y]==10 || board[x+1][y]==10)
			return;
		
		int temp=board[x][y];
		board[x][y]=board[x+1][y];
		board[x+1][y]=temp;
	}
	
	public void printBoard()
	{
		for(int i=0;i<board[0].length;i++)
		{
			for(int u=0;u<board.length;u++)
			{
				System.out.print(board[u][i]+" ");
			}
			System.out.println();
		}
	}
	
	public static int getType(int dot)
	{
		switch(dot)
		{
		case -11026955:
			return 1;
		case -12875882:
			return 2;
		case -15087373:
			return 3;
		case -7806267:
			return 4;
		case -15103798:
			return 5;
		case -16458548:
			return 6;
		case -16286997:
			return 7;
		case -331196:
			return 8;
		case -16711704:
			return 9;
			
		case -14452285:
			return 1;
		case -15178851:
			return 2;
		case -16089662:
			return 3;
		case -13203536:
			return 4;
		case -16096334:
			return 5;
		case -16611917:
			return 6;
		case -16556353:
			return 7;
		case -10187140:
			return 8;
		case -16739394:
			return 9;
		case -15054981:
			return 10;
		}
		return -1;
	}

	public boolean isLike(BilgeBoard other)
	{
		for(int i=0;i<board.length;i++)
		{
			for(int u=0;u<board[i].length;u++)
			{
				if(other.board[i][u] != this.board[i][u])
					return false;
			}
		}
		return true;
	}
	
	public int[][] getBoard()
	{
		return board;
	}
	
	//define the board blank if no more than 5 squares are defineable
	public boolean isBlank()
	{
		int count=0;
		for(int i=0;i<board.length;i++)
		{
			for(int u=0;u<board[i].length;u++)
			{
				if(board[i][u] >= 0)
					count++;
			}
		}
		if(count >= 5)
			return false;
		return true;
	}
}
