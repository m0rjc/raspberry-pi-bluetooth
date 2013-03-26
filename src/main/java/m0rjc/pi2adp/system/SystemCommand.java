package m0rjc.pi2adp.system;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Perform a command. Capture its output
 *
 * @author Richard Corfield &lt;m0rjc@m0rjc.me.uk&gt;
 */
class SystemCommand implements Callable<Integer>, Runnable
{
	private final ProcessBuilder builder;
	private final LineVisitor visitor;
	private int rc;

	/**
	 * @param process
	 */
	public SystemCommand(final ProcessBuilder builder, final LineVisitor visitor)
	{
		this.builder = builder;
		this.visitor = visitor;
	}

	public void run()
	{
		try
		{
			call();
		}
		catch (Exception e)
		{
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Could not call command " + builder + ": " + e);
		}
	}

	public Integer call() throws Exception
	{
		BufferedReader reader;
		Process process = builder.start();
		reader = getInputReader(process);

		String line;
		while( (line = reader.readLine()) != null)
		{
			visitor.readLine(line);
		}

		rc = process.waitFor();
		return rc;
	}

	public int getReturnCode()
	{
		return rc;
	}

	/**
	 * @param process
	 * @return
	 */
	private BufferedReader getInputReader(final Process process)
	{
		InputStream inputStream = process.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(reader);
		return br;
	}
}
