package gameLogic;


import java.util.*;

public class Snake
{
	private String name;
	private LinkedList<SnakeSegment> segments = new LinkedList<SnakeSegment>();
	private Brain brain;
	private Direction direction;
	private boolean grow = false;
	
	public Snake(String name, Brain brain, Position position)
	{
		this.name = name;
		this.brain = brain;
		this.direction = new Direction(Direction.NORTH);
		this.segments.add(new SnakeSegment(position, null));
	}
	
	public SnakeSegment getHead()
	{
		return segments.get(0);
	}
	
	public SnakeSegment getTail()
	{
		return segments.getLast();
	}
	
	public LinkedList<SnakeSegment> getSegments()
	{
		return segments;
	}
	
	/**
	 * NOTE: Currently not used nor working. Functionality has instead been moved to moveSnake() in Session. 
	 */
	void move(GameState currentGameState)
	{
		direction = brain.getNextMove(currentGameState);
		SnakeSegment head = getHead();
		
		Position newHeadPosition = direction.calculateNextPosition(head.getPosition());
		//~ segments.addFirst(new SnakeSegment(session.getBoard(), newHeadPosition, head));
		
		if (grow)
		{
			grow = false;
			return;
		}
		
		SnakeSegment tail = getTail();
		segments.removeLast();
	}
	
	Direction getNextMove(GameState currentGameState)
	{
		return brain.getNextMove(currentGameState);
	}
	
	void updatePosition(LinkedList<SnakeSegment> newSegments)
	{
		segments = newSegments;
	}
	
	void growOneUnitOfLengthNextTimeThisSnakeMoves()
	{
		grow = true;
	}
	
	Brain getBrain()
	{
		return brain;
	}
	
	void tooSlowFault()
	{
		brain.tooSlowFault();
	}
	
	public String toString()
	{
		return name;
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof Snake) {
			Snake otherSnake = (Snake)other;
			return (name.equals(otherSnake.name));
		}
		return false;
	}
}
