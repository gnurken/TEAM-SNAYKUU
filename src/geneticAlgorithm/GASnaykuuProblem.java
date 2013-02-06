package geneticAlgorithm;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bot.CarefulBot;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.DoubleVectorIndividual;

import gameLogic.GameObjectType;
import gameLogic.Metadata;
import gameLogic.Session;
import gameLogic.Snake;
import gameLogic.Team;
import geneticAlgorithm.GEUtil.ScoringPairTuple;

public class GASnaykuuProblem extends Problem implements SimpleProblemForm
{
	
	public static int vision = 10;
	public static int nrOfSnakesPerTeam = 2; 

	private static boolean graphical = false;
	
	private static int boardWidth = 15, boardHeight = 15, growthFrequency = 5, fruitFrequency = 10, thinkingTime = 1000, fruitGoal = 10;
	
	private static GameObjectType objectType = new GameObjectType("Snake", true);
	private static Metadata metadata = new Metadata(boardWidth, boardHeight, growthFrequency, fruitFrequency, thinkingTime, fruitGoal);
	
	private static int snakesPerTeam = 2;
	
	private static final int gamesPerEvaluation = 1;
	
	// Fitness constants
	private static float timeFitnessScaling = 0.2f;
	private static float maxTime = (boardWidth * boardHeight) / (1.0f / (growthFrequency * 2.0f));
	
	public static ScoringPairTuple[][] createScoringTuples(DoubleVectorIndividual individual)
	{
		int oneSnakeGenomeSize = GEUtil.allScoringCategories.length * 2 * 2;
		
		ScoringPairTuple[][] scoring = new ScoringPairTuple[snakesPerTeam][2];
		
		//DoubleVectorIndividual individual = (DoubleVectorIndividual)ind;
		for (int snakeNr = 0; snakeNr < snakesPerTeam; ++snakeNr)
		{
			int geneNr = oneSnakeGenomeSize * snakeNr;
			int stoppingPoint = oneSnakeGenomeSize * snakeNr;
			
			stoppingPoint += GEUtil.normalScoringCategories.length * 2;
			List<Double> normalScoringParameters = new ArrayList<Double>();
			for (; geneNr < stoppingPoint; ++geneNr)
			{
				normalScoringParameters.add(individual.genome[geneNr]);
			}
			
			stoppingPoint += GEUtil.reverseScoringCategories.length * 2;
			List<Double> reverseScoringParameters = new ArrayList<Double>();
			for (; geneNr < stoppingPoint; ++geneNr)
			{
				reverseScoringParameters.add(individual.genome[geneNr]);
			}
			
			scoring[snakeNr][0]
				= new ScoringPairTuple(vision, normalScoringParameters, reverseScoringParameters);
			
			stoppingPoint += GEUtil.normalScoringCategories.length * 2;
			List<Double> allyNormalScoringParameters = new ArrayList<Double>();
			for (; geneNr < stoppingPoint; ++geneNr)
			{
				allyNormalScoringParameters.add(individual.genome[geneNr]);
			}
			
			stoppingPoint += GEUtil.reverseScoringCategories.length * 2;
			List<Double> allyReverseScoringParameters = new ArrayList<Double>();
			for (; geneNr < stoppingPoint; ++geneNr)
			{
				allyReverseScoringParameters.add(individual.genome[geneNr]);
			}
			
			scoring[snakeNr][1]
				= new ScoringPairTuple(vision, allyNormalScoringParameters, allyReverseScoringParameters);
		}
		
		return scoring;
	}
	
	public static Session setupSession(Team contestants, Team opponents, ScoringPairTuple[][] scoring)
	{
		Session session = new Session(metadata);
		
		session.addTeam(contestants);
		
		Set<GEBrain> contestantBrains = new HashSet<GEBrain>();
		
		for (int snakeNr = 0; snakeNr < snakesPerTeam; ++snakeNr)
		{
			GEBrain brain = new GEBrain(scoring[snakeNr][0], scoring[snakeNr][1], vision);
			contestantBrains.add(brain);
			
			Snake snake = new Snake(objectType, "Contestant" + snakeNr, brain, Color.BLUE);
			brain.setSnake(snake);
			session.addSnake(snake, contestants);
		}
		
		for (GEBrain brain : contestantBrains)
		{
			brain.setAllies(contestantBrains, contestants.getSnakes());
		}
		
		session.addTeam(opponents);
		
		for (int i = 0; i < snakesPerTeam; ++i)
		{
			Snake snake = new Snake(objectType, "Opponent" + i, new CarefulBot(), Color.RED);
			session.addSnake(snake, opponents);
		}
		
		return session;
	}
	
