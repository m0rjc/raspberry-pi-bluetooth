/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class m0rjc_pi2adp_pulseaudio_PulseAudio */

#ifndef _Included_m0rjc_pi2adp_pulseaudio_PulseAudio
#define _Included_m0rjc_pi2adp_pulseaudio_PulseAudio
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     m0rjc_pi2adp_pulseaudio_PulseAudio
 * Method:    connect
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_m0rjc_pi2adp_pulseaudio_PulseAudio_connect
  (JNIEnv *, jclass);

/*
 * Class:     m0rjc_pi2adp_pulseaudio_PulseAudio
 * Method:    getLastError
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_m0rjc_pi2adp_pulseaudio_PulseAudio_getLastError
  (JNIEnv *, jclass);

/*
 * Class:     m0rjc_pi2adp_pulseaudio_PulseAudio
 * Method:    disconnect
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_m0rjc_pi2adp_pulseaudio_PulseAudio_disconnect
  (JNIEnv *, jclass);

/*
 * Class:     m0rjc_pi2adp_pulseaudio_PulseAudio
 * Method:    runSubscribeLoop
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_m0rjc_pi2adp_pulseaudio_PulseAudio_runSubscribeLoop
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
