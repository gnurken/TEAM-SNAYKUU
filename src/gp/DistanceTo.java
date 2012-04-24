package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Position;

public class DistanceTo extends GPNode
{

	@Override
	public String toString() 
	{
		return "DistanceTo";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) 
	{
		IntData data = (IntData)input;
		
		PositionData posData = new PositionData();
		Position pos = ((SnaykuuProblem)problem).getActiveSnake(thread).getHeadPosition();
		
		children[0].eval(state, thread, posData, stack, individual, problem);
		data.value = pos.getDistanceTo(posData.pos);
	}
}
