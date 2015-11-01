package main;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI
{
	private JFrame mainFrame;
	private JPanel mainPanel;
	private Main main;
	private JButton b_startButton;
	private JLabel l_log;
	
	boolean isRunning=false;
	
	public GUI(Main main)
	{
		this.main=main;
	}

	public void init()
	{
		mainFrame = new JFrame ();
		
		mainFrame.setSize(200,100);
		
		GridBagConstraints c=new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		c.insets=new Insets(10,10,10,10);
		mainPanel=new JPanel(new GridBagLayout());
		
		b_startButton=new JButton("Start bot!");
		b_startButton.addActionListener(new StartGameListener());
		b_startButton.addKeyListener(new StopGameListener());
		mainPanel.add(b_startButton,c);
		
		l_log=new JLabel();
		
		mainFrame.add(mainPanel,BorderLayout.CENTER);
		mainFrame.add(l_log,BorderLayout.SOUTH);
	}
	
	public void run()
	{
		mainFrame.setVisible(true);
	}
	
	private void startGame()
	{
		b_startButton.setText("Press esc to stop");
		isRunning=true;
		mainFrame.setAlwaysOnTop(true);
		
		main.startSearching();
	}
	
	private void stopGame()
	{
		b_startButton.setText("Start bot!");
		isRunning=false;
		main.stopSearching();
		mainFrame.setAlwaysOnTop(false);
	}
	
	private class StopGameListener implements KeyListener
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			if(e.getKeyCode()==27)
			{
				if(isRunning)
					stopGame();
			}
		}
		@Override
		public void keyReleased(KeyEvent e)
		{
		}
		@Override
		public void keyTyped(KeyEvent e)
		{
		}
	}
	
	private class StartGameListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(!isRunning)
				startGame();
			else
				stopGame();
		}
	}
}
