package gp;

import ec.*;
import ec.gp.*;
import gameLogic.Position;
import gameLogic.Snake;
import gameLogic.Square;

public class FoodLeft extends GPNode
{

	@Override
	public String toString()
	{
		return "FoodLeft";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
					 ADFStack stack, GPIndividual individual, Problem problem)
	{
		BoolData data = (BoolData)input;
		
		Snake snake = ((SnaykuuProblem)problem).getActiveSnake();
		
		Position pos = snake.getCurrentDirection().turnLeft().calculateNextPosition(snake.getHeadPosition());
		
		Square square = ((SnaykuuProblem)problem).getSession().getBoard().getSquare(pos);
		
		data.bool = square.hasFruit();
	}
}
