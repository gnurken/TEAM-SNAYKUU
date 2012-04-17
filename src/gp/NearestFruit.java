package gp;

import java.util.List;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Position;

public class NearestFruit extends GPNode
{

	@Override
	public String toString() 
	{
		return "NearestFruit";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) 
	{
		PositionData data = (PositionData)input;
		
		List<Position> fruits = ((SnaykuuProblem)problem).getSession().getCurrentState().getFruits();
		Position headPos = ((SnaykuuProblem)problem).getActiveSnake().getHeadPosition();
		int currentDistance = Integer.MAX_VALUE;
		
		if(fruits.isEmpty())
			data.pos = headPos;
		
		for(Position fruit : fruits)
		{
			int test = headPos.getDistanceTo(fruit);
			if(test < currentDistance)
			{
				data.pos = fruit;
				currentDistance = test;
			}
		}
	}

}
