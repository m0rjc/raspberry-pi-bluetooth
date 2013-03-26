package m0rjc.pi2adp.pulseaudio;

/**
 * A Java thread to run the subscribe loop on.
 *
 * @author Richard Corfield <m0rjc@m0rjc.me.uk>
 */
class PulseSubscribeThread extends Thread implements SubscribeHandler
{
	public PulseSubscribeThread()
	{
		super("Pulseaudio Subscribe Monitor");
		setDaemon(true);
	}

	@Override
	public synchronized void start()
	{
		PulseAudio.setSubsctribeHandler(this);
		PulseAudio.runSubscribeLoop();
	}

	public void onEvent(final PulseEventType type, final PulseObjectType target)
	{
	}
}
