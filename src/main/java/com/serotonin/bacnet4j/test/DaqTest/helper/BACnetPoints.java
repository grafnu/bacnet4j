package com.serotonin.bacnet4j.test.DaqTest.helper;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.test.DevicesProfile.DistechController;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.ObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.RequestUtils;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BACnetPoints {

    BacnetDictionaryObject bacnetDictionaryObject = new BacnetDictionaryObject();
    Multimap<BacnetObjectType, Hashtable<String, Object>> bacnetObjectMap = ArrayListMultimap.create();
//    DistechController disthechController = new DistechController();
    boolean printPICS = false;

    String[] dictionaryTypes = {
            "Device",
            "Analog Input",
            "Analog Output",
            "Analog Value",
            "Binary Input",
            "Binary Output",
            "Binary Value"
    };

    public void get(LocalDevice localDevice, boolean printPICS) throws  Exception {
        for (RemoteDevice remoteDevice : localDevice.getRemoteDevices()) {
        	String macAddress = remoteDevice.getAddress().getMacAddress().getMacAddressDottedString();
        	DistechController disthechController = new DistechController(macAddress);
        	try {
        		System.out.println("Query remote device " + remoteDevice);
        		System.out.println("macAddress " + remoteDevice.getAddress().getMacAddress().getMacAddressDottedString());
        	bacnetObjectMap.clear();
            RequestUtils.getExtendedDeviceInformation(localDevice, remoteDevice);
            System.out.println("Device ID: " + remoteDevice.getObjectIdentifier());
            @SuppressWarnings("unchecked")
            List<ObjectIdentifier> allObjectsIdentifier = ((SequenceOf<ObjectIdentifier>) RequestUtils
                    .sendReadPropertyAllowNull(
                            localDevice,
                            remoteDevice,
                            remoteDevice.getObjectIdentifier(),
                            PropertyIdentifier.objectList))
                    .getValues();
            
            System.out.println("Start read properties");
            final long start = System.currentTimeMillis();
            
            PropertyReferences propertyRefs = new PropertyReferences();
            propertyRefs.add(remoteDevice.getObjectIdentifier(), PropertyIdentifier.all);
            PropertyValues propertyVal = RequestUtils.readProperties(localDevice, remoteDevice, propertyRefs, null);
			savePropertyReference(allObjectsIdentifier, propertyVal, remoteDevice, disthechController);
            
            // get all bacnet points and save them in the map
            for (ObjectIdentifier objectIdentifier : allObjectsIdentifier) {
            	ObjectType type = objectIdentifier.getObjectType();
            	for (int i = 0; i < ObjectType.ALL.length; i++) {
            		if(type.equals(ObjectType.ALL[i])) {
            			PropertyReferences refs = new PropertyReferences();
            			addPropertyReferences(refs, objectIdentifier);
            			PropertyValues propertyValues = RequestUtils.readProperties(localDevice, remoteDevice, refs, null);
            			savePropertyReference(allObjectsIdentifier, propertyValues, remoteDevice, disthechController);
            		}
            	}
            }
            
            System.out.println(String.format("Properties read done in %d ms", System.currentTimeMillis() - start));

            bacnetDictionaryObject.addObject(remoteDevice.getObjectIdentifier().toString(), bacnetObjectMap);
            if(printPICS) disthechController.print();

        	} catch(Exception e) {
        		System.out.println("Error reading device " + e.getMessage());
        		e.printStackTrace();
        	}
        }
//        bacnetDictionaryObject.printAllDevices();
    }
    
    public void savePropertyReference(List<ObjectIdentifier> allObjectsIdentifier, PropertyValues propertyValues,
    		RemoteDevice remoteDevice, DistechController disthechController) {
        for (ObjectIdentifier objectIdentifier : allObjectsIdentifier) {
            saveObject(objectIdentifier, propertyValues, disthechController);
        }
    }

    private void saveObject(ObjectIdentifier objectIdentifier, PropertyValues propertyValues, DistechController disthechController) {

        Hashtable<String, Object> points = new Hashtable<String, Object>();
        BacnetObjectType bacnetObjectType = null;
        String ObjectIdentifier = "";

        for (ObjectPropertyReference objectPropertyReference : propertyValues) {
            if (objectIdentifier.equals(objectPropertyReference.getObjectIdentifier())) {
                ObjectIdentifier = objectPropertyReference.getObjectIdentifier().toString();

                // get object type and assign it to BacnetObjectTypes
                for (int dictionaryTypesPosition = 0; dictionaryTypesPosition < dictionaryTypes.length; dictionaryTypesPosition++) {
                    if (objectIdentifier.toString().contains(dictionaryTypes[dictionaryTypesPosition])) {
                        BacnetObjectType arr[] = BacnetObjectType.values();
                        for (BacnetObjectType obj : arr) {
                            if (obj.ordinal() == dictionaryTypesPosition) {
                                bacnetObjectType = obj;
                            }
                        }
                    }
                }

                if (bacnetObjectType != null) {
                    try {
						points.put(objectPropertyReference.getPropertyIdentifier().toString(),
						        propertyValues.get(objectPropertyReference));
					} catch (PropertyValueException e) {
						// ignore invalid points 
					}
                }
            }
        }
        if (bacnetObjectType != null) {
            bacnetObjectMap.put(bacnetObjectType, points);
            disthechController.addToProfile(bacnetObjectType.toString(), points, ObjectIdentifier);
        }
    }
    
    private static void addPropertyReferences(PropertyReferences refs, ObjectIdentifier oid) {
    	
    	// Access door
    	PropertyIdentifier[] accessDoorPropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.description, 
    			PropertyIdentifier.statusFlags,
    			PropertyIdentifier.eventState,
    			PropertyIdentifier.reliability, 
    			PropertyIdentifier.outOfService, 
    			PropertyIdentifier.priorityArray,
    			PropertyIdentifier.relinquishDefault, 
    			PropertyIdentifier.doorStatus,
    			PropertyIdentifier.lockStatus,
    			PropertyIdentifier.securedStatus, 
    			PropertyIdentifier.doorMembers,
    			PropertyIdentifier.doorPulseTime, 
    			PropertyIdentifier.doorExtendedPulseTime,
    			PropertyIdentifier.doorUnlockDelayTime,
    			PropertyIdentifier.doorOpenTooLongTime, 
    			PropertyIdentifier.doorAlarmState, 
    			PropertyIdentifier.maskedAlarmValues, 
    			PropertyIdentifier.maintenanceRequired, 
    			PropertyIdentifier.timeDelay, 
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.alarmValues,
    			PropertyIdentifier.faultValues, 
    			PropertyIdentifier.eventEnable, 
    			PropertyIdentifier.ackedTransitions, 
    			PropertyIdentifier.notifyType, 
    			PropertyIdentifier.eventTimeStamps,
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal,  
    			PropertyIdentifier.eventDetectionEnable,
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef,
    	};
    	
    	// Accumulator
    	PropertyIdentifier[] accumulartorPropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.description,
    			PropertyIdentifier.deviceType, 
    			PropertyIdentifier.statusFlags,
    			PropertyIdentifier.eventState, 
    			PropertyIdentifier.reliability, 
    			PropertyIdentifier.outOfService, 
    			PropertyIdentifier.scale, 
    			PropertyIdentifier.units, 
    			PropertyIdentifier.prescale, 
    			PropertyIdentifier.maxPresValue, 
    			PropertyIdentifier.valueChangeTime, 
    			PropertyIdentifier.valueBeforeChange,
    			PropertyIdentifier.valueSet,
    			PropertyIdentifier.loggingRecord,
    			PropertyIdentifier.loggingObject, 
    			PropertyIdentifier.pulseRate, 
    			PropertyIdentifier.highLimit, 
    			PropertyIdentifier.lowLimit, 
    			PropertyIdentifier.limitMonitoringInterval,
    			PropertyIdentifier.notificationClass, 
    			PropertyIdentifier.timeDelay,
    			PropertyIdentifier.limitEnable, 
    			PropertyIdentifier.eventEnable,
    			PropertyIdentifier.ackedTransitions,
    			PropertyIdentifier.notifyType, 
    			PropertyIdentifier.eventTimeStamps,
    			PropertyIdentifier.profileName,
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig,
    			PropertyIdentifier.timeDelayNormal, 
    			PropertyIdentifier.eventDetectionEnable,
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};
    	
    	// Analog Input
    	PropertyIdentifier[] analogInputPropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.description, 
    			PropertyIdentifier.deviceType,
    			PropertyIdentifier.statusFlags, 
    			PropertyIdentifier.eventState,
    			PropertyIdentifier.reliability,
    			PropertyIdentifier.outOfService, 
    			PropertyIdentifier.updateInterval,
    			PropertyIdentifier.units, 
    			PropertyIdentifier.minPresValue, 
    			PropertyIdentifier.maxPresValue,
    			PropertyIdentifier.resolution, 
    			PropertyIdentifier.covIncrement, 
    			PropertyIdentifier.timeDelay, 
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.highLimit, 
    			PropertyIdentifier.lowLimit,
    			PropertyIdentifier.deadband, 
    			PropertyIdentifier.limitEnable,
    			PropertyIdentifier.eventEnable, 
    			PropertyIdentifier.ackedTransitions,
    			PropertyIdentifier.notifyType, 
    			PropertyIdentifier.eventTimeStamps,
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.passbackMode, 
    			PropertyIdentifier.eventMessageTexts,
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal, 
    			PropertyIdentifier.eventDetectionEnable,
    			PropertyIdentifier.eventAlgorithmInhibit, 
    			PropertyIdentifier.eventAlgorithmInhibitRef, 
    			PropertyIdentifier.activeVtSessions, 
    			PropertyIdentifier.reliabilityEvaluationInhibit,
    			PropertyIdentifier.faultHighLimit, 
    			PropertyIdentifier.faultLowLimit, 
    			PropertyIdentifier.profileLocation
    		};
    	
    	// Analog Output
    	PropertyIdentifier[] analogOutputPropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.description, 
    			PropertyIdentifier.deviceType,
    			PropertyIdentifier.statusFlags,
    			PropertyIdentifier.eventState, 
    			PropertyIdentifier.reliability,
    			PropertyIdentifier.outOfService,
    			PropertyIdentifier.units, 
    			PropertyIdentifier.minPresValue, 
    			PropertyIdentifier.maxPresValue, 
    			PropertyIdentifier.resolution, 
    			PropertyIdentifier.priorityArray, 
    			PropertyIdentifier.relinquishDefault,
    			PropertyIdentifier.covIncrement,
    			PropertyIdentifier.timeDelay, 
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.highLimit,
    			PropertyIdentifier.lowLimit, 
    			PropertyIdentifier.deadband,
    			PropertyIdentifier.limitEnable,
    			PropertyIdentifier.eventEnable, 
    			PropertyIdentifier.ackedTransitions, 
    			PropertyIdentifier.notifyType, 
    			PropertyIdentifier.eventTimeStamps, 
    			PropertyIdentifier.profileName,
    			PropertyIdentifier.eventMessageTexts,
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal,
    			PropertyIdentifier.eventDetectionEnable, 
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};
    	
    	// Analog Value
    	PropertyIdentifier[] analogValuePropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.presentValue, 
    			PropertyIdentifier.description,
    			PropertyIdentifier.statusFlags,
    			PropertyIdentifier.eventState, 
    			PropertyIdentifier.reliability, 
    			PropertyIdentifier.outOfService,
    			PropertyIdentifier.units,
    			PropertyIdentifier.priorityArray,
    			PropertyIdentifier.relinquishDefault,
    			PropertyIdentifier.covIncrement,
    			PropertyIdentifier.timeDelay,
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.highLimit, 
    			PropertyIdentifier.lowLimit, 
    			PropertyIdentifier.deadband, 
    			PropertyIdentifier.limitEnable,
    			PropertyIdentifier.eventEnable,
    			PropertyIdentifier.ackedTransitions,
    			PropertyIdentifier.notifyType, 
    			PropertyIdentifier.eventTimeStamps, 
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal,
    			PropertyIdentifier.eventDetectionEnable, 
    			PropertyIdentifier.eventAlgorithmInhibit, 
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};
    	
    	// Averaging
    	PropertyIdentifier[] averagingPropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.minimumValue,
    			PropertyIdentifier.minimumValueTimestamp,
    			PropertyIdentifier.averageValue, 
    			PropertyIdentifier.varianceValue,
    			PropertyIdentifier.maximumValue, 
    			PropertyIdentifier.maximumValueTimestamp,
    			PropertyIdentifier.description, 
    			PropertyIdentifier.attemptedSamples, 
    			PropertyIdentifier.validSamples,
    			PropertyIdentifier.objectPropertyReference,
    			PropertyIdentifier.windowInterval, 
    			PropertyIdentifier.windowSamples,
    			PropertyIdentifier.profileName, 
    	};
    	
    	// Binary Input
    	PropertyIdentifier[] binaryInputPropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.presentValue, 
    			PropertyIdentifier.description, 
    			PropertyIdentifier.deviceType, 
    			PropertyIdentifier.statusFlags,
    			PropertyIdentifier.eventState,
    			PropertyIdentifier.reliability,
    			PropertyIdentifier.outOfService, 
    			PropertyIdentifier.polarity,
    			PropertyIdentifier.inactiveText,
    			PropertyIdentifier.activeText, 
    			PropertyIdentifier.changeOfStateTime,
    			PropertyIdentifier.changeOfStateCount, 
    			PropertyIdentifier.timeOfStateCountReset,
    			PropertyIdentifier.elapsedActiveTime,
    			PropertyIdentifier.timeOfActiveTimeReset, 
    			PropertyIdentifier.notificationClass, 
    			PropertyIdentifier.alarmValue, 
    			PropertyIdentifier.eventEnable,
    			PropertyIdentifier.ackedTransitions, 
    			PropertyIdentifier.notifyType, 
    			PropertyIdentifier.eventTimeStamps, 
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelay, 
    			PropertyIdentifier.timeDelayNormal, 
    			PropertyIdentifier.eventDetectionEnable, 
    			PropertyIdentifier.eventAlgorithmInhibit, 
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};
    	
    	// Binary Output
    	PropertyIdentifier[] binaryOutputPropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.presentValue,
    			PropertyIdentifier.description, 
    			PropertyIdentifier.deviceType, 
    			PropertyIdentifier.statusFlags, 
    			PropertyIdentifier.eventState, 
    			PropertyIdentifier.reliability,
    			PropertyIdentifier.outOfService, 
    			PropertyIdentifier.polarity,
    			PropertyIdentifier.inactiveText,
    			PropertyIdentifier.activeText, 
    			PropertyIdentifier.changeOfStateTime, 
    			PropertyIdentifier.changeOfStateCount, 
    			PropertyIdentifier.timeOfStateCountReset,
    			PropertyIdentifier.elapsedActiveTime, 
    			PropertyIdentifier.timeOfActiveTimeReset, 
    			PropertyIdentifier.minimumOffTime, 
    			PropertyIdentifier.minimumOnTime, 
    			PropertyIdentifier.priorityArray, 
    			PropertyIdentifier.relinquishDefault,
    			PropertyIdentifier.timeDelay, 
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.feedbackValue,
    			PropertyIdentifier.eventEnable, 
    			PropertyIdentifier.ackedTransitions, 
    			PropertyIdentifier.notifyType, 
    			PropertyIdentifier.eventTimeStamps, 
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig,
    			PropertyIdentifier.timeDelayNormal,
    			PropertyIdentifier.eventDetectionEnable,
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};
    	
    	// Binary Value
    	PropertyIdentifier[] binaryValuePropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.presentValue,
    			PropertyIdentifier.description, 
    			PropertyIdentifier.statusFlags,
    			PropertyIdentifier.eventState, 
    			PropertyIdentifier.reliability, 
    			PropertyIdentifier.outOfService, 
    			PropertyIdentifier.inactiveText, 
    			PropertyIdentifier.activeText,
    			PropertyIdentifier.changeOfStateTime,
    			PropertyIdentifier.changeOfStateCount,
    			PropertyIdentifier.timeOfStateCountReset, 
    			PropertyIdentifier.elapsedActiveTime, 
    			PropertyIdentifier.timeOfActiveTimeReset, 
    			PropertyIdentifier.minimumOffTime, 
    			PropertyIdentifier.minimumOnTime, 
    			PropertyIdentifier.priorityArray, 
    			PropertyIdentifier.relinquishDefault,
    			PropertyIdentifier.timeDelay, 
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.alarmValue,
    			PropertyIdentifier.eventEnable, 
    			PropertyIdentifier.ackedTransitions, 
    			PropertyIdentifier.notifyType, 
    			PropertyIdentifier.eventTimeStamps,
    			PropertyIdentifier.profileName,
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig,
    			PropertyIdentifier.timeDelayNormal,
    			PropertyIdentifier.eventDetectionEnable,
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};
    	
    	// Calendar
    	PropertyIdentifier[] calendarPropertyIdentifiers = {
				PropertyIdentifier.objectType,
				PropertyIdentifier.description, 
				PropertyIdentifier.dateList,
				PropertyIdentifier.profileName
    	};
    	
        
    	// Command
    	PropertyIdentifier[] commandPropertyIdentifiers = {
        		PropertyIdentifier.objectType, 
        		PropertyIdentifier.description, 
        		PropertyIdentifier.inProcess,
        		PropertyIdentifier.allWritesSuccessful,
        		PropertyIdentifier.action, 
        		PropertyIdentifier.actionText,
        		PropertyIdentifier.profileName, 
        		PropertyIdentifier.eventMessageTexts,
        		PropertyIdentifier.eventMessageTextsConfig, 
        		PropertyIdentifier.eventDetectionEnable
    	};

    	// Device
    	PropertyIdentifier[] devicePropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.systemStatus, 
    			PropertyIdentifier.vendorName, 
    			PropertyIdentifier.vendorIdentifier, 
    			PropertyIdentifier.modelName, 
    			PropertyIdentifier.firmwareRevision,
    			PropertyIdentifier.applicationSoftwareVersion,
    			PropertyIdentifier.location, 
    			PropertyIdentifier.description, 
    			PropertyIdentifier.protocolVersion,
    			PropertyIdentifier.protocolRevision, 
    			PropertyIdentifier.protocolServicesSupported,
    			PropertyIdentifier.protocolObjectTypesSupported,
    			PropertyIdentifier.objectList, 
    			PropertyIdentifier.structuredObjectList, 
    			PropertyIdentifier.maxApduLengthAccepted, 
    			PropertyIdentifier.segmentationSupported, 
    			PropertyIdentifier.vtClassesSupported,
    			PropertyIdentifier.activeVtSessions, 
    			PropertyIdentifier.localTime, 
    			PropertyIdentifier.localDate, 
    			PropertyIdentifier.utcOffset, 
    			PropertyIdentifier.daylightSavingsStatus, 
    			PropertyIdentifier.apduSegmentTimeout,
    			PropertyIdentifier.apduTimeout,
    			PropertyIdentifier.numberOfApduRetries,
    			PropertyIdentifier.listOfSessionKeys, 
    			PropertyIdentifier.timeSynchronizationRecipients,
    			PropertyIdentifier.maxMaster, 
    			PropertyIdentifier.maxInfoFrames, 
    			PropertyIdentifier.deviceAddressBinding, 
    			PropertyIdentifier.databaseRevision, 
    			PropertyIdentifier.configurationFiles, 
    			PropertyIdentifier.lastRestoreTime,
    			PropertyIdentifier.backupFailureTimeout, 
    			PropertyIdentifier.backupPreparationTime,
    			PropertyIdentifier.restorePreparationTime,
    			PropertyIdentifier.restoreCompletionTime,
    			PropertyIdentifier.backupAndRestoreState, 
    			PropertyIdentifier.activeCovSubscriptions,
    			PropertyIdentifier.maxSegmentsAccepted,
    			PropertyIdentifier.utcTimeSynchronizationRecipients,
    			PropertyIdentifier.timeSynchronizationInterval,
    			PropertyIdentifier.alignIntervals,
    			PropertyIdentifier.intervalOffset, 
    			PropertyIdentifier.slaveProxyEnable, 
    			PropertyIdentifier.autoSlaveDiscovery, 
    			PropertyIdentifier.slaveAddressBinding,
    			PropertyIdentifier.manualSlaveAddressBinding,
    			PropertyIdentifier.lastRestartReason, 
    			PropertyIdentifier.restartNotificationRecipients,
    			PropertyIdentifier.timeOfDeviceRestart,
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.eventDetectionEnable,
    	};
       

