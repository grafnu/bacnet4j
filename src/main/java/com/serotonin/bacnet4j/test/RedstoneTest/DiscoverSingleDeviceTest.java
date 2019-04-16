package com.serotonin.bacnet4j.test.RedstoneTest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.test.BacnetDictionaryObject;
import com.serotonin.bacnet4j.test.BacnetObjectType;
import com.serotonin.bacnet4j.test.DevicesProfile.DistechController;
import com.serotonin.bacnet4j.transport.Transport;

import java.util.Hashtable;

public class DiscoverSingleDeviceTest {

    String broadcastIp = "";
    private static LocalDevice localDevice;
    private boolean networkInitialized = false;
    private int discoverTimeout = 1;
    boolean printPICS = false;

    BacnetDictionaryObject bacnetDictionaryObject = new BacnetDictionaryObject();
    Multimap<BacnetObjectType, Hashtable<String, Object>> bacnetObjectMap = ArrayListMultimap.create();
    DistechController disthechController = new DistechController();

    String[] dictionaryTypes = {
            "Device",
            "Analog Input",
            "Analog Output",
            "Analog Value",
            "Binary Input",
            "Binary Output",
            "Binary Value"
    };

    public DiscoverSingleDeviceTest(String broadcastIp) {
        this.broadcastIp = broadcastIp;
        this.printPICS = printPICS;
        initialiseNetwork();
    }

    public DiscoverSingleDeviceTest(String broadcastIp, boolean printPICS) {
        this.broadcastIp = broadcastIp;
        this.printPICS = printPICS;
        initialiseNetwork();
    }

    private void initialiseNetwork() {
        IpNetwork network = new IpNetwork(broadcastIp, 47808);
        Transport transport = new Transport(network);
        localDevice = new LocalDevice(1338, transport);
        localDevice.getEventHandler().addListener(new Listener());

        try {
            localDevice.initialize();
            networkInitialized = true;
            discoverSingleDevice();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void discoverSingleDevice() throws Exception {
        if (!networkInitialized) {
            initialiseNetwork();
        }

        localDevice.sendGlobalBroadcast(new WhoIsRequest());

        try {
            for (int i = discoverTimeout; i > 0; i--) {
                System.out.println("[BACnet] Waiting for device discover... " + i + "s");
                Thread.sleep(1000);
            }
            getDevicePoints();
            localDevice.terminate();
        } catch (InterruptedException ex) {
            System.out.println("[Exception] Device discover interupted: " +
                    ex.toString() + ex.getMessage());
        }

    }

    private void getDevicePoints() throws Exception {
        BACnetPoints bacnetPoints = new BACnetPoints();
        bacnetPoints.get(localDevice, printPICS);
    }


    static class Listener extends DeviceEventAdapter {

        @Override
        public void iAmReceived(RemoteDevice remoteDevice) {
            System.out.println("IAm received" + remoteDevice);
        }
    }
}
