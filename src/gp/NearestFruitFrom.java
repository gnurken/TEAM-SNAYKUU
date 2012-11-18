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

public class NearestFruitFrom extends GPNode
{

	@Override
	public String toString() 
	{
		return "NearestFruitFrom";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) 
	{
		PositionData data = (PositionData)input;
		PositionData posData = new PositionData();
		
		children[0].eval(state, thread, posData, stack, individual, problem);
		
		SnaykuuProblem snaykuuProblem = (SnaykuuProblem)problem;
		
		List<Position> fruits = snaykuuProblem.getSession(thread).getCurrentState().getFruits();
		int currentDistance = Integer.MAX_VALUE;
		
		Position headPos = snaykuuProblem.getActiveSnake(thread).getHeadPosition();
		
		Team team = snaykuuProblem.getActiveTeam(thread);
		
		data.pos = headPos;
		
		for(Position fruit : fruits)
		{
			int test = posData.pos.getDistanceTo(fruit);
			if(test < currentDistance && snaykuuProblem.hasVision(team, fruit))
			{
				data.pos = fruit;
				currentDistance = test;
			}
		}
		
	}
}
