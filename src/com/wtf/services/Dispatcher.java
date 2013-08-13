package com.wtf.services;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wtf.commons.Configuration;
import com.wtf.commons.Entry;
import com.wtf.commons.ForwarderFactory;
import com.wtf.commons.RegistrySingleton;
import com.wtf.comunications.Forwarder;
import com.wtf.comunications.messages.Message;
import com.wtf.comunications.messages.ReqDispatcherRegisterMessage;
import com.wtf.listener.ReceiverListener;

public class Dispatcher  {
			
	private Forwarder forwarder;		

	
	public Dispatcher() throws IOException {	
		forwarder = ForwarderFactory.get();
		ExecutorService service = Executors.newFixedThreadPool(10);
		service.submit(new ReceiverListener(this));
	}
	
	public void registerService(Message inputMessage){
		ReqDispatcherRegisterMessage input = (ReqDispatcherRegisterMessage) inputMessage;
		Entry theEntry = new Entry(input.getIpAddress() ,input.getPort(), Configuration.PROTOCOL);
		RegistrySingleton.getInstance().put(input.getSender(), theEntry);
		
	}
	
	public void unregisterService(){
		
	}
	
	public void getRegistry(Message inputMessage) throws IOException{
		Message message = new ReqDispatcherRegisterMessage(Configuration.HOST, 
				RegistrySingleton.getInstance().getAll());
		forwarder.sendMessage(inputMessage.getSender() , message);
	}
	                              
	public static void main(String[] args) throws IOException{
		new Dispatcher();
	}
	
}