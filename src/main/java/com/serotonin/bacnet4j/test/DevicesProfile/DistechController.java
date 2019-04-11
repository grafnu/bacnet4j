package com.serotonin.bacnet4j.test.DevicesProfile;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class DistechController {

    public DistechController() {
        initializeProfile();
    }

    Multimap<String, Map<Map<String, String>, Map<Object, String>>> lines = ArrayListMultimap.create();

    Map<String, String[]> DeviceProperties = new Hashtable<String, String[]>();

    String[] Category = {
            "Mandatory", "Optional", "Proprietary" };

    // Object Type -> category -> fields
    Map<String, Map<String, String[]>> profile = new Hashtable<String, Map<String, String[]>>();

    String[] ObjectType = {
            "AnalogInput", "AnalogOutput", "AnalogValue", "BinaryInput", "BinaryOutput", "BinaryValue", "MultistateInput",
            "MultistateValue" };

    String[] AnalogInput_Mandatory = {
            "Object identifier", "Object name", "Object type", "Present value", "Status flags", "Event state",
            "Out of service", "Units", "Property list" };

    String[] AnalogInput_Optional = {
            "Description", "Device type", "Reliability", "Update interval",
            "Min pres value", "Max pres value", "Resolution", "COV increment", "Time delay",
            "Notification class", "High limit", "Low limit", "Deadband", "Limit enable",
            "Event enable", "Acked transitions", "Notify type", "Event time stamps",
            "Event message texts", "Event message texts config", "Event detection enable",
            "Time delay normal", "Event algorithm inhibit", "Event algorithm inhibit ref" };

    String[] AnalogInput_Proprietary = {
            "COV period", "Cov min send time" };

    private void initializeProfile() {
        System.out.println("Initializing DistechController...");
        DeviceProperties.put("AnalogInput_Mandatory", AnalogInput_Mandatory);
        DeviceProperties.put("AnalogInput_Optional", AnalogInput_Optional);
        DeviceProperties.put("AnalogInput_Proprietary", AnalogInput_Proprietary);

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

        if (Arrays.asList(ObjectType).contains(deviceType) && deviceType == "AnalogInput") {
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
                        if(Category[categoryCount] == Category[0]) {
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
        picsValue.put(value, "PASSED");

        Map<Map<String, String>, Map<Object, String>> obj2 = new Hashtable<Map<String, String>, Map<Object, String>>();
        obj2.put(pics, picsValue);
        lines.put(objectIdentifier, obj2);
    }

    public void print() {
        String prevDeviceType = "";
        for (Entry<String, Map<Map<String, String>, Map<Object, String>>> line : lines.entries()) {
            String deviceType = line.getKey();
            String propertyName = "";
            String category = "";
            String value = "";
            String picsResult = "";

            if (prevDeviceType == "") {
                prevDeviceType = deviceType;
                System.out.println("\n" + deviceType + "\n");
            } else {
                if (deviceType != prevDeviceType) {
                    prevDeviceType = deviceType;
                    System.out.println("\n" + deviceType + "\n");
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
                System.out.format("%-30s%-15s%-35s%-25s\n", propertyName, category, value, picsResult);
            }
        }
    }
}
