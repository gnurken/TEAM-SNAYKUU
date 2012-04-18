package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class InDirectionFrom extends GPNode
{

	@Override
	public String toString()
	{
		return "InDirFrom";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		DirectionData dirData = new DirectionData();
		children[0].eval(state, thread, dirData, stack, individual, problem);
		children[1].eval(state, thread, input, stack, individual, problem);
		
		PositionData posData = (PositionData)input;
		posData.pos = dirData.dir.calculateNextPosition(posData.pos);
	}

}
