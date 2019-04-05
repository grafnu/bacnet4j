package com.serotonin.bacnet4j.test;

import java.util.Hashtable;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class BacnetDictionaryObject {

    Report report = new Report();
    private String reportText = "";

    Multimap<String, Multimap<BacnetObjectType, Hashtable<String, String>>> devices = ArrayListMultimap.create();

    public void addObject(String remoteDevice, Multimap<BacnetObjectType, Hashtable<String, String>> bacnetObjectMap2) {
        devices.put(remoteDevice, bacnetObjectMap2);
    }

    public void printAllDevices() {
        System.out.println(" \n \n ******** PRINT ******** \n \n");

        for (Entry<String, Multimap<BacnetObjectType, Hashtable<String, String>>> deviceObject : devices.entries()) {
            String deviceName = deviceObject.getKey();
            Multimap<BacnetObjectType, Hashtable<String, String>> deviceProperties = deviceObject.getValue();

            System.out.println(deviceName);
            reportText += deviceName + '\n';

            for (Entry<BacnetObjectType, Hashtable<String, String>> devicePropertiesObject : deviceProperties
                    .entries()) {
                BacnetObjectType key = devicePropertiesObject.getKey();
                Hashtable<String, String> value = devicePropertiesObject.getValue();

                System.out.println(String.format("\t%s", key));
                reportText += String.format("\t%s", key) + "\n";

                for (Entry<String, String> v : value.entrySet()) {
                    System.out.println(String.format("\t\t%s = %s", v.getKey(), v.getValue()));
                    reportText += String.format("\t\t%s = %s", v.getKey(), v.getValue() + "\n");
                }
                System.out.println("\n");
                reportText += "\n";
            }
        }
        System.out.println(" \n \n ******** END PRINT ******** \n \n");
        report.writeReport(reportText);
    }
}
