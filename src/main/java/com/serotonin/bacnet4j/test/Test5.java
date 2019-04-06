package com.serotonin.bacnet4j.test;

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
        BacnetDriver bacnetDriver = new BacnetDriver(broadcastIp, localIp, loopDiscover);
        bacnetDriver.discoverAllDevices();
    }
}
