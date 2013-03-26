package m0rjc.pi2adp.web;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import m0rjc.pi2adp.model.DeviceStatus;
import m0rjc.pi2adp.system.PollingSystem;

@WebServlet(urlPatterns="/")
public class SimpleStatusServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
	{
		PollingSystem pollingSystem =  (PollingSystem) req.getServletContext().getAttribute(Application.POLLING_SYSTEM_ATTRIBUTE);
		Collection<DeviceStatus> summary = pollingSystem.getDeviceStatusSummary();

		resp.setContentType("text/plain");
		ServletOutputStream out = resp.getOutputStream();
		if(summary == null)
		{
			out.println("No poll completed yet");
		}
		else
		{
			for(DeviceStatus status : summary)
			{
				out.println(status.toString());
			}
		}
		out.flush();
	}

}
