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
import com.serotonin.bacnet4j.test.DaqTest.helper.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddrUniqueTest {

	private static LocalDevice localDevice;
    private String localIp = "";
    private String broadcastIp = "";
    boolean loopDiscover = false;
    
    Report report = new Report("tmp/AddrUniqueTestReport.txt");
    Report appendices = new Report("tmp/AddrUniqueTestReport_APPENDIX.txt");
    
    private String reportText = "";
    private String appendixText = "";
    private String picTestName = "protocol.bacnet.addr_unique";
    
    static ArrayList<String> objectIdentifiers = new ArrayList<String>();
    static List<RemoteDevice> objectIdentifiersDuplicates = new ArrayList<>();

    public AddrUniqueTest(String localIp, String broadcastIp, boolean loopDiscover) {
        this.localIp = localIp;
        this.broadcastIp = broadcastIp;
        this.loopDiscover = loopDiscover;
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
            searchForDuplicates();
            if (!loopDiscover) {
                loopDevice.doTerminate();
            }
        }
    }


    private void searchForDuplicates() throws BACnetException {
    	int remoteDeviceCounter = 0;
        for (RemoteDevice remoteDevice : localDevice.getRemoteDevices()) {
            if(objectIdentifiers.contains(remoteDevice.getObjectIdentifier().toString())) {
            	// add previous remote device too
            	if(remoteDeviceCounter == 1) {
            		RemoteDevice duplicateRemoteDevice = localDevice.getRemoteDevices().get(0);
            		objectIdentifiersDuplicates.add(duplicateRemoteDevice); 
            	}
                objectIdentifiersDuplicates.add(remoteDevice);
            }
            objectIdentifiers.add(remoteDevice.getObjectIdentifier().toString());
            remoteDeviceCounter++;
        }
        printResults();
    }
    
    private void printResults() {
    	// test passed
    	if(objectIdentifiersDuplicates.size() == 0) {
            appendixText = "\nTEST PASSED";
            reportText += "RESULT pass " + picTestName + "\n";
            report.writeReport(reportText);
            appendices.writeReport(appendixText);
        } 
    	
    	// test failed 
    	else {
        	for (int counter = 0; counter < objectIdentifiersDuplicates.size(); counter++) {
        		Map<PropertyIdentifier, Encodable> values;
				try {
					values = RequestUtils.getProperties(
							localDevice, objectIdentifiersDuplicates.get(counter), null,
							PropertyIdentifier.objectName, 
							PropertyIdentifier.vendorName, 
							PropertyIdentifier.modelName,
							PropertyIdentifier.description, 
							PropertyIdentifier.location, 
							PropertyIdentifier.objectList);
					appendixText = Utils.printMap(values);
				} catch (BACnetException e) {
					System.out.println(e.getMessage());
				}
        	}
            appendixText += "TEST FAILED";
            reportText += "RESULT fail " + picTestName + "\n";
            report.writeReport(reportText);
            appendices.writeReport(appendixText);
        }
    	System.out.println(appendixText);
    }
}
