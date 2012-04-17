package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class IfTrue extends GPNode
{

	@Override
	public String toString()
	{
		return "If";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		BoolData data = new BoolData();
		children[0].eval(state, thread, data, stack, individual, problem);
		
		if (data.bool)
			children[1].eval(state, thread, input, stack, individual, problem);
		else
			children[2].eval(state, thread, input, stack, individual, problem);
	}

}
