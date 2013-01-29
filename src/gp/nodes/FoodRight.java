package gp.nodes;

import ec.*;
import ec.gp.*;
import gameLogic.Position;
import gameLogic.Snake;
import gameLogic.Square;
import gp.SnaykuuProblem;
import gp.data.BoolData;

public class FoodRight extends GPNode
{

	@Override
	public String toString()
	{
		return "FoodRight";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
					 ADFStack stack, GPIndividual individual, Problem problem)
	{
		SnaykuuProblem snaykuuProblem = (SnaykuuProblem)problem;
		
		Snake snake = snaykuuProblem.getActiveSnake(thread);
		
		Position pos = snake.getCurrentDirection().turnRight().calculateNextPosition(snake.getHeadPosition());
		
		Square square = snaykuuProblem.getSession(thread).getBoard().getSquare(pos);
		
		((BoolData)input).bool = square.hasFruit();
	}
}
