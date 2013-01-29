package gp.nodes;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Direction;
import gp.data.BoolData;
import gp.data.DirectionData;

public class DirectionEqualTo extends GPNode
{

	@Override
	public String toString()
	{
		return "=";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		DirectionData data = new DirectionData();
		children[0].eval(state, thread, data, stack, individual, problem);
		Direction left = data.dir;
		children[1].eval(state, thread, data, stack, individual, problem);
		Direction right = data.dir;
		((BoolData)input).bool = left == right;
	}

}
