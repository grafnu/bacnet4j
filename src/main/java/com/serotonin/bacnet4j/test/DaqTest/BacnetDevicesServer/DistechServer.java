package com.serotonin.bacnet4j.test.DaqTest.BacnetDevicesServer;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.test.DaqTest.WriteTest.Listener;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.AddressBinding;
import com.serotonin.bacnet4j.type.constructed.CovSubscription;
import com.serotonin.bacnet4j.type.constructed.DateTime;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.LimitEnable;
import com.serotonin.bacnet4j.type.constructed.ObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.ObjectTypesSupported;
import com.serotonin.bacnet4j.type.constructed.Recipient;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.ServicesSupported;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.BackupState;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.enumerated.DeviceStatus;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.Polarity;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Reliability;
import com.serotonin.bacnet4j.type.enumerated.RestartReason;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.Date;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.SignedInteger;
import com.serotonin.bacnet4j.type.primitive.Time;
import com.serotonin.bacnet4j.type.primitive.Unsigned16;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

<<<<<<< HEAD
public class DistechServer implements Runnable {
=======
public class DistechServer {
>>>>>>> 6b4493f5d9b4e2640ebb9dcc659e5e05fda61b33

	private final int deviceId = (int) Math.floor(Math.random() * 1000.0);
//	private final int deviceId = 73;
	private IpNetwork network;
    private LocalDevice localDevice;
    
    private BACnetObject analogInput1;
    private BACnetObject analogOutput1;
    private BACnetObject analogValue1;
    
    private BACnetObject binaryInput1;
    private BACnetObject binaryOutput1;
    private BACnetObject binaryValue1;
    
	public static void main(String[] args) throws BACnetServiceException, Exception {
		if (args.length != 2) {
            throw new RuntimeException("Usage: localIpAddr broadcastIpAddr");
        }
        String localIpAddr = args[0];
        String broadcastIpAddr = args[1];
        new DistechServer(broadcastIpAddr, IpNetwork.DEFAULT_PORT, localIpAddr);
	}
	
