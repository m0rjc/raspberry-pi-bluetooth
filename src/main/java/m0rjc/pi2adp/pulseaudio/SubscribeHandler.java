package m0rjc.pi2adp.pulseaudio;

/**
 * Callback interface for the Pulse Audio subscribe handler.
 *
 * @author Richard Corfield <m0rjc@m0rjc.me.uk>
 */
interface SubscribeHandler
{
	void onEvent(PulseEventType type, PulseObjectType target);
}
