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
		BoolData data = (BoolData)input;
		
		Snake snake = ((SnaykuuProblem)problem).getActiveSnake(thread);
		
		Position pos = snake.getCurrentDirection().turnLeft().calculateNextPosition(snake.getHeadPosition());
		
		Square square = ((SnaykuuProblem)problem).getSession(thread).getBoard().getSquare(pos);
		
		data.bool = square.isLethal();
	}
}
