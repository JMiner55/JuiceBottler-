/**
 * @author Professor Nate Williams
 * Code has not been modified.
 * code has been formated to match the rest of the project
 */
public class Orange 
{
    public enum State 
    {
        Fetched(15),
        Peeled(38),
        Squeezed(29),
        Bottled(17),
        Processed(1);

        private static final int finalIndex = State.values().length - 1;

        final int timeToComplete;

        State(int timeToComplete) 
        {
            this.timeToComplete = timeToComplete;
        }

        State getNext() 
        {
            int currIndex = this.ordinal();
           
            if (currIndex >= finalIndex) 
            {
                throw new IllegalStateException("Already at final state");
            }
            return State.values()[currIndex + 1];
        }
    }

    private State state;

    public Orange() 
    {
        state = State.Fetched;
        doWork();
    }

    //Generated setState so workers can set the state instead of just getting the stateP
    public void setState(State state) 
    {
		this.state = state;
	}

	public State getState() 
    {
        return state;
    }

    public void runProcess() 
    {
        // Don't attempt to process an already completed orange
        if (state == State.Processed) 
        {
            throw new IllegalStateException("This orange has already been processed");
        }
        doWork();
        state = state.getNext();
    }

    private void doWork() 
    {
        // Sleep for the amount of time necessary to do the work
        try 
        {
            Thread.sleep(state.timeToComplete);
        } 
        catch (InterruptedException e) 
        {
            System.err.println("Incomplete orange processing, juice may be bad");
        }
    }
}
