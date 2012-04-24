package gp;

import ec.EvolutionState;
import ec.gp.ADFStack;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import gameLogic.Brain;
import gameLogic.Direction;
import gameLogic.GameState;
import gameLogic.Snake;

public class GPBrain implements Brain
{
	
	private GPNode root;
	private EvolutionState state;
	private int thread;
	private ADFStack stack;
	private GPIndividual individual;
	private SnaykuuProblem problem;
	private DirectionData input;
	
	public void setup(GPNode root, EvolutionState state, int thread, ADFStack stack,
					  GPIndividual individual, SnaykuuProblem problem, DirectionData input)
	{
		this.root = root;
		this.thread = thread;
		this.stack = stack;
		this.individual = individual;
		this.problem = problem;
		this.input = input;
	}

	@Override
	public Direction getNextMove(Snake yourSnake, GameState gameState)
	{
		((SnaykuuProblem)problem).setActiveSnake(yourSnake, thread);
		
		root.eval(state, thread, input, stack, individual, problem);
		
		return input.dir;
	}

}
