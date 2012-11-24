package gp;

import ec.*;
import ec.gp.*;
import gameLogic.Position;
import gameLogic.Snake;
import gameLogic.Square;

public class DangerLeft extends GPNode
{

	@Override
	public String toString()
	{
		return "DangerLeft";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
					 ADFStack stack, GPIndividual individual, Problem problem)
	{
		SnaykuuProblem snaykuuProblem = (SnaykuuProblem)problem;
		
		Snake snake = snaykuuProblem.getActiveSnake(thread);
		
		Position pos = snake.getCurrentDirection().turnLeft().calculateNextPosition(snake.getHeadPosition());
		
		Square square = snaykuuProblem.getSession(thread).getBoard().getSquare(pos);
		
		((BoolData)input).bool = square.isLethal();
	}
}
