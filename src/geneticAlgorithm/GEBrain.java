package geneticAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import gameLogic.Board;
import gameLogic.Brain;
import gameLogic.Direction;
import gameLogic.GameState;
import gameLogic.Position;
import gameLogic.Snake;
import gameLogic.Square;

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
	
	Set<Position> m_visiblePositions = new HashSet<Position>();
	Map<Direction, Set<Position>> m_nextToSearch = new HashMap<Direction, Set<Position>>();
		
	ScoringDistanceTuple search(GameState gameState, Direction direction,
			Set<Position> startingPositions, Set<Position> allyVisiblePositions, int maxDepth)
	{
		//hej thomas :D
		//hej daniel :P
		List<List<Integer>> distanceLists = new LinkedList<List<Integer>>();
		List<Integer> fruitDistances = new LinkedList<Integer>();
		List<Integer> wallDistances = new LinkedList<Integer>();
		List<Integer> enemyHeadDistances = new LinkedList<Integer>();
		List<Integer> enemyBodyDistances = new LinkedList<Integer>();
		List<Integer> enemyTailDistances = new LinkedList<Integer>();
		List<Integer> allyHeadDistances = new LinkedList<Integer>();
		List<Integer> allyBodyDistances = new LinkedList<Integer>();
		List<Integer> allyTailDistances = new LinkedList<Integer>();
		List<Integer> openSquareDistances = new LinkedList<Integer>();
		distanceLists.add(fruitDistances);
		distanceLists.add(wallDistances);
		distanceLists.add(enemyHeadDistances);
		distanceLists.add(enemyBodyDistances);
		distanceLists.add(enemyTailDistances);
		distanceLists.add(allyHeadDistances);
		distanceLists.add(allyBodyDistances);
		distanceLists.add(allyTailDistances);
		distanceLists.add(openSquareDistances);
	  
		int depth = 0;
		
		boolean searchOnlyVisibleSquares = (allyVisiblePositions == null);
	  
		Set<Position> searched = new HashSet<Position>();
		Queue<Position> currentToSearch = new LinkedList<Position>(startingPositions);
		Queue<Position> nextToSearch = new LinkedList<Position>();
	  
		while(!currentToSearch.isEmpty() && depth < maxDepth)
		{
			++depth;
			
			Board board = gameState.getBoard();
	   
			while(!currentToSearch.isEmpty())
			{
				Position currentPosition = currentToSearch.remove();
				Square currentSquare = board.getSquare(currentPosition);
				
				boolean survivable = false;
				boolean visibleToAlly = (!searchOnlyVisibleSquares && allyVisiblePositions.contains(currentPosition));
				
				// Only record distances for squares visible to either the this snake or one of its allies
				if (searchOnlyVisibleSquares || visibleToAlly)
				{
					survivable = GEUtil.isSurvivablePosition(currentPosition, gameState, m_currentRound + depth);
					
					if (survivable)
					{
						// No need to check for an open square, we already know it is
						openSquareDistances.add(depth);
	
						//Check for fruit
						if (currentSquare.hasFruit())
							fruitDistances.add(depth);
						
					}
					else
					{
						//Check for walls
						if (currentSquare.hasWall())
							wallDistances.add(depth);
						
						Set<Snake> otherSnakes = gameState.getSnakes();
						otherSnakes.remove(m_thisSnake);
						
						//Check for snakes
						if (currentSquare.hasSnake())
						{
							
							for (Snake otherSnake : otherSnakes)
							{
								//If otherSnake is an enemy
								if (!m_allySnakes.contains(otherSnake))
								{
									//Check for enemy snake heads and if it's alive
									if (otherSnake.getHeadPosition().equals(currentPosition) && !otherSnake.isDead())
										enemyHeadDistances.add(depth);
									//Check for enemy tails and if it's alive
									else if (otherSnake.getTailPosition().equals(currentPosition) && !otherSnake.isDead())
										enemyTailDistances.add(depth);
									//All other cases counts as enemy body
									else
										enemyBodyDistances.add(depth);
									
									
								} 
								
								//If otherSnake is an ally
								if (m_allySnakes.contains(otherSnake))
								{
									//Check for ally snake heads and if it's alive
									if (otherSnake.getHeadPosition().equals(currentPosition) && !otherSnake.isDead())
										allyHeadDistances.add(depth);
									//Check for ally tails if it's alive
									else if (otherSnake.getTailPosition().equals(currentPosition) && !otherSnake.isDead())
										allyTailDistances.add(depth);
									//All other cases counts as ally body
									else
										allyBodyDistances.add(depth);
									
								}
							}
						}
					}
				}
				
				// Only add neighbors to nextToSearch only if it is survivable while 
				// visible to this snake or one of its allies,
				// or if it is not visible to any snake on this team.
				if (((searchOnlyVisibleSquares || visibleToAlly) && survivable) || !searchOnlyVisibleSquares)
				{
					for (Direction neighborDirection : Direction.values())
					{
						Position neighbor = neighborDirection.calculateNextPosition(currentPosition);
						
						int x = neighbor.getX();
						int y = neighbor.getY();
						boolean validPosition = ((x >= 0 && x < board.getWidth()) &&
												 (y >= 0 && y < board.getHeight()));
						
						if (validPosition && !searched.contains(neighbor))
							nextToSearch.add(neighbor);
					}
				}

				searched.add(currentPosition); 
			}

			currentToSearch = nextToSearch;
			nextToSearch = new LinkedList<Position>();
		}
		
		// If we are only searching our own visible positions,
		// populate m_visiblePositions and m_nextToSearch
		if (searchOnlyVisibleSquares)
		{
			m_visiblePositions.addAll(searched);
			m_nextToSearch.put(direction, new HashSet<Position>(currentToSearch));
		}
		
		return new ScoringDistanceTuple(distanceLists);
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
		return m_visiblePositions;
	}
	
	int m_currentRound = 0;
	
	@Override
	public Direction getNextMove(Snake snake, GameState gameState)
	{
		++m_currentRound;
		
		m_hasSeenAlliesVisibleSquares = false;
		
		Map<Direction, Double> scoredDirections = new TreeMap<Direction, Double>();
		List<Direction> directions = GEUtil.getSurvivableDirections(snake, gameState, m_currentRound);
		for (Direction direction : directions)
		{
			Set<Position> startingPositions = new HashSet<Position>();
			startingPositions.add(direction.calculateNextPosition(snake.getHeadPosition()));
			ScoringDistanceTuple visibleDistances = search(gameState, direction, startingPositions, null, m_vision);
			
			double score = m_visibleSquaresScoring.getTotalScore(visibleDistances);
			scoredDirections.put(direction, score);
		}
		
		m_hasSearched = true;
		
		Set<Position> allyVisiblePositions = new HashSet<Position>();
		
		// Remove the brains of dead snakes from m_livingAllyBrains
		List<GEBrain> deadAllyBrains = new ArrayList<GEBrain>();
		for (GEBrain brain : m_livingAllyBrains)
		{
			if (brain.getSnake().isDead())
				deadAllyBrains.add(brain);
		}
		m_livingAllyBrains.removeAll(deadAllyBrains);
		
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
			int tooBigDepth = gameState.getBoard().getHeight() * gameState.getBoard().getWidth() + 1;
			ScoringDistanceTuple allyVisibleDistances = search(gameState, direction, m_nextToSearch.get(direction), allyVisiblePositions, tooBigDepth);
			
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
