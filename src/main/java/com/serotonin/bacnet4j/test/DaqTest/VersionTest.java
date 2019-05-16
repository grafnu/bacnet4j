package com.serotonin.bacnet4j.test.DaqTest;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.test.LoopDevice;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.util.RequestUtils;
import com.serotonin.bacnet4j.test.DaqTest.helper.Report;

import java.util.Map;

public class VersionTest {
	
	private static LocalDevice localDevice;
	private String localIp = "";
    private String broadcastIp = "";
    
    Report report = new Report("tmp/BacnetVersionTestReport.txt");
    Report appendices = new Report("tmp/BacnetVersionTest_APPENDIX.txt");
    
    private String appendixText = "";
    private String reportText = "";
	private boolean testPassed = false;
    

    public VersionTest(String localIp, String broadcastIp) {
        this.localIp = localIp;
        this.broadcastIp = broadcastIp;
        try {
            discoverAllDevices();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void discoverAllDevices() throws Exception {
    	
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
            getVersion();
            loopDevice.doTerminate();
        }
    }

    private void getVersion() throws BACnetException {
        for (RemoteDevice remoteDevice : localDevice.getRemoteDevices()) {
            Map<PropertyIdentifier, Encodable> values = RequestUtils.getProperties(
            		localDevice, remoteDevice, null,
            		PropertyIdentifier.vendorIdentifier, 
            		PropertyIdentifier.vendorName,
            		PropertyIdentifier.firmwareRevision, 
            		PropertyIdentifier.applicationSoftwareVersion,
            		PropertyIdentifier.objectName, 
            		PropertyIdentifier.modelName, 
            		PropertyIdentifier.description,
            		PropertyIdentifier.location, 
            		PropertyIdentifier.objectList, 
            		PropertyIdentifier.protocolVersion);
            print(values);
        }
        System.out.println(appendixText);
    }

    private void print(Map<PropertyIdentifier, Encodable> values) {
    	appendixText += ("****************************** START ******************************\n");
        for (Map.Entry<PropertyIdentifier, Encodable> property : values.entrySet()) {
            String key = property.getKey().toString();
            if (key.equals("Object list")) {
                String[] value = property.getValue().toString().split(",");
                appendixText += key + " : ";
                for (int i = 0; i < value.length; i ++) {
                    if (i == 0){
                        appendixText += value[i];
                    }  else {
                        appendixText += String.format("%-14s%-20s", "", value[i]);
                    }
                    if (i % 2 == 0) { appendixText += "\n"; };
                }

            } else {
                String value = property.getValue().toString();
                if (key.equals("Protocol version")) {
                    reportText += "RESULT pass protocol.bacnet.version\n";
                    appendixText += key + " : " + value + "\n";
                    testPassed = true;
                } else {
                    appendixText += key + " : " + value + "\n";
                }
            }
        }
        appendixText += ("\n****************************** END ******************************\n\n");
        
        if(testPassed) {
            report.writeReport(reportText);
        } else {
            report.writeReport("RESULT fail protocol.bacnet.version");
        }
        appendices.writeReport(appendixText);
    }
}
