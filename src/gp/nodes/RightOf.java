package gp.nodes;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gp.data.DirectionData;

public class RightOf extends GPNode
{

	@Override
	public String toString()
	{
		return "RightOf";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		children[0].eval(state, thread, input, stack, individual, problem);
		DirectionData data = (DirectionData)input;
		data.dir = data.dir.turnRight();
	}

}
