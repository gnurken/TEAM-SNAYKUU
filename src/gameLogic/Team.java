package gameLogic;

import java.util.Set;
import java.util.TreeSet;


public class Team {

	private Set<Snake> snakes = new TreeSet<Snake>();
	private String name;
	
	public Team(String n)
	{
		name = n;
	}
	
	public Set<Snake> getSnakes()
	{
		return snakes;
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
