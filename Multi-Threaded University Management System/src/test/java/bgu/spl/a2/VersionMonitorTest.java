package bgu.spl.a2;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sagivnet on 12/5/2017.
 */
public class VersionMonitorTest {
    VersionMonitor monitor ;
    int ver;
    boolean isWorking;


    @Before
    public void setUp() throws Exception
    {
        monitor = new VersionMonitor();
        ver = 0;
        isWorking = false;
    }


    @Test
    public void getVersion() throws Exception
    {
        try
        {
            int res =  monitor.getVersion();
            assertEquals("Vesrsion is incorrect!", res, ver);
        }
        catch (Exception e)
        {
            Assert.fail();
        }


    }

    @Test
    public void inc() throws Exception
    {

        try
        {
            monitor.inc();
            ver++;
            assertEquals("Inc didn't increase version", ver, monitor.getVersion());
        }
        catch (Exception e)
        {
            Assert.fail();
        }

    }

    @Test
    public void await() throws Exception
    {


        final Thread thread = new Thread(new Runnable() {
            public void run() {
                try
                {
                     monitor.await(monitor.getVersion());
                     isWorking = true;

                }

                catch (Exception e)
                {
                    Assert.fail();
                }
            }
        });



        try
        {
            thread.start();
            Thread.sleep(1000);
            assertEquals("await Failed!", false, isWorking);

            monitor.inc();
            Thread.sleep(1000);
            assertEquals("await Failed!", true, isWorking);
        }
        catch (Exception e)
        {
            Assert.fail();
        }

    }

}






























