package gp.nodes;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Position;
import gameLogic.Snake;
import gameLogic.Square;
import gameLogic.Team;
import gp.SnaykuuProblem;
import gp.data.DirectionData;
import gp.data.IntData;

public class DangerInOne extends GPNode
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
		
		SnaykuuProblem snaykuuProblem = (SnaykuuProblem)problem;
		
		Position pos = snaykuuProblem.getActiveSnake(thread).getHeadPosition();
		
		int i = 0;
		
		for(;; ++i)
		{
			pos = direction.dir.calculateNextPosition(pos);
			
			Team team = snaykuuProblem.getActiveTeam(thread);
			
			Square square = snaykuuProblem.getSession(thread).getBoard().getSquare(pos);
			
			if(snaykuuProblem.hasVision(team, pos))
			{
				if(square.isLethal())
					break;
			}
			
			else if(square.hasWall())
				break;
		}
		
		data.value = i;
	}
}
