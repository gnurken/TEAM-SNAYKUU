package geneticAlgorithm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.DoubleVectorIndividual;

import gameLogic.Metadata;
import gameLogic.Session;
import gameLogic.Team;
import geneticAlgorithm.GEUtil.ScoringPairTuple;

public class GASnaykuuProblem extends Problem implements SimpleProblemForm
{
	
	public static int vision = 10;
	public static int snakesPerTeam = 2; 
	public static int runsPerSetting = 20;

	private static boolean graphical = false;
	
	private static int boardWidth = 30, boardHeight = 30, growthFrequency = 5, fruitFrequency = 10, thinkingTime = 1000, fruitGoal = 10;
	public static Metadata metadata = new Metadata(boardWidth, boardHeight, growthFrequency, fruitFrequency, thinkingTime, fruitGoal);
	
	private static final int gamesPerEvaluation = 20;
	
	// Fitness constants
	private static float timeFitnessScaling = 0.2f;
	private static float maxTime = (boardWidth * boardHeight) / (1.0f / (growthFrequency * 2.0f));
	
	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum)
	{
		if (ind.evaluated)
			return;
		
		DoubleVectorIndividual individual = (DoubleVectorIndividual)ind;
		ScoringPairTuple[][] scoring = GEUtil.createScoringTuples(individual.genome);
		
		float[] rawFitness = new float[gamesPerEvaluation];
		for (int gameNr = 0; gameNr < gamesPerEvaluation; ++gameNr)
		{
			Team contestants = new Team("Contestants", 1);
			Team opponents = new Team("Opponents", 2);
			
			Session session = GEUtil.setupSession(contestants, opponents, scoring);
			
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
				//System.out.println("Game finished.");
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
		//System.out.println("Fitness: " + standardizedFitness);
		
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
			
			int genesPerSnake = GEUtil.totalNrOfscoringCategories * 2 * 2;
			
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
