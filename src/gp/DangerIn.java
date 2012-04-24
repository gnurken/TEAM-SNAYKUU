package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Position;
import gameLogic.Snake;
import gameLogic.Square;

public class DangerIn extends GPNode
{

	@Override
	public String toString() 
	{
		return "DangerIn";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) 
	{
		IntData data = (IntData)input;
		
		DirectionData direction = new DirectionData();
		children[0].eval(state, thread, direction, stack, individual, problem);
		
		Position pos = ((SnaykuuProblem)problem).getActiveSnake(thread).getHeadPosition();
		
		int i = 0;
		
		for(;; ++i)
		{
			pos = direction.dir.calculateNextPosition(pos);
			
			Square square = ((SnaykuuProblem)problem).getSession(thread).getBoard().getSquare(pos);
			
			if(square.isLethal())
				break;
		}
		
		data.value = i;
	}
}
