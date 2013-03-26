/**
 * Pulse Client separated out to allow better testing.
 */

#ifndef __PULSE_CLIENT_H__
#define __PULSE_CLIENT_H__

#include <stdint.h>
#include <pulse/pulseaudio.h>
#include "PulseBase.h"

enum pulse_client_state_t {
	PC_STATE_NEW,
	PC_STATE_STARTING,
	PC_STATE_RUNNING,
	PC_STATE_FAILED,
	PC_STATE_TERMINATED
};

class PulseClient : public PulseBase {
private:
	pulse_client_state_t m_state;

	friend void _context_state_callback(pa_context *c, void *userData);
	void onContextReady();
	void onContextStartFailed();
	void onContextTerminated();
protected:
	void fail(const char *format, ...);
public:
	PulseClient(const char *clientName);
	~PulseClient();
	pulse_client_state_t getState();

	/**
	 * Conect to Pulse Audio. Blocking operation.
	 * @return true if successful.
	 */
	bool connect();
};

#endif
