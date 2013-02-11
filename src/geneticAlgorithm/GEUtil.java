package geneticAlgorithm;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ec.app.edge.func.Reverse;

import bot.CarefulBot;

import gameLogic.Board;
import gameLogic.Direction;
import gameLogic.GameObjectType;
import gameLogic.GameState;
import gameLogic.Metadata;
import gameLogic.Position;
import gameLogic.Session;
import gameLogic.Snake;
import gameLogic.Square;
import gameLogic.Team;

public final class GEUtil
{
	static class ScoredDirection implements Comparable<ScoredDirection>
	{
		public ScoredDirection(Direction direction, double score)
		{
			this.direction = direction;
			this.score = score;
		}
		
		public Direction direction;
		public double score;
		
		@Override
		public int compareTo(ScoredDirection other)
		{
			if (score > other.score)
				return -1;
			else if (score < other.score)
				return 1;
			else
				return 0;
		}
	}
	
	static boolean isValidPosition(Board board, Position position)
	{
		int x = position.getX();
		int y = position.getY();
		boolean validPosition = ((x >= 0 && x < board.getWidth()) &&
								 (y >= 0 && y < board.getHeight()));
		
		return validPosition;
	}
	
	static boolean isSurvivableDirection(Position currentHeadPosition, Direction direction, GameState gameState, int currentRound)
	{
		Position nextHeadPosition = GameState.calculateNextPosition(direction, currentHeadPosition);
		return isSurvivablePosition(nextHeadPosition, gameState, currentRound);
	}
	
	static boolean isSurvivablePosition(Position position, GameState gameState, int currentRound)
	{
		Square nextSquare = gameState.getBoard().getSquare(position);
		if (nextSquare.isLethal())
		{
			boolean growthRound = ((currentRound + 1) % gameState.getMetadata().getGrowthFrequency()) == 0;
			if (!growthRound)
			{
				for (Snake tailSnake : gameState.getSnakes())
				{
					if (tailSnake.getTailPosition() == position && !tailSnake.isDead())
					{
						return true;
					}
				}
			}
			
			return false;
		}
		
		return true;
	}
	
	static List<Direction> getTurnableDirections(Direction initalDirection)
	{
		Direction[] directions = {initalDirection, initalDirection.turnLeft(), initalDirection.turnRight()};
		return Arrays.asList(directions);
	}
	
	static List<Direction> getSurvivableDirections(Snake snake, GameState gameState, int currentRound)
	{
		Direction currentDirection = snake.getCurrentDirection();
		List<Direction> directions = getTurnableDirections(currentDirection);
		
		List<Direction> survivableDirections = new LinkedList<Direction>();
		for (Direction direction : directions)
		{
			if (GEUtil.isSurvivableDirection(snake.getHeadPosition(), direction, gameState, currentRound))
				survivableDirections.add(direction);
		}
		
		return survivableDirections;
	}
	
	public static class ScoringPair
	{
		public int maxDepth;
		public boolean reverseDepth;
		public double exponentBase, exponentConstant;
		
		public ScoringPair(double exponentBase, double exponentConstant,
		                   int maxDepth, boolean reverseDepth)
		{
			this.exponentBase = exponentBase;
			this.exponentConstant = exponentConstant;
			this.maxDepth = maxDepth;
			this.reverseDepth = reverseDepth;
		}
		
		public double getScore(int depth)
		{
			double exponent = reverseDepth ? maxDepth - depth : depth;
			return exponentConstant * Math.pow(exponentBase, exponent);
		}
	}
	
	public enum NormalScoringCategory
	{
		FRUIT,
		WALL,
		ENEMY_BODY,
		ENEMY_HEAD,
		ENEMY_TAIL,
		ALLY_BODY,
		ALLY_HEAD,
		ALLY_TAIL;
	}
	
	public enum ReverseScoringCategory
	{
		OPEN_SQUARE;
	}
	
	public static int totalNrOfscoringCategories = NormalScoringCategory.values().length +
									  ReverseScoringCategory.values().length;

	public static class ScoringPairTuple
	{
		public Map<NormalScoringCategory, ScoringPair> normalScoringPairs = new HashMap<NormalScoringCategory, ScoringPair>();
		public Map<ReverseScoringCategory, ScoringPair> reverseScoringPairs = new HashMap<ReverseScoringCategory, ScoringPair>();
		
		public ScoringPairTuple(int maxDepth, List<Double> normalScoringParameters, List<Double> reverseScoringParameters)
		{
			assert normalScoringParameters.size() == NormalScoringCategory.values().length;
			assert reverseScoringParameters.size() == ReverseScoringCategory.values().length;
		
			int i = 0;
			for (NormalScoringCategory category : NormalScoringCategory.values())
			{
				normalScoringPairs.put(category, new ScoringPair(normalScoringParameters.get(i), normalScoringParameters.get(i+1), maxDepth, false));
				i += 2;
			}
		
			i = 0;
			for (ReverseScoringCategory category : ReverseScoringCategory.values())
			{
				reverseScoringPairs.put(category, new ScoringPair(reverseScoringParameters.get(i), reverseScoringParameters.get(i+1), maxDepth, true));
				i += 2;
			}
		}
		
		
		public double getTotalScore(ScoringDistanceTuple scoringDistances)
		{
			double score = 0.0;
			for(Map.Entry<NormalScoringCategory, List<Integer>> entry : scoringDistances.normalDistances.entrySet())
			{
				for(Integer distance : entry.getValue())
				{
					score += normalScoringPairs.get(entry.getKey()).getScore(distance);
				}
			}
			for(Map.Entry<ReverseScoringCategory, List<Integer>> entry : scoringDistances.reverseDistances.entrySet())
			{
				for(Integer distance : entry.getValue())
				{
					score += reverseScoringPairs.get(entry.getKey()).getScore(distance);
				}
			}
			
			return score;
		}
	}
	