// Event enrollment
    	PropertyIdentifier[] eventEnrollmentPropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.description, 
    			PropertyIdentifier.eventType, 
    			PropertyIdentifier.notifyType,
    			PropertyIdentifier.eventParameters, 
    			PropertyIdentifier.objectPropertyReference,
    			PropertyIdentifier.eventState, 
    			PropertyIdentifier.eventEnable, 
    			PropertyIdentifier.ackedTransitions, 
    			PropertyIdentifier.notificationClass, 
    			PropertyIdentifier.eventTimeStamps,
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal, 
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};
       
    	// Event log
    	PropertyIdentifier[] eventLogPropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.description, 
    			PropertyIdentifier.statusFlags, 
    			PropertyIdentifier.eventState, 
    			PropertyIdentifier.reliability, 
    			PropertyIdentifier.enable, 
    			PropertyIdentifier.startTime, 
    			PropertyIdentifier.stopTime,
    			PropertyIdentifier.stopWhenFull, 
    			PropertyIdentifier.bufferSize,
    			PropertyIdentifier.logBuffer,
    			PropertyIdentifier.recordCount,
    			PropertyIdentifier.totalRecordCount, 
    			PropertyIdentifier.notificationThreshold,
    			PropertyIdentifier.recordsSinceNotification,
    			PropertyIdentifier.lastNotifyRecord, 
    			PropertyIdentifier.notificationClass, 
    			PropertyIdentifier.eventEnable, 
    			PropertyIdentifier.ackedTransitions,
    			PropertyIdentifier.notifyType, 
    			PropertyIdentifier.eventTimeStamps, 
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.eventDetectionEnable, 
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};

    	// File
    	PropertyIdentifier[] filePropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.description, 
    			PropertyIdentifier.fileType, 
    			PropertyIdentifier.fileSize, 
    			PropertyIdentifier.modificationDate,
    			PropertyIdentifier.archive, 
    			PropertyIdentifier.readOnly, 
    			PropertyIdentifier.fileAccessMethod,
    			PropertyIdentifier.recordCount,
    			PropertyIdentifier.profileName,
    	};

    	// Group
    	PropertyIdentifier[] groupPropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.description, 
    			PropertyIdentifier.listOfGroupMembers, 
    			PropertyIdentifier.presentValue, 
    			PropertyIdentifier.profileName,
    	};
        

    	// Life safety point
    	PropertyIdentifier[] lifeSafetyPointPropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.trackingValue, 
    			PropertyIdentifier.description,
    			PropertyIdentifier.deviceType, 
    			PropertyIdentifier.statusFlags, 
    			PropertyIdentifier.eventState, 
    			PropertyIdentifier.reliability,
    			PropertyIdentifier.outOfService,
    			PropertyIdentifier.mode,
    			PropertyIdentifier.acceptedModes, 
    			PropertyIdentifier.timeDelay, 
    			PropertyIdentifier.alarmValues, 
    			PropertyIdentifier.faultValues, 
    			PropertyIdentifier.eventEnable, 
    			PropertyIdentifier.ackedTransitions,
    			PropertyIdentifier.notifyType,
    			PropertyIdentifier.eventTimeStamps,
    			PropertyIdentifier.silenced,
    			PropertyIdentifier.operationExpected,
    			PropertyIdentifier.maintenanceRequired,
    			PropertyIdentifier.setting, 
    			PropertyIdentifier.directReading,
    			PropertyIdentifier.units, 
    			PropertyIdentifier.memberOf, 
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal,
    			PropertyIdentifier.eventDetectionEnable,
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};
        

    	// Life safety zone
    	PropertyIdentifier[] lifeSafetyZonePropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.trackingValue, 
    			PropertyIdentifier.description, 
    			PropertyIdentifier.deviceType, 
    			PropertyIdentifier.statusFlags,
    			PropertyIdentifier.eventState,
    			PropertyIdentifier.reliability, 
    			PropertyIdentifier.outOfService,
    			PropertyIdentifier.mode, 
    			PropertyIdentifier.acceptedModes, 
    			PropertyIdentifier.timeDelay,
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.lifeSafetyAlarmValues,
    			PropertyIdentifier.alarmValues,
    			PropertyIdentifier.faultValues,
    			PropertyIdentifier.eventEnable,
    			PropertyIdentifier.ackedTransitions,
    			PropertyIdentifier.notifyType,
    			PropertyIdentifier.eventTimeStamps,
    			PropertyIdentifier.silenced,
    			PropertyIdentifier.operationExpected,
    			PropertyIdentifier.maintenanceRequired,
    			PropertyIdentifier.zoneMembers,
    			PropertyIdentifier.memberOf,
    			PropertyIdentifier.profileName,
    			PropertyIdentifier.eventMessageTexts,
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal,
    			PropertyIdentifier.eventDetectionEnable,
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef,
    	};
    	
    	// Load control
    	PropertyIdentifier[] loadControlPropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.description, 
    			PropertyIdentifier.stateDescription,
    			PropertyIdentifier.statusFlags, 
    			PropertyIdentifier.eventState,
    			PropertyIdentifier.reliability, 
    			PropertyIdentifier.requestedShedLevel,
    			PropertyIdentifier.startTime, 
    			PropertyIdentifier.shedDuration, 
    			PropertyIdentifier.dutyWindow,
    			PropertyIdentifier.enable, 
    			PropertyIdentifier.fullDutyBaseline,
    			PropertyIdentifier.expectedShedLevel, 
    			PropertyIdentifier.actualShedLevel,
    			PropertyIdentifier.shedLevels,
    			PropertyIdentifier.shedLevelDescriptions,
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.timeDelay,
    			PropertyIdentifier.eventEnable,
    			PropertyIdentifier.ackedTransitions, 
    			PropertyIdentifier.notifyType,
    			PropertyIdentifier.eventTimeStamps,
    			PropertyIdentifier.profileName,
    			PropertyIdentifier.eventMessageTexts,
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal,
    			PropertyIdentifier.eventDetectionEnable,
    			PropertyIdentifier.eventAlgorithmInhibit, 
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};

    	// Loop
    	PropertyIdentifier[] loopPropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.description,
    			PropertyIdentifier.statusFlags,
    			PropertyIdentifier.eventState,
    			PropertyIdentifier.reliability,
    			PropertyIdentifier.outOfService,
    			PropertyIdentifier.updateInterval,
    			PropertyIdentifier.outputUnits, 
    			PropertyIdentifier.manipulatedVariableReference,
    			PropertyIdentifier.controlledVariableReference,
    			PropertyIdentifier.controlledVariableValue, 
    			PropertyIdentifier.controlledVariableUnits, 
    			PropertyIdentifier.setpointReference,
    			PropertyIdentifier.setpoint,
    			PropertyIdentifier.action,
    			PropertyIdentifier.proportionalConstant,
    			PropertyIdentifier.proportionalConstantUnits,
    			PropertyIdentifier.integralConstant,
    			PropertyIdentifier.integralConstantUnits,
    			PropertyIdentifier.derivativeConstant, 
    			PropertyIdentifier.derivativeConstantUnits, 
    			PropertyIdentifier.bias,
    			PropertyIdentifier.maximumOutput,
    			PropertyIdentifier.minimumOutput,
    			PropertyIdentifier.priorityForWriting,
    			PropertyIdentifier.covIncrement, 
    			PropertyIdentifier.timeDelay, 
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.errorLimit,
    			PropertyIdentifier.deadband,
    			PropertyIdentifier.eventEnable,
    			PropertyIdentifier.ackedTransitions,
    			PropertyIdentifier.notifyType,
    			PropertyIdentifier.eventTimeStamps,
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal, 
    			PropertyIdentifier.eventDetectionEnable,
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};
    	
    	// Multi state input
    	PropertyIdentifier[] multistateInputPropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.description, 
    			PropertyIdentifier.deviceType, 
    			PropertyIdentifier.statusFlags,
    			PropertyIdentifier.eventState, 
    			PropertyIdentifier.reliability, 
    			PropertyIdentifier.outOfService, 
    			PropertyIdentifier.numberOfStates,
    			PropertyIdentifier.stateText, 
    			PropertyIdentifier.timeDelay, 
    			PropertyIdentifier.notificationClass, 
    			PropertyIdentifier.alarmValues, 
    			PropertyIdentifier.faultValues, 
    			PropertyIdentifier.eventEnable, 
    			PropertyIdentifier.ackedTransitions,
    			PropertyIdentifier.notifyType,
    			PropertyIdentifier.eventTimeStamps,
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts,
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal, 
    			PropertyIdentifier.eventDetectionEnable,
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};
    	
    	// Multi state output
    	PropertyIdentifier[] multistateOutputPropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.description,
    			PropertyIdentifier.deviceType,
    			PropertyIdentifier.statusFlags,
    			PropertyIdentifier.eventState,
    			PropertyIdentifier.reliability,
    			PropertyIdentifier.outOfService, 
    			PropertyIdentifier.numberOfStates, 
    			PropertyIdentifier.stateText,
    			PropertyIdentifier.priorityArray,
    			PropertyIdentifier.relinquishDefault, 
    			PropertyIdentifier.timeDelay,
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.feedbackValue,
    			PropertyIdentifier.eventEnable,
    			PropertyIdentifier.ackedTransitions,
    			PropertyIdentifier.notifyType, 
    			PropertyIdentifier.eventTimeStamps,
    			PropertyIdentifier.profileName,
    			PropertyIdentifier.eventMessageTexts,
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal,
    			PropertyIdentifier.eventDetectionEnable,
    			PropertyIdentifier.eventAlgorithmInhibit, 
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};


    	// Multi state value
    	PropertyIdentifier[] multistateValuePropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.description, 
    			PropertyIdentifier.statusFlags, 
    			PropertyIdentifier.eventState, 
    			PropertyIdentifier.reliability, 
    			PropertyIdentifier.outOfService,
    			PropertyIdentifier.numberOfStates, 
    			PropertyIdentifier.stateText, 
    			PropertyIdentifier.priorityArray,
    			PropertyIdentifier.relinquishDefault, 
    			PropertyIdentifier.timeDelay,
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.alarmValues,
    			PropertyIdentifier.faultValues,
    			PropertyIdentifier.eventEnable,
    			PropertyIdentifier.ackedTransitions, 
    			PropertyIdentifier.notifyType,
    			PropertyIdentifier.eventTimeStamps, 
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal, 
    			PropertyIdentifier.eventDetectionEnable, 
    			PropertyIdentifier.eventAlgorithmInhibit, 
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};


    	// Notification class
    	PropertyIdentifier[] notificationClassPropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.description,
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.priority, 
    			PropertyIdentifier.ackRequired, 
    			PropertyIdentifier.recipientList,
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts,
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.eventDetectionEnable,
    	};


    	// Program
    	PropertyIdentifier[] programPropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.programState,
    			PropertyIdentifier.programChange, 
    			PropertyIdentifier.reasonForHalt,
    			PropertyIdentifier.descriptionOfHalt,
    			PropertyIdentifier.programLocation,
    			PropertyIdentifier.description,
    			PropertyIdentifier.instanceOf,
    			PropertyIdentifier.statusFlags,
    			PropertyIdentifier.reliability,
    			PropertyIdentifier.outOfService,
    			PropertyIdentifier.profileName,
    			PropertyIdentifier.eventMessageTexts,
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.eventDetectionEnable,	
    	};

    	// Pulse converter
    	PropertyIdentifier[] pulseConverterPropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.description, 
    			PropertyIdentifier.presentValue, 
    			PropertyIdentifier.inputReference,
    			PropertyIdentifier.statusFlags, 
    			PropertyIdentifier.eventState,
    			PropertyIdentifier.reliability, 
    			PropertyIdentifier.outOfService, 
    			PropertyIdentifier.units, 
    			PropertyIdentifier.scaleFactor,
    			PropertyIdentifier.adjustValue, 
    			PropertyIdentifier.count,
    			PropertyIdentifier.updateTime, 
    			PropertyIdentifier.countChangeTime,
    			PropertyIdentifier.countBeforeChange,
    			PropertyIdentifier.covIncrement,
    			PropertyIdentifier.covPeriod,
    			PropertyIdentifier.notificationClass, 
    			PropertyIdentifier.timeDelay, 
    			PropertyIdentifier.highLimit, 
    			PropertyIdentifier.lowLimit,
    			PropertyIdentifier.deadband, 
    			PropertyIdentifier.limitEnable,
    			PropertyIdentifier.eventEnable,
    			PropertyIdentifier.ackedTransitions,
    			PropertyIdentifier.notifyType,
    			PropertyIdentifier.eventTimeStamps, 
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts,
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.timeDelayNormal,
    			PropertyIdentifier.eventDetectionEnable, 
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef,
    	};
       

    	// Schedule
    	PropertyIdentifier[] schedulePropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.presentValue,
    			PropertyIdentifier.description,
    			PropertyIdentifier.effectivePeriod, 
    			PropertyIdentifier.weeklySchedule, 
    			PropertyIdentifier.scheduleDefault,
    			PropertyIdentifier.exceptionSchedule,
    			PropertyIdentifier.listOfObjectPropertyReferences,
    			PropertyIdentifier.priorityForWriting,
    			PropertyIdentifier.statusFlags, 
    			PropertyIdentifier.reliability, 
    			PropertyIdentifier.outOfService,
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.eventDetectionEnable
    	};
      
    	// Structured View
    	PropertyIdentifier[] structureViewPropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.description,
    			PropertyIdentifier.nodeType, 
    			PropertyIdentifier.nodeSubtype, 
    			PropertyIdentifier.subordinateList,
    			PropertyIdentifier.subordinateAnnotations, 
    			PropertyIdentifier.profileName,
    	};

    	// Trend log
    	PropertyIdentifier[] trendLogPropertyIdentifiers = {
    			PropertyIdentifier.objectType, 
    			PropertyIdentifier.description, 
    			PropertyIdentifier.enable, 
    			PropertyIdentifier.startTime, 
    			PropertyIdentifier.stopTime, 
    			PropertyIdentifier.logDeviceObjectProperty, 
    			PropertyIdentifier.logInterval,
    			PropertyIdentifier.covResubscriptionInterval,
    			PropertyIdentifier.clientCovIncrement, 
    			PropertyIdentifier.stopWhenFull,
    			PropertyIdentifier.bufferSize,
    			PropertyIdentifier.logBuffer, 
    			PropertyIdentifier.recordCount, 
    			PropertyIdentifier.totalRecordCount, 
    			PropertyIdentifier.notificationThreshold, 
    			PropertyIdentifier.recordsSinceNotification,
    			PropertyIdentifier.lastNotifyRecord,
    			PropertyIdentifier.eventState, 
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.eventEnable, 
    			PropertyIdentifier.ackedTransitions,
    			PropertyIdentifier.notifyType, 
    			PropertyIdentifier.eventTimeStamps, 
    			PropertyIdentifier.profileName, 
    			PropertyIdentifier.loggingType, 
    			PropertyIdentifier.alignIntervals, 
    			PropertyIdentifier.intervalOffset, 
    			PropertyIdentifier.trigger, 
    			PropertyIdentifier.statusFlags, 
    			PropertyIdentifier.reliability, 
    			PropertyIdentifier.eventMessageTexts, 
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.eventDetectionEnable, 
    			PropertyIdentifier.eventAlgorithmInhibit, 
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};

    	// Trend log multiple
    	PropertyIdentifier[] trendLogMultiplePropertyIdentifiers = {
    			PropertyIdentifier.objectType,
    			PropertyIdentifier.description, 
    			PropertyIdentifier.statusFlags, 
    			PropertyIdentifier.eventState,
    			PropertyIdentifier.reliability,
    			PropertyIdentifier.enable,
    			PropertyIdentifier.startTime,
    			PropertyIdentifier.stopTime, 
    			PropertyIdentifier.logDeviceObjectProperty,

    			PropertyIdentifier.loggingType, 
    			PropertyIdentifier.logInterval, 
    			PropertyIdentifier.alignIntervals,
    			PropertyIdentifier.intervalOffset,
    			PropertyIdentifier.trigger,
    			PropertyIdentifier.stopWhenFull,
    			PropertyIdentifier.bufferSize,
    			PropertyIdentifier.logBuffer,
    			PropertyIdentifier.recordCount,
    			PropertyIdentifier.totalRecordCount,
    			PropertyIdentifier.notificationThreshold,
    			PropertyIdentifier.recordsSinceNotification,
    			PropertyIdentifier.lastNotifyRecord,
    			PropertyIdentifier.notificationClass,
    			PropertyIdentifier.eventEnable,
    			PropertyIdentifier.ackedTransitions,
    			PropertyIdentifier.notifyType,
    			PropertyIdentifier.eventTimeStamps,
    			PropertyIdentifier.profileName,
    			PropertyIdentifier.eventMessageTexts,
    			PropertyIdentifier.eventMessageTextsConfig, 
    			PropertyIdentifier.eventDetectionEnable,
    			PropertyIdentifier.eventAlgorithmInhibit,
    			PropertyIdentifier.eventAlgorithmInhibitRef
    	};

       
    	Map<String, PropertyIdentifier[]> properties = new HashMap<String, PropertyIdentifier[]>();
    
    	properties.put("Access Door", accessDoorPropertyIdentifiers);
    	properties.put("Accumulator", accumulartorPropertyIdentifiers);
    	properties.put("Analog Input", analogInputPropertyIdentifiers);
    	properties.put("Analog Output", analogOutputPropertyIdentifiers);
    	properties.put("Analog Value", analogValuePropertyIdentifiers);
    	properties.put("Averaging", averagingPropertyIdentifiers);
    	properties.put("Binary Input", binaryInputPropertyIdentifiers);
    	properties.put("Binary Output", binaryOutputPropertyIdentifiers);
    	properties.put("Binary Value", binaryValuePropertyIdentifiers);
    	properties.put("Calendar", calendarPropertyIdentifiers);
    	properties.put("Command", commandPropertyIdentifiers);
    	properties.put("Device", devicePropertyIdentifiers);
    	properties.put("Event Enrollment", eventEnrollmentPropertyIdentifiers);
    	properties.put("Event Log", eventLogPropertyIdentifiers);
    	properties.put("File", filePropertyIdentifiers);
    	properties.put("Group", groupPropertyIdentifiers);
    	properties.put("Life Safety Point", lifeSafetyPointPropertyIdentifiers);
    	properties.put("Life Safety Zone", lifeSafetyZonePropertyIdentifiers);
    	properties.put("Load Control", loadControlPropertyIdentifiers);
    	properties.put("Loop", loopPropertyIdentifiers);
    	properties.put("Multi State Input", multistateInputPropertyIdentifiers);
    	properties.put("Multi State Output", multistateOutputPropertyIdentifiers);
    	properties.put("Multi State Value", multistateValuePropertyIdentifiers);
    	properties.put("Notification Class", notificationClassPropertyIdentifiers);
    	properties.put("Program", programPropertyIdentifiers);
    	properties.put("Pulse Converter", pulseConverterPropertyIdentifiers);
    	properties.put("Shedule", schedulePropertyIdentifiers);
    	properties.put("Structure View",structureViewPropertyIdentifiers);
    	properties.put("Tren Log", trendLogPropertyIdentifiers);
    	properties.put("Trend Log Multiple", trendLogMultiplePropertyIdentifiers);
//    	
    	String type = oid.getObjectType().toString();
    	for(Entry<String, PropertyIdentifier[]> p : properties.entrySet()) {
    		String objectType = p.getKey();
    		if (type.contains(objectType)) {
    			PropertyIdentifier[] propertyIdentifierArray = p.getValue();
    			refs.add(oid, PropertyIdentifier.objectName);
    	        refs.add(oid, PropertyIdentifier.objectIdentifier);
    	        refs.add(oid, PropertyIdentifier.presentValue);
    			for (int i = 0; i < propertyIdentifierArray.length; i++) {
    				refs.add(oid, propertyIdentifierArray[i]);
    			}
    		}
    	}
    }
}
