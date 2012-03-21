package gameLogic;

import java.util.Set;
import java.util.HashSet;


public class Team {

	private Set<Snake> snakes = new HashSet<Snake>();
	private String name;
	
	public Team(String n)
	{
		name = n;
	}
	
	public Team(Team other)
	{
		name = other.name;
		
		for (Snake snake : other.getSnakes())
		{
			snakes.add(snake);
		}
	}
	
	public Set<Snake> getSnakes()
	{
		return new HashSet<Snake>(snakes);
	}
	
	public int getSize()
	{
		return snakes.size();
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
	
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		return getName();
	}
	
}
