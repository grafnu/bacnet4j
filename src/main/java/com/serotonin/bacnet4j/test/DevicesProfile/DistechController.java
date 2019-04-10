package com.serotonin.bacnet4j.test.DevicesProfile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class DistechController {
    
//    Map<Map<String, String>, String> lines = new HashMap<Map<String, String>, String>();
    
    Multimap<String, Map<Map<String, String>, Map<Object, String>>> lines = ArrayListMultimap.create();

    String[] ObjectType = {
            "AnalogInput", "AnalogOutput", "AnalogValue", "BinaryInput", "BinaryOutput", "BinaryValue", "MultistateInput",
            "MultistateValue" };

    String[] AnalogInput_Manadatory = {
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

    public void compare(String deviceType, Hashtable<String, Object> devicePoints, String objectIdentifier) {
//        if (Arrays.asList(ObjectType).contains(deviceType)) {
        if (deviceType == "AnalogInput") {
//            System.out.println("\n\n" + deviceType.toUpperCase() + "\n");
            String[] AnalogInput_Mandatory_Copy = AnalogInput_Manadatory;
            String[] AnalogInput_Optional_Copy = AnalogInput_Optional;
            String[] AnalogInput_Proprietary_Copy = AnalogInput_Proprietary;
                        
            for (Entry<String, Object> v : devicePoints.entrySet()) {

                if (Arrays.asList(AnalogInput_Manadatory).contains(v.getKey())) {
                    AnalogInputMandatoryAnalysis(deviceType, v.getKey(), v.getValue(), objectIdentifier);
                    int index = Arrays.asList(AnalogInput_Mandatory_Copy).indexOf(v.getKey());
                    AnalogInput_Mandatory_Copy = ArrayUtils.remove(AnalogInput_Mandatory_Copy, index);
                }

                if (Arrays.asList(AnalogInput_Optional).contains(v.getKey())) {
                    AnalogInputOptionalAnalysis(deviceType, v.getKey(), v.getValue(), objectIdentifier);
                    int index = Arrays.asList(AnalogInput_Optional_Copy).indexOf(v.getKey());
                    AnalogInput_Optional_Copy = ArrayUtils.remove(AnalogInput_Optional_Copy, index);
                }

                if (Arrays.asList(AnalogInput_Proprietary).contains(v.getKey())) {
                    AnalogInputProprietaryAnalysis(deviceType, v.getKey(), v.getValue(), objectIdentifier);
                    int index = Arrays.asList(AnalogInput_Proprietary_Copy).indexOf(v.getKey());
                    AnalogInput_Proprietary_Copy = ArrayUtils.remove(AnalogInput_Proprietary_Copy, index);
                }
            }
            
            if(AnalogInput_Mandatory_Copy.length > 0) {
                for(int i = 0; i < AnalogInput_Mandatory_Copy.length; i++) {
                    Map<String, String> pics = new Hashtable<String, String>();
                    pics.put(AnalogInput_Mandatory_Copy[i], "Mandatory");
                    Map<Object, String> picsValue = new Hashtable<Object, String>();
                    picsValue.put("", "FAILED");
                    
                    Map<Map<String, String>, Map<Object,String>> obj2 = new Hashtable<Map<String, String>, Map<Object,String>>();
                    obj2.put(pics, picsValue);
                    lines.put(objectIdentifier, obj2);
                }
            }
            
            if(AnalogInput_Optional_Copy.length > 0) {
                for(int i = 0; i < AnalogInput_Optional_Copy.length; i++) {
                    Map<String, String> pics = new Hashtable<String, String>();
                    pics.put(AnalogInput_Optional_Copy[i], "Optional");
                    Map<Object, String> picsValue = new Hashtable<Object, String>();
                    picsValue.put("", "WARNING");
                    
                    Map<Map<String, String>, Map<Object,String>> obj2 = new Hashtable<Map<String, String>, Map<Object,String>>();
                    obj2.put(pics, picsValue);
                    lines.put(objectIdentifier, obj2);
                }
            }
            
            if(AnalogInput_Proprietary_Copy.length > 0) {
                for(int i = 0; i < AnalogInput_Proprietary_Copy.length; i++) {
                    Map<String, String> pics = new Hashtable<String, String>();
                    pics.put(AnalogInput_Proprietary_Copy[i], "Proprietary");
                    Map<Object, String> picsValue = new Hashtable<Object, String>();
                    picsValue.put("", "WARNING");
                    
                    Map<Map<String, String>, Map<Object,String>> obj2 = new Hashtable<Map<String, String>, Map<Object,String>>();
                    obj2.put(pics, picsValue);
                    lines.put(objectIdentifier, obj2);
                }
            }
            System.out.println("\n");
        }
    }

    private void AnalogInputMandatoryAnalysis(String deviceType, String key, Object value, String objectIdentifier) {
//        System.out.println(key.toUpperCase() + " is contained in..." + deviceType);
        Map<String, String> pics = new Hashtable<String, String>();
        pics.put(key, "Mandatory");
        
        Map<Object, String> picsValue = new Hashtable<Object, String>();
        picsValue.put(value, "PASSED");
        
        Map<Map<String, String>, Map<Object,String>> obj2 = new Hashtable<Map<String, String>, Map<Object,String>>();
        obj2.put(pics, picsValue);
        lines.put(objectIdentifier, obj2);
    }
    
    private void AnalogInputOptionalAnalysis(String deviceType, String key, Object value, String objectIdentifier) {
//        System.out.println(key.toUpperCase() + " is contained in..." + deviceType);
        Map<String, String> pics = new Hashtable<String, String>();
        pics.put(key, "Optional");
        
        Map<Object, String> picsValue = new Hashtable<Object, String>();
        picsValue.put(value, "PASSED");
        
        Map<Map<String, String>, Map<Object,String>> obj2 = new Hashtable<Map<String, String>, Map<Object,String>>();
        obj2.put(pics, picsValue);
        lines.put(objectIdentifier, obj2);
    }
    
    private void AnalogInputProprietaryAnalysis(String deviceType, String key, Object value, String objectIdentifier) {
//        System.out.println(key.toUpperCase() + " is contained in..." + deviceType);
        Map<String, String> pics = new Hashtable<String, String>();
        pics.put(key, "Proprietary");
        
        Map<Object, String> picsValue = new Hashtable<Object, String>();
        picsValue.put(value, "PASSED");
        
        Map<Map<String, String>, Map<Object,String>> obj2 = new Hashtable<Map<String, String>, Map<Object,String>>();
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
            
            if(prevDeviceType == "") {
                prevDeviceType = deviceType;
                System.out.println("\n"+deviceType+"\n");
            } else {
                if (deviceType != prevDeviceType) {
                    prevDeviceType = deviceType;
                    System.out.println("\n"+deviceType+"\n");
                }
            }
            
            for(Entry<Map<String, String>, Map<Object, String>> map : line.getValue().entrySet()) {
                for (Entry<String, String> keySet : map.getKey().entrySet()) {
                  propertyName = keySet.getKey();
                  category = keySet.getValue();
                }
                
                for(Entry<Object, String> valueSet : map.getValue().entrySet()) {
                    value = valueSet.getKey().toString();
                    picsResult = valueSet.getValue();
                }
                System.out.format("%-30s%-15s%-35s%-25s\n", propertyName, category, value, picsResult);
            }
//            System.out.println("\n");
        }
    }
}
