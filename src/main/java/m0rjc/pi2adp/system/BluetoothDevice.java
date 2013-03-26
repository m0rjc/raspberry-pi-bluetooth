package m0rjc.pi2adp.system;

public class BluetoothDevice implements AddressedItem
{
	private final String address;
	private final String name;

	public BluetoothDevice(final String address, final String name)
	{
		this.address = address;
		this.name = name;
	}

	public String getAddress()
	{
		return address;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return "BluetoothDevice[name=\"" + name + "\", address=\"" + address + "\"]";
	}
}
