package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;


public class DirectionOf extends GPNode
{

	@Override
	public String toString() {
		return "DirectionOf";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		SnakeData data = new SnakeData();
		children[0].eval(state, thread, data, stack, individual, problem);
		((DirectionData)input).dir = data.snake.getCurrentDirection();		
	}

}
