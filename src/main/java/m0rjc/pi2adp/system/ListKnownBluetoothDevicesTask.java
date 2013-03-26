package m0rjc.pi2adp.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * Task to list bluetooth devices known to the system.
 *
 * @author Richard Corfield <m0rjc@m0rjc.me.uk>
 */
public class ListKnownBluetoothDevicesTask implements Callable<Collection<BluetoothDevice>>
{
	private final ProcessBuilder processBuilder = new ProcessBuilder("bluez-test-device","list");

	public Collection<BluetoothDevice> call() throws Exception
	{
		final List<BluetoothDevice> found = new ArrayList<BluetoothDevice>();

		SystemCommand command = new SystemCommand(processBuilder, new LineVisitor() {
			public void readLine(final String line)
			{
				String[] fields = line.split(" ", 2);
				if(fields.length > 1)
				{
					found.add(new BluetoothDevice(fields[0], fields[1]));
				}
			}
		});

		command.call();

		return found;
	}
}
