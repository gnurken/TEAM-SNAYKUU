package userInterface;

import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static java.awt.GridBagConstraints.*;
import gameLogic.*;

public class ScoreBoardPanel extends JPanel
{
	private GridBagLayout gbl = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	private Insets insets = new Insets(2,4,2,4);
	
	public ScoreBoardPanel(Game game)
	{
		setLayout(gbl);
		updateScore(game.getGameResult());
	}
	
	public void updateScore(GameResult gameResult)
	{
		this.removeAll();
		
		List<Team> teams = gameResult.getTeams();
		int placedSnakes = 0;
		
		printTeams(placedSnakes++, teams);
		printLegend(placedSnakes++);
		
		int i = 1;
		
		for(Team t : teams)
		{
			List<Snake> snakes = new ArrayList<Snake>(t.getSnakes());
			Collections.sort(snakes, new Snake.ScoreComparator());
			for(Snake s : snakes)
			{
				placeRow(placedSnakes++, i, s, t);
			}
			++i;
		}
		
		setPreferredSize(this.getPreferredSize());
		validate();
	}
	
	private void placeRow(int gridy, int p, Snake s, Team t)
	{
		c.anchor = NORTHWEST;
		c.fill = HORIZONTAL;
		c.weighty = 0.0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = insets;
		
		c.gridx = 0;
		c.weightx = 0.0;
		JLabel place = new JLabel(""+p);
		place.setPreferredSize(place.getPreferredSize());
		gbl.setConstraints(place, c);
		add(place);
		
		c.gridx = 1;
		c.weightx = 0.0;
		JLabel color = new JLabel("   ");
		color.setPreferredSize(color.getPreferredSize());
		color.setOpaque(true);
		color.setBackground(s.getColor());
		color.setText("   " + t.getNumber());
		gbl.setConstraints(color, c);
		add(color);
		
		c.gridx = 2;
		c.weightx = 10.0;
		JLabel snake = new JLabel(s.getName());
		
		if(s.isDead())
		{
			snake.setForeground(new Color(0xD00000));
		}
		
		snake.setPreferredSize(snake.getPreferredSize());
		gbl.setConstraints(snake, c);
		add(snake);
		
		c.gridx = 3;
		c.weightx = 0.0;
		JLabel score = new JLabel(""+s.getScore());
		score.setPreferredSize(score.getPreferredSize());
		gbl.setConstraints(score, c);
		add(score);
		
		c.gridx = 4;
		c.weightx = 0.0;
		JLabel age = new JLabel(""+s.getLifespan());
		age.setPreferredSize(age.getPreferredSize());
		gbl.setConstraints(age, c);
		add(age);
	}
	
	private void printLegend(int gridy)
	{
		c.anchor = NORTHWEST;
		c.fill = HORIZONTAL;
		c.weighty = 0.0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = insets;
		
		c.gridx = 0;
		c.weightx = 0.0;
		JLabel place = new JLabel("Place");
		place.setPreferredSize(place.getPreferredSize());
		gbl.setConstraints(place, c);
		add(place);
		
		c.gridx = 1;
		c.weightx = 0.0;
		JLabel color = new JLabel("Color");
		color.setPreferredSize(color.getPreferredSize());
		gbl.setConstraints(color, c);
		add(color);
		
		c.gridx = 2;
		c.weightx = 10.0;
		JLabel snake = new JLabel("Name");
		snake.setPreferredSize(snake.getPreferredSize());
		gbl.setConstraints(snake, c);
		add(snake);
		
		c.gridx = 3;
		c.weightx = 0.0;
		JLabel score = new JLabel("Score");
		score.setPreferredSize(score.getPreferredSize());
		gbl.setConstraints(score, c);
		add(score);
		
		c.gridx = 4;
		c.weightx = 0.0;
		JLabel age = new JLabel("Age");
		age.setPreferredSize(age.getPreferredSize());
		gbl.setConstraints(age, c);
		add(age);
	}
	
	void printTeams(int gridy, List<Team> t)
	{
		Team team1, team2;

		if (t.get(0).getNumber() < t.get(1).getNumber())
		{
			team1 = t.get(0);
			team2 = t.get(1);
		}
		else 
		{
			team1 = t.get(1);
			team2 = t.get(0);
		}
		
		c.anchor = NORTHWEST;
		c.fill = HORIZONTAL;
		c.weighty = 0.0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = insets;
		
		c.gridx = 0;
		c.weightx = 0.0;
		JLabel team1label = new JLabel(team1.getName() + ":");
		team1label.setPreferredSize(team1label.getPreferredSize());
		gbl.setConstraints(team1label, c);
		add(team1label);
		
		c.gridx = 1;
		c.weightx = 0.0;
		JLabel team1score = new JLabel("" + team1.getScore());
		team1score.setPreferredSize(team1score.getPreferredSize());
		gbl.setConstraints(team1score, c);
		add(team1score);
		
		c.gridx = 2;
		c.weightx = 0.0;
		JLabel team2label = new JLabel(team2.getName() + ":");
		team2label.setPreferredSize(team2label.getPreferredSize());
		gbl.setConstraints(team2label, c);
		add(team2label);
	
		c.gridx = 3;
		c.weightx = 0.0;
		JLabel team2score = new JLabel("" + team2.getScore());
		team2score.setPreferredSize(team2score.getPreferredSize());
		gbl.setConstraints(team2score, c);
		add(team2score);
	}
}
