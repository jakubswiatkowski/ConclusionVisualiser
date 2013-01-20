package Core;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import javax.swing.SwingUtilities;

import DataClasses.*;
import TypeClasses.*;

public class Controller implements Runnable
{
	private final String dataFilePath = "data.txt";
	private CalculationEngine calculationEngine;
	private Vision vision;
	private BlockingQueue<ActionEvent> eventQueue;
	ArrayList<Formula> currentUsedFormulasList;
	private String drawingDirection;
	
	public Controller(CalculationEngine cE, Vision v, BlockingQueue<ActionEvent> q)
	{
		calculationEngine = cE;
		vision = v;
		eventQueue = q;
		reactOnEvent(Commands.Start);
	}
	
	public void run()
	{
		ActionEvent event;
		while(true)
		{
			try
			{	event = eventQueue.take();	}
			catch (InterruptedException ex)
			{	event = new ActionEvent(ex, 0, ErrorTypes.Pop);	}
			reactOnEvent(event.getActionCommand());
		}
	}
	
	private void reactOnEvent(String eventType)
	{	
		if(eventType == EventTypes.NextStepButton)
			commandDrawingNextChainingStep();
		
		else if(eventType == EventTypes.ForwardConclusionButton)
			commandForwardConclusion();
		
		else if(eventType == EventTypes.BackwardConclusionButton)
			commandBackwardConclusion();
		
		else if(eventType == EventTypes.ShowFileContentButton)
			commandShowFileContent();
		
		else if(eventType == EventTypes.MainMenuButton || eventType == Commands.Start)
			commandViewMainMenu();
		
		else if(eventType == EventTypes.ExitButton)
			System.exit(0);
		
		else if(eventType == ErrorTypes.Pop)
			return;
	}
	
	private void commandDrawingNextChainingStep()
	{
		final Formula formulaWithNextStepToDraw = currentUsedFormulasList.remove(0);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				vision.drawNextChainingStep(formulaWithNextStepToDraw);
				if(wholeConclusionPathIsDrawn())
					vision.endDrawingGraph();
			}
		});
	}
	
	private void commandForwardConclusion()
	{
		drawingDirection = Commands.DrawGraphForwards; 
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				currentUsedFormulasList = calculationEngine.doForwardChaining(dataFilePath);
				vision.startDrawingGraph(Commands.DrawGraphForwards, currentUsedFormulasList.size()+1, calculationEngine.readDataFromFile(dataFilePath));
				vision.changeWindow(WindowTypes.ResultWindow);
			}
		});
	}
	
	private void commandBackwardConclusion()
	{
		drawingDirection = Commands.DrawGraphBackwards;
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				currentUsedFormulasList = calculationEngine.doBackwardChaining(dataFilePath);
				vision.startDrawingGraph(Commands.DrawGraphBackwards, currentUsedFormulasList.size(), calculationEngine.readDataFromFile(dataFilePath));
				vision.changeWindow(WindowTypes.ResultWindow);
			}
		});
	}
	
	private void commandShowFileContent()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				vision.showFileContent(calculationEngine.readDataFromFile(dataFilePath));
			}
		});
	}
	
	private void commandViewMainMenu()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				vision.changeWindow(WindowTypes.MenuWindow);
			}
		});
	}
	
	private boolean wholeConclusionPathIsDrawn()
	{
		if(currentUsedFormulasList.size() == 1 && drawingDirection == Commands.DrawGraphBackwards)
			return true;
		if(currentUsedFormulasList.isEmpty())
			return true;
		return false;
	}
}