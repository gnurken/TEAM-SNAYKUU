package gp.nodes;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Position;
import gameLogic.Snake;
import gameLogic.Team;
import gp.SnaykuuProblem;
import gp.data.PositionData;
import gp.data.SnakeData;

public class TailOf extends GPNode
{

	@Override
	public String toString() {
		return "TailOf";
	}
	
	// If the given snake is visible to the team of the evaluating snake, 
	// return the position of the given snake,
	// otherwise return the position of the head of the evaluating snake

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem)
	{
		PositionData data = (PositionData)input;
		SnaykuuProblem snaykuuProblem = ((SnaykuuProblem)problem);
		
		SnakeData snakeData = new SnakeData();
		children[0].eval(state, thread, snakeData, stack, individual, problem);
		Snake thatSnake = snaykuuProblem.getSnakeById(thread, snakeData.snakeId);
		Position tailPosition = thatSnake.getTailPosition();
		
		Team thisTeam = snaykuuProblem.getActiveTeam(thread);
		if (!snaykuuProblem.hasVision(thisTeam, tailPosition))
		{
			tailPosition = snaykuuProblem.getActiveSnake(thread).getHeadPosition();
		}
		
		data.pos = tailPosition;
	}
}
