package com.serotonin.bacnet4j.test;

public class Test5 {

	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			throw new IllegalArgumentException("Expected Ip address as argument.");
		}
		
		String broadcastIp = args[0];
		
		BacnetDriver bacnetDriver = new BacnetDriver(broadcastIp);
		
		bacnetDriver.initialiseNetwork();
		bacnetDriver.discoverDevices();
		bacnetDriver.getDevicePoints();
		bacnetDriver.terminateConnection();
	}

}
