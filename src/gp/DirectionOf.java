package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Position;
import gameLogic.Snake;
import gameLogic.Team;


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
		
		SnaykuuProblem snaykuuProblem = (SnaykuuProblem)problem;
		
		Snake snake = snaykuuProblem.getSnakeById(thread, data.snakeId);
		
		Position snakePos = snake.getHeadPosition();
		Team team = snaykuuProblem.getActiveTeam(thread);
		
		if(snaykuuProblem.hasVision(team, snakePos))
		{
			((DirectionData)input).dir = snake.getCurrentDirection();
		}
		((DirectionData)input).dir = snaykuuProblem.getActiveSnake(thread).getCurrentDirection();
	}
}