	static class GraphicalGameRunner extends Thread
	{
		private Session m_session;
		
		public GraphicalGameRunner(DoubleVectorIndividual individual)
		{
			ScoringPairTuple[][] scoring = createScoringTuples(individual);
			
			Team contestants = new Team("Contestants", 1);
			Team opponents = new Team("Opponents", 2);
			
			m_session = setupSession(contestants, opponents, scoring);
		}
		
		@Override
		public void run()
		{
			m_session.prepareForStart();
			gameMain.Main.runGame(m_session, thinkingTime, 25);
		}
	}
	
	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum)
	{
		if (ind.evaluated)
			return;
		
		DoubleVectorIndividual individual = (DoubleVectorIndividual)ind;
		ScoringPairTuple[][] scoring = createScoringTuples(individual);
		
		float[] rawFitness = new float[gamesPerEvaluation];
		for (int gameNr = 0; gameNr < gamesPerEvaluation; ++gameNr)
		{
			Team contestants = new Team("Contestants", 1);
			Team opponents = new Team("Opponents", 2);
			
			Session session = setupSession(contestants, opponents, scoring);
			
			session.prepareForStart();
			
			// Play through one session of the game
			
			if (graphical)
				gameMain.Main.runGame(session, thinkingTime, 25);
			else
			{
				while (!session.hasEnded())
				{
					session.tick();
					
					//System.out.println("tick.");
				}
				System.out.println("Game finished.");
			}
			
			// Set fitness of the contestants during this game
			
			int time = session.getGameTime();
			int score = contestants.getScore();
			
			int winSign = 0;
			
			int comparedLifespan = contestants.getLifespan() - opponents.getLifespan();
			int comparedScore = score - opponents.getScore();
			if (comparedLifespan != 0)
				winSign = comparedLifespan / Math.abs(comparedLifespan);
			else if (comparedScore != 0)
				winSign = comparedScore / Math.abs(comparedScore);
			
			float timeFitness = 0.0f;
			if (winSign > 0)
				timeFitness = (maxTime - time) / maxTime;
			else
				timeFitness = time / maxTime;
				
			rawFitness[gameNr] = timeFitness * timeFitnessScaling + (score / (float)fruitGoal);
		}
		
		float maxRawFitness = 1.0f + timeFitnessScaling;
		
		float rawFitnessSum = 0.0f;
		for (float f : rawFitness)
			rawFitnessSum += f;
		
		float finalRawFitness = rawFitnessSum / gamesPerEvaluation;
		float standardizedFitness = finalRawFitness / maxRawFitness;
		
		((SimpleFitness)ind.fitness).setFitness(state, standardizedFitness, false);
		System.out.println("Fitness: " + standardizedFitness);
		
		ind.evaluated = true;
	}
	
	public void describe(
	        final EvolutionState state, 
	        final Individual ind, 
	        final int subpopulation,
	        final int threadnum,
	        final int log)
	{
		try
		{
			DoubleVectorIndividual individual = (DoubleVectorIndividual)ind;
			double[] genome = individual.genome;
			
			boolean append = true;
			FileWriter fstream = new FileWriter("evolution_results_" + vision + ".txt", append);
			BufferedWriter out = new BufferedWriter(fstream);
			
			out.write("Best team of run:");
			out.newLine();
			
			int genesPerSnake = GEUtil.allScoringCategories.length * 4;
			
			for (int snakeNr = 0; snakeNr < snakesPerTeam; ++snakeNr)
			{
				out.write("Snake " + (snakeNr + 1) + ":");
				out.newLine();
				int snakeGenomeStartingPoint = snakeNr * genesPerSnake;
				for (int geneNr = 0; geneNr < genesPerSnake; ++geneNr)
				{
					out.write(genome[snakeGenomeStartingPoint + geneNr] + " ");
				}
				out.newLine();
			}
			
			out.newLine();
			out.close();
			
			System.out.println("Succesfully recorded genome of the best team of generation to file.");
		}
		catch (IOException e)
		{
			System.out.println("Failed to record genome of the best team of generation to file.");
			e.printStackTrace();
		}
	}

}
