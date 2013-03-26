package m0rjc.pi2adp.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A command to read the sources from Pulse Audio and pick out those from bluetooth devices.
 *
 * @author Richard Corfield <m0rjc@m0rjc.me.uk>
 */
public class ListBluetoothSourcesTask implements Callable<Collection<BluetoothSource>>
{
	private final ProcessBuilder processBuilder = new ProcessBuilder("pactl","list","short","sources");
	private final Pattern bluetoothPattern = Pattern.compile("bluez_source\\.([A-F0-9_]+)");

	public Collection<BluetoothSource> call() throws Exception
	{
		final List<BluetoothSource> foundSources = new ArrayList<BluetoothSource>();

		SystemCommand command = new SystemCommand(processBuilder, new LineVisitor() {
			public void readLine(final String line)
			{
				String[] fields = line.split("\t");
				if(fields.length > 1)
				{
					String name = fields[1];
					Matcher matcher = bluetoothPattern.matcher(name);
					if(matcher.matches())
					{
						String address = matcher.group(1).replace('_', ':');
						foundSources.add(new BluetoothSource(address, name));
					}
				}
			}
		});

		command.call();

		return foundSources;
	}

}
