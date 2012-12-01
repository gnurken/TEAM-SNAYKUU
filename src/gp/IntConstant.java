package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.ERC;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class IntConstant extends ERC
{

	private int value;
	
	@Override
	public String toString()
	{
		return "" + value;
	}
	
	@Override
	public void resetNode(EvolutionState state, int thread)
	{
		// Hardcoded max constant value, change if the size of hte board is changed
		value = state.random[thread].nextInt(30);
	}

	@Override
	public boolean nodeEquals(GPNode node)
	{
		return (node instanceof IntConstant && ((IntConstant)node).value == value);
	}

	@Override
	public String encode()
	{
		return toString();
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		IntData data = (IntData)input;
		data.value = value;
	}

}
