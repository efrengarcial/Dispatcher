package com.wtf.listener;

import java.io.IOException;

import com.wtf.commons.Configuration;
import com.wtf.commons.ReceiverFactory;
import com.wtf.comunications.Receiver;
import com.wtf.comunications.messages.Message;
import com.wtf.comunications.messages.ReqDispatcherRegisterMessage;
import com.wtf.comunications.messages.ReqDispatcherUnRegisterMessage;
import com.wtf.comunications.messages.ReqDispatcherUpFrecuencyMessage;
import com.wtf.services.Dispatcher;

public class AppListener implements Runnable  {

	private Receiver receiver;
	private Dispatcher dispatcher; 

	public AppListener(Dispatcher dispatcher){
		this.dispatcher = dispatcher;
		receiver = ReceiverFactory.get(Integer.parseInt(Configuration.PORT), Configuration.PROTOCOL);
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
		if (message instanceof ReqDispatcherRegisterMessage) {
			dispatcher.registerService(message);
		} else if (message instanceof ReqDispatcherUnRegisterMessage) {
			dispatcher.unregisterService(message);
		} else if (message instanceof ReqDispatcherUpFrecuencyMessage) {
			dispatcher.setFrecuency(Integer.valueOf(message.getData().toString()));
		}
	}

}
