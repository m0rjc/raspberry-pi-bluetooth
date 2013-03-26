/*
 * test.c
 *
 *  Created on: 18 Mar 2013
 *      Author: richard
 */

#include "PulseClient.h"
#include "PulseSubscription.h"
#include <stdio.h>

class MySubscribeHandler: public PulseEventHandler {
public:
	void onEvent(pulse_client_event_type_t type,
			pulse_client_service_type_t service, uint32_t index) {
		switch (type) {
		case PC_EVENT_NEW:
			printf("Added ");
			break;
		case PC_EVENT_REMOVE:
			printf("Removed ");
			break;
		default:
			printf("(unknown event type) ");
			break;
		}

		switch (service) {
		case PC_SERVICE_SOURCE:
			printf(" Source ");
			break;
		case PC_SERVICE_SINK:
			printf(" Sink ");
			break;
		case PC_SERVICE_MODULE:
			printf(" Module ");
			break;
		default:
			printf("(unknown facility) ");
			break;
		}

		printf("%d\n", index);
		fflush(stdout);
	}
};

int main(int argc, char **argv) {
	PulseClient client("Test Client");
	bool result = client.connect();
	printf("Pulse test client result %d\n", result);
	if (!result) {
		puts(client.getLastError());
		putchar('\n');
	} else {
		MySubscribeHandler handler;
		PulseSubscription subscription(client, handler);
		subscription.run();
	}
}
