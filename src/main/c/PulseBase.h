/**
 * Base class to support working with Pulse.
 */

#ifndef __PULSE_BASE_H_
#define __PULSE_BASE_H_

#include <stdint.h>
#include <stdarg.h>
#include <pulse/pulseaudio.h>

class PulseBase {
private:
	pa_threaded_mainloop *m_mainLoop;
	pa_mainloop_api *m_api;
	pa_context *m_context;
	char *m_error;
protected:
	/**
	 * Constructor for a class which will call setPulseAudio when ready.
	 */
	PulseBase();

	/**
	 * Constructor to allow easy assignment of the Pulse Audio parts.
	 */
	PulseBase(PulseBase &copy);

	virtual void fail(const char *format, ...);
	void fail(const char *format, va_list argptr);

	pa_threaded_mainloop *getMainLoop();
	pa_context *getContext();
	void setPulseAudio(pa_threaded_mainloop *mainLoop, pa_mainloop_api *api, pa_context *context);

	void mainLoopLock();
	void mainLoopUnlock();
	void mainLoopWait();
	void mainLoopNotify();

public:
	const char *getLastError();
	bool hasError();
	virtual ~PulseBase();
};

inline const char *PulseBase::getLastError() { return m_error; }
inline bool PulseBase::hasError() { return m_error != NULL; }
inline pa_threaded_mainloop *PulseBase::getMainLoop() { return m_mainLoop; }
inline pa_context *PulseBase::getContext() { return m_context; }
inline void PulseBase::setPulseAudio(pa_threaded_mainloop *mainLoop, pa_mainloop_api *api, pa_context *context){
	m_mainLoop = mainLoop;
	m_api = api;
	m_context = context;
}

inline void PulseBase::mainLoopLock() { pa_threaded_mainloop_lock(m_mainLoop); }
inline void PulseBase::mainLoopUnlock() { pa_threaded_mainloop_unlock(m_mainLoop); }
inline void PulseBase::mainLoopWait() { pa_threaded_mainloop_wait(m_mainLoop); }
inline void PulseBase::mainLoopNotify() { pa_threaded_mainloop_signal(m_mainLoop, 0); }

#endif

