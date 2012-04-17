package gp;

import java.util.List;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Position;

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
		
		List<Position> fruits = ((SnaykuuProblem)problem).getSession().getCurrentState().getFruits();
		int currentDistance = Integer.MAX_VALUE;
		
		if(fruits.isEmpty())
			data.pos = posData.pos;
		
		for(Position fruit : fruits)
		{
			int test = posData.pos.getDistanceTo(fruit);
			if(test < currentDistance)
			{
				data.pos = fruit;
				currentDistance = test;
			}
		}
		
	}

}
