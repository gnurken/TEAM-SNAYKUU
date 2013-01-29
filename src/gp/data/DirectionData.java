package gp.data;

import ec.gp.GPData;
import gameLogic.Direction;

public class DirectionData extends GPData
{
	public Direction dir;
	
	@Override
	public void copyTo(GPData gpd) 
	{
		((DirectionData)gpd).dir = dir;
	}

}