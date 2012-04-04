package gp;

import ec.gp.GPData;
import gameLogic.Snake;

public class SnakeData extends GPData
{
	public Snake snake;
	public SnakeId snakeId;
	
	@Override
	public void copyTo(GPData gpd) 
	{
		((SnakeData)gpd).snake = snake;
		((SnakeData)gpd).snakeId = snakeId;		
	}

	public enum SnakeId
	{
		TEAM1SNAKE1, TEAM1SNAKE2, TEAM2SNAKE1, TEAM2SNAKE2;
	}
}