	public static class ScoringDistanceTuple
	{
		public Map<NormalScoringCategory, List<Integer>> normalDistances = new HashMap<NormalScoringCategory, List<Integer>>();
		public Map<ReverseScoringCategory, List<Integer>> reverseDistances = new HashMap<ReverseScoringCategory, List<Integer>>();
		
		public ScoringDistanceTuple(List<List<Integer>> normalDistances, 
				List<List<Integer>> reverseDistances)
		{
			assert normalDistances.size() == NormalScoringCategory.values().length;
			assert reverseDistances.size() == ReverseScoringCategory.values().length;
			
			int i = 0;
			for (NormalScoringCategory category : NormalScoringCategory.values())
			{
				this.normalDistances.put(category, normalDistances.get(i++));
			}
			
			i = 0;
			for (ReverseScoringCategory category : ReverseScoringCategory.values())
			{
				this.reverseDistances.put(category, reverseDistances.get(i++));
			}
		}
	}
	
	public static ScoringPairTuple[][] createScoringTuples(double[] genome)
	{	
		int snakesPerTeam = GASnaykuuProblem.snakesPerTeam;
		int vision = GASnaykuuProblem.vision;
		int genesPerSnake = totalNrOfscoringCategories * 2 * 2;
		
		ScoringPairTuple[][] scoring = new ScoringPairTuple[snakesPerTeam][2];
		
		for (int snakeNr = 0; snakeNr < snakesPerTeam; ++snakeNr)
		{
			int geneNr = genesPerSnake * snakeNr;
			int stoppingPoint = genesPerSnake * snakeNr;
			
			stoppingPoint += NormalScoringCategory.values().length * 2;
			List<Double> normalScoringParameters = new ArrayList<Double>();
			for (; geneNr < stoppingPoint; ++geneNr)
			{
				normalScoringParameters.add(genome[geneNr]);
			}
			
			stoppingPoint += ReverseScoringCategory.values().length * 2;
			List<Double> reverseScoringParameters = new ArrayList<Double>();
			for (; geneNr < stoppingPoint; ++geneNr)
			{
				reverseScoringParameters.add(genome[geneNr]);
			}
			
			scoring[snakeNr][0]
				= new ScoringPairTuple(vision, normalScoringParameters, reverseScoringParameters);
			
			stoppingPoint += NormalScoringCategory.values().length * 2;
			List<Double> allyNormalScoringParameters = new ArrayList<Double>();
			for (; geneNr < stoppingPoint; ++geneNr)
			{
				allyNormalScoringParameters.add(genome[geneNr]);
			}
			
			stoppingPoint += ReverseScoringCategory.values().length * 2;
			List<Double> allyReverseScoringParameters = new ArrayList<Double>();
			for (; geneNr < stoppingPoint; ++geneNr)
			{
				allyReverseScoringParameters.add(genome[geneNr]);
			}
			
			scoring[snakeNr][1]
				= new ScoringPairTuple(vision, allyNormalScoringParameters, allyReverseScoringParameters);
		}
		
		return scoring;
	}
	
	public static ScoringPairTuple[][][] readScoringTuplesFromGenomeFile(String filePath)
	{
		int nrOfTeams = GASnaykuuProblem.runsPerSetting;
		int snakesPerTeam = GASnaykuuProblem.snakesPerTeam;
		int genesPerSnake = totalNrOfscoringCategories * 2 * 2;
		
		ScoringPairTuple scoring[][][] = new ScoringPairTuple[nrOfTeams][snakesPerTeam][2]; 
		
		try
		{
			FileReader fstream = new FileReader(filePath);
			BufferedReader reader = new BufferedReader(fstream);
			
			for (int teamNr = 0; teamNr < nrOfTeams; ++teamNr)
			{
				while (!reader.readLine().contains("Best team of run:"));
				
				double[] genome = new double[genesPerSnake * snakesPerTeam];
				
				for (int snakeNr = 0; snakeNr < snakesPerTeam; ++snakeNr)
				{
					int snakeGenomeStartingPoint = snakeNr * genesPerSnake;
					
					while (!reader.readLine().contains("Snake"));
					
					String[] genomeLine = reader.readLine().split("\\s+");
					for(int geneNr = 0; geneNr < genesPerSnake; ++geneNr)
					{
						genome[snakeGenomeStartingPoint + geneNr] = Double.parseDouble(genomeLine[geneNr]);
					}
				}
				
				scoring[teamNr] = createScoringTuples(genome);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return scoring;
	}
	
	public static Session setupSession(Team contestants, Team opponents, ScoringPairTuple[][] scoring)
	{
		int snakesPerTeam = GASnaykuuProblem.snakesPerTeam;
		int vision = GASnaykuuProblem.vision;
		Metadata metadata = GASnaykuuProblem.metadata;
		
		GameObjectType objectType = new GameObjectType("Snake", true);
		
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
		private int m_squareSize;
		
		public GraphicalGameRunner(double[] genome, int squareSize)
		{
			ScoringPairTuple[][] scoring = GEUtil.createScoringTuples(genome);
			
			Team contestants = new Team("Contestants", 1);
			Team opponents = new Team("Opponents", 2);
			
			m_session = GEUtil.setupSession(contestants, opponents, scoring);
			
			m_squareSize = squareSize;
		}
		
		@Override
		public void run()
		{
			m_session.prepareForStart();
			gameMain.Main.runGame(m_session, GASnaykuuProblem.metadata.getMaximumThinkingTime(), m_squareSize);
		}
	}
}
