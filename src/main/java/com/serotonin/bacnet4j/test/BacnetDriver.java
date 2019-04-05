package com.serotonin.bacnet4j.test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.ObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.RequestUtils;

public class BacnetDriver {

    private static LocalDevice localDevice;
    private static List<RemoteDevice> allDevices = new ArrayList<>();
    private int discoverTimeout = 1;
    private String broadcastIp = "";
    private String localIp = "";
    boolean loopDiscover = false;
    private boolean networkInitialized = false;

    BacnetDictionaryObject bacnetDictionaryObject = new BacnetDictionaryObject();
    Multimap<BacnetObjectType, Hashtable<String, String>> bacnetObjectMap = ArrayListMultimap.create();

    String[] dictionaryTypes = {
            "Device",
            "Analog Input",
            "Analog Output",
            "Analog Value",
            "Binary Input",
            "Binary Output",
            "Binary Value"
    };

    public BacnetDriver(String broadcastIp) {
        this.broadcastIp = broadcastIp;
        initialiseNetwork();
    }

    public BacnetDriver(String broadcastIp, String localIp, boolean loopDiscover) {
        this.broadcastIp = broadcastIp;
        this.localIp = localIp;
        this.loopDiscover = loopDiscover;
    }

    public void initialiseNetwork() {
        IpNetwork network = new IpNetwork(broadcastIp, 47808);
        Transport transport = new Transport(network);
        localDevice = new LocalDevice(1338, transport);
        localDevice.getEventHandler().addListener(new Listener());

        try {
            localDevice.initialize();
            networkInitialized = true;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    	public void discoverSingleDevice() throws Exception {
            if (!networkInitialized) {
                initialiseNetwork();
            }
        
            localDevice.sendGlobalBroadcast(new WhoIsRequest()); 
            
            try {
                for (int i = discoverTimeout; i > 0; i--) {
                    System.out.println("[BACnet] Waiting for device discover... " + i + "s");               
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ex) {
                System.out.println("[Exception] Device discover interupted: " + 
                                  ex.toString() + ex.getMessage());
            }        
            
            getDevicePoints();
            terminateConnection();
        }

    public void discoverAllDevices() throws Exception {

        LoopDevice loopDevice = new LoopDevice(IpNetwork.DEFAULT_BROADCAST_IP, 
                    IpNetwork.DEFAULT_PORT, localIp);

        while (!loopDevice.isTerminate()) {
            localDevice = loopDevice.getLocalDevice();
            System.err.println("Sending whois...");
            localDevice.sendGlobalBroadcast(new WhoIsRequest());
            // Wait a bit for responses to come in.
            System.err.println("Waiting...");
            Thread.sleep(5000);
            System.err.println("Processing...");
            getDevicePoints();
            if (!loopDiscover) {
                loopDevice.doTerminate();
            }
        }
    }

    public void getDevicePoints() throws Exception {

        for (RemoteDevice remoteDevice : localDevice.getRemoteDevices()) {
            RequestUtils.getExtendedDeviceInformation(localDevice, remoteDevice);

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
            }

            System.out.println("Start read properties");
            final long start = System.currentTimeMillis();

            PropertyValues propertyValues = RequestUtils.readProperties(localDevice, remoteDevice, refs, null);
            System.out.println(String.format("Properties read done in %d ms", System.currentTimeMillis() - start));
            //            printObject(remoteDevice.getObjectIdentifier(), propertyValues);

            for (ObjectIdentifier objectIdentifier : allObjectsIdentifier) {
                saveObject(objectIdentifier, propertyValues, remoteDevice.getObjectIdentifier().toString());
            }

            bacnetDictionaryObject.addObject(remoteDevice.getObjectIdentifier().toString(), bacnetObjectMap);
            bacnetDictionaryObject.printAllDevices();
        }
    }

    private void saveObject(ObjectIdentifier objectIdentifier, PropertyValues propertyValues, String remoteDevice) {

        Hashtable<String, String> points = new Hashtable<String, String>();

        BacnetObjectType bacnetObjectType = null;

        //        System.out.println(String.format("\t%s", objectIdentifier));
        for (ObjectPropertyReference objectPropertyReference : propertyValues) {
            if (objectIdentifier.equals(objectPropertyReference.getObjectIdentifier())) {

                //                System.out.println(String.format("\t\t%s = %s", opr.getPropertyIdentifier().toString(),
                //                        pvs.getNoErrorCheck(objectPropertyReference)));

                // get object type and assign it to BacnetObjectTypes
                for (int dictionaryTypesPosition = 0; dictionaryTypesPosition < dictionaryTypes.length; dictionaryTypesPosition++) {
                    if (objectIdentifier.toString().contains(dictionaryTypes[dictionaryTypesPosition])) {
                        BacnetObjectType arr[] = BacnetObjectType.values();
                        for (BacnetObjectType obj : arr) {
                            if (obj.ordinal() == dictionaryTypesPosition) {
                                bacnetObjectType = obj;
                            }
                        }
                    }
                }

                if (bacnetObjectType != null) {
                    points.put(objectPropertyReference.getPropertyIdentifier().toString(),
                                propertyValues.getNoErrorCheck(objectPropertyReference).toString());
                }
            }
        }
        if (bacnetObjectType != null) {
            bacnetObjectMap.put(bacnetObjectType, points);
        }
    }

    public void terminateConnection() {
        localDevice.terminate();
    }

    static class Listener extends DeviceEventAdapter {

        @Override
        public void iAmReceived(RemoteDevice remoteDevice) {
            allDevices.add(remoteDevice);
            System.out.println("IAm received" + remoteDevice);
        }
    }
}
