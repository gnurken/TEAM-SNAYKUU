package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Parameter;
import gameLogic.Direction;
import gameLogic.Snake;

public class ThisSnakeNode extends GPNode
{
	@Override
	public String toString() 
	{
		return "ThisSnake";
	}

	@Override
    public void checkConstraints(final EvolutionState state,
            final int tree,
            final GPIndividual typicalIndividual,
            final Parameter individualBase)
	{
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children.length!=0)
		state.output.error("Incorrect number of children for node " + 
							toStringForError() + " at " +
							individualBase);
	}
	
	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) 
	{
		
		SnaykuuProblem snaykuuProblem = (SnaykuuProblem)problem;
		
		int snakeId = snaykuuProblem.getSnakeId(thread, snaykuuProblem.getActiveSnake(thread));
		
		((SnakeData)input).snakeId = snakeId;
	}
	
}
