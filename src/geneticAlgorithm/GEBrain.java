package geneticAlgorithm;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import gameLogic.Brain;
import gameLogic.Direction;
import gameLogic.GameState;
import gameLogic.Snake;
import geneticAlgorithm.GEUtil.ScoredDirection;
import geneticAlgorithm.GEUtil.ScoringDistanceTuple;
import geneticAlgorithm.GEUtil.ScoringPairTuple;

/**
 * 
 * @author Thomas Zebühr
 * 
 * A Snaykuu brain based on RodeOrmII, the winning snake of DSV:s Snaykuu championships of 2012,
 * to be used together with an genetic algorithm to hopefully generate a team player snake.
 *
 */

public class GEBrain implements Brain
{
	private ScoringPairTuple m_visibleSquaresScoring, m_allyVisibleSquaresScoring;
	
	public ScoringPairTuple getVisibleSquaresScoring()
	{
		return m_visibleSquaresScoring;
	}
	
	public ScoringPairTuple getAllyVisibleSquaresScoring()
	{
		return m_allyVisibleSquaresScoring;
	}
	
	public GEBrain(ScoringPairTuple visibleSquaresScoring,
	               ScoringPairTuple allyVisibleSquaresScoring)
	{
		m_visibleSquaresScoring = visibleSquaresScoring;
		m_allyVisibleSquaresScoring = allyVisibleSquaresScoring;
	}
	
	private List<GEBrain> m_allies;
	
	public void setAllies(List<GEBrain> allies)
	{
		m_allies = allies;
	}
	
	private Map<Direction, ScoringDistanceTuple> m_scoringDistances;
	private Map<Direction, ScoringDistanceTuple> m_allyScoringDistances;
	
	void search()
	{
		// TODO: Populate m_scoringDistances & m_allyScoringDistances
	}
	
	double evaluateDirection(Snake snake, Direction direction)
	{
		double score = 0.0;
		
		score += m_visibleSquaresScoring.getTotalScore(m_scoringDistances.get(direction));
		score += m_allyVisibleSquaresScoring.getTotalScore(m_allyScoringDistances.get(direction));
		
		return score;
	}
	
	int m_currentRound = 0;
	
	@Override
	public Direction getNextMove(Snake snake, GameState gameState)
	{
		++m_currentRound;
		
		search();
		
		TreeSet<ScoredDirection> scoredDirections = new TreeSet<ScoredDirection>();
		List<Direction> directions = GEUtil.getSurvivableDirections(snake, gameState, m_currentRound);
		for (int i = 0; i < directions.size(); ++i)
		{
			Direction direction = directions.get(i);
			scoredDirections.add(new ScoredDirection(direction, evaluateDirection(snake, direction)));
		}
		
		ScoredDirection bestDirection = scoredDirections.pollFirst();
		return bestDirection != null ? bestDirection.direction : snake.getCurrentDirection();
	}
	
}
