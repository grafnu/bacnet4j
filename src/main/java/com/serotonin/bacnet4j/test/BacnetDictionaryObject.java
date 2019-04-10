package com.serotonin.bacnet4j.test;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.serotonin.bacnet4j.type.constructed.ObjectPropertyReference;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.type.AmbiguousValue;

public class BacnetDictionaryObject {

    Report report = new Report();
    private String reportText = "";

    Multimap<String, Multimap<BacnetObjectType, Hashtable<String, Object>>> devices = ArrayListMultimap.create();

    public void addObject(String remoteDevice, Multimap<BacnetObjectType, Hashtable<String, Object>> bacnetObjectMap2) {
        devices.put(remoteDevice, bacnetObjectMap2);
    }

    public void printAllDevices() {
        System.out.println(" \n \n ******** PRINT ******** \n \n");

        for (Entry<String, Multimap<BacnetObjectType, Hashtable<String, Object>>> deviceObject : devices.entries()) {
            String deviceName = deviceObject.getKey();
            Multimap<BacnetObjectType, Hashtable<String, Object>> deviceProperties = deviceObject.getValue();

            System.out.println(deviceName);
            reportText += deviceName + '\n';

            for (Entry<BacnetObjectType, Hashtable<String, Object>> devicePropertiesObject : deviceProperties
                    .entries()) {
                BacnetObjectType key = devicePropertiesObject.getKey();
                Hashtable<String, Object> value = devicePropertiesObject.getValue();

                System.out.println(String.format("\t%s", key) + " - " + value.get("Object identifier"));
                reportText += String.format("\t%s", key) + "\n";

                for (Entry<String, Object> v : value.entrySet()) {
                     if(v.getValue() instanceof AmbiguousValue) {
                         System.out.println(String.format("\t\t%s = %s", v.getKey(), v.getValue()));
                     } else {
                         System.out.println(String.format("\t\t%s = %s", v.getKey(), v.getValue().toString()));
                       reportText += String.format("\t\t%s = %s", v.getKey(), v.getValue() + "\n");
                     }
                }
                System.out.println("\n");
                reportText += "\n";
            }
        }
        System.out.println(" \n \n ******** END PRINT ******** \n \n");
        report.writeReport(reportText);
    }
}
