package com.serotonin.bacnet4j.test;

import java.util.Map;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.RequestUtils;

public class Test3 {
    private static final int MAX_APDU_LENGTH_ACCEPTED = 1476;

    public static void main(String[] args) throws Exception {
        IpNetwork network = new IpNetwork(IpNetwork.DEFAULT_BROADCAST_IP, 47808,
            IpNetwork.DEFAULT_BIND_IP, 1, "172.25.73.60");
        Transport transport = new Transport(network);
        LocalDevice ld = new LocalDevice(1234, transport);
        ld.initialize();

        RemoteDevice rd = ld.getRemoteDeviceCreate(3400205,
            new Address(34002, "172.17.7.74:47808"),
            new OctetString("172.17.7.74:47808"));
        rd.setSegmentationSupported(Segmentation.noSegmentation);
        rd.setMaxAPDULengthAccepted(MAX_APDU_LENGTH_ACCEPTED);

        ObjectIdentifier obj = new ObjectIdentifier(ObjectType.analogInput, 1);
        System.out.println("" + readProperty(ld, rd, obj, PropertyIdentifier.presentValue));
        ld.terminate();
    }

    private static Encodable readProperty(LocalDevice ld, RemoteDevice rd, ObjectIdentifier objectIdentifier,
        PropertyIdentifier propertyIdentifier) throws PropertyValueException, BACnetException {
        PropertyReferences refs = new PropertyReferences();
        refs.add(objectIdentifier, propertyIdentifier);
        PropertyValues pvs = RequestUtils.readProperties(ld, rd, refs, null);
        return pvs.get(objectIdentifier, propertyIdentifier);
    }
}
