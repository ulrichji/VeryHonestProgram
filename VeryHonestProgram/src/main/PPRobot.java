package main;

public interface PPRobot
{
	public void init();
	//will make the robot play the game. Will return the exit status from the game
	public GameResult playGame();
	//Force the game to abort.
	public void abortGame();
	
	public void setScreen(Coordinate topLeft,Coordinate bottomRight);
}
