package com.serotonin.bacnet4j.test.DaqTest.helper;

import java.util.Map;

import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;

public class Utils {
	
	private static String appendixText = "";
	
	public static String printMap(Map<PropertyIdentifier, Encodable> values) {
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
                   appendixText += key + " : " + value + "\n";
            }
        }
        appendixText += ("\n******************************************************************\n\n");
        
        return appendixText;
    }

}
