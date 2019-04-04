package com.serotonin.bacnet4j.test;

import java.util.Hashtable;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class BacnetDictionaryObject {
	
	Multimap<String, Multimap<BacnetObjectType, Hashtable<String, String>>>devices = ArrayListMultimap.create();
	
	public void addObject(String remoteDevice, Multimap<BacnetObjectType, Hashtable<String, String>> bacnetObjectMap2) {
		devices.put(remoteDevice, bacnetObjectMap2);
	}
	
	public void printAllDevices() {
		System.out.println(" \n \n ******** PRINT ******** \n \n");
		
		for (Entry<String, Multimap<BacnetObjectType, Hashtable<String, String>>> deviceObject: devices.entries()) {
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
}
