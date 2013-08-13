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
import com.wtf.comunications.messages.RespDispatcherRegisterMessage;
import com.wtf.listener.ReceiverListener;

public class Dispatcher  {
			
	private Forwarder forwarder;		
	private int frecuency;

	
	public Dispatcher() throws IOException {
		frecuency=Integer.valueOf(Configuration.FRECUENCY);
		forwarder = ForwarderFactory.get();
		ExecutorService service = Executors.newFixedThreadPool(10);
		service.submit(new ReceiverListener(this));
	}
	
	public void registerService(Message inputMessage) throws IOException{
		ReqDispatcherRegisterMessage input = (ReqDispatcherRegisterMessage) inputMessage;
		Entry theEntry = new Entry(input.getIpAddress() ,input.getPort(), Configuration.PROTOCOL);
		RegistrySingleton.getInstance().put(input.getSender(), theEntry);
		
		RespDispatcherRegisterMessage message = new RespDispatcherRegisterMessage(Configuration.HOST, 
				RegistrySingleton.getInstance().getAll());
		message.setFrecuency(frecuency);
		forwarder.sendMessage(inputMessage.getSender() , message);
			
		//TODO: Notiticar a todos los participantes que ha cambiado el registro
		
	}
	
	public void unregisterService(Message inputMessage){
		RegistrySingleton.getInstance().remove(inputMessage.getSender());
		
		//TODO: Notiticar a todos los participantes que ha cambiado el registro
	}
	
	                              
	public static void main(String[] args) throws IOException{
		new Dispatcher();
		Entry theEntry = new Entry(Configuration.IP , Integer.valueOf(Configuration.PORT), Configuration.PROTOCOL);
		RegistrySingleton.getInstance().put(Configuration.HOST, theEntry);
		
	}
	
}