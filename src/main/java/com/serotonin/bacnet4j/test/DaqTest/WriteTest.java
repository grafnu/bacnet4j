package com.serotonin.bacnet4j.test.DaqTest;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.event.DeviceEventListener;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.service.confirmed.ReinitializeDeviceRequest;
import com.serotonin.bacnet4j.service.confirmed.SubscribeCOVRequest;
import com.serotonin.bacnet4j.service.confirmed.WritePropertyRequest;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.*;
import com.serotonin.bacnet4j.type.enumerated.*;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.RequestUtils;

import java.util.Arrays;
import java.util.List;

public class WriteTest {

    String broadcastIp = "";
    private int discoverTimeout = 1;
    private static LocalDevice localDevice;
    private boolean networkInitialized = false;

    String propertyName = "Present value";
    String propertyValue = "0.4";
    String objectType = "Analog output 105";

    String[] RealType = {"Present value", "Low limit", "Min pres value", "Max pres value", "Resolution",
            "Relinquish default", "COV increment", "Low limit", "High limit", "Deadband"};

    String[] BooleanType= {"Out of service", "Event Detection Enable", "Event algorithm inhibit"};

    String[] CharacterStringType = {"Object name", "Description", "Device type", "Profile name",
            "Event message texts", "Event message texts config", };

    public WriteTest(String broadcastIp, String objectType, String propertyName, String propertyValue) throws Exception {
        this.broadcastIp = broadcastIp;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.objectType = objectType;
        initialiseNetwork();
    }

