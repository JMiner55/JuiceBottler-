import java.util.ArrayList;
import java.util.List;

public class Worker
{
	private Plant plt;
	List<Orange> orangesBeingProcessed = new ArrayList<>();  
	
	public Worker(Plant plt) 
	{
		this.plt = plt;
	}
	   public void processPartOrange(Orange o) 
	   {
		   if (o.getState() != Orange.State.Bottled)
		   {
			  o.runProcess();
		   }
	   }
	   
	   public void proecessOrangeComplete(Orange o) 
	   {
		   if (o.getState() != Orange.State.Processed)
		   {
			   o.runProcess();
		   }
	   }
}
