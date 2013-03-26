/**
 * Code for the Pulse Subscription Class.
 * Manage a subscription to Pulse Events.
 */

#include <stdio.h>
#include <stdarg.h>
#include <pulse/pulseaudio.h>
#include "PulseSubscription.h"


/**
 * Handle the event on the Pulse Audio thread by passing data to the Java thread.
 */
void _subscribe_callback(pa_context *c, pa_subscription_event_type_t t, uint32_t index, void *userData)
{
	PulseSubscription *subscription = (PulseSubscription *)userData;
	subscription->onEvent(t, index);
}

void PulseSubscription::onEvent(pa_subscription_event_type_t t, uint32_t index)
{
	m_eventType = t;
	m_index = index;
	m_triggered = 1;
	mainLoopNotify();
}

/**
 * Handle the event on the Java thread.
 */
void PulseSubscription::fireEvent()
{
	pulse_client_event_type_t eventType;
	pulse_client_service_type_t serviceType;

	switch (m_eventType & PA_SUBSCRIPTION_EVENT_TYPE_MASK){
	case PA_SUBSCRIPTION_EVENT_NEW:
		eventType = PC_EVENT_NEW;
		break;
	case PA_SUBSCRIPTION_EVENT_REMOVE:
		eventType = PC_EVENT_REMOVE;
		break;
	default:
		return; // Not interested.
	}

	switch (m_eventType & PA_SUBSCRIPTION_EVENT_FACILITY_MASK){
	case PA_SUBSCRIPTION_EVENT_SOURCE:
		serviceType = PC_SERVICE_SOURCE;
		break;
	case PA_SUBSCRIPTION_EVENT_SINK:
		serviceType = PC_SERVICE_SINK;
		break;
	case PA_SUBSCRIPTION_EVENT_MODULE:
		serviceType = PC_SERVICE_MODULE;
		break;
	default:
		return; // Not interested.
	}

	m_handler->onEvent(eventType, serviceType, m_index);
}

PulseSubscription::PulseSubscription(PulseClient &client, PulseEventHandler &handler) :
		PulseBase(client), m_handler(&handler){}


bool PulseSubscription::run()
{
	pa_context *context = getContext();
	pa_operation *operation;
	mainLoopLock();

	// Must assume only one subscription will be alive. Can fix this if needed one day.
	pa_context_set_subscribe_callback(context, _subscribe_callback, this);

	m_triggered = 0;
	operation = pa_context_subscribe(context,
			(pa_subscription_mask_t)( PA_SUBSCRIPTION_MASK_SOURCE |
			PA_SUBSCRIPTION_MASK_SINK |
			PA_SUBSCRIPTION_MASK_MODULE),
			NULL,
			NULL);

	while(pa_operation_get_state(operation) != PA_OPERATION_CANCELLED){
		mainLoopWait();
		if(m_triggered){
			fireEvent();
			m_triggered = 0;
		}
	}

	pa_operation_unref(operation);
	mainLoopUnlock();
	return true;
}

PulseSubscription::~PulseSubscription()
{
}
