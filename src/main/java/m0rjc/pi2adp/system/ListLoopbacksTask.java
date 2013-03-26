package m0rjc.pi2adp.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * A task to list pulse-audio loopbacks.
 * @author Richard Corfield <m0rjc@m0rjc.me.uk>
 */
public class ListLoopbacksTask implements Callable<Collection<LoopBack>>
{
	private static final String MODULE_LOOPBACK = "module-loopback";
	private static final String PARAMETER_SOURCE = "source=";
	private static final String PARAMETER_SINK = "sink=";
	private final ProcessBuilder processBuilder = new ProcessBuilder("pactl","list","short","modules");

	public Collection<LoopBack> call() throws Exception
	{
		final List<LoopBack> found = new ArrayList<LoopBack>();

		SystemCommand command = new SystemCommand(processBuilder, new LineVisitor() {
			public void readLine(final String line)
			{
				String[] fields = line.split("\\s");
				if(fields.length > 2 && MODULE_LOOPBACK.equals(fields[1]))
				{
					int index = Integer.parseInt(fields[0]);
					String source = null;
					String sink = null;
					for(int i = 2; i < fields.length; i++)
					{
						String field = fields[i];
						if(field.startsWith(PARAMETER_SOURCE))
						{
							source = field.substring(PARAMETER_SOURCE.length());
						}
						else if(field.startsWith(PARAMETER_SINK))
						{
							sink = field.substring(PARAMETER_SINK.length());
						}
					}
					if(source != null)
					{
						found.add(new LoopBack(index, source, sink));
					}
				}
			}
		});

		command.call();

		return found;
	}
}
