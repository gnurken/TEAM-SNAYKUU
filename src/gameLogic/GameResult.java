package gameLogic;

import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class GameResult
{
	private Set<Team> teams;
	private Metadata metadata;
	private RecordedGame recordedGame;
	
	public GameResult(Set<Team> teams, Metadata metadata, RecordedGame recordedGame)
	{
		this.metadata = metadata;		
		this.teams = teams;
		this.recordedGame = recordedGame;
	}
	
	public RecordedGame getRecordedGame()
	{
		return recordedGame;
	}
	
	public List<Team> getTeams()
	{
		List<Team> result = new ArrayList<Team>(teams);
		Collections.sort(result, new TeamComparator());
		return result;
	}
	/*
	public String toString()
	{
		List<List<Snake>> winners = getWinners();
		
		int placement = 1;
		String retVal = "";
		
		for (List<Snake> snakeList : winners)
		{
			retVal += (placement + ":");
			for (Snake snake : snakeList)
			{
				retVal += ("\t" + snake + " (" + snake.getScore() + ", " + snake.getLifespan() + ")\n");
				++placement;
			}
		}
		
		return retVal;
	}
	*/
	
	private static class TeamComparator implements Comparator<Team>
	{
		public int compare(Team first, Team second)
		{
			int comparedLifespan = second.getLifespan() - first.getLifespan();
			if(comparedLifespan != 0)
				return comparedLifespan;
			return second.getScore() - first.getScore();
		}
	}
}
