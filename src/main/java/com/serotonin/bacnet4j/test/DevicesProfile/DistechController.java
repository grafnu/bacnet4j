package com.serotonin.bacnet4j.test.DevicesProfile;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import com.serotonin.bacnet4j.test.DaqTest.helper.Report;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class DistechController {

    
    

    Multimap<String, Map<Map<String, String>, Map<Object, String>>> lines = ArrayListMultimap.create();

    String reportText = "";
    Report report;
    String appendixText = "";
    Report appendices;
    Map<String, String[]> DeviceProperties = new Hashtable<String, String[]>();

    boolean picFailed = false;
    String picTestName = "protocol.bacnet.pic";
    
    public DistechController() {
    	this.report = new Report ("tmp/PicTestReport.txt");
    	this.appendices = new Report("tmp/PicTest_Appendix.txt");
        initializeProfile();
    }
    
    public DistechController(String macAddress) {
    	this.report = new Report ("tmp/"+ macAddress+" - PicTestReport.txt");
    	this.appendices = new Report("tmp/"+ macAddress+" - PicTest_Appendix.txt");
    	initializeProfile();
    }

    String[] Category = {
            "Mandatory", "Optional", "Proprietary" };

    // Object Type -> category -> fields
    Map<String, Map<String, String[]>> profile = new Hashtable<String, Map<String, String[]>>();

    String[] ObjectType = {
            "AnalogInput", "AnalogOutput", "AnalogValue", "BinaryInput", "BinaryOutput", "BinaryValue",
            "Calendar", "Device", "EventEnrollment", "File", "Loop", "Program", "NotificationClass",
            "Multi_stateInput", "Multi_stateOutput", "Multi_stateValue", "Schedule", "TrendLog" };

    String[] AnalogInput_Mandatory = {
            "Object identifier", "Object name", "Object type", "Present value", "Status flags", "Event state",
            "Out of service", "Units"
    };
    String[] AnalogInput_Optional = {
            "Description", "Device type", "Reliability", "Update interval", "Min pres value", "Max pres value",
            "Resolution", "Cov increment", "Time delay", "Notification class", "High limit", "Low limit", "Deadband",
            "Limit enable", "Event enable", "Notify type", "Event time stamps", "Event message texts",
            "Event message texts config", "Event detection enable", "Time delay normal", "Event algorithm inhibit",
            "Event algorithm inhibit ref",
    };
    String[] AnalogInput_Proprietary = {
            "Cov period", "Cov min send time", "Acked transitions",
    };
    String[] AnalogOutput_Mandatory = {
            "Object identifier", "Object name", "Object type", "Present value", "Status flags", "Event state",
            "Out of service", "Units", "Priority array", "Relinquish default"
    };
    String[] AnalogOutput_Optional = {
            "Description", "Device type", "Reliability", "Min present value", "Max present value", "Resolution",
            "Cov increment", "Time delay", "Notification class", "High limit", "Low limit", "Deadband", "Limit enable",
            "Event enable", "Notify type", "Event time stamps", "Event message texts", "Event message texts config",
            "Event detection enable", "Time delay normal", "Event algorithm inhibit", "Event algorithm inhibit ref",
    };
    String[] AnalogOutput_Proprietary = {
            "Cov period", "Cov min send time", "Acked transitions"
    };
    String[] AnalogValue_Mandatory = {
            "Object identifier", "Object name", "Object type", "Present value", "Status flags", "Event state",
            "Out of service", "Units"
    };
    String[] AnalogValue_Optional = {
            "Description", "Reliability", "Priority array", "Relinquish default", "Min present value",
            "Max present value", "Cov increment", "Time delay", "Notification class", "High limit", "Low limit",
            "Deadband", "Limit enable", "Event enable", "Notify type", "Event time stamps", "Event message texts",
            "Event message texts config", "Event detection enable", "Time delay normal", "Event algorithm inhibit",
            "Event algorithm inhibit ref"
    };
    String[] AnalogValue_Proprietary = {
            "Cov period", "Cov min send time", "Acked transitions",
    };
    String[] BinaryInput_Mandatory = {
            "Object identifier", "Object name", "Object type", "Present value", "Status flags", "Event state",
            "Out of service", "Polarity"
    };
    String[] BinaryInput_Optional = {
            "Description", "Device type", "Reliability", "Inactive text", "Active text", "Change of state time",
            "Change of state count", "Time of state count reset", "Elapsed active time", "Time of active time reset",
            "Time delay", "Notification class", "Alarm value", "Event enable", "Notify type", "Event time stamps",
            "Event message texts", "Event message texts config", "Event detection enable", "Time delay normal",
            "Event algorithm inhibit", "Event algorithm inhibit ref",
    };
    String[] BinaryInput_Proprietary = {
            "Cov period", "Cov min send time", "Acked transitions"
    };
    String[] BinaryOutput_Mandatory = {
            "Object identifier", "Object name", "Object type", "Present value", "Status flags", "Event state",
            "Out of service", "Polarity"
    };
    String[] BinaryOutput_Optional = {
            "Description", "Device type", "Reliability", "Inactive text", "Active text", "Change of state time",
            "Change of state count", "Time of state count reset", "Elapsed active time", "Time of active time reset",
            "Minimum off time", "Minimum on time", "Priority array", "Relinquish default", "Time delay",
            "Notification class", "Feedback value", "Event enable", "Notify type", "Event time stamps",
            "Event message texts", "Event message texts config", "Event detection enable", "Time delay normal",
            "Event algorithm inhibit", "Event algorithm inhibit ref"
    };
    String[] BinaryOutput_Proprietary = {
            "Cov period", "Cov min send time", "Acked transitions",
    };
    String[] BinaryValue_Mandatory = {
            "Object identifier", "Object name", "Object type", "Present value", "Status flags", "Event state",
            "Out of service",
    };
    String[] BinaryValue_Optional = {
            "Description", "Reliability", "Inactive text", "Active text", "Change of state time", "Change of state count",
            "Time of state count reset", "Elapsed active time", "Time of active time reset", "Minimum off time",
            "Minimum on time", "Priority array", "Relinquish default", "Time delay", "Notification class", "Alarm value",
            "Event enable", "Notify type", "Event time stamps", "Event message texts", "Event message texts config",
            "Event detection enable", "Time delay normal", "Event algorithm inhibit", "Event algorithm inhibit ref",
            "Property list",
    };
    String[] BinaryValue_Proprietary = {
            "Cov period", "Cov min send time", "Acked transitions",
    };
    String[] Calendar_Mandatory = {
            "Object identifier", "Object name", "Object type", "Present value", "Date list"
    };
    String[] Calendar_Optional = {
            "Description",
    };
    String[] Calendar_Proprietary = {
            "Time to next state", "Next state"
    };
    String[] Device_Mandatory = {
            "Object identifier", "Object name", "Object type", "System status", "Vendor name", "Vendor identifier",
            "Model name", "Firmware revision", "Application software version", "Protocol version", "Protocol revision",
            "Protocol services supported", "Protocol object types supported", "Object list", "Max apdu length accepted",
            "Segmentation supported", "Max segments accepted", "Apdu timeout", "Number of apdu retries",
            "Device address binding", "Database revision"
    };
    String[] Device_Optional = {
            "Location", "Description", "Local date", "Local time", "Utc offset", "Daylight savings status",
            "Apdu segment timeout", "Time synchronization recipients", "Configuration files", "Last restore time",
            "Backup failure timeout", "Backup preparation time", "Restore preparation time", "Restore completion time",
            "Backup and restore state", "Active cov subscriptions", "Last restart reason", "Time of device restart",
            "Restart notification recipients", "Utc time synchronization recipients", "Max master", "Max info frames",
            "Time synchronization interval", "Align intervals", "Interval offset"
    };
    String[] Device_Proprietary = {

    };
    String[] EventEnrollment_Mandatory = {
            "Object identifier", "Object name", "Object type", "Event type", "Notify type", "Event parameters",
            "Object property reference", "Event state", "Event enable", "Notification class", "Event time stamps",
            "Event detection enable", "Status flags", "Reliability"
    };
    String[] EventEnrollment_Optional = {
            "Description", "Acked transitions", "Event message texts", "Event message texts config", "Time delay normal"
    };
    String[] EventEnrollment_Proprietary = {

    };
    String[] File_Mandatory = {
            "Object identifier", "Object name", "Object type", "File type", "File size", "Modification date", "Archive",
            "Read only", "File access method"
    };
    String[] File_Optional = {
            "Description"
    };
    String[] File_Proprietary = {

    };
    String[] Loop_Mandatory = {
            "Object identifier", "Object name", "Object type", "Present value", "Status flags", "Event state",
            "Out of service", "Output units", "Manipulated variable reference", "Controlled variable reference",
            "Controlled variable value", "Controlled variable units", "Setpoint reference", "Setpoint", "Action",
            "Priority for writing"
    };
    String[] Loop_Optional = {
            "Description", "Reliability", "Update interval", "Proportional constant", "Proportional constant units",
            "Integral constant", "Integral constant units", "Derivative constant", "Derivative constant units", "Bias",
            "Maximum output", "Minimum output", "Cov increment", "Time delay", "Notification class", "Error limit",
            "Deadband", "Event enable", "Notify type", "Event time stamps", "Event message texts",
            "Event message texts config", "Event detection enable", "Time delay normal", "Event algorithm inhibit",
            "Event algorithm inhibit ref"
    };
    String[] Loop_Proprietary = {
            "Loopdeadband", "Saturation time", "Cov period", "Cov min send time", "Ramp time",
            "Saturation time low limit enable", "Saturation time high limit enable", "Acked transitions"
    };
    String[] Multi_stateInput_Mandatory = {
            "Object identifier", "Object name", "Object type", "Present value", "Status flags", "Event state",
            "Out of service", "Number of states"
    };
    String[] Multi_stateInput_Optional = {
            "Description", "Device type", "Reliability", "State text", "Time delay", "Notification class", "Alarm values",
            "Fault values", "Event enable", "Notify type", "Event time stamps", "Event message texts",
            "Event message texts config", "Event detection enable", "Time delay normal", "Event algorithm inhibit",
            "Event algorithm inhibit ref"
    };
    String[] Multi_stateInput_Proprietary = {
            "Cov period", "Cov min send time", "Acked transitions"
    };
    String[] Multi_stateValue_Mandatory = {
            "Object identifier", "Object name", "Object type", "Present value", "Status flags", "Event state",
            "Out of service", "Number of states"
    };
    String[] Multi_stateValue_Optional = {
            "Description", "Reliability", "State text", "Priority array", "Relinquish default", "Time delay",
            "Notification class", "Alarm values", "Fault values", "Event enable", "Notify type", "Event time stamps",
            "Event message texts", "Event message texts config", "Event detection enable", "Time delay normal",
            "Event algorithm inhibit", "Event algorithm inhibit ref"
    };
    String[] Multi_stateValue_Proprietary = {
            "Cov period", "Cov min send time", "Acked transitions"
    };
    String[] Program_Mandatory = {
            "Object identifier", "Object name", "Object type", "Program state", "Program change", "Status flags",
            "Out of service"
    };
    String[] Program_Optional = {
            "Description", "Description of halt", "Reason for halt", "Reliability"
    };
    String[] Program_Proprietary = {

    };
    String[] NotificationClass_Mandatory = {
            "Object identifier", "Object name", "Object type", "Notification class", "Priority", "Ack required",
            "Recipient list"
    };
    String[] NotificationClass_Optional = {
            "Description"
    };
    String[] NotificationClass_Proprietary = {

    };
    String[] Schedule_Mandatory = {
            "Object identifier", "Object name", "Object type", "Present value", "Effective period", "Schedule default",
            "List of object property references", "Priority for writing", "Status flags", "Out of service"
    };
    String[] Schedule_Optional = {
            "Description", "Weekly schedule", "Exception schedule", "Reliability"
    };
    String[] Schedule_Proprietary = {
            "Time to next state", "Next state"
    };
    String[] TrendLog_Mandatory = {
            "Object identifier", "Object name", "Object type", "Enable", "Stop when full", "Buffer size", "Log buffer",
            "Record count", "Total record count", "Logging type", "Status flags", "Event state"
    };
    String[] TrendLog_Optional = {
            "Description", "Start time", "Stop time", "Log device object property", "Log interval",
            "Cov resubscription interval", "Client cov increment", "Align intervals", "Interval offset", "Trigger",
            "Reliability", "Notification threshold", "Records since notification", "Last notify record",
            "Notification class", "Event enable", "Acked transitions", "Notify type", "Event time stamps",
            "Event message texts", "Event message texts config", "Event detection enable", "Event algorithm inhibit",
            "Event algorithm inhibit ref"
    };
    String[] TrendLog_Proprietary = {

    };

    private void initializeProfile() {
//        System.out.println("Initializing DistechController...");
        DeviceProperties.put("AnalogInput_Mandatory", AnalogInput_Mandatory);
        DeviceProperties.put("AnalogInput_Optional", AnalogInput_Optional);
        DeviceProperties.put("AnalogInput_Proprietary", AnalogInput_Proprietary);

        DeviceProperties.put("AnalogOutput_Mandatory", AnalogOutput_Mandatory);
        DeviceProperties.put("AnalogOutput_Optional", AnalogOutput_Optional);
        DeviceProperties.put("AnalogOutput_Proprietary", AnalogOutput_Proprietary);

        DeviceProperties.put("AnalogValue_Mandatory", AnalogValue_Mandatory);
        DeviceProperties.put("AnalogValue_Optional", AnalogValue_Optional);
        DeviceProperties.put("AnalogValue_Proprietary", AnalogValue_Proprietary);

        DeviceProperties.put("BinaryInput_Mandatory", BinaryInput_Mandatory);
        DeviceProperties.put("BinaryInput_Optional", BinaryInput_Optional);
        DeviceProperties.put("BinaryInput_Proprietary", BinaryInput_Proprietary);

        DeviceProperties.put("BinaryOutput_Mandatory", BinaryOutput_Mandatory);
        DeviceProperties.put("BinaryOutput_Optional", BinaryOutput_Optional);
        DeviceProperties.put("BinaryOutput_Proprietary", BinaryOutput_Proprietary);

        DeviceProperties.put("BinaryValue_Mandatory", BinaryValue_Mandatory);
        DeviceProperties.put("BinaryValue_Optional", BinaryValue_Optional);
        DeviceProperties.put("BinaryValue_Proprietary", BinaryValue_Proprietary);

        DeviceProperties.put("Calendar_Mandatory", Calendar_Mandatory);
        DeviceProperties.put("Calendar_Optional", Calendar_Optional);
        DeviceProperties.put("Calendar_Proprietary", Calendar_Proprietary);

        DeviceProperties.put("Device_Mandatory", Device_Mandatory);
        DeviceProperties.put("Device_Optional", Device_Optional);
        DeviceProperties.put("Device_Proprietary", Device_Proprietary);

        DeviceProperties.put("EventEnrollment_Mandatory", EventEnrollment_Mandatory);
        DeviceProperties.put("EventEnrollment_Optional", EventEnrollment_Optional);
        DeviceProperties.put("EventEnrollment_Proprietary", EventEnrollment_Proprietary);

        DeviceProperties.put("File_Mandatory", File_Mandatory);
        DeviceProperties.put("File_Optional", File_Optional);
        DeviceProperties.put("File_Proprietary", File_Proprietary);

        DeviceProperties.put("Loop_Mandatory", Loop_Mandatory);
        DeviceProperties.put("Loop_Optional", Loop_Optional);
        DeviceProperties.put("Loop_Proprietary", Loop_Proprietary);

        DeviceProperties.put("Multi_stateInput_Mandatory", Multi_stateInput_Mandatory);
        DeviceProperties.put("Multi_stateInput_Optional", Multi_stateInput_Optional);
        DeviceProperties.put("Multi_stateInput_Proprietary", Multi_stateInput_Proprietary);

        DeviceProperties.put("Multi_stateValue_Mandatory", Multi_stateValue_Mandatory);
        DeviceProperties.put("Multi_stateValue_Optional", Multi_stateValue_Optional);
        DeviceProperties.put("Multi_stateValue_Proprietary", Multi_stateValue_Proprietary);

        DeviceProperties.put("Program_Mandatory", Program_Mandatory);
        DeviceProperties.put("Program_Optional", Program_Optional);
        DeviceProperties.put("Program_Proprietary", Program_Proprietary);

        DeviceProperties.put("NotificationClass_Mandatory", NotificationClass_Mandatory);
        DeviceProperties.put("NotificationClass_Optional", NotificationClass_Optional);
        DeviceProperties.put("NotificationClass_Proprietary", NotificationClass_Proprietary);

        DeviceProperties.put("Schedule_Mandatory", Schedule_Mandatory);
        DeviceProperties.put("Schedule_Optional", Schedule_Optional);
        DeviceProperties.put("Schedule_Proprietary", Schedule_Proprietary);

        DeviceProperties.put("TrendLog_Mandatory", TrendLog_Mandatory);
        DeviceProperties.put("TrendLog_Optional", TrendLog_Optional);
        DeviceProperties.put("TrendLog_Proprietary", TrendLog_Proprietary);

        for (int count = 0; count < ObjectType.length; count++) {
            String objectType = ObjectType[count];

            Map<String, String[]> propertiesTable = new Hashtable<String, String[]>();
            for (int categoryCount = 0; categoryCount < Category.length; categoryCount++) {
                String category = Category[categoryCount];

                for (Entry<String, String[]> deviceProperty : DeviceProperties.entrySet()) {
                    String propertyName = deviceProperty.getKey();
                    if (propertyName.contains(objectType) && propertyName.contains(category)) {
                        String[] propertyValues = deviceProperty.getValue();
                        propertiesTable.put(category, propertyValues);
                    }
                }
            }
            profile.put(objectType, propertiesTable);
        }
    }

    public void printProfile() {
        System.out.println("printing profile");
        for (Entry<String, Map<String, String[]>> p : profile.entrySet()) {
            String key = p.getKey();
            Map<String, String[]> values = p.getValue();
            for (Entry<String, String[]> v : values.entrySet()) {
                String k = v.getKey();
                String[] v1 = v.getValue();

                System.out.println(String.format("%s \n\t %s \n\t\t %s", key, k, Arrays.toString(v1)));
            }
        }
    }

    public void addToProfile(String deviceType, Hashtable<String, Object> DevicePoints, String objectIdentifier) {

        if (Arrays.asList(ObjectType).contains(deviceType)
                    /*&& (deviceType == "AnalogInput" || deviceType == "AnalogOutput")*/) {
            String[] mandatoryPropertiesArrayCopy = DeviceProperties.get(deviceType + "_Mandatory");
            String[] optionalPropertiesArrayCopy = DeviceProperties.get(deviceType + "_Optional");
            String[] proprietaryPropertiesArrayCopy = DeviceProperties.get(deviceType + "_Proprietary");

            Map<String, String[]> copyArrays = new Hashtable<String, String[]>();
            copyArrays.put(deviceType + "_Mandatory_Copy", mandatoryPropertiesArrayCopy);
            copyArrays.put(deviceType + "_Optional_Copy", optionalPropertiesArrayCopy);
            copyArrays.put(deviceType + "_Proprietary_Copy", proprietaryPropertiesArrayCopy);

            for (Entry<String, Object> devicePoints : DevicePoints.entrySet()) {
                for (int count = 0; count < Category.length; count++) {
                    if (Arrays.asList(DeviceProperties.get(deviceType + "_" + Category[count]))
                                .contains(devicePoints.getKey())) {
                        addToMap(deviceType, devicePoints.getKey(), devicePoints.getValue(), objectIdentifier,
                                    Category[count]);
                        int index = Arrays.asList(copyArrays.get(deviceType + "_" + Category[count] + "_Copy"))
                                    .indexOf(devicePoints.getKey());
                        copyArrays.put(deviceType + "_" + Category[count] + "_Copy", ArrayUtils
                                    .remove(copyArrays.get(deviceType + "_" + Category[count] + "_Copy"), index));
                    }
                }
            }

            for (int categoryCount = 0; categoryCount < Category.length; categoryCount++) {
                String[] arrayRef = copyArrays.get(deviceType + "_" + Category[categoryCount] + "_Copy");
                if (arrayRef.length > 0) {
                    for (int i = 0; i < arrayRef.length; i++) {
                        Map<String, String> pics = new Hashtable<String, String>();
                        pics.put(arrayRef[i], Category[categoryCount]);
                        Map<Object, String> picsValue = new Hashtable<Object, String>();
                        if (Category[categoryCount] == Category[0]) {
                            picFailed = true;
                            picsValue.put("", "FAILED");
                        } else {
                            picsValue.put("", "WARNING");
                        }

                        Map<Map<String, String>, Map<Object, String>> obj2 = new Hashtable<Map<String, String>, Map<Object, String>>();
                        obj2.put(pics, picsValue);
                        lines.put(objectIdentifier, obj2);
                    }
                }
            }
        }

    }

    private void addToMap(String deviceType, String key, Object value, String objectIdentifier, String category) {
        Map<String, String> pics = new Hashtable<String, String>();
        pics.put(key, category);

        Map<Object, String> picsValue = new Hashtable<Object, String>();
        if (value.toString().isEmpty()) {
            picsValue.put(value, "PASSED/EMPTY VALUE");
        } else {
            picsValue.put(value, "PASSED");
        }

        Map<Map<String, String>, Map<Object, String>> obj2 = new Hashtable<Map<String, String>, Map<Object, String>>();
        obj2.put(pics, picsValue);
        lines.put(objectIdentifier, obj2);
    }

    public void print() {
        
        System.out.format("\n\n%-30s%-50s%-15s%-35s%-25s\n\n", "Type", "Property Name", "Category", "Value", "Status");
        appendixText += String.format("\n\n%-30s%-50s%-15s%-25s\n\n", "Type", "Property Name", "Category", "Status");
        String prevDeviceType = "";

        // Include full report of the Test in report.txt
        for (Entry<String, Map<Map<String, String>, Map<Object, String>>> line : lines.entries()) {
            String deviceType = line.getKey();
            String propertyName = "";
            String category = "";
            String value = "";
            String picsResult = "";

            if (prevDeviceType == "") {
                prevDeviceType = deviceType;
                System.out.println("\n" + deviceType);
                appendixText += "\n" + deviceType + "\n";
            } else {
                if (deviceType != prevDeviceType) {
                    prevDeviceType = deviceType;
                    System.out.println("\n" + deviceType + "\n");
                    appendixText += "\n" + deviceType + "\n";
                }
            }

            for (Entry<Map<String, String>, Map<Object, String>> map : line.getValue().entrySet()) {
                for (Entry<String, String> keySet : map.getKey().entrySet()) {
                    propertyName = keySet.getKey();
                    category = keySet.getValue();
                }

                for (Entry<Object, String> valueSet : map.getValue().entrySet()) {
                    value = valueSet.getKey().toString();
                    picsResult = valueSet.getValue();
                }
                System.out.format("%-30s%-50s%-15s%-35s%-25s\n", "", propertyName, category, value, picsResult);
                appendixText += String.format("%-30s%-50s%-15s%-25s\n", "", propertyName, category, picsResult);
            }
        }

        if(picFailed) reportText += "RESULT fail " + picTestName + "\n";
        if(!picFailed) reportText += "RESULT pass " + picTestName + "\n";
        report.writeReport(reportText);
        appendices.writeReport(appendixText);
    }
}
