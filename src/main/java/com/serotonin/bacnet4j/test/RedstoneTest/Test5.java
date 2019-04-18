package com.serotonin.bacnet4j.test.RedstoneTest;

public class Test5 {

    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: broadcastIp, localIp, loopDiscover");
        }

//        if (args.length != 1) {
//            throw new IllegalArgumentException("Usage: broadcastIp");
//        }

        String broadcastIp = args[0];
        String localIp = args[1];
        boolean loopDiscover = "loop".equals(args[2]);

        // Write to BACnet Point
//        new WriteTest(broadcastIp,"Analog Output 105", "Present value", "0.5");

        // Discover Single Device
//        new DiscoverSingleDeviceTest(broadcastIp, true);

        // Discover All Devices
        new DiscoverAllDevicesTest(localIp, loopDiscover, true);

        // Check for devices with same ObjectIdentifier
//        new SearchDuplicatesTest(localIp, loopDiscover);


    }
}
