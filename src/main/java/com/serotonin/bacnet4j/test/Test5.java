package com.serotonin.bacnet4j.test;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.test.*;

public class Test5 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String broadcastIp = "10.20.64.66";
		BacnetDriver bacnetDriver = new BacnetDriver(broadcastIp);
		
		
		bacnetDriver.initialiseNetwork();
		bacnetDriver.discoverDevices();
		bacnetDriver.getDevicePoints();
		
		
		bacnetDriver.terminateConnection();
	}

}
