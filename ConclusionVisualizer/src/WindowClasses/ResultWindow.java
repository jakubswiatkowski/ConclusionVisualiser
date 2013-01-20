package WindowClasses;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import DataClasses.*;
import TypeClasses.Commands;
import TypeClasses.EventTypes;
import WindowClasses.SupportClasses.GraphDrawer;

public class ResultWindow extends BasicWindow
{
	private JPanel resultsPanel = new JPanel();
	private JPanel graphPanel = new JPanel();
	private JPanel conclusionPanel = new JPanel();
	private JButton nextStepButton;
	private JLabel graphLabel = new JLabel("GRAPH COMPLETED!");
	private mxGraph graph = new mxGraph();
	private GraphDrawer graphDrawer = new GraphDrawer(graph);
	
	public ResultWindow(int mainFrameHeight, BlockingQueue<ActionEvent> eventQueue)
	{
		super(mainFrameHeight, eventQueue);
		setAndAddResultsPanel();
		setAndAddResultPanel();
		setAndAddConclusionPanel();
		addButtonsToMenuPanel();
	}
	
	private void setAndAddResultsPanel()
	{
		this.add(resultsPanel);
		resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.X_AXIS));
	}
	
	private void setAndAddResultPanel()
	{
		resultsPanel.add(graphPanel);
		graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.Y_AXIS));
		graphPanel.setBorder(BorderFactory.createTitledBorder("Graph"));
		graphPanel.add(graphLabel);
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		graphPanel.add(graphComponent);
	}
	
	private void setAndAddConclusionPanel()
	{
		resultsPanel.add(conclusionPanel);
		conclusionPanel.setLayout(new BoxLayout(conclusionPanel, BoxLayout.Y_AXIS));
		conclusionPanel.setBorder(BorderFactory.createTitledBorder("Conclusion info"));
	}
	
	private void addButtonsToMenuPanel()
	{
		nextStepButton = new JButton("Next Step");
		menuPanel.add(nextStepButton);
		nextStepButton.setActionCommand(EventTypes.NextStepButton);
		nextStepButton.addActionListener(this);
		addButtonToPanel(menuPanel, "Main Menu", EventTypes.MainMenuButton);
	}
	
	public void drawNextChainingStep(Formula formulaWithNextStep)
	{
		graphDrawer.drawNextChainingStep(formulaWithNextStep);
	}
	
	public void beginDrawingGraph(String drawingDirection, int numberOfStepsNeeded, Data data)
	{
		graph = new mxGraph();
		graphDrawer.reset(graph, drawingDirection, numberOfStepsNeeded);
		resetGraphPanel();
		addConclusionInfoToConclusionPanel(data.getToBeProved(), data.getFormulas());
		nextStepButton.setEnabled(true);
	}
	
	private void addConclusionInfoToConclusionPanel(Literal toBeProved, ArrayList<Formula> formulas)
	{
		conclusionPanel.removeAll();
		conclusionPanel.add(new JLabel("Literal to be proved:  " + toBeProved.toString()));
		conclusionPanel.add(new JLabel("Clauses:"));
		
		for(int i=1, j=0 ; j < formulas.size() ; i++)
		{
			String currentClause = formulas.get(j).getOriginClause();
			conclusionPanel.add(new JLabel(i + ".    " + currentClause));
			
			while(j < formulas.size() && formulas.get(j).getOriginClause().equals(currentClause))
				j++;
		}
		
		conclusionPanel.add(new JLabel("\n\n"));
		
		for(Integer i=1, j=0 ; j < formulas.size() ; i++)
		{
			String currentClause = formulas.get(j).getOriginClause();
			conclusionPanel.add(new JLabel("Formulas made from clause " + i + "."));
			
			String premises = formulas.get(j).getPremisesAsString();
			if(premises.isEmpty())
				premises = "true";
			for( ; j < formulas.size() && formulas.get(j).getOriginClause().equals(currentClause) ; j++)
				conclusionPanel.add(new JLabel( (j+1) + ".  " + premises + " => " + formulas.get(j).getConclusion().toString()));
		}
	}
	
	private void resetGraphPanel()
	{
		graphLabel.setVisible(false);
		graphPanel.removeAll();
		graphPanel.add(graphLabel);
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		graphPanel.add(graphComponent);
	}
	
	public void endDrawingGraph()
	{
		graphLabel.setVisible(true);
		nextStepButton.setEnabled(false);
	}
}
