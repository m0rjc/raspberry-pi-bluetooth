package m0rjc.pi2adp.system;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class MakeMapByAddressTask<T extends AddressedItem> implements Callable<Map<String, T>>
{
	private final Callable<Collection<T>> task;

	public MakeMapByAddressTask(final Callable<Collection<T>> task)
	{
		this.task = task;
	}

	public Map<String, T> call() throws Exception
	{
		Map<String, T> map = new HashMap<String, T>();
		for(T item : task.call())
		{
			map.put(item.getAddress(), item);
		}
		return map;
	}
}
