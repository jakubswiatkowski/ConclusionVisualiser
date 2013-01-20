package Core;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.awt.event.*;
import javax.swing.*;

import TypeClasses.WindowTypes;
import WindowClasses.*;
import DataClasses.*;

public class Vision
{
	private final int mainFrameWidth = 800, mainFrameHeight = 700;
	private final BlockingQueue<ActionEvent> eventQueue;
	private JFrame mainFrame = new JFrame();
	private MenuWindow menuWindow;
	private ResultWindow resultWindow;
	
	public Vision(BlockingQueue<ActionEvent> q)
	{
		eventQueue = q;
		initializeMainFrame();
		createWindows();
	}
	
	private void initializeMainFrame()
	{
		mainFrame.setTitle("Conclusion Visualizer");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(mainFrameWidth, mainFrameHeight);
	}
	
	private void createWindows()
	{
		menuWindow = new MenuWindow(mainFrameWidth, eventQueue);
		resultWindow = new ResultWindow(mainFrameWidth, eventQueue);
	}
		
	public void changeWindow(String windowType)
	{
		if(windowType == WindowTypes.MenuWindow)
			mainFrame.setContentPane(menuWindow);
		else if(windowType == WindowTypes.ResultWindow)
			mainFrame.setContentPane(resultWindow);
		mainFrame.setVisible(true);
	}
	
	public void showFileContent(Data fileContent)
	{
		menuWindow.showFileContent(fileContent);
		mainFrame.setVisible(true);
	}
	
	public void startDrawingGraph(String drawingDirection, int numberOfStepsNeeded, Data data)
	{
		resultWindow.beginDrawingGraph(drawingDirection, numberOfStepsNeeded, data);
	}
	
	public void drawNextChainingStep(Formula formulaWithNextStepToDraw)
	{
		resultWindow.drawNextChainingStep(formulaWithNextStepToDraw);
		mainFrame.setVisible(true);
	}
	
	public void endDrawingGraph()
	{
		resultWindow.endDrawingGraph();
		mainFrame.setVisible(true);
	}
} 
