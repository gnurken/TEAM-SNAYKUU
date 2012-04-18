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
		if (direction != null)
		{
			String str = direction.toString();
			str = str.charAt(0) + str.substring(1).toLowerCase();
			return str;
		}
		
		return "direction";
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
		int n = state.random[thread].nextInt(Direction.values().length);
		
		direction = Direction.values()[n];
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
