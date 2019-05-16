package com.serotonin.bacnet4j.test.DaqTest;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.test.DaqTest.WriteTest.Listener;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.LimitEnable;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.queue.ByteQueue;

public class BacnetServer {
	
	private final int deviceId = (int) Math.floor(Math.random() * 1000.0);

    public static void main(String[] args) throws Exception{
    	if (args.length != 2) {
            throw new RuntimeException("Usage: localIpAddr broadcastIpAddr");
        }
        String localIpAddr = args[0];
        String broadcastIpAddr = args[1];
        new BacnetServer(broadcastIpAddr, IpNetwork.DEFAULT_PORT, localIpAddr);
    }
    
    private boolean terminate;
    private IpNetwork network;
    private LocalDevice localDevice;
    
    private BACnetObject analogInput1;
    private BACnetObject analogOutput1;
    private BACnetObject analogValue1;
    
    private BACnetObject binaryInput1;
    private BACnetObject binaryOutput1;
    private BACnetObject binaryValue1;
    
    private BACnetObject Device;
    private BACnetObject File1;
    private BACnetObject Program1;
    
    private float resolution = 0.1f;

    
    public BacnetServer(String broadcastAddress, int port) throws BACnetServiceException, Exception {
        this(broadcastAddress, port, IpNetwork.DEFAULT_BIND_IP);
    }
    
    public BacnetServer(String broadcastAddress, int port, String localAddress) throws BACnetServiceException, Exception {
    	network = new IpNetwork(broadcastAddress, port,
                IpNetwork.DEFAULT_BIND_IP, 0, localAddress);
            System.out.println("Creating LoopDevice id " + deviceId);
          Transport transport = new Transport(network);
          transport.setTimeout(1000);
          localDevice = new LocalDevice(deviceId, transport);
          try {
        	  localDevice.getEventHandler().addListener(new Listener());
        	  
        	  localDevice.getConfiguration().setProperty(PropertyIdentifier.modelName,
                      new CharacterString("BACnet4J LoopDevice"));
        	  
        	  analogInput1 = new BACnetObject(localDevice, localDevice.getNextInstanceObjectIdentifier(ObjectType.analogInput));
        	  
        	  analogInput1.setProperty(PropertyIdentifier.presentValue, new Real(11));
        	  analogInput1.setProperty(PropertyIdentifier.objectName, new CharacterString("G1-RLT03-TM-01"));
        	  analogInput1.setProperty(PropertyIdentifier.deadband, new Real(14));
        	  analogInput1.setProperty(PropertyIdentifier.resolution, new Real(resolution));
        	  analogInput1.setProperty(PropertyIdentifier.eventEnable, new CharacterString("[true, true, true]"));
        	  analogInput1.setProperty(PropertyIdentifier.eventState, new EventState(0)); // 0 = normal
        	  analogInput1.setProperty(PropertyIdentifier.objectType, new ObjectType(0)); // 0 = analogInput
        	  analogInput1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
        	  analogInput1.setProperty(PropertyIdentifier.lowLimit, new Real(0));
        	  //analogInput1.setProperty(PropertyIdentifier.limitEnable, new LimitEnable(0));
        	  
        	  
        	  analogInput1.setProperty(PropertyIdentifier.outOfService, new Boolean(false));
        	  analogInput1.setProperty(PropertyIdentifier.units, EngineeringUnits.degreesCelsius);
        	  
          } catch (RuntimeException e) {
        	  System.out.println(e.getMessage());
          }
    }
}
