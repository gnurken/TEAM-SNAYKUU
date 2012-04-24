package gp;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Position;
import gameLogic.Square;

public class FoodIn extends GPNode
{

	@Override
	public String toString() 
	{
		return "FoodIn";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) 
	{
		IntData data = (IntData)input;
		
		DirectionData direction = new DirectionData();
		children[0].eval(state, thread, direction, stack, individual, problem);
		
		Position pos = ((SnaykuuProblem)problem).getActiveSnake().getHeadPosition();
		
		int i = 0;
		
		for(;; ++i)
		{
			pos = direction.dir.calculateNextPosition(pos);
			
			Square square = ((SnaykuuProblem)problem).getSession().getBoard().getSquare(pos);
			
			if(square.hasFruit())
				break;
		}	
		data.value = i;
	}
}
