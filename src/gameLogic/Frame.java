package gameLogic;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

public class Frame implements Serializable
{
	private Board board;
	private Set<Team> teams = new HashSet<Team>();
	
	public Frame(Board board, Set<Team> teams)
	{
		this.board = new Board(board);

		for (Team team : teams)
			this.teams.add(new Team(team));
	}
	
	public Board getBoard()
	{
		return board;
	} 
	
	public Set<Team> getTeams()
	{
		return teams;
	}
	
	public Set<Snake> getSnakes()
	{
		Set<Snake> snakes = new HashSet<Snake>();
		
		for (Team team : teams)
		{
			snakes.addAll(team.getSnakes());
		}
		
		return snakes;
	}
}
