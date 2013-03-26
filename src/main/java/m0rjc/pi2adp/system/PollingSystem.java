/**
 *
 */
package m0rjc.pi2adp.system;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import m0rjc.pi2adp.model.DeviceStatus;

/**
 * @author Richard Corfield &lt;m0rjc@m0rjc.me.uk&gt;
 *
 */
public class PollingSystem extends Thread
{
	private static final long HEARTBEAT_INTERVAL = 15000;

    private ExecutorService executor;
    private boolean stop = false;

    private Map<String, BluetoothDevice> knownDevices;
    private Map<String, DeviceStatus> statuses;

    /**
     * Standalone polling system.
     * @param args unused.
     */
    public static void main(final String[] args)
	{
		new PollingSystem().run();
	}

	/**
	 * Run the polling loop.
	 */
	@Override
	public void run()
	{
		executor = new ThreadPoolExecutor(0, 3, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
		try
		{
			while(!stop)
			{
				Thread.sleep(HEARTBEAT_INTERVAL);
				performPoll();
			}
		}
		catch (ExecutionException e1)
		{
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Exception in polling loop: " + e1);
		}
		catch (InterruptedException e)
		{
			// Nothing to do.
		}
		finally
		{
			executor.shutdown();
			executor = null;
		}
	}

	private void performPoll() throws InterruptedException, ExecutionException
	{
		HashMap<String, DeviceStatus> deviceStatuses = new HashMap<String, DeviceStatus>();

		Future<Collection<BluetoothSource>> futureSources = executor.submit(new ListBluetoothSourcesTask());
		Future<Collection<LoopBack>> futureLoopbacks = executor.submit(new ListLoopbacksTask());

		Set<String> patchedSources = readLoopbacks(futureLoopbacks);
		Set<String> unpatchedSources = readSourceList(deviceStatuses, futureSources, patchedSources);

		associateDeviceNamesReloadingIfNeeded(deviceStatuses);

		addDisconnectedDevices(deviceStatuses);

//		for(DeviceStatus status : deviceStatuses.values())
//		{
//			Logger.getLogger(getClass().getName()).log(Level.INFO, "Polled: " + status);
//		}

		statuses = deviceStatuses;
		// TODO: Notify here

		for(String name : unpatchedSources)
		{
			// TODO: Remember this future so I can report back on it
			executor.submit(new MakeLoopbackTask(name));
		}

		// TODO: Update statuses and notify here.
	}

	private void addDisconnectedDevices(final HashMap<String, DeviceStatus> deviceStatuses)
	{
		if(knownDevices != null)
		{
			for(BluetoothDevice knownDevice : knownDevices.values())
			{
				if(!deviceStatuses.containsKey(knownDevice.getAddress()))
				{
					DeviceStatus status = new DeviceStatus(knownDevice.getAddress());
					status.setName(knownDevice.getName());
					status.setState(DeviceStatus.State.DISCONNECTED);
					deviceStatuses.put(knownDevice.getAddress(), status);
				}
			}
		}
	}

	private Set<String> readLoopbacks(final Future<Collection<LoopBack>> futureLoopbacks) throws InterruptedException, ExecutionException
	{
		Set<String> patchedSources = new HashSet<String>();
		for(LoopBack loopback : futureLoopbacks.get())
		{
			patchedSources.add(loopback.getSource());
		}
		return patchedSources;
	}

	private Set<String> readSourceList(final HashMap<String, DeviceStatus> deviceStatuses, final Future<Collection<BluetoothSource>> futureSources, final Set<String> patchedSources) throws InterruptedException, ExecutionException
	{
		Set<String> unpatchedSources = new HashSet<String>();
		for(BluetoothSource source : futureSources.get())
		{
			String address = source.getAddress();
			String name = source.getName();

			DeviceStatus deviceStatus = new DeviceStatus(address);
			deviceStatuses.put(address, deviceStatus);

			if(patchedSources.contains(name))
			{
				deviceStatus.setState(DeviceStatus.State.PATCHED);
			}
			else
			{
				deviceStatus.setState(DeviceStatus.State.CONNECTED);
				unpatchedSources.add(name);
			}
		}
		return unpatchedSources;
	}

	private void associateDeviceNamesReloadingIfNeeded(final HashMap<String, DeviceStatus> deviceStatuses) throws InterruptedException, ExecutionException
	{
		boolean hasUnknownDevices = false;
		if(knownDevices != null)
		{
			hasUnknownDevices = associateDeviceNames(deviceStatuses);
		}

		// Reread known devices if I need to.
		if(knownDevices == null || hasUnknownDevices)
		{
			Future<Map<String, BluetoothDevice>> futureKnownDevices = executor.submit(new MakeMapByAddressTask<BluetoothDevice>(new ListKnownBluetoothDevicesTask()));
			knownDevices = futureKnownDevices.get();
			associateDeviceNames(deviceStatuses);
		}
	}

	private boolean associateDeviceNames(final HashMap<String, DeviceStatus> deviceStatuses)
	{
		boolean hasUnknownDevices = false;
		for(DeviceStatus status : deviceStatuses.values())
		{
			BluetoothDevice device = knownDevices.get(status.getAddress());
			if(device != null)
			{
				status.setName(device.getName());
			}
			else hasUnknownDevices = true;
		}
		return hasUnknownDevices;
	}

	/**
	 * Request the polling system stop.
	 */
	public void pleaseStop()
	{
		stop = true;
		interrupt();
	}

	public Collection<DeviceStatus> getDeviceStatusSummary()
	{
		if(statuses != null)
		{
			return Collections.unmodifiableCollection(statuses.values());
		}
		return null;
	}
}
