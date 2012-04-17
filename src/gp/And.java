package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class And extends GPNode
{

	@Override
	public String toString()
	{
		return "And";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		BoolData data = (BoolData)input;
		children[0].eval(state, thread, data, stack, individual, problem);
		boolean left = data.bool;
		children[1].eval(state, thread, data, stack, individual, problem);
		boolean right = data.bool;
		
		data.bool = left && right;
	}

}
