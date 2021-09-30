/**
 * @author Professor Nate Williams
 * modified by Jason Miner 
 * starts and runs plants using workers to get and process oranges. 
 */
public class Plant implements Runnable 
{
    // How long do we want to run the juice processing
    public static final long PROCESSING_TIME = 5 * 1000;
    private static final int NUM_PLANTS = 2;

    public static void main(String[] args)
    {
        // Startup the plants
        Plant[] plants = new Plant[NUM_PLANTS];
       
        for (int i = 0; i < NUM_PLANTS; i++) 
        {
            plants[i] = new Plant(1);
            plants[i].startPlant();
        }

        // Give the plants time to do work
        delay(PROCESSING_TIME, "Plant malfunction");

        // Stop the plant, and wait for it to shutdown
        for (Plant p : plants) 
        {
            p.stopPlant();
        }
        for (Plant p : plants) 
        {
            p.waitToStop();
        }

        // Summarize the results
        int totalProvided = 0;
        int totalProcessed = 0;
        int totalBottles = 0;
        int totalWasted = 0;
        
        for (Plant p : plants) 
        {
            totalProvided += p.getProvidedOranges();
            totalProcessed += p.getProcessedOranges();
            totalBottles += p.getBottles();
            totalWasted += p.getWaste();
        }
        System.out.println("Total provided/processed = " + totalProvided + "/" + totalProcessed);
        System.out.println("Created " + totalBottles +
                           ", wasted " + totalWasted + " oranges");
    }

    private static void delay(long time, String errMsg) 
    {
        long sleepTime = Math.max(1, time);
       
        try 
        {
            Thread.sleep(sleepTime);
        } 
        catch (InterruptedException e) 
        {
            System.err.println(errMsg);
        }
    }

    public final int ORANGES_PER_BOTTLE = 3;
    private final int threadNum;
    private int orangesProvided;
    private int orangesProcessed;
    private volatile boolean timeToWork;
    private static final int NUM_WORKERS = 5;
    private final Thread[] workers;
    Worker worker = new Worker(this);

    /**
     * plant constructor
     * added for statement to add 100 oranges to the plant 
     */
    Plant(int threadNum) 
    {
    	this.threadNum = threadNum;
        orangesProvided = 0;
        orangesProcessed = 0;
        
        //adds 100 oranges to the orangesBeingProcessed list 
        for (int i= 0; i >= 100; i++) 
        {
        	worker.orangesBeingProcessed.add(new Orange());
        	orangesProvided++;
        }
        workers = new Thread[NUM_WORKERS];
    }

    /**
     * added workers to startPlant() 
     * because plant needs to be started in order for workers work
     */
    public void startPlant() 
    {
        timeToWork = true;
        
        for(int i = 0; i <NUM_WORKERS; i++)
        {
        	workers[i] = new Thread(this, "Plant[" +threadNum + "] Worker [" + (i+1) + "]" );
        	workers[i].start();
        }
    }

    public void stopPlant() 
    {
        timeToWork = false;
    }

    /**
     * modified from waiting for the plants to stop, 
     * to waiting for workers to stop
     */
    public void waitToStop() 
    {
    	for (Thread worker : workers) 
    	{
            try 
            {
                worker.join();
            }
            catch (InterruptedException e) 
            {
                System.err.println(worker.getName() + " stop malfunction");
            }
    	}	 	
    }
    
    /**
     * has 1st worker threads fetch oranges 
     * while the rest of the worker threads process the oranges
     * increments the counters for orangesProvided and orangesProcessed
     */
    public void run() 
    {
        System.out.println(Thread.currentThread().getName() + " Processing oranges");
        
        while (timeToWork) 
        {
        	if (Thread.currentThread() == workers[0])
        	{
        		worker.orangesBeingProcessed.add(new Orange());
            	orangesProvided++;
        	}
        	
        	else
        	{
        		Orange workingOrange = worker.get();  
        		
        		if (workingOrange != null)
        		{
        			if(workingOrange.getState() != Orange.State.Bottled)
        			{
        				worker.processPartOrange(workingOrange);     
                    	
                    	worker.add(workingOrange);
        			}
        			else
        			{
        				worker.proecessOrangeComplete(workingOrange);
                		orangesProcessed++;
        			}        			
        		}    	           	
        	}              		       		
        }
        System.out.println(Thread.currentThread().getName() + " Done");
    }


    public int getProvidedOranges() 
    {
        return orangesProvided;
    }

    public int getProcessedOranges() 
    {
        return orangesProcessed;
    }

    public int getBottles() 
    {
        return orangesProcessed / ORANGES_PER_BOTTLE;
    }

    public int getWaste() 
    {
        return orangesProcessed % ORANGES_PER_BOTTLE;
    }
}
