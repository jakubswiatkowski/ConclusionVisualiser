import java.util.concurrent.*;
import java.awt.event.ActionEvent;

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
 */ 
