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
import com.wtf.comunications.messages.RespDispatcherUnRegisterMessage;
import com.wtf.listener.AppListener;

public class Dispatcher  {
			
	private Forwarder forwarder;		
	private int frecuency;

	
	public Dispatcher() throws IOException {
		this.starUp();
		setFrecuency(Integer.valueOf(Configuration.FRECUENCY));
		forwarder = ForwarderFactory.get(Integer.parseInt(Configuration.PORT), Configuration.PROTOCOL);
		ExecutorService service = Executors.newFixedThreadPool(10);
		service.submit(new AppListener(this));
	}
	
	private void starUp(){
		Entry theEntry = new Entry(Configuration.IP , Integer.valueOf(Configuration.PORT), Configuration.PROTOCOL);
		RegistrySingleton.getInstance().put(Configuration.lOCALHOST, theEntry);
	}
	
	public void registerService(Message inputMessage) throws IOException{
		ReqDispatcherRegisterMessage input = (ReqDispatcherRegisterMessage) inputMessage;
		Entry theEntry = new Entry(input.getIpAddress() ,input.getPort(), Configuration.PROTOCOL);
		RegistrySingleton.getInstance().put(input.getSender(), theEntry);
		//Notiticar a todos los participantes que ha cambiado el registro
		notifyChangesRegisterAll();
	}
		
	
	public void unregisterService(Message inputMessage) throws IOException{
		RegistrySingleton.getInstance().remove(inputMessage.getSender());		
		//Notiticar a todos los participantes que ha cambiado el registro
		notifyChangesUnRegisterAll(inputMessage);		
	}
		
	
	private void notifyChangesRegisterAll() throws IOException{
		System.out.println("Estaciones registradas...");
		for (java.util.Map.Entry<String, Entry> station : RegistrySingleton.getInstance().getAll().entrySet()) {
			System.out.println(station.getKey() + "__"+ station.getValue().getDestinationId() +"__"+ station.getValue().getPortNr());
			if (!station.getKey().equals(Configuration.lOCALHOST)) {			
				RespDispatcherRegisterMessage message = new RespDispatcherRegisterMessage(Configuration.lOCALHOST, 
						RegistrySingleton.getInstance().getAll());
				message.setFrecuency(frecuency);
				forwarder.sendMessage(station.getKey() , message);
			}
		}	
	}
	
	private void notifyChangesUnRegisterAll(Message inputMessage) throws IOException{
		System.out.println("Estaciones registradas...");
		//Se envia a todos menos a el sender y Dispatcher
		for (java.util.Map.Entry<String, Entry> station : RegistrySingleton.getInstance().getAll().entrySet()) {
			System.out.println(station.getKey() + "__"+ station.getValue().getDestinationId() +"__"+ station.getValue().getPortNr());
			if (!station.getKey().equals(Configuration.lOCALHOST) && !station.getKey().equals(inputMessage.getSender()) ) {			
				RespDispatcherUnRegisterMessage message = new RespDispatcherUnRegisterMessage(Configuration.lOCALHOST, 
						RegistrySingleton.getInstance().getAll());				
				forwarder.sendMessage(station.getKey() , message);
			}
		}	
	}
	                              
	public int getFrecuency() {
		return frecuency;
	}

	public void setFrecuency(int frecuency) {
		System.out.println("Frecuencia actualiza.... "+ frecuency);
		this.frecuency = frecuency;
	}

	public static void main(String[] args) throws IOException{
		System.out.println("Iniciando Dispatcher......");
		new Dispatcher();		
	}
	
}