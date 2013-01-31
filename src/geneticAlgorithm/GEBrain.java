package geneticAlgorithm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import gameLogic.Brain;
import gameLogic.Direction;
import gameLogic.GameState;
import gameLogic.Position;
import gameLogic.Snake;
import gameLogic.Square;
import geneticAlgorithm.GEUtil.ScoredDirection;
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
	
	private Set<GEBrain> m_allyBrains;
	private Set<Snake> m_allySnakes;
	
	public void setAllies(Set<GEBrain> allyBrains, Set<Snake> allySnakes)
	{
		m_allyBrains = allyBrains;
		m_allySnakes = allySnakes;
	}
		
	ScoringDistanceTuple search(GameState gameState, Position startPosition)
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
	  
		Set<Position> searched = new HashSet<Position>();
		Queue<Position> currentToSearch = new LinkedList<Position>();
		Queue<Position> nextToSearch = new LinkedList<Position>();
		  
		currentToSearch.add(startPosition);
	  
		while(!currentToSearch.isEmpty() && depth < m_vision)
		{
			++depth;
	   
			while(!currentToSearch.isEmpty())
			{
				Position currentPosition = currentToSearch.remove();
				Square currentSquare = gameState.getBoard().getSquare(currentPosition);
				    
				openSquareDistances.add(depth);
				    
				//Check for fruit
				if (currentSquare.hasFruit())
					fruitDistances.add(depth);
				
				//Check neighbours
				for (Direction direction : Direction.values())
				{
					Position neighbor = direction.calculateNextPosition(currentPosition);
					if (searched.contains(neighbor))
						break;
					
					if (GEUtil.isSurvivable(currentPosition, direction, gameState, m_currentRound + depth))
						nextToSearch.add(neighbor);
					else
					{
						//Check for walls
						if (currentSquare.hasWall())
							wallDistances.add(depth);
						
						Set<Snake> otherSnakes = gameState.getSnakes();
						otherSnakes.remove(m_thisSnake);
						
						for (Snake otherSnake : otherSnakes)
						{
							//If otherSnake is an enemy
							if (!m_allySnakes.contains(otherSnake))
							{
								//Check for enemy snake heads and if it's alive, else it will count as a body
								if (otherSnake.getHeadPosition().equals(currentPosition) && !otherSnake.isDead())
									enemyHeadDistances.add(depth);
								else if (otherSnake.getHeadPosition().equals(currentPosition) && otherSnake.isDead())
									enemyBodyDistances.add(depth);
								
								//Check for enemy tails
								if (otherSnake.getTailPosition().equals(currentPosition) && !otherSnake.isDead())
									enemyTailDistances.add(depth);
								else if (otherSnake.getTailPosition().equals(currentPosition) && otherSnake.isDead())
									enemyBodyDistances.add(depth);
								
								//Check for enemy body position but not the head or tail
								for (Position enemyBodyPosition : otherSnake.getSegments())
								{
									if (enemyBodyPosition.equals(currentPosition) && !enemyBodyPosition.equals(otherSnake.getTailPosition()) && !enemyBodyPosition.equals(otherSnake.getHeadPosition()))
										enemyBodyDistances.add(depth);
								}
								
							} 
							
							//If otherSnake is an ally
							if (m_allySnakes.contains(otherSnake))
							{
								//Check for ally snake heads and if it's alive, else it will count as a body
								if (otherSnake.getHeadPosition().equals(currentPosition) && !otherSnake.isDead())
									allyHeadDistances.add(depth);
								else if (otherSnake.getHeadPosition().equals(currentPosition) && otherSnake.isDead())
									allyBodyDistances.add(depth);
								
								//Check for ally tails if it's alive, else it will count as a body
								if (otherSnake.getTailPosition().equals(currentPosition) && !otherSnake.isDead())
									allyTailDistances.add(depth);
								else if (otherSnake.getTailPosition().equals(currentPosition) && otherSnake.isDead())
									allyBodyDistances.add(depth);
								
								//Check for ally body position but not the head or tail
								for (Position allyBodyPosition : otherSnake.getSegments())
								{
									if (allyBodyPosition.equals(currentPosition) && !allyBodyPosition.equals(otherSnake.getTailPosition()) && !allyBodyPosition.equals(otherSnake.getHeadPosition()))
										allyBodyDistances.add(depth);
								}
							}
						}
						
						searched.add(neighbor);
					}
				}

				searched.add(currentPosition); 
			}

			currentToSearch = nextToSearch;
			nextToSearch = new LinkedList<Position>();
		}
		
		return new ScoringDistanceTuple(distanceLists);
	}
	
	double evaluateDirection(Snake snake, Direction direction, GameState gameState)
	{
		double score = 0.0;
		
		ScoringDistanceTuple scoringDistances = search(gameState, direction.calculateNextPosition(snake.getHeadPosition()));
		score += m_visibleSquaresScoring.getTotalScore(scoringDistances);
		
		//TODO: Evaluate the squares visible to allies:
		//score += m_allyVisibleSquaresScoring.getTotalScore(m_allyScoringDistances.get(direction));
		
		return score;
	}
	
	int m_currentRound = 0;
	
	@Override
	public Direction getNextMove(Snake snake, GameState gameState)
	{
		++m_currentRound;
		
		TreeSet<ScoredDirection> scoredDirections = new TreeSet<ScoredDirection>();
		List<Direction> directions = GEUtil.getSurvivableDirections(snake, gameState, m_currentRound);
		for (int i = 0; i < directions.size(); ++i)
		{
			Direction direction = directions.get(i);
			scoredDirections.add(new ScoredDirection(direction, evaluateDirection(snake, direction, gameState)));
		}
		
		ScoredDirection bestDirection = scoredDirections.pollFirst();
		return bestDirection != null ? bestDirection.direction : snake.getCurrentDirection();
	}
	
}
