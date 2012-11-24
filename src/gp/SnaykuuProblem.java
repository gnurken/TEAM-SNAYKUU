package gp;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import bot.CarefulBot;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;
import gameLogic.GameObjectType;
import gameLogic.Metadata;
import gameLogic.Position;
import gameLogic.Session;
import gameLogic.Snake;
import gameLogic.Team;

public class SnaykuuProblem extends GPProblem
{
	private boolean graphical = false;
	private int vision = 10;
	
	public static int nrOfSnakesPerTeam = 2; 
	
	static private GameObjectType objectType = new GameObjectType("Snake", true);
	static private final int gamesPerEval = 10;
	
	static private int boardWidth = 30, boardHeight = 30, growthFrequency = 5, fruitFrequency = 10, thinkingTime = 300, fruitGoal = 10;
	
	private Map<Integer, ThreadData> threadData = new HashMap<Integer, ThreadData>();
	
	private DirectionData input;
	
	// Fitness constants
	float timeFitnessScaling = 0.2f;
	float treeSizeScaling = 0.01f;
	long bigTreeSize = 100;
	
	final float maxTime = (boardWidth * boardHeight) / (1.0f / (growthFrequency * 2.0f));
	
	class ThreadData
	{
		private Session session = null;
		public Snake activeSnake = null;
		public Team activeTeam = null;
		
		public Map<Integer, Snake> snakes = new HashMap<Integer, Snake>();
	}
	
	public Snake getSnakeById(int threadNumber, int snakeId)
	{
		return threadData.get(threadNumber).snakes.get(snakeId);
	}
	
	public Snake setSnakeById(int threadNumber, int snakeId, Snake snake)
	{
		return threadData.get(threadNumber).snakes.put(snakeId, snake);
	}
	
	public Session getSession(int threadNumber)
	{
		return threadData.get(threadNumber).session;
	}
	
	public void setActiveSnake(Snake s, int threadNumber)
	{
		threadData.get(threadNumber).activeSnake = s;
	}
	
	public Snake getActiveSnake(int threadNumber)
	{
		return threadData.get(threadNumber).activeSnake;
	}
	
	public Team getActiveTeam(int threadNumber)
	{
		return threadData.get(threadNumber).activeTeam;
	}
	
	public boolean hasVision(Team team, Position position)
	{
		boolean hasVision = false;
		
		for (Snake snake : team.getSnakes())
		{
			if (!snake.isDead())
			{
				Position snakePosition = snake.getHeadPosition();
				
				int dX = Math.abs(snakePosition.getX() - position.getX());
				int dY = Math.abs(snakePosition.getY() - position.getY());
				
				if ((dX <= vision) && (dY <= vision))
					hasVision = true;
			}
		}
		
		return hasVision;
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
		
		for (int i = 0; i < state.evalthreads; ++i)
		{
			threadData.put(i, new ThreadData());
		}
	}

	@Override
	public void evaluate(EvolutionState state, Individual ind,
						 int subpopulation, int threadNumber)
	{
		if (!ind.evaluated)
		{
			float[] fitness = new float[gamesPerEval];
			
			for (int game = 0; game < gamesPerEval; ++game)
			{
				Metadata metadata = new Metadata(boardWidth, boardHeight, growthFrequency, fruitFrequency, thinkingTime, fruitGoal);
				Session thisSession = new Session(metadata);
				threadData.get(threadNumber).session = thisSession;
				
				Team contestants = new Team("Contestants", 1);
				threadData.get(threadNumber).session.addTeam(contestants);
				threadData.get(threadNumber).activeTeam = contestants;
				
				int snakeCount = 0;
				
				GPIndividual individual = (GPIndividual)ind;
				int snakesPerTeam = individual.trees.length;
				for (int i = 0; i < snakesPerTeam; ++i)
				{
					GPNode root = individual.trees[i].child;
					
					GPBrain brain = new GPBrain();
					brain.setup(root, state, threadNumber, stack, individual, this, input);
					
					Snake snake = new Snake(objectType, "Contestant" + i, brain, Color.BLUE);
					thisSession.addSnake(snake, contestants);
					
					setSnakeById(threadNumber, snakeCount++, snake);
				}
				
				// Create Opponent team
				Team opponents = new Team("Opponents", 2);
				thisSession.addTeam(opponents);
				
				for (int i = 0; i < snakesPerTeam; ++i)
				{
					Snake snake = new Snake(objectType, "Opponent" + i, new CarefulBot(), Color.RED);
					thisSession.addSnake(snake, opponents);
					setSnakeById(threadNumber, snakeCount++, snake);
				}
				
				thisSession.prepareForStart();
				
				if(graphical)
					gameMain.Main.runGame(thisSession, thinkingTime, 25);
				else
				{
					while (!thisSession.hasEnded())
					{
						thisSession.tick();
					}
				}				
				
				int time = thisSession.getGameTime();
				int score = contestants.getScore();
				
				int winSign = 0;
				
				int comparedLifespan = contestants.getLifespan() - opponents.getLifespan();
				int comparedScore = contestants.getScore() - opponents.getScore();
				if (comparedLifespan != 0)
					winSign = comparedLifespan / Math.abs(comparedLifespan);
				else if (comparedScore != 0)
					winSign = comparedScore / Math.abs(comparedScore);
				
				float timeFitness = 0.0f;
				if (winSign > 0)
					timeFitness = (maxTime - time) / maxTime;
				else
					timeFitness = time / maxTime;
					
				fitness[game] = timeFitness * timeFitnessScaling + (score / (float)fruitGoal);
			}
			
			// TODO: evaluate fitness better
			
			float maxRawFitness = timeFitnessScaling + 1.0f;
			
			float rawFitnessSum = 0.0f;
			for (float f : fitness)
				rawFitnessSum += f;
			
			float rawFitness = rawFitnessSum / gamesPerEval;
			
			//Slightly penalize larger trees to promote fast thinking
			long treeSize = ((GPIndividual)ind).size();
			rawFitness -= (Math.min(treeSize, bigTreeSize) / (bigTreeSize * 1.0f)) * treeSizeScaling;
			
			float standardizedFitness = maxRawFitness - rawFitness;
			((KozaFitness)ind.fitness).setStandardizedFitness(state, standardizedFitness);
			
			ind.evaluated = true;
			
			System.out.println("Generation " + state.generation + ".");
			System.out.println("Team evaluated, fitnes: " + rawFitness);
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
