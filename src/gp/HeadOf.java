package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class HeadOf extends GPNode
{

	@Override
	public String toString() {
		return "HeadOf";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		PositionData data = (PositionData)input;
		
		SnakeData snakeData = new SnakeData();
		
		children[0].eval(state, thread, snakeData, stack, individual, problem);
		data.pos = snakeData.snake.getHeadPosition();
	}
}
