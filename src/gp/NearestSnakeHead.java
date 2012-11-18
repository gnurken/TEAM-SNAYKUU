package gp;
import java.util.LinkedList;
import java.util.List;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Position;
import gameLogic.Snake;


public class NearestSnakeHead extends GPNode
{

	@Override
	public String toString() 
	{
		return "NearestSnakeHead";
	}
	
	// If the nearest snake head is visible to the evaluating snake, return that head,
	// otherwise return the head of the evaluating snake.

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) 
	{
		PositionData data = (PositionData)input;
		
		PositionData posData = new PositionData();
		
		children[0].eval(state, thread, posData, stack, individual, problem);
		
		SnaykuuProblem snaykuuProblem = (SnaykuuProblem)problem;
		Snake thisSnake = snaykuuProblem.getActiveSnake(thread);
		
		List<Snake> snakes = new LinkedList<Snake>(snaykuuProblem.getSession(thread).getSnakes());
		snakes.remove(thisSnake);
		
		int currentDistance = Integer.MAX_VALUE;
		
		for(Snake snake : snakes)
		{
			int test = posData.pos.getDistanceTo(snake.getHeadPosition());
			if(test < currentDistance)
			{	
				data.pos = snake.getHeadPosition();
				currentDistance = test;
			}
		}
		
		if (!snaykuuProblem.hasVision(snaykuuProblem.getActiveTeam(thread), data.pos))
		{
			data.pos = thisSnake.getHeadPosition();
		}
	}
}
