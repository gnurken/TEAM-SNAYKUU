package gameLogic;

import userInterface.GraphicsTile;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;


public class Team implements Serializable
{

	private Set<Snake> snakes = new HashSet<Snake>();
	private String name;
	private int nr;
	
	private GraphicsTile teamNumberTile = GraphicsTile.UNKNOWN_TEAM;
	
	public Team(String n, int nr)
	{
		name = n;
		this.nr = nr;
		
		if (nr == 1)
			teamNumberTile = GraphicsTile.TEAM_1;
		else if (nr == 2)
			teamNumberTile = GraphicsTile.TEAM_2;
	}
	
	public Team(Team other)
	{
		name = other.name;
		nr = other.nr;
		
		for (Snake snake : other.getSnakes())
		{
			snakes.add(snake);
		}
		
		teamNumberTile = other.teamNumberTile;
	}
	
	public Set<Snake> getSnakes()
	{
		return new HashSet<Snake>(snakes);
	}
	
	public int getSize()
	{
		return snakes.size();
	}
	
	public boolean contains(Snake snake)
	{
		return snakes.contains(snake);
	}
	
	public void addSnake(Snake newSnake)
	{
		snakes.add(newSnake);
	}
	
	public boolean allDead()
	{
		for (Snake snake : snakes)
		{
			if (!snake.isDead())
				return false;
		}
		
		return true;
	}
	
	public int getScore()
	{
		int score = 0;
		for (Snake snake : snakes)
		{
			score += snake.getScore();
		}
		return score;
	}
	
	public int getLifespan()
	{
		int lifespan = 0;
		
		for (Snake snake : snakes)
		{
			lifespan = Math.max(lifespan, snake.getLifespan());
		}
		
		return lifespan;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getNumber()
	{
		return nr;
	}
	
	public String toString()
	{
		return getName();
	}
	
	public GraphicsTile getTeamNumberTile()
	{
		return teamNumberTile;
	}
	
}
