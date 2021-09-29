
public class Worker 
{

	private Plant plt;
	
	public Worker(Plant plt) 
	{
		this.plt = plt;
	}

	   public void processEntireOrange(Orange o) 
	   {
	        do 
	        {
	            processOrange(o);
	        } 
	        while (o.getState() != Orange.State.Bottled);
	        plt.processEntireOrange(o);
	    }

	public void processOrange(Orange o) 
	{
		// TODO Auto-generated method stub
		 o.runProcess();
	}

}
