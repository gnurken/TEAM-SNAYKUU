package gp.nodes;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gp.data.BoolData;
import gp.data.IntData;

public class LessThan extends GPNode
{

	@Override
	public String toString()
	{
		return "<";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		IntData data = new IntData();
		children[0].eval(state, thread, data, stack, individual, problem);
		int left = data.value;
		children[1].eval(state, thread, data, stack, individual, problem);
		int right = data.value;
		((BoolData)input).bool = left < right;
	}

}
