package gp;

import java.util.List;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Position;
import gameLogic.Team;

public class NearestFruit extends GPNode
{

	@Override
	public String toString() 
	{
		return "NearestFruit";
	}
	
	// If there is at least one fruit visible to the the active team,
	// the position of the one closest to the current snake.

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) 
	{
		PositionData data = (PositionData)input;
		
		SnaykuuProblem snaykuuProblem = (SnaykuuProblem)problem;
		Team team = snaykuuProblem.getActiveTeam(thread);
		
		List<Position> fruits = snaykuuProblem.getSession(thread).getCurrentState().getFruits();
		Position headPos = snaykuuProblem.getActiveSnake(thread).getHeadPosition();
		int currentDistance = Integer.MAX_VALUE;
		
		if(fruits.isEmpty())
			data.pos = headPos;
		
		for(Position fruit : fruits)
		{
			int test = headPos.getDistanceTo(fruit);
			if(test < currentDistance && snaykuuProblem.hasVision(team, fruit))
			{
				data.pos = fruit;
				currentDistance = test;
			}
		}
	}
}
