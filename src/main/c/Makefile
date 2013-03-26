JAVA_HOME=/usr/lib/jvm/default-java
CFLAGS=-g -I$(JAVA_HOME)/include -fPIC
LDFLAGS=-lstdc++ -lpulse-mainloop-glib -lpulse -lglib-2.0
CC=g++
OBJECTS=PulseBase.o PulseClient.o PulseSubscription.o m0rjc_pi2adp_pulseaudio_PulseAudio.o

libJavaPulse.so: $(OBJECTS)
	$(CC) -shared $(OBJECTS) $(LDFLAGS) -o libJavaPulse.so

test: PulseBase.o PulseClient.o PulseSubscription.o test.o
	$(CC) -o test PulseBase.o PulseClient.o PulseSubscription.o test.o $(LDFLAGS)

clean:
	rm *.o *.so test

PulseBase.o: PulseBase.cpp PulseBase.h
	$(CC) -c $(CFLAGS) PulseBase.cpp
	
PulseClient.o: PulseClient.cpp PulseClient.h PulseBase.h
	$(CC) -c $(CFLAGS) PulseClient.cpp

PulseSubscription.o: PulseSubscription.cpp PulseSubscription.h PulseClient.h PulseBase.h
	$(CC) -c $(CFLAGS) PulseSubscription.cpp

m0rjc_pi2adp_pulseaudio_PulseAudio.o: m0rjc_pi2adp_pulseaudio_PulseAudio.cpp m0rjc_pi2adp_pulseaudio_PulseAudio.h PulseClient.h PulseSubscription.h PulseBase.h
	$(CC) -c $(CFLAGS)  m0rjc_pi2adp_pulseaudio_PulseAudio.cpp

test.o: test.cpp PulseClient.h PulseBase.h PulseSubscription.h
	$(CC) -c $(CFLAGS) test.cpp

