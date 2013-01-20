package WindowClasses.SupportClasses;

import DataClasses.Formula;
import TypeClasses.Commands;
import com.mxgraph.view.mxGraph;

public class GraphDrawer
{
	private Integer stepNumber;
	private Integer totalSteps;
	private String drawingDirection;
	private mxGraph graph;
	private Object graphParent;

	public GraphDrawer(mxGraph graph)
	{
		this.graph = graph;
		graphParent = graph.getDefaultParent();
	}
	
	public void drawNextChainingStep(Formula formulaWithNextStep)
	{
		String edge = formulaWithNextStep.getOriginClause();
		String conclusion;
		String premises;
		if(drawingDirection == Commands.DrawGraphForwards)
		{
			conclusion = formulaWithNextStep.getConclusion().toString();
			premises = formulaWithNextStep.getPremisesAsString();			
		}
		else
		{
			conclusion = formulaWithNextStep.getPremisesAsString();
			premises = formulaWithNextStep.getConclusion().toString();
		}
		
		if( existsVertexWithLabel(premises) )
			drawVertexAndEdge(premises, edge, conclusion);
		else
			drawTwoVerticesAndEdge(premises, edge, conclusion);
		
		stepNumber++;
	}
	
	private void drawVertexAndEdge(String premises, String edge, String conclusion)
	{
		Object premisesVertex = getPremisesVertex(premises);
		int [] vertexPosition = setVertexPositionForCurrentStep();
		graph.getModel().beginUpdate();
		try
		{	
			Object conclusionVertex = graph.insertVertex(graphParent, null, conclusion, vertexPosition[0], vertexPosition[1], 30, 30);
			if(drawingDirection == Commands.DrawGraphForwards)
				graph.insertEdge(graphParent, null, edge, premisesVertex, conclusionVertex);
			else
				graph.insertEdge(graphParent, null, edge, conclusionVertex, premisesVertex);
		}
		finally
		{
			graph.getModel().endUpdate();
		}	
	}
	
	private void drawTwoVerticesAndEdge(String premises, String edge, String conclusion)
	{
		int [] vertex1Position = setVertexPositionForCurrentStep();
		stepNumber++;
		int [] vertex2Position = setVertexPositionForCurrentStep();
		graph.getModel().beginUpdate();
		try
		{
			Object v1 = graph.insertVertex(graphParent, null, premises, vertex1Position[0], vertex1Position[1], 30, 30);
			Object v2 = graph.insertVertex(graphParent, null, conclusion, vertex2Position[0], vertex2Position[1], 30, 30);
			if(drawingDirection == Commands.DrawGraphForwards)
				graph.insertEdge(graphParent, null, edge, v1, v2);
			else
				graph.insertEdge(graphParent, null, edge, v2, v1);
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}
	
	private boolean existsVertexWithLabel(String premises)
	{
		Object[] allVertices = graph.getChildVertices(graph.getDefaultParent());
		for(int i = 0 ; i < allVertices.length ; i++)
		{
			Object currentVertex = allVertices[i];
			if( graph.getLabel(currentVertex).equals(premises) )
				return true;
		}
		return false;
	}
	
	private Object getPremisesVertex(String premises)
	{
		Object[] allVertices = graph.getChildVertices(graph.getDefaultParent());
		for(int i = 0 ; i < allVertices.length ; i++)
		{
			Object currentVertex = allVertices[i];
			if( graph.getLabel(currentVertex).equals(premises) )
				return currentVertex;
		}
		return null;
	}
	
	private int[] setVertexPositionForCurrentStep()
	{
		int[] vertexPosition = new int[2];
		int radius = 200;
		double angle = -0.5*Math.PI + 2*Math.PI*stepNumber/totalSteps;
		vertexPosition[0] = (int) (radius * Math.cos(angle)) + 290;
		vertexPosition[1] = (int) (radius * Math.sin(angle)) + 280;
		return vertexPosition;
	}
	
	public void reset(mxGraph newGraph, String drawingDirection, Integer totalStepsNeeded)
	{
		this.graph = newGraph;
		graphParent = graph.getDefaultParent();
		stepNumber = 0;
		this.totalSteps = totalStepsNeeded;
		this.drawingDirection = drawingDirection;
	}
}
