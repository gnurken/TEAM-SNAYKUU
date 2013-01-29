package gp.nodes;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gp.data.IntData;
import gp.data.PositionData;

public class DistanceFrom extends GPNode
{

	@Override
	public String toString() 
	{
		return "DistanceFrom";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) 
	{
		IntData data = (IntData)input;
		PositionData firstPos = new PositionData();
		PositionData secondPos = new PositionData();
		
		children[0].eval(state, thread, firstPos, stack, individual, problem);
		children[1].eval(state, thread, secondPos, stack, individual, problem);
		
		data.value = firstPos.pos.getDistanceTo(secondPos.pos);
	}
}
