package WindowClasses;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import DataClasses.*;
import TypeClasses.EventTypes;

public class MenuWindow extends BasicWindow
{	
	private JPanel fileContentPanel = new JPanel();
	
	public MenuWindow(int mainFrameHeight, BlockingQueue<ActionEvent> eventQueue)
	{
		super(mainFrameHeight, eventQueue);
		addButtonsToMenuPanel();
		fileContentPanel.setLayout(new BoxLayout(fileContentPanel, BoxLayout.Y_AXIS));
		fileContentPanel.setBorder(BorderFactory.createTitledBorder("File Content"));
	}
	
	private void addButtonsToMenuPanel()
	{
		addButtonToPanel(menuPanel, "Show file content", EventTypes.ShowFileContentButton);
		addButtonToPanel(menuPanel, "Forward Conclusion", EventTypes.ForwardConclusionButton);
		addButtonToPanel(menuPanel, "Backward Conclusion", EventTypes.BackwardConclusionButton);
		addButtonToPanel(menuPanel, "Exit", EventTypes.ExitButton);
	}
	
	public void showFileContent(Data fileContent)
	{		
		this.add(fileContentPanel);
		addFileContentToPanel(fileContent.getToBeProved(), fileContent.getFormulas());
	}
	
	private void addFileContentToPanel(Literal toBeProved, ArrayList<Formula> formulas)
	{
		fileContentPanel.removeAll();
		fileContentPanel.add(new JLabel("Literal to be proved:  " + toBeProved.toString()));
		fileContentPanel.add(new JLabel("Clauses:"));
		
		for(int i=1, j=0 ; j < formulas.size() ; i++)
		{
			String currentClause = formulas.get(j).getOriginClause();
			fileContentPanel.add(new JLabel(i + ".    " + currentClause));
			
			while(j < formulas.size() && formulas.get(j).getOriginClause().equals(currentClause))
				j++;
		}
	}
}