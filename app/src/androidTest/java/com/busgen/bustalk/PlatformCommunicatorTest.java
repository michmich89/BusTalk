package com.busgen.bustalk;

import android.test.AndroidTestCase;



/**
 * Testclass for PlatformCommunicator. It is important to note that the 2 last tests
 * will only work if the platform is up and running. (Our last test 30/10-15 the platform was down)
 * The last method also requires that particular bus to be up and running.
 */
public class PlatformCommunicatorTest extends AndroidTestCase{
    protected PlatformCommunicator platformCommunicator;

    @Override
    public void setUp(){
        platformCommunicator = new PlatformCommunicator();
    }

    public void testGetPlatformDataForBusStop(){
        String busStopName = "Chalmersplatsen";
        String platformData = platformCommunicator.getNextStopData(busStopName);
        assertTrue(platformData.equals(busStopName));
    }

    public void testGetPlatformDataForNull(){
        String busName = null;
        String platformData = platformCommunicator.getNextStopData(busName);
        assertTrue(platformData.equals("Next busstop could not be found"));
    }

    public void testGetPlatformDataForIllegalString(){
        String busName = "";
        String platformData = platformCommunicator.getNextStopData(busName);
        assertTrue(platformData.equals("Next busstop could not be found"));
    }

    public void testGetPlatformDataForSimulatedBus(){
        String busName = "Test";
        String platformData = platformCommunicator.getNextStopData(busName);
        System.out.println(platformData);
        assertTrue(!platformData.equals("Next busstop could not be found"));
    }

    public void testGetPlatformDataForBus(){
        //needs to have an active bus.
        String busName = "EPO 136";
        String platformData = platformCommunicator.getNextStopData(busName);
        System.out.println(platformData);
        assertTrue(!platformData.equals("Next busstop could not be found"));
    }

}
