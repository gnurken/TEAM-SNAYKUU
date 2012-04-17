package gp;

import ec.gp.GPData;

public class TypeData extends GPData
{
	public TileType type;
	
	@Override
	public void copyTo(GPData gpd) 
	{
		((TypeData)gpd).type = type;
	}

	public enum TileType
	{
		FRUIT, WALL, EMPTY, HEAD, BODY, TAIL, DEADSNAKE;
	}
	
}