package userInterface;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;

import gameLogic.*;

class DeveloperPanel extends JPanel
{
	private SettingsWindow settingsWindow;
	private JButton statsButton;
	private JTextField numberOfRuns;
	private JTextArea output;
	
	DeveloperPanel(SettingsWindow settingsWindow)
	{
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		
		GridBagConstraints constraint = new GridBagConstraints();
		
		this.settingsWindow = settingsWindow;
		
		constraint.fill = NONE;
		constraint.insets = new Insets(4, 4, 4, 4);
		constraint.gridwidth = 1;
		constraint.gridheight = 1;
		constraint.gridx = 0;
		constraint.gridy = 0;
		constraint.weightx = 0.0;
		constraint.weighty = 0.0;
		
			JLabel runLabel = new JLabel("Number of runs:");
			gridbag.setConstraints(runLabel, constraint);
			add(runLabel);
		
		constraint.fill = HORIZONTAL;
		constraint.gridwidth = 1;
		constraint.gridheight = 1;
		constraint.gridx = 1;
		constraint.gridy = 0;
		constraint.weightx = 1.0;
		constraint.weighty = 0.0;
		
			numberOfRuns = new JTextField("50");
			numberOfRuns.setPreferredSize(numberOfRuns.getPreferredSize());
			gridbag.setConstraints(numberOfRuns, constraint);
			add(numberOfRuns);
		
		constraint.fill = NONE;
		constraint.gridwidth = 1;
		constraint.gridheight = 1;
		constraint.gridx = 2;
		constraint.gridy = 0;
		constraint.weightx = 0.0;
		constraint.weighty = 0.0;
		
			statsButton = new JButton("Run test games");
			statsButton.addActionListener
			(
				new ActionListener()
				{ 
					public void actionPerformed(ActionEvent ae) 
					{ 
						playOverNineThousandGames(); 
					} 
				} 
			);
			
			statsButton.setPreferredSize(statsButton.getPreferredSize());
			gridbag.setConstraints(statsButton, constraint);
			add(statsButton);
		
		constraint.fill = NONE;
		constraint.insets = new Insets(4, 4, 4, 4);
		constraint.gridwidth = 1;
		constraint.gridheight = 1;
		constraint.gridx = 0;
		constraint.gridy = 1;
		constraint.weightx = 0.0;
		constraint.weighty = 0.0;
		
			JLabel outputLabel = new JLabel("Output:");
			outputLabel.setHorizontalAlignment(JLabel.LEFT);
			gridbag.setConstraints(outputLabel, constraint);
			add(outputLabel);
		
		constraint.fill = BOTH;
		constraint.gridwidth = 3;
		constraint.gridheight = 1;
		constraint.gridx = 0;
		constraint.gridy = 2;
		constraint.weightx = 1.0;
		constraint.weighty = 1.0;
		
			output = new JTextArea();
			output.setEditable(false);
			JScrollPane jsp1 = new JScrollPane(output);
			jsp1.setPreferredSize(jsp1.getPreferredSize());
			gridbag.setConstraints(jsp1, constraint);
			add(jsp1);
	}
	
	private void println(String s)
	{
		output.append(s+"\n");
		output.setCaretPosition(output.getText().length() - 1);
	}
	
	private void print(String s)
	{
		output.append(s);
		output.setCaretPosition(output.getText().length() - 1);
	}
	
	private void clear()
	{
		output.setText("");
		output.setCaretPosition(0);
	}
	
	private void playOverNineThousandGames()
	{
		statsButton.setEnabled(false);
		GameRunner gr = new GameRunner();
		gr.start();
		statsButton.setEnabled(true);
	}
	
	private class GameRunner extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				clear();
				Session session = settingsWindow.generateSession();
				
				HashMap<String, Results> scores = new HashMap<String, Results>();
				
				for(Team t : session.getTeams())
				{
					scores.put(t.getName(), new Results());
				}
				
				final int numberOfGames = Integer.parseInt(numberOfRuns.getText());
				for (int currentGame = 0; currentGame < numberOfGames; ++currentGame)
				{
					println("Starting game #" + currentGame);
					repaint();
					try
					{
						session = settingsWindow.generateSession();
						
						//TODO fixa med snapshots hära
						while (!session.hasEnded())
							session.tick();
						
						List<Team> result = session.getGameResult().getTeams();
						
						int placement = 0;
						for(Team t : result)
						{
							int score = t.getScore();
							int age = t.getLifespan();
							scores.get(t.getName()).addResult(placement++, score, age);
						}
					}
					catch (Exception e)
					{
						println("Error: " + e);
					}
				}
				
				for(Map.Entry<String, Results> me : scores.entrySet())
				{
					//TODO fixa att den skriver till fil också
					println(me.getKey()+" (place: frequency : avgscore)");
					Results r = me.getValue();
					
					println("\t"+ 1 +": "+r.getFreq(0)+" times : " + r.getAvgScore());
					println("\t"+ 2 +": "+r.getFreq(1)+" times : " + r.getAvgScore());
					
				}
				
				println("DONE");
			}
			catch(Exception e)
			{
				println("Error: " + e);
			}
		}
	}
	
	private class Results
	{
		private int[] placements;
		private ArrayList<Integer> scores = new ArrayList<Integer>();
		private ArrayList<Integer> ages = new ArrayList<Integer>();
		
		public Results()
		{
			placements = new int[2];
			
			placements[0] = 0;
			placements[1] = 0;			
		}
		
		void addResult(int p, int s, int a)
		{
			placements[p] += 1;
			scores.add(s);
			ages.add(a);
		}
		
		int getFreq(int place)
		{
			return placements[place];
		}
		
		List<Integer> getScores()
		{
			return new ArrayList<Integer>(scores);
		}
		
		List<Integer> getAges()
		{
			return new ArrayList<Integer>(ages);
		}
		
		int getAvgScore()
		{
			int sum = 0;
			for(Integer i : scores)
			{
				sum += i;
			}
			return sum/scores.size();
		}
		
		int getAvgAge()
		{
			int sum = 0;
			for (Integer i : scores)
			{
				sum += i;
			}
			return sum/ages.size();
		}
	}
}


