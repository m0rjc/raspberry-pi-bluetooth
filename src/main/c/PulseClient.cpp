/**
 * Pulse Client separated out to allow better testing.
 */

#include <stdio.h>
#include <pulse/pulseaudio.h>
#include "PulseClient.h"

PulseClient::PulseClient(const char *clientName) : PulseBase() {
	pa_threaded_mainloop *mainLoop;
	pa_mainloop_api *api;
	pa_context *context;

	if(!(mainLoop = pa_threaded_mainloop_new())){
		fail("Unable to create PA Main Loop.");
		return;
	}

	api = pa_threaded_mainloop_get_api(mainLoop);

	if(!(context = pa_context_new(api, clientName))){
		fail("Unable to create PA Context");
		return;
	}

	setPulseAudio(mainLoop, api, context);
	m_state = PC_STATE_NEW;
}

PulseClient::~PulseClient(){
	// TODO: Destroy the Pulse Audio artefacts.
}

/**
 * Override fail to maintain the state variable too.
 */
void PulseClient::fail(const char *format, ...)
{
	va_list argptr;
	va_start(argptr, format);
	PulseBase::fail(format, argptr);
	va_end(argptr);

	m_state = PC_STATE_FAILED;
}

/**
 * C callback for the Pulse Audio engine for context state change.
 */
void _context_state_callback(pa_context *c, void *userData)
{
	PulseClient *client = (PulseClient *)userData;

	switch (pa_context_get_state(c)){
	case PA_CONTEXT_READY:
		client->onContextReady();
		break;
	case PA_CONTEXT_FAILED:
		client->onContextStartFailed();
		break;
	case PA_CONTEXT_TERMINATED:
		client->onContextTerminated();
		break;
	case PA_CONTEXT_UNCONNECTED:
	case PA_CONTEXT_CONNECTING:
	case PA_CONTEXT_AUTHORIZING:
	case PA_CONTEXT_SETTING_NAME:
		break;
	}
}

bool PulseClient::connect()
{
	pa_context *context = getContext();
	pa_threaded_mainloop *mainLoop = getMainLoop();

	if(m_state != PC_STATE_NEW) {
		if(!hasError()){
			fail("Cannot call connect() when not new.");
		}
		return false;
	}

	pa_context_set_state_callback(context, _context_state_callback, this);
	if (pa_context_connect(context, NULL, PA_CONTEXT_NOAUTOSPAWN, NULL) < 0){
		fail("Unable to start PA connect.");
		return false;
	}

	m_state = PC_STATE_STARTING;
	if(pa_threaded_mainloop_start(mainLoop) < 0){
		fail("Pulse Audio Connection Failure: %s", pa_strerror(pa_context_errno(context)));
		return -1;
	}

	mainLoopLock();
	while(m_state == PC_STATE_STARTING){
		mainLoopWait();
	}
	mainLoopUnlock();
	return m_state == PC_STATE_RUNNING;
}

void PulseClient::onContextReady(){
	m_state = PC_STATE_RUNNING;
	mainLoopNotify();
}

void PulseClient::onContextStartFailed(){
	fail("Pulse connection failed: %s", pa_strerror(pa_context_errno(getContext())));
	mainLoopNotify();
}

void PulseClient::onContextTerminated(){
	fail("Pulse context terminated.");
	m_state = PC_STATE_TERMINATED;
	mainLoopNotify();
}


