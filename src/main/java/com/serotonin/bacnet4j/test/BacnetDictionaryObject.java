package com.serotonin.bacnet4j.test;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class BacnetDictionaryObject {
	Multimap<BacnetObjectType, Points>bacnetObjectMap = ArrayListMultimap.create();
	
	Multimap<String, DeviceObject>devices = ArrayListMultimap.create();
	
	Multimap<String, Multimap<BacnetObjectType, Hashtable<String, String>>>devices1 = ArrayListMultimap.create();
	
	public void addObject(BacnetObjectType bacnetObjectType, Hashtable<String, String> bacnetObject) {
		Points points = new Points(bacnetObject);
		bacnetObjectMap.put(bacnetObjectType, points);
	}
	
	public void addObject1(String remoteDevice, DeviceObject deviceObject) {
		devices.put(remoteDevice, deviceObject);
	}
	
	public void addObject2(String remoteDevice, Multimap<BacnetObjectType, Hashtable<String, String>> bacnetObjectMap2) {
		devices1.put(remoteDevice, bacnetObjectMap2);
	}
	
	public void printAllDevices() {
		System.out.println(" \n \n ******** PRINT ******** \n \n");
		
		for (Entry<String, Multimap<BacnetObjectType, Hashtable<String, String>>> deviceObject: devices1.entries()) {
			String deviceName = deviceObject.getKey();
			Multimap<BacnetObjectType, Hashtable<String, String>> deviceProperties = deviceObject.getValue();
			
			System.out.println("Device Name: " + deviceName);
			
			for (Entry<BacnetObjectType, Hashtable<String, String>> devicePropertiesObject: deviceProperties.entries()) {
				BacnetObjectType key = devicePropertiesObject.getKey();
			    Hashtable<String, String> value = devicePropertiesObject.getValue();
			    System.out.println(key);
			    for(Entry<String,String> v: value.entrySet()) {
			    	System.out.println(v.getKey() + ": " + v.getValue());
			    }
			    System.out.println("\n");
			}
		}
		System.out.println(" \n \n ******** END PRINT ******** \n \n");
	}
	
	public void print() {
		System.out.println(" \n \n ******** PRINT ******** \n \n");
		
		for (Entry<BacnetObjectType, Points> bacnetObject: bacnetObjectMap.entries()) {
			BacnetObjectType key = bacnetObject.getKey();
		    Points value = bacnetObject.getValue();
		    System.out.println(key);
		    for(Entry<String,String> v: value.points.entrySet()) {
		    	System.out.println(v.getKey() + ": " + v.getValue());
		    }
		    System.out.println("\n");
		}
		
		System.out.println(" \n \n ******** END PRINT ******** \n \n");
	}
	
	
	private static class DeviceObject {
		Multimap<BacnetObjectType, Points>bacnetObjectMap = ArrayListMultimap.create();
		
		public DeviceObject(BacnetObjectType bacnetObjectType, Hashtable<String, String> bacnetObject) {
			Points points = new Points(bacnetObject);
			this.bacnetObjectMap.put(bacnetObjectType, points);
		}
	}
	
	
	private static class Points {
		public Map<String, String> points = new Hashtable<String, String>();
		
		public Points(Hashtable<String, String> bacnetObject) {
			this.points = (Hashtable<String, String>) bacnetObject.clone();
		}
		
		public String get(String key) {
			return points.get(key);
		}
		
		public void getAll(Hashtable<String, String> points) {
			for(String a: points.keySet()) {
				
			}
		}
	}

}
