package gp;

import ec.gp.GPData;
import gameLogic.Snake;

public class SnakeData extends GPData
{
	public int snakeId;
	
	@Override
	public void copyTo(GPData gpd) 
	{
		((SnakeData)gpd).snakeId = snakeId;		
	}
}
