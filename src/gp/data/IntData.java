package gp.data;

import ec.gp.GPData;

public class IntData extends GPData
{
	public int value;
	
	@Override
	public void copyTo(GPData gpd) 
	{
		((IntData)gpd).value = value;
	}

}