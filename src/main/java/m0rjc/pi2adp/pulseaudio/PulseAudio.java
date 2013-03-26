package m0rjc.pi2adp.pulseaudio;

/**
 * Provide access to Pulse Audio.
 * Must be a singleton. Works with the Pulse Audio Threaded Main Loop implementation.
 *
 * @author Richard Corfield <m0rjc@m0rjc.me.uk>
 */
final class PulseAudio
{
	private static SubscribeHandler subscribeHandler;
	
	static
	{
		System.loadLibrary("JavaPulse");
	}
	
	public static void main(String[] args) {
		if(connect())
		{
			runSubscribeLoop();
		} else {
			System.err.println(getLastError());
		}
	}

	/**
	 * Connect to Pulse Audio.
	 * @return true if success.
	 */
	public static native boolean connect();

	/**
	 * @return the last error message from the Pulse Audio layer.
	 */
	public static native String getLastError();
	
	/**
	 * Disconnect and clean pulse audio.
	 */
	public static native void disconnect();

	/**
	 * Run the subscribe loop on the current Java thread, calling back to the callback when interesting events come in.
	 * @param callback
	 * @return true if success.
	 */
	public static native boolean runSubscribeLoop();
	
	private static void fireSubscribeEvent(int eventType, int objectType, int index)
	{
		System.out.println("Java Subscribe Event: " + eventType + ", " + objectType + ", " + index);
		System.out.flush();
		if(subscribeHandler != null)
		{
			
		}
	}
	
	public static void setSubsctribeHandler(SubscribeHandler handler)
	{
		subscribeHandler = handler;
	}
}
