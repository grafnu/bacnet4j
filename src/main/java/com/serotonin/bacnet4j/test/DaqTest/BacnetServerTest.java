package com.serotonin.bacnet4j.test.DaqTest;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;

public class BacnetServerTest {
	
	private final int deviceId = (int) Math.floor(Math.random() * 1000.0);
	private IpNetwork network;
    private LocalDevice localDevice;

	public static void main(String[] args) throws BACnetServiceException, Exception {
		if (args.length != 2) {
            throw new RuntimeException("Usage: localIpAddr broadcastIpAddr");
        }
        String localIpAddr = args[0];
        String broadcastIpAddr = args[1];
        BacnetServerTest ld = new BacnetServerTest(broadcastIpAddr, IpNetwork.DEFAULT_PORT, localIpAddr);
        // java never terminates because of background daemon thread.

	}
	
	public BacnetServerTest(String broadcastAddress, int port, String localAddress) throws BACnetServiceException, Exception  {
		network = new IpNetwork(broadcastAddress, port,
	            IpNetwork.DEFAULT_BIND_IP, 0, localAddress);
	        System.out.println("Creating LoopDevice id " + deviceId);
	      Transport transport = new Transport(network);
	      transport.setTimeout(1000);
	      try {
	      localDevice = new LocalDevice(deviceId, transport);
	      localDevice.initialize();
	      
	      System.out.println("Local device is running with device id " + deviceId);
	        
	        ObjectIdentifier objectId = new ObjectIdentifier(ObjectType.analogValue, 1);

	        BACnetObject object = new BACnetObject(localDevice, objectId);
	        // BACnetObject object = new BACnetObject(objectId, "B'U'TOa");
	        
	        localDevice.getConfiguration().setProperty(PropertyIdentifier.modelName,
                    new CharacterString("BACnet4J LoopDevice"));
	        
	        object.setProperty(PropertyIdentifier.presentValue, new Real(12.3f));
	        object.setProperty(PropertyIdentifier.description, new CharacterString("Temperaturwert"));
	        object.setProperty(PropertyIdentifier.units, EngineeringUnits.degreesCelsius);
	        object.setProperty(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, false));
	        object.setProperty(PropertyIdentifier.eventState, EventState.normal);
	        object.setProperty(PropertyIdentifier.outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(false));

	        localDevice.addObject(object);
	      }catch (RuntimeException e) {
	    	  System.out.println("Ex in LoopDevice() ");
	            e.printStackTrace();
	            localDevice.terminate();
	            localDevice = null;
	            throw e;
	      }
	}

}
