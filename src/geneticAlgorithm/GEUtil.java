package geneticAlgorithm;

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
		public int compareTo(ScoredDirection other) {
			if (score > other.score)
				return -1;
			else if (score < other.score)
				return 1;
			else
				return 0;
					
		}
	}
	
	static boolean isSurvivable(Position currentHeadPosition, Direction direction, GameState gameState, int currentRound)
	{
		Position nextHeadPosition = GameState.calculateNextPosition(direction, currentHeadPosition);
		Square nextSquare = gameState.getBoard().getSquare(nextHeadPosition);
		if (nextSquare.isLethal())
		{
			boolean growthRound = ((currentRound + 1) % gameState.getMetadata().getGrowthFrequency()) == 0;
			if (growthRound)
			{
				for (Snake tailSnake : gameState.getSnakes())
				{
					if (tailSnake.getTailPosition() == nextHeadPosition && !tailSnake.isDead())
					{
						return true;
					}
				}
			}
			
			return false;
		}
		
		return true;
	}
	
	static List<Direction> getSurvivableDirections(Snake snake, GameState gameState, int currentRound)
	{
		Direction currentDirection = snake.getCurrentDirection();
		Direction[] directions = {currentDirection, currentDirection.turnLeft(), currentDirection.turnRight()};
		
		List<Direction> survivableDirections = new LinkedList<Direction>();
		for (Direction direction : directions)
		{
			if (GEUtil.isSurvivable(snake.getHeadPosition(), direction, gameState, currentRound))
				survivableDirections.add(direction);
		}
		
		return survivableDirections;
	}
	
	public class ScoringPair
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
	   int aLen = A.length;
	   int bLen = B.length;
	   String[] C= new String[aLen+bLen];
	   System.arraycopy(A, 0, C, 0, aLen);
	   System.arraycopy(B, 0, C, aLen, bLen);
	   return C;
	}
	
	public static String[] normalScoringCategories = {"fruit", "wall",
	                                                  "enemyBody", "enemyHead", "enemyTail",
	                                                  "allyBody", "allyHead", "allyTail"};
	
	public static String[] reverseScoringCategories = {"openSquare"};
	
	public static String[] allScoringCategories = concatinate(normalScoringCategories, reverseScoringCategories);

	public class ScoringPairTuple
	{
		public Map<String, ScoringPair> scoringPairs = new HashMap<String, ScoringPair>();
		
		public ScoringPairTuple(int maxDepth, double[] normalScoringParameters, double[] reverseScoringParameters)
		{
			int i = 0;
			for (String category : normalScoringCategories)
			{
				scoringPairs.put(category, new ScoringPair(normalScoringParameters[i], normalScoringParameters[i+1], maxDepth, false));
				i += 2;
			}
		
			i = 0;
			for (String category : reverseScoringCategories)
			{
				scoringPairs.put(category, new ScoringPair(reverseScoringParameters[i], reverseScoringParameters[i+1], maxDepth, true));
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
	
	public class ScoringDistanceTuple
	{
		public Map<String, List<Integer>> distances = new HashMap<String, List<Integer>>();
		
		public ScoringDistanceTuple(List<List<Integer>> distances)
		{
			int i = 0;
			for(String category : allScoringCategories)
			{
				this.distances.put(category, distances.get(i++));
			}
		}
	}
}