	public DistechServer(String broadcastAddress, int port, String localAddress) throws BACnetServiceException, Exception {
		
		network = new IpNetwork(broadcastAddress, port,
	            IpNetwork.DEFAULT_BIND_IP, 0, localAddress);
	    System.out.println("Creating LoopDevice id " + deviceId);
	    Transport transport = new Transport(network);
	    transport.setTimeout(1000);
	    try {
	    	localDevice = new LocalDevice(deviceId, transport);
		      
		    System.out.println("Local device is running with device id " + deviceId);
		        
		    SequenceOf<Recipient> recipient = new SequenceOf<Recipient>();
		    SequenceOf<ObjectIdentifier> configurationFiles = new SequenceOf<ObjectIdentifier>();
		    localDevice.getConfiguration().setProperty(PropertyIdentifier.modelName,
		                        new CharacterString("BACnet4J LoopDevice"));
		    SequenceOf<EventTransitionBits> eventTransitionBits = new SequenceOf<EventTransitionBits>();

		    analogInput1 = new BACnetObject(localDevice, localDevice.getNextInstanceObjectIdentifier(ObjectType.analogInput));
		    analogInput1.setProperty(PropertyIdentifier.presentValue, new Real(0.0f));
		    analogInput1.setProperty(PropertyIdentifier.objectName, new CharacterString("device_run_command"));
		    analogInput1.setProperty(PropertyIdentifier.deadband, new Real(14));
		    analogInput1.setProperty(PropertyIdentifier.outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    analogInput1.setProperty(PropertyIdentifier.resolution, new Real(0.1f));
		    analogInput1.setProperty(PropertyIdentifier.eventEnable, new EventTransitionBits(true, true, true));
		    analogInput1.setProperty(PropertyIdentifier.eventState, new EventState(0)); // 0 = normal
		    analogInput1.setProperty(PropertyIdentifier.objectType, new ObjectType(0)); // 0 = analogInput
		    analogInput1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
		    analogInput1.setProperty(PropertyIdentifier.lowLimit, new Real(0));
		    analogInput1.setProperty(PropertyIdentifier.limitEnable, new LimitEnable(false, false));
		    analogInput1.setProperty(PropertyIdentifier.covIncrement, new Real(1.0f));
		    analogInput1.setProperty(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, false));
		    analogInput1.setProperty(PropertyIdentifier.updateInterval, new UnsignedInteger(1000));
		    analogInput1.setProperty(PropertyIdentifier.ackedTransitions, new EventTransitionBits(true, true, true));
		    analogInput1.setProperty(PropertyIdentifier.highLimit, new Real(0));
		    analogInput1.setProperty(PropertyIdentifier.notifyType, new NotifyType(0));
		    analogInput1.setProperty(PropertyIdentifier.eventDetectionEnable, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    analogInput1.setProperty(PropertyIdentifier.reliability, new Reliability(4));
		    analogInput1.setProperty(PropertyIdentifier.eventMessageTexts, eventTransitionBits);
		    analogInput1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
		    analogInput1.setProperty(PropertyIdentifier.notificationClass, new UnsignedInteger(0));
		    analogInput1.setProperty(PropertyIdentifier.description, new CharacterString("Distech device"));
		    analogInput1.setProperty(PropertyIdentifier.eventAlgorithmInhibit, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    analogInput1.setProperty(PropertyIdentifier.units, new EngineeringUnits(62)); // 62 = degrees celsius
		    localDevice.addObject(analogInput1);

		    analogOutput1 = new BACnetObject(localDevice, localDevice.getNextInstanceObjectIdentifier(ObjectType.analogOutput));
		    analogOutput1.setProperty(PropertyIdentifier.eventState, new EventState(0)); // 0 = normal
		    analogOutput1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
		    analogOutput1.setProperty(PropertyIdentifier.reliability, new Reliability(4));
		    analogOutput1.setProperty(PropertyIdentifier.resolution, new Real(0.1f));
		    analogOutput1.setProperty(PropertyIdentifier.eventAlgorithmInhibit, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    analogOutput1.setProperty(PropertyIdentifier.outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    analogOutput1.setProperty(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, false));
		    analogOutput1.setProperty(PropertyIdentifier.objectType, new ObjectType(0)); // 0 = analogInput
		    analogOutput1.setProperty(PropertyIdentifier.description, new CharacterString("Distech device"));
		    analogOutput1.setProperty(PropertyIdentifier.lowLimit, new Real(0));
		    analogOutput1.setProperty(PropertyIdentifier.deadband, new Real(14));
		    analogOutput1.setProperty(PropertyIdentifier.covIncrement, new Real(1.0f));
		    analogOutput1.setProperty(PropertyIdentifier.limitEnable, new LimitEnable(false, false));
		    analogOutput1.setProperty(PropertyIdentifier.objectName, new CharacterString("fan_run_status"));
		    analogOutput1.setProperty(PropertyIdentifier.notificationClass, new UnsignedInteger(0));
		    analogOutput1.setProperty(PropertyIdentifier.units, new EngineeringUnits(62)); // 62 = degrees celsius
		    analogOutput1.setProperty(PropertyIdentifier.notifyType, new NotifyType(0));
		    analogOutput1.setProperty(PropertyIdentifier.presentValue, new Real(0.0f));
		    analogOutput1.setProperty(PropertyIdentifier.deviceType, new CharacterString("Analog0To10Volts"));
		    analogOutput1.setProperty(PropertyIdentifier.relinquishDefault, new Real(0.0f));
		    analogOutput1.setProperty(PropertyIdentifier.eventEnable, new EventTransitionBits(true, true, true));
		    analogOutput1.setProperty(PropertyIdentifier.eventDetectionEnable, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    analogOutput1.setProperty(PropertyIdentifier.eventMessageTexts, eventTransitionBits);
		    analogOutput1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
		    analogOutput1.setProperty(PropertyIdentifier.highLimit, new Real(0));
		    analogOutput1.setProperty(PropertyIdentifier.ackedTransitions, new EventTransitionBits(true, true, true));
		    localDevice.addObject(analogOutput1);

		    analogValue1 = new BACnetObject(localDevice, localDevice.getNextInstanceObjectIdentifier(ObjectType.analogValue));
		    analogValue1.setProperty(PropertyIdentifier.objectName, new CharacterString("fan_run_speed_percentage_command"));
		    analogValue1.setProperty(PropertyIdentifier.presentValue, new Real(0.0f));
		    analogValue1.setProperty(PropertyIdentifier.deadband, new Real(14));
		    analogValue1.setProperty(PropertyIdentifier.outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    analogValue1.setProperty(PropertyIdentifier.relinquishDefault, new Real(0.0f));
		    analogValue1.setProperty(PropertyIdentifier.eventEnable, new EventTransitionBits(true, true, true));
		    analogValue1.setProperty(PropertyIdentifier.eventState, new EventState(0)); // 0 = normal
		    analogValue1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
		    analogValue1.setProperty(PropertyIdentifier.lowLimit, new Real(0));
		    analogValue1.setProperty(PropertyIdentifier.covIncrement, new Real(1.0f));
		    analogValue1.setProperty(PropertyIdentifier.limitEnable, new LimitEnable(false, false));
		    analogValue1.setProperty(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, false));
		    analogValue1.setProperty(PropertyIdentifier.ackedTransitions, new EventTransitionBits(true, true, true));
		    analogValue1.setProperty(PropertyIdentifier.highLimit, new Real(0));
		    analogValue1.setProperty(PropertyIdentifier.notifyType, new NotifyType(0));
		    analogValue1.setProperty(PropertyIdentifier.eventDetectionEnable, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    analogValue1.setProperty(PropertyIdentifier.reliability, new Reliability(4));
		    analogValue1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
		    analogValue1.setProperty(PropertyIdentifier.eventMessageTexts, eventTransitionBits);
		    analogValue1.setProperty(PropertyIdentifier.notificationClass, new UnsignedInteger(0));
		    analogValue1.setProperty(PropertyIdentifier.description, new CharacterString("Distech device"));
		    analogValue1.setProperty(PropertyIdentifier.units, new EngineeringUnits(62)); // 62 = degrees celsius
		    analogValue1.setProperty(PropertyIdentifier.eventAlgorithmInhibit, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    localDevice.addObject(analogValue1);

		    binaryInput1 = new BACnetObject(localDevice, localDevice.getNextInstanceObjectIdentifier(ObjectType.binaryInput));
		    binaryInput1.setProperty(PropertyIdentifier.objectName, new CharacterString("chiller_water_valve_percentage_command"));
		    binaryInput1.setProperty(PropertyIdentifier.presentValue, new BinaryPV(0)); //****//
		    binaryInput1.setProperty(PropertyIdentifier.outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    binaryInput1.setProperty(PropertyIdentifier.activeText, new CharacterString("TRUE"));
		    binaryInput1.setProperty(PropertyIdentifier.timeOfStateCountReset, new DateTime(13/05/2019));
		    binaryInput1.setProperty(PropertyIdentifier.eventEnable, new EventTransitionBits(true, true, true));
		    binaryInput1.setProperty(PropertyIdentifier.changeOfStateCount, new UnsignedInteger(0));
		    binaryInput1.setProperty(PropertyIdentifier.eventState, new EventState(0)); // 0 = normal
		    binaryInput1.setProperty(PropertyIdentifier.objectType, new ObjectType(0)); // 0 = analogInput
		    binaryInput1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
		    binaryInput1.setProperty(PropertyIdentifier.inactiveText, new CharacterString("FALSE"));
		    binaryInput1.setProperty(PropertyIdentifier.polarity, new Polarity(0));
		    binaryInput1.setProperty(PropertyIdentifier.alarmValue, new BinaryPV(0));
		    binaryInput1.setProperty(PropertyIdentifier.ackedTransitions, new EventTransitionBits(true, true, true));
		    binaryInput1.setProperty(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, false));
		    binaryInput1.setProperty(PropertyIdentifier.changeOfStateTime, new DateTime(13/05/2019));
		    binaryInput1.setProperty(PropertyIdentifier.notifyType, new NotifyType(0));
		    binaryInput1.setProperty(PropertyIdentifier.timeOfActiveTimeReset, new DateTime(13/05/2019));
		    binaryInput1.setProperty(PropertyIdentifier.eventDetectionEnable, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    binaryInput1.setProperty(PropertyIdentifier.reliability, new Reliability(4));
		    binaryInput1.setProperty(PropertyIdentifier.deviceType, new CharacterString("Analog0To10Volts"));
		    binaryInput1.setProperty(PropertyIdentifier.eventMessageTexts, eventTransitionBits);
		    binaryInput1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
		    binaryInput1.setProperty(PropertyIdentifier.elapsedActiveTime, new UnsignedInteger(0));
		    binaryInput1.setProperty(PropertyIdentifier.notificationClass, new UnsignedInteger(0));
		    binaryInput1.setProperty(PropertyIdentifier.description, new CharacterString("Distech device"));
		    binaryInput1.setProperty(PropertyIdentifier.eventAlgorithmInhibit, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    localDevice.addObject(binaryInput1);

		    binaryOutput1 = new BACnetObject(localDevice, localDevice.getNextInstanceObjectIdentifier(ObjectType.binaryOutput));
		    binaryOutput1.setProperty(PropertyIdentifier.eventState, new EventState(0)); // 0 = normal
		    binaryOutput1.setProperty(PropertyIdentifier.timeOfStateCountReset, new DateTime(13/05/2019));
		    binaryOutput1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
		    binaryOutput1.setProperty(PropertyIdentifier.reliability, new Reliability(4));
		    binaryOutput1.setProperty(PropertyIdentifier.inactiveText, new CharacterString("FALSE"));
		    binaryOutput1.setProperty(PropertyIdentifier.eventAlgorithmInhibit, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    binaryOutput1.setProperty(PropertyIdentifier.outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    binaryOutput1.setProperty(PropertyIdentifier.polarity, new Polarity(0));
		    binaryOutput1.setProperty(PropertyIdentifier.changeOfStateCount, new UnsignedInteger(0));
		    binaryOutput1.setProperty(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, false));
		    binaryOutput1.setProperty(PropertyIdentifier.objectType, new ObjectType(0)); // 0 = analogInput
		    binaryOutput1.setProperty(PropertyIdentifier.description, new CharacterString("Distech device"));
		    binaryOutput1.setProperty(PropertyIdentifier.minimumOnTime, new UnsignedInteger(0));
		    binaryOutput1.setProperty(PropertyIdentifier.objectName, new CharacterString("heating_water_valve_percentage_command"));
		    binaryOutput1.setProperty(PropertyIdentifier.elapsedActiveTime, new UnsignedInteger(0));
		    binaryOutput1.setProperty(PropertyIdentifier.notificationClass, new UnsignedInteger(0));
		    binaryOutput1.setProperty(PropertyIdentifier.notifyType, new NotifyType(0));
		    binaryOutput1.setProperty(PropertyIdentifier.presentValue, new BinaryPV(0));
		    binaryOutput1.setProperty(PropertyIdentifier.minimumOffTime, new UnsignedInteger(0));
		    binaryOutput1.setProperty(PropertyIdentifier.deviceType, new CharacterString("Analog0To10Volts"));
		    binaryOutput1.setProperty(PropertyIdentifier.activeText, new CharacterString("TRUE"));
		    binaryOutput1.setProperty(PropertyIdentifier.relinquishDefault, new BinaryPV(0));
		    binaryOutput1.setProperty(PropertyIdentifier.timeOfActiveTimeReset, new DateTime(13/05/2019));
		    binaryOutput1.setProperty(PropertyIdentifier.eventEnable, new EventTransitionBits(true, true, true));
		    binaryOutput1.setProperty(PropertyIdentifier.feedbackValue, new BinaryPV(0));
		    binaryOutput1.setProperty(PropertyIdentifier.eventDetectionEnable, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    binaryOutput1.setProperty(PropertyIdentifier.eventMessageTexts, eventTransitionBits);
		    binaryOutput1.setProperty(PropertyIdentifier.changeOfStateTime, new DateTime(13/05/2019));
		    binaryOutput1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
		    binaryOutput1.setProperty(PropertyIdentifier.ackedTransitions, new EventTransitionBits(true, true, true));
		    localDevice.addObject(binaryOutput1);

		    binaryValue1 = new BACnetObject(localDevice, localDevice.getNextInstanceObjectIdentifier(ObjectType.binaryValue));
		    binaryValue1.setProperty(PropertyIdentifier.objectName, new CharacterString("discharge_air_temperature_sensor"));
		    binaryValue1.setProperty(PropertyIdentifier.presentValue, new BinaryPV(0));
		    binaryValue1.setProperty(PropertyIdentifier.outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    binaryValue1.setProperty(PropertyIdentifier.activeText, new CharacterString("TRUE"));
		    binaryValue1.setProperty(PropertyIdentifier.timeOfStateCountReset, new DateTime(13/05/2019));
		    binaryValue1.setProperty(PropertyIdentifier.relinquishDefault, new BinaryPV(0));
		    binaryValue1.setProperty(PropertyIdentifier.eventEnable, new EventTransitionBits(true, true, true));
		    binaryValue1.setProperty(PropertyIdentifier.changeOfStateCount, new UnsignedInteger(0));
		    binaryValue1.setProperty(PropertyIdentifier.eventState, new EventState(0)); // 0 = normal
		    binaryValue1.setProperty(PropertyIdentifier.objectType, new ObjectType(0)); // 0 = analogInput
		    binaryValue1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
		    binaryValue1.setProperty(PropertyIdentifier.inactiveText, new CharacterString("FALSE"));
		    binaryValue1.setProperty(PropertyIdentifier.alarmValue, new BinaryPV(0));
		    binaryValue1.setProperty(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, false));
		    binaryValue1.setProperty(PropertyIdentifier.ackedTransitions, new EventTransitionBits(true, true, true));
		    binaryValue1.setProperty(PropertyIdentifier.changeOfStateTime, new DateTime(13/05/2019));
		    binaryValue1.setProperty(PropertyIdentifier.timeOfActiveTimeReset, new DateTime(13/05/2019));
		    binaryValue1.setProperty(PropertyIdentifier.notifyType, new NotifyType(0));
		    binaryValue1.setProperty(PropertyIdentifier.eventDetectionEnable, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    binaryValue1.setProperty(PropertyIdentifier.reliability, new Reliability(4));
		    binaryValue1.setProperty(PropertyIdentifier.eventMessageTexts, eventTransitionBits);
		    binaryValue1.setProperty(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(0));
		    binaryValue1.setProperty(PropertyIdentifier.minimumOnTime, new UnsignedInteger(0));
		    binaryValue1.setProperty(PropertyIdentifier.elapsedActiveTime, new UnsignedInteger(0));
		    binaryValue1.setProperty(PropertyIdentifier.minimumOffTime, new UnsignedInteger(0));
		    binaryValue1.setProperty(PropertyIdentifier.notificationClass, new UnsignedInteger(0));
		    binaryValue1.setProperty(PropertyIdentifier.description, new CharacterString("Distech device"));
		    binaryValue1.setProperty(PropertyIdentifier.eventAlgorithmInhibit, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
		    binaryValue1.setProperty(PropertyIdentifier.presentValue, new BinaryPV(0));
		    localDevice.addObject(binaryValue1);

		    		    
		    localDevice.initialize();
		    System.out.println("Device initialized...");
<<<<<<< HEAD
		    new Thread(this).start();
=======
		    
>>>>>>> 6b4493f5d9b4e2640ebb9dcc659e5e05fda61b33
	    } catch(RuntimeException e) {
	    	System.out.println("Ex in LoopDevice() ");
            e.printStackTrace();
            localDevice.terminate();
            localDevice = null;
            throw e;
	    }
	}

<<<<<<< HEAD
	@Override
	public void run() {
		try {
//            System.out.println("LoopDevice start changing values" + this);

            // Let it go...
            float ai0value = 0;
            float ai1value = (float) deviceId;
            boolean bi0value = false;

            while (true) {
                ai0value = getNewRandomValue();
//                System.out.println("Change values of LoopDevice " + this + "=" + ai0value);

                // Update the values in the objects.
                analogInput1.setProperty(PropertyIdentifier.presentValue, new Real(ai0value));
                analogOutput1.setProperty(PropertyIdentifier.presentValue, new Real(ai1value));
                binaryInput1.setProperty(PropertyIdentifier.presentValue, bi0value ? BinaryPV.active : BinaryPV.inactive);

                synchronized (this) {
                    wait(1000); // 1 second or notified (faster exit then stupid wait for 1 second)
                }
            }
        }
        catch (Exception ex) {
            // no op
        }
        localDevice.terminate();
        localDevice = null;
	}
	
	private float getNewRandomValue() {
        return (float) deviceId + (float) Math.random();
    }

=======
>>>>>>> 6b4493f5d9b4e2640ebb9dcc659e5e05fda61b33
}
