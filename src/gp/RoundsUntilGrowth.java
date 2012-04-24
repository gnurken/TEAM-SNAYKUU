package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Session;

public class RoundsUntilGrowth extends GPNode
{

	@Override
	public String toString()
	{
		return "RoundsUntilGrowth";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		IntData data = (IntData)input;
		Session session = ((SnaykuuProblem)problem).getSession(thread);
		data.value = session.getGameResult().getRecordedGame().getTurnCount() % session.getMetadata().getFruitFrequency();
	}

}
