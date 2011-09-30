package userInterface;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import gameLogic.*;


class GameSettingsPanel extends JPanel
{
	private JLabel lolText;
	private JTextField boardWidth;
	private JTextField boardHeight;
	
	public GameSettingsPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		lolText = new JLabel("GAME SETTINGS");
				
		add(lolText);
		
		initializeBoardSizeSettings();
	}
	
	private void initializeBoardSizeSettings()
	{
		boardWidth = new JTextField("10");
		boardHeight = new JTextField("10");
		
		boardWidth.setColumns(4);
		boardHeight.setColumns(4);
		
		JPanel widthPanel = new JPanel();
		JPanel heightPanel = new JPanel();
		
		widthPanel.add(new JLabel("Board width:"));
		widthPanel.add(boardWidth);
		heightPanel.add(new JLabel("Board height:"));
		heightPanel.add(boardHeight);
				
		add(widthPanel);
		add(heightPanel);
	}
	
	
	public int getBoardWidth()
	{
		return Integer.parseInt(boardWidth.getText());
	}
	
	public int getBoardHeight()
	{
		return Integer.parseInt(boardHeight.getText());
	}
}
