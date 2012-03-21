package userInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import gameLogic.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.io.*;
import static java.awt.GridBagConstraints.*;

class SnakeSettingsPanel extends JPanel
{
	private JList snakeJList1, snakeJList2;
	private JList brainJList;
	private JButton addSnakeButton1, addSnakeButton2;
	private JButton removeSnakeButton1, removeSnakeButton2;
	private JButton reloadAllBrainsButton;
	private Map<String, String> snakes = new TreeMap<String, String>();
	private Map<String, Class<? extends Brain>> brains = new TreeMap<String, Class<? extends Brain>>();
	private Map<String, String> teams = new TreeMap<String, String>();
	
	public SnakeSettingsPanel()
	{	
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		
		GridBagConstraints constraint = new GridBagConstraints();
		
		constraint.fill = HORIZONTAL;
		constraint.insets = new Insets(4, 4, 4, 4);
		constraint.gridwidth = 1;
		constraint.gridheight = 1;
		constraint.gridx = 0;
		constraint.gridy = 0;
		
			JLabel selected = new JLabel("Available snakes:");
			selected.setHorizontalAlignment(JLabel.CENTER);
			gridbag.setConstraints(selected, constraint);
			add(selected);
		
		constraint.fill = HORIZONTAL;
		constraint.insets = new Insets(4, 4, 4, 4);
		constraint.gridwidth = 1;
		constraint.gridheight = 1;
		constraint.gridx = 2;
		constraint.gridy = 0;
		
			JLabel available = new JLabel("Snakes in game:");
			available.setHorizontalAlignment(JLabel.CENTER);
			gridbag.setConstraints(available, constraint);
			add(available);
			
		constraint.fill = BOTH;
		constraint.gridwidth = 1;
		constraint.gridheight = 8;
		constraint.weightx = 0.5;
		constraint.weighty = 0.5;
		constraint.gridx = 0;
		constraint.gridy = 1;
		
			brainJList = new JList();
			
			JScrollPane jspb = new JScrollPane(brainJList);
			jspb.setPreferredSize(jspb.getPreferredSize());
			gridbag.setConstraints(jspb, constraint);
			add(jspb);
		
		constraint.fill = HORIZONTAL;
		constraint.insets = new Insets(4, 4, 4, 4);
		constraint.gridwidth = 1;
		constraint.gridheight = 1;
		constraint.gridx = 2;
		constraint.gridy = 1;
		constraint.anchor = NORTH;
		
			JLabel team1 = new JLabel("Team 1:");
			team1.setHorizontalAlignment(JLabel.CENTER);
			gridbag.setConstraints(team1, constraint);
			add(team1);
			
		constraint.fill = BOTH;
		constraint.gridwidth = 1;
		constraint.gridheight = 3;
		constraint.weightx = 0.5;
		constraint.weighty = 0.5;
		constraint.gridx = 2;
		constraint.gridy = 1;
		
			snakeJList1 = new JList();
			
			JScrollPane jsp1 = new JScrollPane(snakeJList1);
			jsp1.setPreferredSize(jsp1.getPreferredSize());
			gridbag.setConstraints(jsp1, constraint);
			add(jsp1);
		
		constraint.fill = HORIZONTAL;
		constraint.insets = new Insets(4, 4, 4, 4);
		constraint.gridwidth = 1;
		constraint.gridheight = 1;
		constraint.gridx = 2;
		constraint.gridy = 4;
		
			JLabel team2 = new JLabel("Team 2:");
			team2.setHorizontalAlignment(JLabel.CENTER);
			gridbag.setConstraints(team2, constraint);
			add(team2);
			
		constraint.fill = BOTH;
		constraint.gridwidth = 1;
		constraint.gridheight = 3;
		constraint.weightx = 0.5;
		constraint.weighty = 0.5;
		constraint.gridx = 2;
		constraint.gridy = 4;
		
			snakeJList2 = new JList();
			
			JScrollPane jsp2 = new JScrollPane(snakeJList2);
			jsp1.setPreferredSize(jsp2.getPreferredSize());
			gridbag.setConstraints(jsp2, constraint);
			add(jsp2);
		
		constraint.fill = NONE;
		constraint.weightx = 0.1;
		constraint.weighty = 0.1;
		constraint.insets = new Insets(4, 4, 4, 4);
		constraint.gridheight = 4;
		constraint.gridx = 1;
		constraint.anchor = SOUTH;
		constraint.gridy = 0;
		
			addSnakeButton1 = new JButton("Team 1");
			addSnakeButton1.addActionListener(new AddSnakeTeam1Listener());
			gridbag.setConstraints(addSnakeButton1, constraint);
			add(addSnakeButton1);
		
		constraint.fill = NONE;
		constraint.weightx = 0.1;
		constraint.weighty = 0.1;
		constraint.insets = new Insets(4, 4, 4, 4);
		constraint.gridheight = 4;
		constraint.gridx = 1;
		constraint.anchor = NORTH;
		constraint.gridy = 4;
		
			addSnakeButton2 = new JButton("Team 2");
			addSnakeButton2.addActionListener(new AddSnakeTeam2Listener());
			gridbag.setConstraints(addSnakeButton2, constraint);
			add(addSnakeButton2);
			
		constraint.fill = NONE;
		constraint.weightx = 0.1;
		constraint.weighty = 0.1;
		constraint.insets = new Insets(4, 4, 4, 4);
		constraint.gridheight = 4;
		constraint.gridx = 1;
		constraint.anchor = CENTER;
		constraint.gridy = 3;
			
			removeSnakeButton1 = new JButton("RM T1");
			removeSnakeButton1.addActionListener(new RemoveSnakeTeam1Listener());
			gridbag.setConstraints(removeSnakeButton1, constraint);
			add(removeSnakeButton1);
		
		constraint.fill = NONE;
		constraint.weightx = 0.1;
		constraint.weighty = 0.1;
		constraint.insets = new Insets(4, 4, 4, 4);
		constraint.gridheight = 4;
		constraint.gridx = 1;
		constraint.anchor = SOUTH;
		constraint.gridy = 4;
			
			removeSnakeButton2 = new JButton("RM T2");
			removeSnakeButton2.addActionListener(new RemoveSnakeTeam2Listener());
			gridbag.setConstraints(removeSnakeButton2, constraint);
			add(removeSnakeButton2);
			
		constraint.fill = NONE;
		constraint.weightx = 0.1;
		constraint.weighty = 0.1;
		constraint.insets = new Insets(4, 4, 4, 4);
		constraint.anchor = SOUTH;
		constraint.gridx = 0;
		constraint.gridy = 9;
		constraint.gridwidth = 3;
		constraint.gridheight = 1;
		constraint.weighty = 0.0;
		
			reloadAllBrainsButton = new JButton("Reload all brains");
			reloadAllBrainsButton.addActionListener(new ReloadBrainsListener());
			gridbag.setConstraints(reloadAllBrainsButton, constraint);
			add(reloadAllBrainsButton);
		
		loadBrains();
	}
	
