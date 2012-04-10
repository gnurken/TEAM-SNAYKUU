package gp;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class SnaykuuProblem extends GPProblem
{
	
	public DirectionData input;
	
	public Object clone()
	{
		SnaykuuProblem newProblem = (SnaykuuProblem)super.clone();
		newProblem.input = input;
		return newProblem;
	}
	
	public void setup(final EvolutionState state, final Parameter base)
	{
	super.setup(state, base);
	
	input = (DirectionData) state.parameters.getInstanceForParameterEq(
							base.push(P_DATA), null, DirectionData.class);
	input.setup(state,base.push(P_DATA));
	}

	@Override
	public void evaluate(EvolutionState state, Individual ind,
						 int subpopulation, int threadnum)
	{
		if (!ind.evaluated)
		{
			ind.evaluated = true;
		}
	}

}
