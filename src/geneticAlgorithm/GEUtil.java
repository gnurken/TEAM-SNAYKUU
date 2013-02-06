package geneticAlgorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gameLogic.Direction;
import gameLogic.GameState;
import gameLogic.Position;
import gameLogic.Snake;
import gameLogic.Square;

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
	
	public static String[] concatinate(String[] A, String[] B)
	{
	   String[] C= new String[A.length + B.length];
	   System.arraycopy(A, 0, C, 0, A.length);
	   System.arraycopy(B, 0, C, A.length, B.length);
	   return C;
	}
	
	public static String[] normalScoringCategories = {"fruit", "wall",
	                                                  "enemyBody", "enemyHead", "enemyTail",
	                                                  "allyBody", "allyHead", "allyTail"};
	
	public static String[] reverseScoringCategories = {"openSquare"};
	
	public static String[] allScoringCategories = concatinate(normalScoringCategories, reverseScoringCategories);

	public static class ScoringPairTuple
	{
		public Map<String, ScoringPair> scoringPairs = new HashMap<String, ScoringPair>();
		
		public ScoringPairTuple(int maxDepth, List<Double> normalScoringParameters, List<Double> reverseScoringParameters)
		{
			assert normalScoringParameters.size() == normalScoringCategories.length;
			assert reverseScoringParameters.size() == reverseScoringCategories.length;
		
			int i = 0;
			for (String category : normalScoringCategories)
			{
				scoringPairs.put(category, new ScoringPair(normalScoringParameters.get(i), normalScoringParameters.get(i+1), maxDepth, false));
				i += 2;
			}
		
			i = 0;
			for (String category : reverseScoringCategories)
			{
				scoringPairs.put(category, new ScoringPair(reverseScoringParameters.get(i), reverseScoringParameters.get(i+1), maxDepth, true));
				i += 2;
			}
		}
		
		
		public double getTotalScore(ScoringDistanceTuple scoringDistances)
		{
			double score = 0.0;
			for(Map.Entry<String, List<Integer>> entry : scoringDistances.distances.entrySet())
			{
				for(Integer distance : entry.getValue())
				{
					score += scoringPairs.get(entry.getKey()).getScore(distance);
				}
			}
			return score;
		}
	}
	
	public static class ScoringDistanceTuple
	{
		public Map<String, List<Integer>> distances = new HashMap<String, List<Integer>>();
		
		public ScoringDistanceTuple(List<List<Integer>> distances)
		{
			assert distances.size() == allScoringCategories.length;
		
			int i = 0;
			for(String category : allScoringCategories)
			{
				this.distances.put(category, distances.get(i++));
			}
		}
	}
}
