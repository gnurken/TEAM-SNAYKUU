package userInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import gameLogic.*;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SettingsWindow extends JFrame
{
	private boolean done = false;
	private JTabbedPane tabbedPane;	
	private SnakeSettingsPanel snakeSettingsPanel;
	private GameSettingsPanel gameSettingsPanel;
	private ReplayPanel replayPanel;
	private DeveloperPanel developerPanel;
	
	private JButton startButton;
	private JPanel startButtonPanel;
	
	public SettingsWindow()
	{
		super("SNAYKUU - settings");
		setLayout(new BorderLayout());
		
		tabbedPane = new JTabbedPane();
		
		snakeSettingsPanel = new SnakeSettingsPanel();
		gameSettingsPanel = new GameSettingsPanel();
		replayPanel = new ReplayPanel(this);
		developerPanel = new DeveloperPanel(this);
		
		tabbedPane.addTab("Snayks", snakeSettingsPanel);
		tabbedPane.addTab("Game settings", gameSettingsPanel);
		tabbedPane.addTab("Replay", replayPanel);
		tabbedPane.addTab("Developer", developerPanel);
		
		startButton = new JButton("Start");
		startButton.addActionListener(new StartButtonListener());
		startButtonPanel = new JPanel();
		startButtonPanel.add(startButton);
		
		add(tabbedPane, BorderLayout.CENTER);
		
		add(startButtonPanel, BorderLayout.SOUTH);
		
		setSize(600, 400);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void putThisDamnWindowInMyFace()
	{
		done = false;
		setVisible(true);
	}
	
	
	private class StartButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			synchronized(SettingsWindow.this)
			{
				done = true;
			}
		}
	}
	
	public synchronized boolean isDone()
	{
		return done;
	}
	
	public Session generateSession() throws Exception
	{
		Metadata metadata = gameSettingsPanel.generateMetadata();
		
		Session session = new Session(metadata);
		
		GameObjectType objectType = new GameObjectType("Snake", true);
		
		Random r = new Random(4L);
		int numSnakes = snakeSettingsPanel.getSnakes().size();
		float stepSize = 0.8f/numSnakes;
		int currentSnake = 0;
		
		Map<String, Team> teams = createTeams(session);
		
		for (Map.Entry<String, String> teamEntry : snakeSettingsPanel.getTeams().entrySet())
		{
			Team team = teams.get(teamEntry.getValue());
			String snakeName = teamEntry.getKey();
			Brain brain = snakeSettingsPanel.getSnakes().get(snakeName);
			
			Snake snake = new Snake(objectType, snakeName, brain, Color.getHSBColor(stepSize*currentSnake++, r.nextFloat()/2+0.5f, r.nextFloat()/2+0.5f));
			session.addSnake(snake, team);
		}
		
		session.prepareForStart();
		return session;
	}
	
	private Map<String, Team> createTeams(Session session) 
	{
		TreeSet<String> teamNames = new TreeSet<String>(snakeSettingsPanel.getTeams().values());
		TreeMap<String, Team> teams = new TreeMap<String, Team>();
		
		int nr = 1;
		for (String s : teamNames)
		{
			Team team = new Team(s, nr++);
			session.addTeam(team);
			teams.put(s, team);
		}
		
		return teams;
	}

	public int getGameSpeed()
	{
		return gameSettingsPanel.getGameSpeed();
	}
	
	public int getPixelsPerUnit()
	{
		return gameSettingsPanel.getPixelsPerUnit();
	}
	
}
