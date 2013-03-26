
#include "PulseBase.h"
#include <pulse/pulseaudio.h>
#include <stdarg.h>
#include <stdio.h>

PulseBase::PulseBase():
	m_mainLoop(NULL), m_api(NULL), m_context(NULL), m_error(NULL){}

PulseBase::PulseBase(PulseBase &copy) :
	m_mainLoop(copy.m_mainLoop), m_api(copy.m_api), m_context(copy.m_context), m_error(NULL) {}

PulseBase::~PulseBase(){
	if(m_error){
		delete m_error;
	}
}

void PulseBase::fail(const char *format, ...){
	va_list argptr;
	va_start(argptr, format);
	fail(format, argptr);
	va_end(argptr);
}

void PulseBase::fail(const char *format, va_list argptr){
	if(m_error == NULL){
		m_error = new char[1024];
	}
	vsnprintf(m_error, 1024, format, argptr);
	m_error[1023] = '\0'; // Ensure termination in case snprintf overflowed.
}

