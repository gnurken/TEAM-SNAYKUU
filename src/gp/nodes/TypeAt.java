package gp.nodes;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Square;
import gameLogic.Snake;
import gp.SnaykuuProblem;
import gp.data.PositionData;
import gp.data.TypeData;

public class TypeAt extends GPNode
{

	@Override
	public String toString() 
	{
		return "TypeAt";
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) 
	{
		TypeData data = (TypeData)input;
		
		PositionData posData = new PositionData();
		
		children[0].eval(state, thread, posData, stack, individual, problem);
		
		SnaykuuProblem snaykuuProblem = (SnaykuuProblem)problem;
		Square square = snaykuuProblem.getSession(thread).getBoard().getSquare(posData.pos);
		
		if(square.isEmpty())
			data.type = TypeData.TileType.EMPTY;
		else if(square.hasWall())
			data.type = TypeData.TileType.WALL;
		else if(square.hasFruit())
			data.type = TypeData.TileType.FRUIT;
		else if(square.hasSnake())
		{
			if(square.getSnakes().size() > 1)
				data.type = TypeData.TileType.DEADSNAKE;
			else
			{
				Snake s = square.getSnakes().get(0);
				if(s.isDead())
					data.type = TypeData.TileType.DEADSNAKE;
				else if(s.getHeadPosition() == posData.pos)
					data.type = TypeData.TileType.HEAD;
				else if(s.getTailPosition()== posData.pos)
					data.type = TypeData.TileType.TAIL;
				else
					data.type = TypeData.TileType.BODY;
			}
		}
		
		if (snaykuuProblem.hasVision(snaykuuProblem.getActiveTeam(thread), posData.pos) && data.type != TypeData.TileType.WALL)
			data.type = TypeData.TileType.EMPTY;
		
	}
}