	private String loadBrains()
	{
		ClassLoader parentClassLoader = MainWindow.class.getClassLoader();
		BotClassLoader classLoader = new BotClassLoader(parentClassLoader);
		
		FilenameFilter filter = new ClassfileFilter();
		File botFolder = new File("./bot");
		File[] listOfFiles = botFolder.listFiles(filter);
		
		String loadedBrains = "";
		for (File file : listOfFiles)
		{
			if (file.isDirectory())
				continue;
			
			String name = file.getName();
			name = name.substring(0, name.lastIndexOf("."));
			
			Class<?> c;
			try
			{
				c = classLoader.getClass(name);
			}
			catch (Throwable e)
			{
				JOptionPane.showMessageDialog(this, e.toString());
				continue;
			}
			
			Class<? extends Brain> brainClass;
			try
			{
				brainClass = c.asSubclass(Brain.class);
			}
			catch (Exception e)
			{
				continue;
			}
			
			loadedBrains += name + '\n';
			brains.put(name, brainClass);
		}
		
		brainJList.setListData(brains.keySet().toArray());
		
		return loadedBrains;
	}
	
	static private class ClassfileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name)
		{
			name = name.substring(name.lastIndexOf("."), name.length());
			return name.equalsIgnoreCase(".class");
		}
	}

	
		
	private class AddSnakeTeam1Listener implements ActionListener
	{	
		public void actionPerformed(ActionEvent event)
		{
			Object selectedObject = brainJList.getSelectedValue();
			if (selectedObject == null)
				return;
			
			
			String name = selectedObject.toString();
			String snakeName = generateSnakeName(name);
			String teamName = "Team 1";
			
			snakes.put(snakeName, name);
			teams.put(snakeName, teamName);
			
			snakeJList1.setListData(getSnakesOnTeam(teamName).toArray());
		}
	}
	
	private class AddSnakeTeam2Listener implements ActionListener
	{	
		public void actionPerformed(ActionEvent event)
		{
			Object selectedObject = brainJList.getSelectedValue();
			if (selectedObject == null)
				return;
			
			
			String name = selectedObject.toString();
			String snakeName = generateSnakeName(name);
			String teamName = "Team 2";
			
			snakes.put(snakeName, name);
			teams.put(snakeName, teamName);
			
			snakeJList2.setListData(getSnakesOnTeam(teamName).toArray());
		}
	}
	
	private String generateSnakeName(String name)
	{
		String snakeName = name;
		int numberOfSnakesWithTheSameBrain = 1;
		
		while (snakes.containsKey(snakeName))
		{
			++numberOfSnakesWithTheSameBrain;
			snakeName = name + "#" + numberOfSnakesWithTheSameBrain;
		}
		
		return snakeName;
	}
	
	public ArrayList<String> getSnakesOnTeam(String team) {
		ArrayList<String> teamNames = new ArrayList<String>();
		
		for (Map.Entry<String, String> teamEntry : teams.entrySet())
		{
			if (teamEntry.getValue().equals(team))
				teamNames.add(teamEntry.getKey());
		}
		return teamNames;
	}

	private class RemoveSnakeTeam1Listener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			Object selectedObject = snakeJList1.getSelectedValue();
			if (selectedObject == null)
				return;
			
			String snakeName = selectedObject.toString();
			
			snakes.remove(snakeName);
			teams.remove(snakeName);
			
			snakeJList1.setListData(getSnakesOnTeam("Team 1").toArray());
		}
	}
	
	private class RemoveSnakeTeam2Listener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			Object selectedObject = snakeJList2.getSelectedValue();
			if (selectedObject == null)
				return;
			
			String snakeName = selectedObject.toString();
			
			snakes.remove(snakeName);
			teams.remove(snakeName);
			
			snakeJList1.setListData(getSnakesOnTeam("Team 2").toArray());
		}
	}
	
	private class ReloadBrainsListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String reloadedBrains = loadBrains();
			JOptionPane.showMessageDialog(SnakeSettingsPanel.this, "Successfully reloaded:\n" + reloadedBrains);
		}
	}
	
	public Map<String, Brain> getSnakes() throws Exception
	{
		Map<String, Brain> snakeMap = new TreeMap<String, Brain>();
		for (Map.Entry<String, String> snake : snakes.entrySet())
		{
			Brain brain = brains.get(snake.getValue()).newInstance();
			snakeMap.put(snake.getKey(), brain);
		}
		return snakeMap;
	}
	
	public Map<String, String> getTeams()
	{
		return new TreeMap<String, String>(teams);
	}
}
