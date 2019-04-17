package com.serotonin.bacnet4j.test.RedstoneTest;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;

public class BacnetServer {

    public static void main(String[] args) throws Exception{
        IpNetwork network = new IpNetwork("10.78.20.255", 0xBAC5);
        Transport transport = new Transport(network);

        // create device with random device number
        int localDeviceID = 10000 + (int) ( Math.random() * 10000);
        LocalDevice localDevice = new LocalDevice(localDeviceID, transport);

        localDevice.getConfiguration().setProperty(PropertyIdentifier.objectName,
                new CharacterString("BACnet4J slave device test"));

        // create sample BACnet object
        ObjectIdentifier objectId = new ObjectIdentifier(ObjectType.analogValue, 1);
        BACnetObject object = new BACnetObject(localDevice, objectId);

        localDevice.addObject(object);
        localDevice.initialize();

        System.out.println("Local device is running with device id " + localDeviceID);


        // Send an iam.
        localDevice.sendGlobalBroadcast(localDevice.getIAm());

        BACnetPoints bacnetObject = new BACnetPoints();
        bacnetObject.get(localDevice, false);

        localDevice.terminate();
    }
}
