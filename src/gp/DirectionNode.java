package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Parameter;
import gameLogic.Direction;

public class DirectionNode extends ERC
{
	
	private Direction direction;

	@Override
	public String toString() 
	{
		return "direction(" + direction.toString() + ")";
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
		DirectionData data = (DirectionData)input;
		data.dir = direction;
	}

	@Override
	public void resetNode(EvolutionState state, int thread)
	{
		int n = state.random[thread].nextInt(4);
		
		switch (n)
		{
		case 0:
			direction = Direction.EAST;
			break;
		case 1:
			direction = Direction.WEST;
			break;
		case 2:
			direction = Direction.EAST;
			break;
		case 3:
			direction = Direction.NORTH;
			break;
		}
	}

	@Override
	public boolean nodeEquals(GPNode node)
	{
		return (node instanceof DirectionNode && ((DirectionNode)node).direction == direction);
	}

	@Override
	public String encode()
	{
		return toString();
	}
	
}
