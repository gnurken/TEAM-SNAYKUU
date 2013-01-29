package gp.data;

import ec.gp.GPData;
import gameLogic.Position;

public class PositionData extends GPData
{
	public Position pos;
	
	@Override
	public void copyTo(GPData gpd) 
	{
		((PositionData)gpd).pos = pos;
	}

}
