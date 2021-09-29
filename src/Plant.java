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

    private final Thread thread;
    private int orangesProvided;
    private int orangesProcessed;
    private volatile boolean timeToWork;
    
    private static final int NUM_WORKERS = 10;
    private final Thread[] workers;
    
    Plant(int threadNum) 
    {
        orangesProvided = 0;
        orangesProcessed = 0;
        thread = new Thread(this, "Plant[" + threadNum + "]");
        workers = new Thread[NUM_WORKERS];
    }

    public void startPlant() 
    {
        timeToWork = true;
        thread.start();
        
        for(int i = 0; i <NUM_WORKERS; i++)
        {
        	workers[i] = new Thread(this, "Plant[" + thread + "] Worker [" + (i+1) + "]" );
        	workers[i].start();
        }
    }

    public void stopPlant() 
    {
        timeToWork = false;
    }

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
    	 	
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            System.err.println(thread.getName() + " stop malfunction");
//        }
    }

    public void run() 
    {
        System.out.println(Thread.currentThread().getName() + " Processing oranges");
        
        Worker worker = new Worker(this);
        
        while (timeToWork) 
        {
            worker.processEntireOrange(new Orange());
            orangesProvided++;
            System.out.print(".");
        }
        System.out.println("");
        System.out.println(Thread.currentThread().getName() + " Done");
    }

    public void processEntireOrange(Orange o) 
    {
        while (o.getState() != Orange.State.Bottled) 
        {
            o.runProcess();
        }
        orangesProcessed++;
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