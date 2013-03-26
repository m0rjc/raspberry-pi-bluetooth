package m0rjc.pi2adp.system;

/**
 * A Blutooth Pulse Source
 *
 * @author Richard Corfield <m0rjc@m0rjc.me.uk>
 */
public class BluetoothSource implements AddressedItem
{
	private final String name;
	private final String address;

	public BluetoothSource(final String address, final String name)
	{
		this.name = name;
		this.address = address;
	}

	/**
	 * @return the name known to pulseaudio
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the mac address in aa:bb:cc format.
	 */
	public String getAddress()
	{
		return address;
	}

	@Override
	public String toString()
	{
		return "BluetoothSource[name=\"" + name + "\", address=\"" + address + "\"]";
	}


}
