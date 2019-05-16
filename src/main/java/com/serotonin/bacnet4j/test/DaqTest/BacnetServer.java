package com.serotonin.bacnet4j.test.RedstoneTest;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.test.LoopDevice;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.RequestUtils;

public class BacnetServer extends Thread {

    private static final int MAX_APDU_LENGTH_ACCEPTED = 1476;

    public static void main(String[] args) throws Exception{

        IpNetwork network = new IpNetwork("10.255.255.255", 47808,
                IpNetwork.DEFAULT_BIND_IP, 1, "10.20.94.185");
        Transport transport = new Transport(network);

        int localDeviceID = 10000 + (int) ( Math.random() * 10000);

        LocalDevice localDevice = new LocalDevice(localDeviceID, transport);

        localDevice.getEventHandler().addListener(new Listener());
        // create sample BACnet object
        BACnetObject ai0 = new BACnetObject(localDevice,
                localDevice.getNextInstanceObjectIdentifier(ObjectType.analogInput));
        ai0.setProperty(PropertyIdentifier.units, EngineeringUnits.centimeters);
        // Set the COV threshold/increment which is the value at which COV notifications will be triggered
        ai0.setProperty(PropertyIdentifier.covIncrement, new Real(0.2f));
        localDevice.addObject(ai0);



        localDevice.initialize();
//        localDevice.sendGlobalBroadcast(localDevice.getIAm());
        localDevice.sendGlobalBroadcast(new WhoIsRequest());

//        RemoteDevice rd = localDevice.getRemoteDeviceCreate(3400205,
//                new Address(34002, "172.17.7.74:47808"),
//                new OctetString("172.17.7.74:47808"));
//        rd.setSegmentationSupported(Segmentation.noSegmentation);
//        rd.setMaxAPDULengthAccepted(MAX_APDU_LENGTH_ACCEPTED);
////
//        ObjectIdentifier obj = new ObjectIdentifier(ObjectType.analogInput, 1);
//        System.out.println("" + readProperty(localDevice, rd, obj, PropertyIdentifier.covIncrement));
//        localDevice.terminate();
    }

    private static Encodable readProperty(LocalDevice ld, RemoteDevice rd, ObjectIdentifier objectIdentifier,
                                          PropertyIdentifier propertyIdentifier) throws PropertyValueException, BACnetException {
        PropertyReferences refs = new PropertyReferences();
        refs.add(objectIdentifier, propertyIdentifier);
        PropertyValues pvs = RequestUtils.readProperties(ld, rd, refs, null);
        return pvs.get(objectIdentifier, propertyIdentifier);
    }

    static class Listener extends DeviceEventAdapter {

        @Override
        public void iAmReceived(RemoteDevice remoteDevice) {
            System.out.println("IAm received" + remoteDevice);
        }
    }
}
