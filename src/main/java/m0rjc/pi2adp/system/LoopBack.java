package m0rjc.pi2adp.system;

public class LoopBack
{
	private final int index;
	private final String source;
	private final String sink;

	public LoopBack(final int index, final String source, final String sink)
	{
		this.index = index;
		this.source = source;
		this.sink = sink;
	}

	public int getIndex()
	{
		return index;
	}

	public String getSource()
	{
		return source;
	}

	public String getSink()
	{
		return sink;
	}

	@Override
	public String toString()
	{
		return "LoopBack[" + index+ ": " + source + " -> " + (sink != null ? sink : "(default)") + "]";
	}
}
