package com.serotonin.bacnet4j.test.RedstoneTest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.test.BacnetDictionaryObject;
import com.serotonin.bacnet4j.test.BacnetObjectType;
import com.serotonin.bacnet4j.test.DevicesProfile.DistechController;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.ObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.RequestUtils;

import java.util.Hashtable;
import java.util.List;

public class BACnetPoints {

    BacnetDictionaryObject bacnetDictionaryObject = new BacnetDictionaryObject();
    Multimap<BacnetObjectType, Hashtable<String, Object>> bacnetObjectMap = ArrayListMultimap.create();
    DistechController disthechController = new DistechController();
    boolean printPICS = false;

    String[] dictionaryTypes = {
            "Device",
            "Analog Input",
            "Analog Output",
            "Analog Value",
            "Binary Input",
            "Binary Output",
            "Binary Value"
    };

    public void get(LocalDevice localDevice, boolean printPICS) throws  Exception {
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
            }

            System.out.println("Start read properties");
            final long start = System.currentTimeMillis();

            PropertyValues propertyValues = RequestUtils.readProperties(localDevice, remoteDevice, refs, null);
            System.out.println(String.format("Properties read done in %d ms", System.currentTimeMillis() - start));
            //            printObject(remoteDevice.getObjectIdentifier(), propertyValues);

            for (ObjectIdentifier objectIdentifier : allObjectsIdentifier) {
                saveObject(objectIdentifier, propertyValues, remoteDevice.getObjectIdentifier().toString(), remoteDevice);
            }

            // Get Device name, version
//            Map<PropertyIdentifier, Encodable> values = RequestUtils.getProperties(localDevice, remoteDevice, null,
//                        PropertyIdentifier.objectName, PropertyIdentifier.vendorName, PropertyIdentifier.modelName,
//                        PropertyIdentifier.description, PropertyIdentifier.location, PropertyIdentifier.objectList);
//
//            System.out.println(values);


            bacnetDictionaryObject.addObject(remoteDevice.getObjectIdentifier().toString(), bacnetObjectMap);
            bacnetDictionaryObject.printAllDevices();

            if(printPICS) disthechController.print();
        }
    }

    private void saveObject(ObjectIdentifier objectIdentifier, PropertyValues propertyValues, String remoteDevice, RemoteDevice r) {

        Hashtable<String, Object> points = new Hashtable<String, Object>();
        BacnetObjectType bacnetObjectType = null;
        String ObjectIdentifier = "";

        //        System.out.println(String.format("\t%s", objectIdentifier));
        for (ObjectPropertyReference objectPropertyReference : propertyValues) {
            if (objectIdentifier.equals(objectPropertyReference.getObjectIdentifier())) {
                ObjectIdentifier = objectPropertyReference.getObjectIdentifier().toString();

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
                            propertyValues.getNoErrorCheck(objectPropertyReference));
                }
            }
        }
        if (bacnetObjectType != null) {
            bacnetObjectMap.put(bacnetObjectType, points);
            disthechController.addToProfile(bacnetObjectType.toString(), points, ObjectIdentifier);
        }
    }
}
