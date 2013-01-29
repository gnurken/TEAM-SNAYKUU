package gp.nodes;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Parameter;
import gameLogic.Direction;
import gameLogic.Snake;
import gp.data.SnakeData;

public class SnakeNode extends ERC
{
	
	private int snakeId;

	@Override
	public String toString() 
	{
		return "Snake" + snakeId;
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
		SnakeData data = (SnakeData)input;
		data.snakeId = snakeId;
	}

	@Override
	public void resetNode(EvolutionState state, int thread)
	{
		// Hardcoded number of snakes per team, change here if number of trees per
		// individual in the .params file are changed.
		snakeId = state.random[thread].nextInt(4);
	}

	@Override
	public boolean nodeEquals(GPNode node)
	{
		return (node instanceof SnakeNode && ((SnakeNode)node).snakeId == snakeId);
	}

	@Override
	public String encode()
	{
		return toString();
	}
	
}
