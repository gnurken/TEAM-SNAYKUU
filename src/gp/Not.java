package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class Not extends GPNode
{

	@Override
	public String toString()
	{
		return "Not";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		children[0].eval(state, thread, input, stack, individual, problem);
		((BoolData)input).bool = ! ((BoolData)input).bool;
	}

}
