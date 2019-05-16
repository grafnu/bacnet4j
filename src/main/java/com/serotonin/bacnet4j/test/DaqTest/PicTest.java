package com.serotonin.bacnet4j.test.DaqTest;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.test.LoopDevice;
import com.serotonin.bacnet4j.test.DaqTest.helper.BACnetPoints;

public class PicTest {

    String localIp = "";
    String broadcastIp = "";
    boolean loopDiscover = false;
    private static LocalDevice localDevice;
    boolean printPICS = false;

    public PicTest(String localIp, boolean loopDiscover) {
        this.localIp = localIp;
        this.loopDiscover = loopDiscover;
        this.printPICS = printPICS;
        try {
            discoverAllDevices();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public PicTest(String localIp, String broadcastIp, boolean loopDiscover, boolean printPICS) {
        this.localIp = localIp;
        this.broadcastIp = broadcastIp;
        this.loopDiscover = loopDiscover;
        this.printPICS = printPICS;
        try {
            discoverAllDevices();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void discoverAllDevices() throws Exception {

        LoopDevice loopDevice = new LoopDevice(broadcastIp,
                IpNetwork.DEFAULT_PORT, localIp);

        while (!loopDevice.isTerminate()) {
            localDevice = loopDevice.getLocalDevice();
            
            System.err.println("Sending whois...");
            localDevice.sendGlobalBroadcast(new WhoIsRequest());
            
            // Wait a bit for responses to come in.
            System.err.println("Waiting...");
            Thread.sleep(5000);
            
            System.err.println("Processing...");
            
            getDevicesPoints();
            if (!loopDiscover) {
                loopDevice.doTerminate();
            }
        }
    }

    private void getDevicesPoints() throws Exception {
        BACnetPoints bacnetPoints = new BACnetPoints();
        bacnetPoints.get(localDevice, printPICS);
    }
}
