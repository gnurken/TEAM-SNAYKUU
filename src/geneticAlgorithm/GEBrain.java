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
 * @author Thomas Zeb�hr, Daniel Malmqvist
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
	
	Map<Direction, Set<Position>> m_visiblePositions = new HashMap<Direction, Set<Position>>();
	Map<Direction, Set<Position>> m_nextToSearch = new HashMap<Direction, Set<Position>>();
		
	ScoringDistanceTuple search(GameState gameState, Direction direction,
			Set<Position> startingPositions, Set<Position> allyVisiblePositions, int maxDepth)
	{
		Map<String, List<Integer>> distances = new HashMap<String, List<Integer>>();
		
		for (String scoringCategory : GEUtil.allScoringCategories)
			distances.put(scoringCategory, new ArrayList<Integer>());
	  
		int depth = 0;
		
		boolean searchOnlyVisibleSquares = (allyVisiblePositions == null);
		
		Set<Position> searched = new HashSet<Position>();
		Queue<Position> currentToSearch = new LinkedList<Position>(startingPositions);
		Queue<Position> nextToSearch = new LinkedList<Position>();
		
		Set<Position> remainingAllyVisiblePositions = new HashSet<Position>();
		
		if (!searchOnlyVisibleSquares)
		{
			// Add all searched squares from the first search
			// to the set of searched positions, to avoid duplicate searching.
			searched.addAll(m_visiblePositions.get(direction));
			
			remainingAllyVisiblePositions.addAll(allyVisiblePositions);
			remainingAllyVisiblePositions.removeAll(searched);
		}
		
		while(!currentToSearch.isEmpty() && depth < maxDepth)
		{
			++depth;
			
			Board board = gameState.getBoard();
	   
			while(!currentToSearch.isEmpty() &&
					(searchOnlyVisibleSquares ||
					(!searchOnlyVisibleSquares && !remainingAllyVisiblePositions.isEmpty())))
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
						distances.get("openSquare").add(depth);
	
						//Check for fruit
						if (currentSquare.hasFruit())
							distances.get("fruit").add(depth);
						
					}
					else
					{
						//Check for walls
						if (currentSquare.hasWall())
							distances.get("wall").add(depth);
						
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
										distances.get("enemyHead").add(depth);
									//Check for enemy tails and if it's alive
									else if (otherSnake.getTailPosition().equals(currentPosition) && !otherSnake.isDead())
										distances.get("enemyTail").add(depth);
									//All other cases counts as enemy body
									else
										distances.get("enemyBody").add(depth);
									
									
								} 
								
								//If otherSnake is an ally
								if (m_allySnakes.contains(otherSnake))
								{
									//Check for ally snake heads and if it's alive
									if (otherSnake.getHeadPosition().equals(currentPosition) && !otherSnake.isDead())
										distances.get("allyHead").add(depth);
									//Check for ally tails if it's alive
									else if (otherSnake.getTailPosition().equals(currentPosition) && !otherSnake.isDead())
										distances.get("allyTail").add(depth);
									//All other cases counts as ally body
									else
										distances.get("allyBody").add(depth);
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
				remainingAllyVisiblePositions.remove(currentPosition);
			}

			currentToSearch = nextToSearch;
			nextToSearch = new LinkedList<Position>();
		}
		
		// If we are only searching our own visible positions,
		// populate m_visiblePositions and m_nextToSearch
		if (searchOnlyVisibleSquares)
		{
			m_visiblePositions.put(direction, searched);
			m_nextToSearch.put(direction, new HashSet<Position>(currentToSearch));
		}
		
		List<List<Integer>> finalDistances = new ArrayList<List<Integer>>();
		
		for (String scoringCategory : GEUtil.allScoringCategories)
		{
			finalDistances.add(distances.get(scoringCategory));
		}
		
		return new ScoringDistanceTuple(finalDistances);
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
