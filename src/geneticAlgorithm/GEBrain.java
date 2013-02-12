package geneticAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import gameLogic.Board;
import gameLogic.Brain;
import gameLogic.Direction;
import gameLogic.GameState;
import gameLogic.Position;
import gameLogic.Snake;
import gameLogic.Square;

import geneticAlgorithm.GEUtil.NormalScoringCategory;
import geneticAlgorithm.GEUtil.ReverseScoringCategory;
import geneticAlgorithm.GEUtil.ScoringDistanceTuple;
import geneticAlgorithm.GEUtil.ScoringPairTuple;

/**
 * 
 * @author Thomas Zebühr, Daniel Malmqvist
 * 
 * A Snaykuu brain based on RodeOrmII, the winning snake of DSV:s Snaykuu championships of 2012,
 * to be used together with an genetic algorithm to hopefully generate a team player snake.
 *
 */

public class GEBrain implements Brain
{
	private ScoringPairTuple m_visibleSquaresScoring, m_allyVisibleSquaresScoring;
	private int m_vision;
	
	public ScoringPairTuple getVisibleSquaresScoring()
	{
		return m_visibleSquaresScoring;
	}
	
	public ScoringPairTuple getAllyVisibleSquaresScoring()
	{
		return m_allyVisibleSquaresScoring;
	}
	
	public GEBrain(ScoringPairTuple visibleSquaresScoring,
	               ScoringPairTuple allyVisibleSquaresScoring, int vision)
	{
		m_visibleSquaresScoring = visibleSquaresScoring;
		m_allyVisibleSquaresScoring = allyVisibleSquaresScoring;
		m_vision = vision;
	}
	
	private Snake m_thisSnake;
	
	public void setSnake(Snake thisSnake)
	{
		m_thisSnake = thisSnake;
	}
	
	public Snake getSnake()
	{
		return m_thisSnake;
	}
	
	private Set<GEBrain> m_livingAllyBrains;
	private Set<Snake> m_allySnakes;
	
	public void setAllies(Set<GEBrain> allyBrains, Set<Snake> allySnakes)
	{
		m_livingAllyBrains = allyBrains;
		m_allySnakes = allySnakes;
	}
			
	private void recordSquareDistances(Map<NormalScoringCategory, List<Integer>> normalDistances,
			Map<ReverseScoringCategory, List<Integer>> reverseDistances,
			GameState gameState, Position position, boolean survivable, int depth)
	{
		Board board = gameState.getBoard();
		Square square = board.getSquare(position);
		
		if (survivable)
		{
			// No need to check for an open square, we already know it is
			reverseDistances.get(ReverseScoringCategory.OPEN_SQUARE).add(depth);

			//Check for fruit
			if (square.hasFruit())
				normalDistances.get(NormalScoringCategory.FRUIT).add(depth);
			
		}
		else
		{
			//Check for walls
			if (square.hasWall())
				normalDistances.get(NormalScoringCategory.WALL).add(depth);
			
			Set<Snake> otherSnakes = gameState.getSnakes();
			
			//Check for snakes
			if (square.hasSnake())
			{
				for (Snake otherSnake : otherSnakes)
				{
					//If otherSnake is an enemy
					if (!m_allySnakes.contains(otherSnake))
					{
						//Check for enemy snake heads and if it's alive
						if (otherSnake.getHeadPosition().equals(position) && !otherSnake.isDead())
							normalDistances.get(NormalScoringCategory.ENEMY_HEAD).add(depth);
						//Check for enemy tails and if it's alive
						else if (otherSnake.getTailPosition().equals(position) && !otherSnake.isDead())
							normalDistances.get(NormalScoringCategory.ENEMY_TAIL).add(depth);
						//All other cases counts as enemy body
						else
							normalDistances.get(NormalScoringCategory.ENEMY_BODY).add(depth);
						
						
					} 
					
					//If otherSnake is an ally
					if (m_allySnakes.contains(otherSnake))
					{
						//Check for ally snake heads and if it's alive
						if (otherSnake.getHeadPosition().equals(position) && !otherSnake.isDead())
							normalDistances.get(NormalScoringCategory.ALLY_HEAD).add(depth);
						//Check for ally tails if it's alive
						else if (otherSnake.getTailPosition().equals(position) && !otherSnake.isDead())
							normalDistances.get(NormalScoringCategory.ALLY_TAIL).add(depth);
						//All other cases counts as ally body
						else
							normalDistances.get(NormalScoringCategory.ALLY_BODY).add(depth);
					}
				}
			}
		}
	}
	
