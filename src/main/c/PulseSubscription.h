#ifndef __PULSE_SUBSCRIPTION_H__
#define __PULSE_SUBSCRIPTION_H__

#include <stdint.h>
#include <pulse/pulseaudio.h>
#include "PulseClient.h"

/**
 * Event types reported by the subscribe loop.
 */
enum pulse_client_event_type_t {
	/** An item has been created. */
	PC_EVENT_NEW,
	/** An item has been removed. */
	PC_EVENT_REMOVE
};

enum pulse_client_service_type_t {
	/** A source */
	PC_SERVICE_SOURCE,
	/** A sink */
	PC_SERVICE_SINK,
	/** A module */
	PC_SERVICE_MODULE
};

class PulseEventHandler {
public:
	virtual ~PulseEventHandler() {};
	virtual void onEvent(pulse_client_event_type_t event, pulse_client_service_type_t service, uint32_t index) = 0;
};

class PulseSubscription : public PulseBase {
private:
	int m_triggered;
	pa_subscription_event_type_t m_eventType;
	uint32_t m_index;
	PulseEventHandler *m_handler;

	/** Pulse API level callback function. */
	friend void _subscribe_callback(pa_context *c, pa_subscription_event_type_t t, uint32_t index, void *userData);
	/** Handle an event on the Pulse Thread by storing the information and handing back to the main thread. */
	void onEvent(pa_subscription_event_type_t t, uint32_t index);
	/** Handle the event on the blocking thread. */
	void fireEvent();
public:
	PulseSubscription(PulseClient &client, PulseEventHandler &handler);
	~PulseSubscription();

	/** Run the subscription on the current thread. This method blocks. */
	bool run();

};

#endif
