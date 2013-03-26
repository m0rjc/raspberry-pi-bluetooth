package m0rjc.pi2adp.system;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Subscribe to events from Pulse Audio.
 *
 * @author Richard Corfield <m0rjc@m0rjc.me.uk>
 */
public class PactlSubscribe extends Thread
{
	/** Time to buffer events so that multiple events close to eachother coalesce. */
	private static final int EVENT_BUFFERING_TIME = 1000;
	/** Maximum time to wait from the first event even if lots of new events are coming in. */
	private static final int MAXIMUM_DELAY = 10000;
	/** Pattern to recognise interesting output from the pactl command. */
	private static final Pattern INTERESTING = Pattern.compile("Event '(new|remove)' on source.*", Pattern.CASE_INSENSITIVE);
	/** Command to run. */
	private static final ProcessBuilder PACTL_PROCESS_BUILDER = new ProcessBuilder().command("pactl","subscribe").redirectErrorStream(true);

	private final Object internalSemaphore = new Object();
	private boolean interestingFound;
	private long timeOfFirstPendingEvent;
	private int sequence = 0;
	private State state;

	public PactlSubscribe()
	{
		setName("PACTL Subscribe Monitor");
		setDaemon(true);
	}

	public int getSequence()
	{
		return sequence;
	}

	@Override
	public void run()
	{
		try
		{
			startCommand();
		}
		catch (Exception e1)
		{
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to start the PACTL command: " + e1);
			return;
		}

		try
		{
			state = State.WAITING;
			while (true)
			{
				synchronized (internalSemaphore)
				{
					interestingFound = false;
					internalSemaphore.wait(EVENT_BUFFERING_TIME);
					state.onWakeup(this);
				}
			}
		}
		catch (InterruptedException e)
		{
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Thread interrupted.");
		}
	}

	/**
	 * Start the PACTL Subscribe Command
	 * @throws Exception
	 */
	private void startCommand() throws Exception
	{
		SystemCommand command = new SystemCommand(PACTL_PROCESS_BUILDER, new LineVisitor() {
			public void readLine(final String line)
			{
				if(INTERESTING.matcher(line).matches())
				{
					onInterestingInput();
				}
			}
		});

		Thread captureThread = new Thread(command, "PACTL Subscribe output capture");
		captureThread.setDaemon(true);
		captureThread.start();
	}

	/**
	 * Respond to interesting input.
	 */
	private void onInterestingInput()
	{
		synchronized (internalSemaphore)
		{
			interestingFound = true;
			internalSemaphore.notifyAll();
		}
	}

	/**
	 * Indicate that something has happened.
	 */
	private synchronized void fireEvent()
	{
		sequence++;
		notifyAll();
	}

	/**
	 * State Pattern for the PACTL Subscribe Monitor.
	 *
	 * @author Richard Corfield <m0rjc@m0rjc.me.uk>
	 */
	private static enum State
	{
		WAITING
		{
			@Override
			void onWakeup(final PactlSubscribe caller)
			{
				if(caller.interestingFound)
				{
					caller.timeOfFirstPendingEvent = System.currentTimeMillis();
					caller.state = BUFFERING;
				}
			}
		},
		BUFFERING
		{
			@Override
			void onWakeup(final PactlSubscribe caller)
			{
				if(caller.interestingFound)
				{
					long now = System.currentTimeMillis();
					if((now - caller.timeOfFirstPendingEvent) > MAXIMUM_DELAY)
					{
						caller.fireEvent();
						caller.timeOfFirstPendingEvent = now;
					}
				}
				else
				{
					caller.fireEvent();
					caller.state = WAITING;
				}
			}
		};

		abstract void onWakeup(PactlSubscribe caller);
	}

	/**
	 * For testing
	 * @param args
	 * @throws InterruptedException waiting for notification.
	 */
	public static void main(final String[] args) throws InterruptedException
	{
		PactlSubscribe pactl = new PactlSubscribe();
		pactl.start();
		synchronized (pactl)
		{
			while(pactl.isAlive())
			{
				pactl.wait();
				System.out.println("PACTL Notification seqeunce: " + pactl.getSequence());
			}
		}
		System.out.println("Monitor stopped.");
	}
}