	Map<Direction, Set<Position>> m_visiblePositions = new HashMap<Direction, Set<Position>>();
	Map<Direction, Set<Position>> m_nextToSearch = new HashMap<Direction, Set<Position>>();
	
	private ScoringDistanceTuple searchVisibleSquares(GameState gameState,
			Direction direction, Position startingPosition, int maxDepth)
	{
		Map<NormalScoringCategory, List<Integer>> normalDistances = new HashMap<NormalScoringCategory, List<Integer>>();
		for (NormalScoringCategory scoringCategory : GEUtil.NormalScoringCategory.values())
			normalDistances.put(scoringCategory, new ArrayList<Integer>());
		
		Map<ReverseScoringCategory, List<Integer>> reverseDistances = new HashMap<ReverseScoringCategory, List<Integer>>();
		for (ReverseScoringCategory scoringCategory : ReverseScoringCategory.values())
			reverseDistances.put(scoringCategory, new ArrayList<Integer>());
		
		Board board = gameState.getBoard();
		
		Set<Position> searched = new HashSet<Position>();
		Set<Position> currentToSearch = new HashSet<Position>();
		Set<Position> nextToSearch = new HashSet<Position>();
		
		currentToSearch.add(startingPosition);
		
		int depth = 0;
		
		while (++depth < maxDepth)
		{
			for (Position currentPosition : currentToSearch)
			{
				boolean survivable = GEUtil.isSurvivablePosition(currentPosition, gameState, m_currentRound, depth);
				recordSquareDistances(normalDistances, reverseDistances, gameState, currentPosition, survivable, depth);
				
				if (survivable)
				{
					for (Direction neighborDirection : Direction.values())
					{
						Position neighbor = neighborDirection.calculateNextPosition(currentPosition);
						
						boolean validPosition = GEUtil.isValidPosition(board, neighbor);
						if (validPosition && !searched.contains(neighbor))
							nextToSearch.add(neighbor);
					}
				}

				searched.add(currentPosition);
			}
			
			if (nextToSearch.isEmpty())
				break;

			currentToSearch = nextToSearch;
			nextToSearch = new HashSet<Position>();
		}
		
		m_visiblePositions.put(direction, searched);
		m_nextToSearch.put(direction, new HashSet<Position>(currentToSearch));
		
		List<List<Integer>> finalNormalDistances = new ArrayList<List<Integer>>();
		for (NormalScoringCategory scoringCategory : NormalScoringCategory.values())
		{
			finalNormalDistances.add(normalDistances.get(scoringCategory));
		}
		
		List<List<Integer>> finalReverseDistances = new ArrayList<List<Integer>>();
		for (ReverseScoringCategory scoringCategory : ReverseScoringCategory.values())
		{
			finalReverseDistances.add(reverseDistances.get(scoringCategory));
		}
		
		return new ScoringDistanceTuple(finalNormalDistances, finalReverseDistances);
	}
	
	private ScoringDistanceTuple searchAllyVisibleSquares(GameState gameState, Direction direction,
			Set<Position> startingPositions, Set<Position> allyVisiblePositions)
	{
		Map<NormalScoringCategory, List<Integer>> normalDistances = new HashMap<NormalScoringCategory, List<Integer>>();
		for (NormalScoringCategory scoringCategory : GEUtil.NormalScoringCategory.values())
			normalDistances.put(scoringCategory, new ArrayList<Integer>());
		
		Map<ReverseScoringCategory, List<Integer>> reverseDistances = new HashMap<ReverseScoringCategory, List<Integer>>();
		for (ReverseScoringCategory scoringCategory : ReverseScoringCategory.values())
			reverseDistances.put(scoringCategory, new ArrayList<Integer>());
	  
		int depth = m_vision;
		Board board = gameState.getBoard();
		
		Set<Position> searched = new HashSet<Position>(m_visiblePositions.get(direction));
		Set<Position> currentToSearch = new HashSet<Position>(startingPositions);
		Set<Position> nextToSearch = new HashSet<Position>();
		
		Set<Position> remainingAllyVisiblePositions = allyVisiblePositions;
		
		while (!remainingAllyVisiblePositions.isEmpty() && !currentToSearch.isEmpty())
		{
			++depth;
	    
			for (Position currentPosition : currentToSearch)
			{
				boolean survivable = false;
				
				boolean visibleToAlly = allyVisiblePositions.contains(currentPosition);
				if (visibleToAlly)
				{
					survivable = GEUtil.isSurvivablePosition(currentPosition, gameState, m_currentRound, depth);
					recordSquareDistances(normalDistances, reverseDistances, gameState, currentPosition, survivable, depth);
				}
				
				if (!visibleToAlly || survivable)
				{
					for (Direction neighborDirection : Direction.values())
					{
						Position neighbor = neighborDirection.calculateNextPosition(currentPosition);
						
						boolean validPosition = GEUtil.isValidPosition(board, neighbor);
						
						if (validPosition && !searched.contains(neighbor))
							nextToSearch.add(neighbor);
					}
				}

				searched.add(currentPosition);
				remainingAllyVisiblePositions.remove(currentPosition);
			}

			currentToSearch = nextToSearch;
			nextToSearch = new HashSet<Position>();
		}
		
		List<List<Integer>> finalNormalDistances = new ArrayList<List<Integer>>();
		for (NormalScoringCategory scoringCategory : NormalScoringCategory.values())
		{
			finalNormalDistances.add(normalDistances.get(scoringCategory));
		}
		
		List<List<Integer>> finalReverseDistances = new ArrayList<List<Integer>>();
		for (ReverseScoringCategory scoringCategory : ReverseScoringCategory.values())
		{
			finalReverseDistances.add(reverseDistances.get(scoringCategory));
		}
		
		return new ScoringDistanceTuple(finalNormalDistances, finalReverseDistances);
	}
	
