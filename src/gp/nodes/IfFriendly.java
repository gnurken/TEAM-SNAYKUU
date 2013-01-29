package gp.nodes;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Snake;
import gp.SnaykuuProblem;
import gp.data.BoolData;
import gp.data.SnakeData;

public class IfFriendly extends GPNode
{

	@Override
	public String toString() {
		return "IfFriendly";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) 
	{
		BoolData data = (BoolData)input;
		
		SnakeData snakeData = new SnakeData();
		
		children[0].eval(state, thread, snakeData, stack, individual, problem);
		
		Snake snake = ((SnaykuuProblem)problem).getSnakeById(thread, snakeData.snakeId);
		data.bool = ((SnaykuuProblem)problem).getActiveTeam(thread).contains(snake);
	}
}
