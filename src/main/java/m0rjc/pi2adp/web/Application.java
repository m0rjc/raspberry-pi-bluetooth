/**
 *
 */
package m0rjc.pi2adp.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import m0rjc.pi2adp.system.PollingSystem;

/**
 * @author Richard Corfield &lt;m0rjc@m0rjc.me.uk&gt;
 */
@WebListener
public class Application implements ServletContextListener
{
	public static final String POLLING_SYSTEM_ATTRIBUTE = "m0rjc.pi2adp.PollingSystem";

	public void contextInitialized(final ServletContextEvent event)
	{
		PollingSystem system = new PollingSystem();
		system.start();

		event.getServletContext().setAttribute(POLLING_SYSTEM_ATTRIBUTE, system);
	}

	public void contextDestroyed(final ServletContextEvent event)
	{
		PollingSystem polling = (PollingSystem) event.getServletContext().getAttribute(POLLING_SYSTEM_ATTRIBUTE);
		polling.pleaseStop();
	}
}
