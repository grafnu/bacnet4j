package com.serotonin.bacnet4j.test;

import com.serotonin.bacnet4j.test.RedstoneTest.DiscoverAllDevicesTest;
import com.serotonin.bacnet4j.test.RedstoneTest.DiscoverSingleDeviceTest;
import com.serotonin.bacnet4j.test.RedstoneTest.SearchDuplicatesTest;

public class Test5 {

    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            throw new IllegalArgumentException("Expected Ip address as argument.");
        }

        String broadcastIp = args[0];
        String localIp = args[1];
        boolean loopDiscover = "loop".equals(args[2]);

//      Discover single device
//      BacnetDriver bacnetDriver = new BacnetDriver(broadcastIp);
//      bacnetDriver.discoverSingleDevice();
        
//      Discover all devices 
//        BacnetDriver bacnetDriver = new BacnetDriver(localIp, loopDiscover);
//        bacnetDriver.discoverAllDevices();

//        SearchDuplicatesTest searchDuplicatesTest = new SearchDuplicatesTest(localIp, loopDiscover);
//        searchDuplicatesTest.searchForDuplicate();

        // Discover Single Device
        new DiscoverSingleDeviceTest(broadcastIp, false);

        // Discover All Devices
//        new DiscoverAllDevicesTest(localIp, loopDiscover);

        // Check for devices with same ObjectIdentifier
//        new SearchDuplicatesTest(localIp, loopDiscover);
    }
}
