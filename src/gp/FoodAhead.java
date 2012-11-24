package gp;

import ec.*;
import ec.gp.*;
import gameLogic.Position;
import gameLogic.Snake;
import gameLogic.Square;

public class FoodAhead extends GPNode
{

	@Override
	public String toString()
	{
		return "FoodAhead";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
					 ADFStack stack, GPIndividual individual, Problem problem)
	{
		SnaykuuProblem snaykuuProblem = (SnaykuuProblem)problem;
		
		Snake snake = snaykuuProblem.getActiveSnake(thread);
		
		Position pos = snake.getCurrentDirection().calculateNextPosition(snake.getHeadPosition());
		
		Square square = snaykuuProblem.getSession(thread).getBoard().getSquare(pos);
		
		((BoolData)input).bool = square.hasFruit();
	}
}