	private boolean m_hasSearched = false;
	private boolean m_hasSeenAlliesVisibleSquares = false;
	
	public synchronized boolean hasSearchedVisibleSquares()
	{
		return m_hasSearched;
	}
	
	public synchronized boolean hasSeenAlliesVisibleSquares()
	{
		return m_hasSeenAlliesVisibleSquares;
	}
	
	public Set<Position> getVisiblePositions()
	{
		Set<Position> allVisiblePositions = new HashSet<Position>();
		
		for (Set<Position> directionalPositions : m_visiblePositions.values())
			allVisiblePositions.addAll(directionalPositions);
		
		return allVisiblePositions;
	}
	
	int m_currentRound = 0;
	
	@Override
	public Direction getNextMove(Snake snake, GameState gameState)
	{
		++m_currentRound;
		
		m_hasSeenAlliesVisibleSquares = false;
		
		Map<Direction, Double> scoredDirections = new TreeMap<Direction, Double>();
		List<Direction> directions = GEUtil.getTurnableDirections(snake.getCurrentDirection());
		for (Direction direction : directions)
		{
			Position startingPosition = direction.calculateNextPosition(snake.getHeadPosition());
			ScoringDistanceTuple visibleDistances = searchVisibleSquares(gameState, direction, startingPosition, m_vision);
			
			double score = m_visibleSquaresScoring.getTotalScore(visibleDistances);
			scoredDirections.put(direction, score);
		}
		
		m_hasSearched = true;
		
		// Remove the brains of dead snakes from m_livingAllyBrains
		List<GEBrain> deadAllyBrains = new ArrayList<GEBrain>();
		for (GEBrain brain : m_livingAllyBrains)
		{
			if (brain.getSnake().isDead())
				deadAllyBrains.add(brain);
		}
		m_livingAllyBrains.removeAll(deadAllyBrains);
		
		Set<Position> allyVisiblePositions = new HashSet<Position>();
		
		for (GEBrain ally : m_livingAllyBrains)
		{
			while (!ally.hasSearchedVisibleSquares())
			{
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			allyVisiblePositions.addAll(ally.getVisiblePositions());
		}
		
		m_hasSeenAlliesVisibleSquares = true;
		
		for (Direction direction : directions)
		{
			ScoringDistanceTuple allyVisibleDistances = searchAllyVisibleSquares(gameState, direction, m_nextToSearch.get(direction), allyVisiblePositions);
			
			double score = m_visibleSquaresScoring.getTotalScore(allyVisibleDistances);
			scoredDirections.put(direction, scoredDirections.get(direction) + score);
		}
		
		Direction bestDirection = null;
		double bestScore = Double.MIN_VALUE;
		for (Map.Entry<Direction, Double> entry : scoredDirections.entrySet())
		{
			if (entry.getValue() > bestScore)
			{
				bestScore = entry.getValue();
				bestDirection = entry.getKey();
			}
		}
		
		for (GEBrain ally : m_livingAllyBrains)
		{
			while (!ally.hasSeenAlliesVisibleSquares())
			{
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		// Cleanup for next tick:
		m_hasSearched = false;
		m_visiblePositions.clear();
		m_nextToSearch.clear();
		
		return bestDirection != null ? bestDirection : snake.getCurrentDirection();
	}
	
}
