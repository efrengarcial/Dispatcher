package com.wtf.controller;

import java.io.IOException;

import com.wtf.commons.ReceiverFactory;
import com.wtf.comunications.Receiver;
import com.wtf.comunications.messages.Message;
import com.wtf.comunications.messages.ReqDispatcherRegistryMessage;
import com.wtf.services.Dispatcher;

public class ControllerReceiver implements Runnable  {

	private Receiver receiver;
	private Dispatcher dispatcher; 

	public ControllerReceiver(Dispatcher dispatcher){
		this.dispatcher = dispatcher;
		receiver = ReceiverFactory.get();
	}

	@Override
	public void run() {
		while (true) {
			Message message;
			try {
				message = receiver.receiveMessage();
				manejarMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}


	private void manejarMessage(Message message) throws IOException {
		if (message instanceof ReqDispatcherRegistryMessage) {
			dispatcher.getRegistry(message);
		}
	}

}
