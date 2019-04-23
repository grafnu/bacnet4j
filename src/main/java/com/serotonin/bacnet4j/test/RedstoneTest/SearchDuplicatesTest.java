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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchDuplicatesTest {

    String localIp = "";
    String broadcastIp = "";
    boolean loopDiscover = false;
    private static LocalDevice localDevice;
    Report report = new Report("tmp/DuplicatesTestReport.txt");
    private String reportText = "";
    private String picTestName = "protocol.bacnet.addr_unique";

    private static List<RemoteDevice> allRemoteDevices = new ArrayList<>();
    static ArrayList<String> objectIdentifiers = new ArrayList<String>();
    static ArrayList<String> objectIdentifiersDuplicates = new ArrayList<String>();

    public SearchDuplicatesTest(String localIp, String broadcastIp, boolean loopDiscover) {
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
            searchForDuplicate();
            if (!loopDiscover) {
                loopDevice.doTerminate();
            }
        }
    }


    private void searchForDuplicate() throws BACnetException {

        for (RemoteDevice remoteDevice : localDevice.getRemoteDevices()) {

            if(objectIdentifiers.contains(remoteDevice.getObjectIdentifier().toString())) {
                System.err.println("\n\nDUPLICATE FOUND! \nObject Identifier: " + remoteDevice.getObjectIdentifier().toString());

                Map<PropertyIdentifier, Encodable> values = RequestUtils.getProperties(localDevice, remoteDevice, null,
                        PropertyIdentifier.objectName, PropertyIdentifier.vendorName, PropertyIdentifier.modelName,
                        PropertyIdentifier.description, PropertyIdentifier.location, PropertyIdentifier.objectList);
                System.out.println(values);
                objectIdentifiersDuplicates.add(remoteDevice.getObjectIdentifier().toString());
                continue;
            }
            objectIdentifiers.add(remoteDevice.getObjectIdentifier().toString());
            allRemoteDevices.add(remoteDevice);
        }

        if(objectIdentifiersDuplicates.size() == 0) {
            System.out.println("No duplicates found! \nTEST PASSED");
            reportText = "No duplicates found! \nTEST PASSED";
            reportText += "\nRESULT pass " + picTestName + "\n";
            report.writeReport(reportText);
        }

        else {
            for(String duplicateObjectIdentifier : objectIdentifiersDuplicates) {
                List<RemoteDevice> remoteDeviceTemp = new ArrayList<>();
                for(RemoteDevice remoteDevice : allRemoteDevices) {
                    if(remoteDevice.getObjectIdentifier().equals(duplicateObjectIdentifier)) {
                        remoteDeviceTemp.add(remoteDevice);
                    }
                }

                for (RemoteDevice remoteDevice : remoteDeviceTemp) {
                    System.out.println("ObjectIdentifier: " + remoteDevice.getObjectIdentifier());
                    Map<PropertyIdentifier, Encodable> values = RequestUtils.getProperties(localDevice, remoteDevice, null,
                            PropertyIdentifier.objectName, PropertyIdentifier.vendorName, PropertyIdentifier.modelName,
                            PropertyIdentifier.description, PropertyIdentifier.location, PropertyIdentifier.objectList);

                    System.out.println(values);
                    reportText += values;
                }
                System.out.println("\n\n\n");
                reportText += "\n\n\n";
            }
            System.out.println("\nTEST FAILED");
            reportText += "\nTEST FAILED";
            reportText += "\nRESULT fail " + picTestName + "\n";
            report.writeReport(reportText);
        }
    }
}
