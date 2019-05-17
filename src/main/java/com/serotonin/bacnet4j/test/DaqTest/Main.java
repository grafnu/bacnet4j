package com.serotonin.bacnet4j.test.DaqTest;

public class Main {

    public static void main(String[] args) {

        if (args.length < 1) {
          throw new IllegalArgumentException(("Usage: bacnetTestId"));
       }

        String bacnetTestId = args[0];
        String broadcastIp = args[1];
        String localIp = args[2];

        switch (bacnetTestId) {

            case "bacnet_VERSION":
                if (args.length != 3) {
                    throw new IllegalArgumentException("Usage: bacnetTestId, broadcastIp, localIp");
                }
                new VersionTest(localIp, broadcastIp);
                break;

            default:
                throw new IllegalArgumentException("Invalid bacnetTestId.");
        }
    }
}
