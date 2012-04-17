package gp;

import java.awt.Color;
import java.util.List;

import bot.FruitEaterBot;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;
import gameLogic.GameObjectType;
import gameLogic.Metadata;
import gameLogic.Session;
import gameLogic.Snake;
import gameLogic.Team;

public class SnaykuuProblem extends GPProblem
{
	
	public DirectionData input;
	
	private Snake activeSnake;
	
	static private GameObjectType objectType = new GameObjectType("Snake", true);
	static private final int gamesPerEval = 10;
	
	static private int bw = 30, bh = 30, gf = 5, ff = 10, tt = 1000, fg = 10;
	
	private Session session = null;
	
	public Session getSession()
	{
		return session;
	}
	
	public void setActiveSnake(Snake s)
	{
		activeSnake = s;
	}
	
	public Snake getActiveSnake()
	{
		return activeSnake;
	}
	
	public Object clone()
	{
		SnaykuuProblem newProblem = (SnaykuuProblem)super.clone();
		newProblem.input = input;
		return newProblem;
	}
	
	public void setup(final EvolutionState state, final Parameter base)
	{
		super.setup(state, base);
		
		input = (DirectionData) state.parameters.getInstanceForParameterEq(
								base.push(P_DATA), null, DirectionData.class);
		input.setup(state, base.push(P_DATA));
	}

	@Override
	public void evaluate(EvolutionState state, Individual ind,
						 int subpopulation, int threadnum)
	{
		if (!ind.evaluated)
		{
			int contestantLifetime = 0;
			int opponentLifetime = 0;
			
			for (int game = 0; game < gamesPerEval; ++game)
			{
				Metadata metadata = new Metadata(bw, bh, gf, ff, tt, fg);
				session = new Session(metadata);
				
				Team contestants = new Team("Contestants", 1);
				session.addTeam(contestants);
				
				GPIndividual individual = (GPIndividual)ind;
				int snakesPerTeam = individual.trees.length;
				for (int i = 0; i < snakesPerTeam; ++i)
				{
					GPNode root = individual.trees[i].child;
					
					GPBrain brain = new GPBrain();
					brain.setup(root, state, threadnum, stack, individual, this, input);
					
					Snake snake = new Snake(objectType, "Contestant" + i, brain, Color.BLUE);
					session.addSnake(snake, contestants);
				}
				
				// Create Opponent team
				Team opponents = new Team("Opponents", 2);
				session.addTeam(opponents);
				
				for (int i = 0; i < snakesPerTeam; ++i)
				{
					Snake snake = new Snake(objectType, "Opponent" + i, new FruitEaterBot(), Color.RED);
					session.addSnake(snake, opponents);
				}
				
				session.prepareForStart();
				
				while (!session.hasEnded())
				{
					session.tick();
				}
				
				List<Team> result = session.getGameResult().getTeams();
				
				if (result.get(0).getNumber() == 1)
				{
					contestantLifetime += result.get(0).getLifespan();
					opponentLifetime += result.get(1).getLifespan();
				}
				else
				{
					contestantLifetime += result.get(1).getLifespan();
					opponentLifetime += result.get(0).getLifespan();
				}
				
				
			}
			
			// TODO: evaluate fitness better
			
			float fitness = (float)contestantLifetime / opponentLifetime;
			((KozaFitness)ind.fitness).setStandardizedFitness(state, fitness);
			
			ind.evaluated = true;
			
			System.out.println("Generation " + state.generation + ".");
			System.out.println("Team evaluated, fitnes: " + fitness);
			GPIndividual individual = (GPIndividual)ind;
			for (int i = 0; i < individual.trees.length; ++i)
			{
				System.out.println("Tree " + (i + 1) + ":");
				individual.trees[i].printTreeForHumans(state, 0);
			}
			System.out.println();
		}
	}
}
