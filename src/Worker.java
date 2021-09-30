import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason Miner
 * worker class
 * creates workers, Array List and methods for workers
 * to process and bottle oranges. 
 */
public class Worker
{
	private Plant plt;
	List<Orange> orangesBeingProcessed = new ArrayList<>();  
	
	/**
	 * worker constructor for creating workers in plant class
	 */
	public Worker(Plant plt) 
	{
		this.plt = plt;
	}
	
	/**
	 * created for workers to process the 
	 * orange up to bottled state
	 */
	public void processPartOrange(Orange o) 
	   {
		   if (o.getState() != Orange.State.Bottled)
		   {
			  o.runProcess();
		   }
	   }
	   
	/**
	 * created for workers to finish processing an orange
	 */
	public void proecessOrangeComplete(Orange o) 
	   {
		   if (o.getState() != Orange.State.Processed)
		   {
			   o.runProcess();
		   }
	   }
	   
	/**
	 * pulled from ReaderBlockList to stabilize
	 * orangesBeingProcessed ArrayList
	 */
	public void add(Orange o)
	   {
		   synchronized (orangesBeingProcessed)
		   {
			   orangesBeingProcessed.add(o);
			   orangesBeingProcessed.notify();
		   }
	   }
	   
	/**
	 * pulled from ReaderBlockList to stabilize
	 * orangesBeingProcessed ArrayList
	 */
	public Orange get() 
	   {
		   synchronized (orangesBeingProcessed)
		   {
			   if (orangesBeingProcessed.size() == 0)
			   {
				   try
				   {
					   // added 100 milliseconds to wait time so 
					   // workers wont get stuck waiting forever
					   orangesBeingProcessed.wait(100);
				   }
				   catch (InterruptedException ignored)
				   {					   
				   }
			   }
			   return orangesBeingProcessed.remove(0);
		   }
	   }
}
