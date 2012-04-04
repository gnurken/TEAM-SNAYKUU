package gp;

import ec.gp.GPData;

public class BoolData extends GPData
{
	public boolean bool;
	
	@Override
	public void copyTo(GPData gpd) 
	{
		((BoolData)gpd).bool = bool;
	}

}
