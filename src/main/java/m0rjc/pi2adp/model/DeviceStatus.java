package m0rjc.pi2adp.model;

public class DeviceStatus
{
	public static enum State
	{
		DISCONNECTED,
		CONNECTED,
		PATCHED
	}

	private final String address;
	private String name;
	private State state;

	public DeviceStatus(final String address)
	{
		this.address = address;
		this.name = address;
		this.state = State.DISCONNECTED;
	}

	public String getName()
	{
		return name;
	}
	public void setName(final String name)
	{
		this.name = name;
	}
	public State getState()
	{
		return state;
	}
	public void setState(final State state)
	{
		this.state = state;
	}
	public String getAddress()
	{
		return address;
	}

	@Override
	public String toString()
	{
		return name + " (" + address + ") " + state;
	}


}
