package com.serotonin.bacnet4j.test.DaqTest;

public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
          throw new IllegalArgumentException(("Usage: bacnetTestId"));
       }

        String bacnetTestId = args[0];

        switch (bacnetTestId) {
            case "bacnet_PICS":
                if (args.length != 4) {
                    throw new IllegalArgumentException("Usage: bacnetTestId, broadcastIp, localIp, loopDiscover");
                }
                String broadcastIp = args[1];
                String localIp = args[2];
                boolean loopDiscover = "loop".equals(args[3]);
                new PicTest(localIp, broadcastIp, loopDiscover, true);
                break;

            case "bacnet_ADDR_UNIQUE":
                if (args.length != 4) {
                    throw new IllegalArgumentException("Usage: bacnetTestId, broadcastIp, localIp, loopDiscover");
                }
                broadcastIp = args[1];
                localIp = args[2];
                loopDiscover = "loop".equals(args[3]);
                new AddrUniqueTest(localIp, broadcastIp, loopDiscover);
                break;

            case "bacnet_VERSION":
                if (args.length != 3) {
                    throw new IllegalArgumentException("Usage: bacnetTestId, broadcastIp, localIp");
                }
                broadcastIp = args[1];
                localIp = args[2];
                new VersionTest(localIp, broadcastIp);
                break;

            default:
                throw new IllegalArgumentException("Invalid bacnetTestId.");
        }
    }
}
