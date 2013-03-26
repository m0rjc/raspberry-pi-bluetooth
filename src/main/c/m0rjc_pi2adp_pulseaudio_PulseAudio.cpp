/**
 * Bridge between Java JNI and the Pulse Client.
 */

#include <jni.h>
#include "m0rjc_pi2adp_pulseaudio_PulseAudio.h"
#include "PulseClient.h"
#include "PulseSubscription.h"

static PulseClient *pulseClient;

class JniPulseEventHandler : public PulseEventHandler {
private:
	JNIEnv *m_jni;
	jmethodID m_callbackMethod;
	jclass m_callbackTarget;
public:
	JniPulseEventHandler(JNIEnv *jni, jclass javaWrapperClass);
	void onEvent(pulse_client_event_type_t event, pulse_client_service_type_t service, uint32_t index);
};

static void _throwRuntimeException(JNIEnv * jni, const char *message)
{
	jclass exClass = jni->FindClass("java/lang/RuntimeException");
	jni->ThrowNew(exClass, message);
}

/*
 * Class:     m0rjc_pi2adp_pulseaudio_PulseAudio
 * Method:    connect
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_m0rjc_pi2adp_pulseaudio_PulseAudio_connect
  (JNIEnv * jni, jclass cls)
{
	pulseClient = new PulseClient("Java JNI Client");
	return pulseClient->connect();
}

/*
 * Class:     m0rjc_pi2adp_pulseaudio_PulseAudio
 * Method:    getLastError
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_m0rjc_pi2adp_pulseaudio_PulseAudio_getLastError
  (JNIEnv * jni, jclass cls)
{
	const char *error = pulseClient->getLastError();
	return jni->NewStringUTF(error);
}

/*
 * Class:     m0rjc_pi2adp_pulseaudio_PulseAudio
 * Method:    disconnect
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_m0rjc_pi2adp_pulseaudio_PulseAudio_disconnect
  (JNIEnv * jni, jclass cls)
{
	delete pulseClient;
}

JniPulseEventHandler::JniPulseEventHandler(JNIEnv *jni, jclass javaWrapperClass) :
		m_jni(jni), m_callbackTarget(javaWrapperClass){
	m_callbackMethod = jni->GetStaticMethodID(javaWrapperClass, "fireSubscribeEvent", "(III)V");
	if(m_callbackMethod == 0){
		_throwRuntimeException(m_jni, "Unable to find fireSubscribeEvent(int, int, int) method of PulseAudio wrapper class.");
	}
}

void JniPulseEventHandler::onEvent(pulse_client_event_type_t event, pulse_client_service_type_t service, uint32_t index){
	m_jni->CallStaticVoidMethod(m_callbackTarget, m_callbackMethod, (int)event, (int)service, (int)index);
}

/*
 * Class:     m0rjc_pi2adp_pulseaudio_PulseAudio
 * Method:    runSubscribeLoop
 * Signature: (Lm0rjc/pi2adp/pulseaudio/SubscribeHandler;)Z
 */
JNIEXPORT jboolean JNICALL Java_m0rjc_pi2adp_pulseaudio_PulseAudio_runSubscribeLoop
  (JNIEnv * jni, jclass cls)
{
	JniPulseEventHandler handler(jni, cls);
	PulseSubscription subscription(*pulseClient, handler);
	return subscription.run();
}

