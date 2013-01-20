package WindowClasses;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public abstract class BasicWindow extends JPanel implements ActionListener
{
	private final BlockingQueue<ActionEvent> eventQueue;
	protected JPanel menuPanel = new JPanel();
	
	public BasicWindow(int mainFrameHeight, BlockingQueue<ActionEvent> eventQueue)
	{
		this.eventQueue = eventQueue;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAndAddMenuPanel(mainFrameHeight);
	}
	
	protected void setAndAddMenuPanel(int mainFrameHeight)
	{
		this.add(menuPanel);
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));
		menuPanel.setBorder(BorderFactory.createTitledBorder("Menu"));
		setMaximumSize(new Dimension(99999, 2*mainFrameHeight/10));
	}
	
	protected void addButtonToPanel(JPanel panel, String buttonName, String generatedEventType)
	{
		JButton button = new JButton(buttonName);
		panel.add(button);
		button.setActionCommand(generatedEventType);
		button.addActionListener(this);
	}	
	
	public void actionPerformed(ActionEvent e)
	{
		while(true)
		{ 
			try
			{ 
			 	eventQueue.put(e);
			 	break;
			}
			 catch (InterruptedException ex) {continue;}
		}
	}
}