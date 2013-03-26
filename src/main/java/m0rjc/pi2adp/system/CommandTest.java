/**
 *
 */
package m0rjc.pi2adp.system;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author Richard Corfield &lt;m0rjc@m0rjc.me.uk&gt;
 *
 */
public class CommandTest
{
	public static void main(final String[] args) throws IOException, InterruptedException, ExecutionException
	{
		final ExecutorService executor = new ThreadPoolExecutor(0, 3, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

		Future<Collection<BluetoothSource>> futureSources = executor.submit(new ListBluetoothSourcesTask());
		Future<Collection<BluetoothDevice>> futureDevices = executor.submit(new ListKnownBluetoothDevicesTask());
		Future<Collection<LoopBack>> futureLoopbacks = executor.submit(new ListLoopbacksTask());

		listResult(futureSources);
		listResult(futureDevices);
		listResult(futureLoopbacks);

		executor.shutdown();
	}

	private static <T> void listResult(final Future<Collection<T>> futureSources) throws InterruptedException, ExecutionException
	{
		Collection<T> sources = futureSources.get();
		for(T source : sources)
		{
			System.out.println(source);
		}
	}
}
