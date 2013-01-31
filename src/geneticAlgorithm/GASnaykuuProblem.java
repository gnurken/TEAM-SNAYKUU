package geneticAlgorithm;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bot.CarefulBot;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
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

	private static boolean graphical = false;
	private static int vision = 10;
	
	public static int nrOfSnakesPerTeam = 2; 
	
	private static int boardWidth = 30, boardHeight = 30, growthFrequency = 5, fruitFrequency = 10, thinkingTime = 300, fruitGoal = 10;
	
	private static GameObjectType objectType = new GameObjectType("Snake", true);
	private static Metadata metadata = new Metadata(boardWidth, boardHeight, growthFrequency, fruitFrequency, thinkingTime, fruitGoal);
	
	private static final int gamesPerEvaluation = 10;
	
	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum)
	{
		if (ind.evaluated)
			return;
	
		float[] rawFitness = new float[gamesPerEvaluation];
		for (int gameNr = 0; gameNr < gamesPerEvaluation; ++gameNr)
		{
			Session session = new Session(metadata);
			Team contestants = new Team("Contestants", 1);
			session.addTeam(contestants);
			
			Set<GEBrain> contestantBrains = new HashSet<GEBrain>();
			
			int snakesPerTeam = 2;
			DoubleVectorIndividual individual = (DoubleVectorIndividual)ind;
			for (int i = 0; i < snakesPerTeam; ++i)
			{
				int geneNr = 0;
				
				int stoppingPoint = GEUtil.normalScoringCategories.length;
				List<Double> normalScoringParameters = new ArrayList<Double>();
				for (; geneNr < stoppingPoint; ++geneNr)
				{
					normalScoringParameters.add(individual.genome[geneNr * 2]);
					normalScoringParameters.add(individual.genome[geneNr * 2 + 1]);
				}
				
				stoppingPoint += GEUtil.reverseScoringCategories.length;
				List<Double> reverseScoringParameters = new ArrayList<Double>();
				for (; geneNr < stoppingPoint; ++geneNr)
				{
					reverseScoringParameters.add(individual.genome[geneNr * 2]);
					reverseScoringParameters.add(individual.genome[geneNr * 2 + 1]);
				}
				
				ScoringPairTuple visibleSquaresScoring
					= new ScoringPairTuple(vision, normalScoringParameters, reverseScoringParameters);
				
				stoppingPoint += GEUtil.normalScoringCategories.length;
				List<Double> allyNormalScoringParameters = new ArrayList<Double>();
				for (; geneNr < stoppingPoint; ++geneNr)
				{
					allyNormalScoringParameters.add(individual.genome[geneNr * 2]);
					allyNormalScoringParameters.add(individual.genome[geneNr * 2 + 1]);
				}
				
				stoppingPoint += GEUtil.reverseScoringCategories.length;
				List<Double> allyReverseScoringParameters = new ArrayList<Double>();
				for (; geneNr < stoppingPoint; ++geneNr)
				{
					allyReverseScoringParameters.add(individual.genome[geneNr * 2]);
					allyReverseScoringParameters.add(individual.genome[geneNr * 2 + 1]);
				}
				
				ScoringPairTuple allyVisibleSquaresScoring
					= new ScoringPairTuple(vision, allyNormalScoringParameters, allyReverseScoringParameters);
				
				GEBrain brain = new GEBrain(visibleSquaresScoring, allyVisibleSquaresScoring, vision);
				contestantBrains.add(brain);
				
				Snake snake = new Snake(objectType, "Contestant" + i, brain, Color.BLUE);
				session.addSnake(snake, contestants);
			}
			
			for (GEBrain brain : contestantBrains)
			{
				brain.setAllies(contestantBrains, contestants.getSnakes());
			}
			
			// Create Opponent team
			Team opponents = new Team("Opponents", 2);
			session.addTeam(opponents);
			
			for (int i = 0; i < snakesPerTeam; ++i)
			{
				Snake snake = new Snake(objectType, "Opponent" + i, new CarefulBot(), Color.RED);
				session.addSnake(snake, opponents);
			}
			
			session.prepareForStart();
			
			// Play through one session of the game
			
			if (graphical)
				gameMain.Main.runGame(session, thinkingTime, 25);
			else
			{
				while (!session.hasEnded())
				{
					for (GEBrain brain : contestantBrains)
						brain.prepareForTick();
					
					session.tick();
					
					System.out.println("tick.");
				}
				System.out.println("Game finished.");
			}
			
			// TODO: fitness per game
			rawFitness[gameNr] = 0;
		}
		
		// TODO: fitness per individual
		
	}

}
