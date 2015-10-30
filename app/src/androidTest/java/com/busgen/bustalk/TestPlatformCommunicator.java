package com.busgen.bustalk;

import android.test.AndroidTestCase;

import com.busgen.bustalk.service.PlatformCommunicator;


/**
 * Created by Alexander Kloutschek on 2015-10-29.
 */
public class TestPlatformCommunicator extends AndroidTestCase{
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

    public void testGetPlatformDataForTestBus(){
        //needs to have an active bus.
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
