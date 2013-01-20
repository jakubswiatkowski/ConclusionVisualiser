import java.util.concurrent.*;
import java.awt.event.ActionEvent;

import Core.CalculationEngine;
import Core.Controller;
import Core.Vision;

public class Main
{	
	public static void main(String[] args)
	{
		BlockingQueue<ActionEvent> eventQueue = new LinkedBlockingQueue<ActionEvent>();
		Vision vision = new Vision(eventQueue);	
		CalculationEngine calculationEngine = new CalculationEngine();
		Controller controller = new Controller(calculationEngine, vision, eventQueue);
		
		Thread controllerThread = new Thread(controller);
		controllerThread.start();
	}
}  

/*TODO
 * -MenuWindow Layout
 * -MAYBE one window at a time in Vision
 */ 
