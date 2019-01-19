/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * Copyright (C) 2006-2009 Serotonin Software Technologies Inc. http://serotoninsoftware.com
 * @author Matthew Lohbihler
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 */
package com.serotonin.bacnet4j.test;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.ObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.RequestUtils;

import java.util.List;

/**
 * @author Matthew Lohbihler
 */
public class DiscoveryTest {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new RuntimeException("Usage: localIpAddr broadcastIpAddr subcmd");
        }
        String localIpAddr = args[0];
        String broadcastIpAddr = args[1];
        boolean loopDiscover = "loop".equals(args[2]);

        LoopDevice loopDevice = new LoopDevice(broadcastIpAddr, IpNetwork.DEFAULT_PORT, localIpAddr);

        while (!loopDevice.isTerminate()) {
            doWhoIs(loopDevice);
            if (!loopDiscover) {
                loopDevice.doTerminate();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void doWhoIs(LoopDevice loopDevice)
        throws BACnetException, InterruptedException {
        LocalDevice localDevice = loopDevice.getLocalDevice();

        System.err.println("Sending whois...");
        localDevice.sendGlobalBroadcast(new WhoIsRequest());

        // Wait a bit for responses to come in.
        System.err.println("Waiting...");
        Thread.sleep(5000);

        System.err.println("Processing...");
        // Get extended information for all remote devices.
        int deviceId = loopDevice.getDeviceId();
        for (RemoteDevice d : localDevice.getRemoteDevices()) {
            try {
                if (d.getInstanceNumber() == deviceId) {
                    System.out.println("Ignoring other device with self-same ID " + deviceId);
                    continue;
                }
                System.out.println("Query remote device " + d);
                RequestUtils.getExtendedDeviceInformation(localDevice, d);
                List<ObjectIdentifier>
                    oids =
                    ((SequenceOf<ObjectIdentifier>) RequestUtils.sendReadPropertyAllowNull(
                        localDevice, d, d.getObjectIdentifier(), PropertyIdentifier.objectList))
                        .getValues();

                PropertyReferences refs = new PropertyReferences();
                for (ObjectIdentifier oid : oids)
                    addPropertyReferences(refs, oid);

                PropertyValues pvs = RequestUtils.readProperties(localDevice, d, refs, null);
                for (ObjectPropertyReference opr : pvs) {
                    System.out.println("  " + opr.getObjectIdentifier() + "/" + opr.getPropertyIdentifier() + " = " + pvs.get(opr));
                }
            } catch (Exception e) {
                System.out.println("Error reading device " + e.getMessage());
            }
        }

        System.err.println("Done with whoIs.");
    }

    private static void addPropertyReferences(PropertyReferences refs, ObjectIdentifier oid) {
        refs.add(oid, PropertyIdentifier.objectName);

        ObjectType type = oid.getObjectType();
        if (ObjectType.accumulator.equals(type)) {
            refs.add(oid, PropertyIdentifier.units);
        }
        else if (ObjectType.analogInput.equals(type) || ObjectType.analogOutput.equals(type)
                || ObjectType.analogValue.equals(type) || ObjectType.pulseConverter.equals(type)) {
            refs.add(oid, PropertyIdentifier.units);
        }
        else if (ObjectType.binaryInput.equals(type) || ObjectType.binaryOutput.equals(type)
                || ObjectType.binaryValue.equals(type)) {
            refs.add(oid, PropertyIdentifier.inactiveText);
            refs.add(oid, PropertyIdentifier.activeText);
        }
        else if (ObjectType.lifeSafetyPoint.equals(type)) {
            refs.add(oid, PropertyIdentifier.units);
        }
        else if (ObjectType.loop.equals(type)) {
            refs.add(oid, PropertyIdentifier.outputUnits);
        }
        else if (ObjectType.multiStateInput.equals(type) || ObjectType.multiStateOutput.equals(type)
                || ObjectType.multiStateValue.equals(type)) {
            refs.add(oid, PropertyIdentifier.stateText);
        }
        else
            return;

        refs.add(oid, PropertyIdentifier.presentValue);
    }
}
