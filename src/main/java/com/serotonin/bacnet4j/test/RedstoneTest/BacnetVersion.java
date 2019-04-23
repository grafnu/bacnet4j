package com.serotonin.bacnet4j.test.RedstoneTest;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.test.LoopDevice;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.util.RequestUtils;

import java.util.Map;

public class BacnetVersion {
    String localIp = "";
    String broadcastIp = "";
    private static LocalDevice localDevice;
    Report report = new Report("tmp/DuplicatesTestReport.txt");
    private String reportText = "";
    private String picTestName = "protocol.bacnet.addr_unique";

    public BacnetVersion(String localIp, String broadcastIp) {
        this.localIp = localIp;
        this.broadcastIp = broadcastIp;
        try {
            discoverAllDevices();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void discoverAllDevices() throws Exception {

        LoopDevice loopDevice = new LoopDevice(IpNetwork.DEFAULT_BROADCAST_IP,
                IpNetwork.DEFAULT_PORT, localIp);

        while (!loopDevice.isTerminate()) {
            localDevice = loopDevice.getLocalDevice();
            System.err.println("Sending whois...");
            localDevice.sendGlobalBroadcast(new WhoIsRequest());
            // Wait a bit for responses to come in.
            System.err.println("Waiting...");
            Thread.sleep(5000);
            System.err.println("Processing...");
            getVersion();
            loopDevice.doTerminate();
        }
    }

    private void getVersion() throws BACnetException {
        for (RemoteDevice remoteDevice : localDevice.getRemoteDevices()) {

            // Get Device name, version
            Map<PropertyIdentifier, Encodable> values = RequestUtils.getProperties(localDevice, remoteDevice, null,
                        PropertyIdentifier.objectName, PropertyIdentifier.vendorName, PropertyIdentifier.modelName,
                        PropertyIdentifier.description, PropertyIdentifier.location, PropertyIdentifier.objectList);

//            System.out.println(values);
            print(values);
        }
    }

    private void print(Map<PropertyIdentifier, Encodable> values) {
        for (Map.Entry<PropertyIdentifier, Encodable> property : values.entrySet()) {
            String key = property.getKey().toString();

            if (key.equals("Object list")) {
                String[] value = property.getValue().toString().split(",");
                System.out.print(key + " : ");
                for (int i = 0; i < value.length; i ++) {
                    if (i == 0){
                        System.out.print(value[i]);
                    }  else {
                        System.out.format("%-14s%-20s", "", value[i]);
                    }
                    if (i % 2 == 0) System.out.println();
                }
                System.out.println();

            } else {
                String value = property.getValue().toString();
                System.out.println(key + " : " + value);
            }
        }
    }
}
