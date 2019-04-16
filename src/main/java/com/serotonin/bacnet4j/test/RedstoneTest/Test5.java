package com.serotonin.bacnet4j.test.RedstoneTest;

public class Test5 {

    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: broadcastIp, localIp, loopDiscover");
        }

        String broadcastIp = args[0];
        String localIp = args[1];
        boolean loopDiscover = "loop".equals(args[2]);

        // Write to BACnet Point
        new WriteTest(broadcastIp, "Present value", "0.7");

        // Discover Single Device
        new DiscoverSingleDeviceTest(broadcastIp);

        // Discover All Devices
//        new DiscoverAllDevicesTest(localIp, loopDiscover);

        // Check for devices with same ObjectIdentifier
//        new SearchDuplicatesTest(localIp, loopDiscover);


    }
}