    private void initialiseNetwork() {
        IpNetwork network = new IpNetwork(broadcastIp, 47808);
        Transport transport = new Transport(network);
        localDevice = new LocalDevice(1338, transport);
        localDevice.getEventHandler().addListener(new Listener());

        try {
            localDevice.initialize();
            networkInitialized = true;
            discoverSingleDevice();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void discoverSingleDevice() throws Exception {
        if (!networkInitialized) {
            initialiseNetwork();
        }

        localDevice.sendGlobalBroadcast(new WhoIsRequest());

        try {
            for (int i = discoverTimeout; i > 0; i--) {
                System.out.println("[BACnet] Waiting for device discover... " + i + "s");
                Thread.sleep(1000);
            }
            get(localDevice);
            localDevice.terminate();
        } catch (InterruptedException ex) {
            System.out.println("[Exception] Device discover interupted: " +
                    ex.toString() + ex.getMessage());
        }
    }



    public void get(LocalDevice localDevice) throws  Exception {
        for (RemoteDevice remoteDevice : localDevice.getRemoteDevices()) {
            RequestUtils.getExtendedDeviceInformation(localDevice, remoteDevice);
            System.out.println("Device ID: " + remoteDevice.getObjectIdentifier());
            @SuppressWarnings("unchecked")
            List<ObjectIdentifier> allObjectsIdentifier = ((SequenceOf<ObjectIdentifier>) RequestUtils
                    .sendReadPropertyAllowNull(
                            localDevice,
                            remoteDevice,
                            remoteDevice.getObjectIdentifier(),
                            PropertyIdentifier.objectList))
                    .getValues();

            PropertyReferences refs = new PropertyReferences();
            // add the property references of the "device object" to the list
            refs.add(remoteDevice.getObjectIdentifier(), PropertyIdentifier.all);

            // and now from all objects under the device object >> ai0, ai1,bi0,bi1...
            for (ObjectIdentifier objectIdentifier : allObjectsIdentifier) {
                refs.add(objectIdentifier, PropertyIdentifier.all);
                if(objectIdentifier.toString().equals(objectType)){
                    setPresentValue(localDevice, remoteDevice, objectIdentifier);
                }
            }
        }
    }



    public void setPresentValue(LocalDevice localDevice, RemoteDevice remoteDevice, ObjectIdentifier objectIdentifier) {
        try {
            
            Encodable property = null;

            for (PropertyIdentifier propertyIdentifier  :  PropertyIdentifier.ALL) {
                if (propertyIdentifier.toString().equals(propertyName)) {
                    if(Arrays.asList(RealType).contains(propertyName)) {
                        float floatValue = Float.valueOf(propertyValue.trim()).floatValue();
                        property = new Real(floatValue);
                        WritePropertyRequest request =
                                new WritePropertyRequest(objectIdentifier, propertyIdentifier,
                                        null, property, null);
                        localDevice.send(remoteDevice, request);
                        PropertyValue propertyValue = new PropertyValue(propertyIdentifier, property);
                        BACnetObject bacnetObject = new BACnetObject(localDevice, objectIdentifier);
                        subscribeToPropertyWritten(bacnetObject, propertyValue);
                    }

                    if(Arrays.asList(BooleanType).contains(propertyName)) {
                        boolean bool = java.lang.Boolean.valueOf(propertyValue);
                        property = new Boolean(bool);
                        // this line need to be dynamic
                        WritePropertyRequest request =
                                new WritePropertyRequest(objectIdentifier, PropertyIdentifier.outOfService,
                                        null, property, null);
                        localDevice.send(remoteDevice, request);
                        PropertyValue propertyValue = new PropertyValue(propertyIdentifier, property);
                        BACnetObject bacnetObject = new BACnetObject(localDevice, objectIdentifier);
                        subscribeToPropertyWritten(bacnetObject, propertyValue);
                    }

                    if(Arrays.asList(CharacterStringType).contains(propertyName)) {
                        property = new CharacterString(propertyValue);
                        // this line need to be dynamic
                        WritePropertyRequest request =
                                new WritePropertyRequest(objectIdentifier, PropertyIdentifier.description,
                                        null, property, null);
                        localDevice.send(remoteDevice, request);
                        PropertyValue propertyValue = new PropertyValue(propertyIdentifier, property);
                        BACnetObject bacnetObject = new BACnetObject(localDevice, objectIdentifier);
                        subscribeToPropertyWritten(bacnetObject, propertyValue);
                    }
                }
            }

            System.out.println("ObjectIdentifier: " + objectIdentifier + " \nWriting to: " + propertyName);
            // Subscribe to this objectidentifier
            subscribeToCOVNotification(remoteDevice, objectIdentifier);



            Thread.sleep(10000); // 10 seconds

        } catch (BACnetException | InterruptedException e) {
            System.out.println("BACnet Exception: " + e.getMessage());
        } finally {
            // Unsubscribe to COV Notification
            unsubscribeToCOVNotification(remoteDevice, objectIdentifier);
        }
    }

    private void subscribeToCOVNotification(RemoteDevice remoteDevice, ObjectIdentifier objectIdentifier) {
        try {
            System.out.println("Subscribing to COV Notification...");
            SubscribeCOVRequest request = new SubscribeCOVRequest(new UnsignedInteger(0), objectIdentifier,
                    new Boolean(true),
                    new UnsignedInteger(0));
            localDevice.send(remoteDevice, request);

        } catch(Exception e ) {
            System.out.println("WritePropertyRequest Exception: " + e.getMessage());
        }
    }

    private void subscribeToPropertyWritten(BACnetObject bacnetObject, PropertyValue propertyValue) {
        localDevice.getEventHandler().propertyWritten(bacnetObject, propertyValue);
    }

    private void unsubscribeToCOVNotification(RemoteDevice remoteDevice, ObjectIdentifier objectIdentifier) {
        try {
            System.out.println("Unsubscribing to COV Notification...");
            localDevice.send(remoteDevice, new SubscribeCOVRequest(new UnsignedInteger(0), objectIdentifier,
                    null, null));
        } catch (BACnetException e) {
            System.out.println("Unsubscribe Exception" + e.getMessage());
        }
    }

    public static class Listener implements DeviceEventListener {

        @Override
        public void listenerException(Throwable e) {

        }

        @Override
        public void iAmReceived(RemoteDevice d) {
            System.out.println("LISTENER: iAmReceived " + d.toString());
        }

        @Override
        public boolean allowPropertyWrite(BACnetObject obj, PropertyValue pv) {
            System.out.println("LISTENER: allowPropertyWrite " + obj.toString());
            return true;
        }

        @Override
        public void propertyWritten(BACnetObject obj, PropertyValue pv) {
            System.out.println("Wrote " + pv + " to " + obj.getId());
        }

        @Override
        public void iHaveReceived(RemoteDevice d, RemoteObject o) {
            System.out.println("LISTENER: iHaveReceived " + d.toString());
        }

        @Override
        public void covNotificationReceived(UnsignedInteger subscriberProcessIdentifier,
                                            RemoteDevice initiatingDevice, ObjectIdentifier monitoredObjectIdentifier,
                                            UnsignedInteger timeRemaining, SequenceOf<PropertyValue> listOfValues) {
            System.out.println("\nLISTENER: covNotificationReceived monitoredObjectIdentifier:  " +
                    monitoredObjectIdentifier.toString());
            System.out.println("LISTENER: covNotificationReceived time remaining:  " + timeRemaining.toString());
            System.out.println("LISTENER: covNotificationReceived list of value:  " + listOfValues.toString() + "\n\n");
        }

        @Override
        public void eventNotificationReceived(UnsignedInteger processIdentifier,
                                              RemoteDevice initiatingDevice, ObjectIdentifier eventObjectIdentifier,
                                              TimeStamp timeStamp, UnsignedInteger notificationClass,
                                              UnsignedInteger priority, EventType eventType, CharacterString messageText,
                                              NotifyType notifyType, Boolean ackRequired, EventState fromState,
                                              EventState toState, NotificationParameters eventValues) {
            System.out.println("LISTENER: eventNotificationReceived " + processIdentifier.toString());
        }

        @Override
        public void textMessageReceived(RemoteDevice textMessageSourceDevice, Choice messageClass,
                                        MessagePriority messagePriority, CharacterString message) {
            System.out.println("LISTENER: textMessageReceived " + textMessageSourceDevice.toString());
        }

        @Override
        public void privateTransferReceived(UnsignedInteger vendorId, UnsignedInteger serviceNumber,
                                            Encodable serviceParameters) {
            System.out.println("LISTENER: privateTransferReceived " + vendorId.toString());
        }

        @Override
        public void reinitializeDevice(ReinitializeDeviceRequest.ReinitializedStateOfDevice reinitializedStateOfDevice) {
            System.out.println("LISTENER: reinitializeDevice ");
        }

        @Override
        public void synchronizeTime(DateTime dateTime, boolean utc) {
            System.out.println("LISTENER: reinitializeDevice " + dateTime.toString());
        }
    }
}
