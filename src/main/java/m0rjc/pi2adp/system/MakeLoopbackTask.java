package m0rjc.pi2adp.system;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task to make a loopback.
 *
 * @author Richard Corfield <m0rjc@m0rjc.me.uk>
 */
public class MakeLoopbackTask implements Callable<LoopBack>
{
	private final String sourceName;
	private int assignedId;

	public MakeLoopbackTask(final String sourceName)
	{
		this.sourceName = sourceName;
	}

	public LoopBack call() throws Exception
	{
		ProcessBuilder processBuilder = new ProcessBuilder("pactl","load-module","module-loopback","source=" + sourceName);
		SystemCommand command = new SystemCommand(processBuilder, new LineVisitor() {
			public void readLine(final String line)
			{
				assignedId = Integer.parseInt(line);
			}
		});

		Logger.getLogger(getClass().getName()).log(Level.INFO, "patching source " + sourceName);
		command.call();
		return new LoopBack(assignedId, sourceName, null);
	}
}
