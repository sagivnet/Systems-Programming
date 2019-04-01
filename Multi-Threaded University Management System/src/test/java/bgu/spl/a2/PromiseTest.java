package bgu.spl.a2;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sagivnet on 12/5/2017.
 */
public class PromiseTest {
    Promise<Integer> prom;

    @Before
    public void setUp() throws Exception {
        try {
            prom = new Promise<Integer>();
        } catch (Exception e) {
            Assert.fail("Constructor has failed");
        }

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void get() throws Exception {
        try {
            prom.get();
            Assert.fail();
        } catch (IllegalStateException e) {
            prom.resolve(13);
            int res = prom.get();
            assertEquals("get() returns wrong value", res, 13);
        } catch (Exception e) {
            Assert.fail();
        }

    }

    @Test
    public void isResolved() throws Exception {
        boolean isRes;
        // check if returns false before resolving
        try {
            isRes = prom.isResolved();
            assertEquals("Wrong isResolved output", isRes, false);
        } catch (Exception e) {
            Assert.fail();
        }

        // check if returns true after resolving
        try {
            prom.resolve(10);
            isRes = prom.isResolved();
            assertEquals("Wrong isResolved output", isRes, true);
        } catch (Exception e) {
            Assert.fail();
        }



    }

    @Test
    public void resolve() throws Exception {


        int[] beforeResolve = {0, 0, 0};
        int[] shouldBe = {1, 1, 1};
        try {
            callback incFirst = () -> { beforeResolve[0]++; };
            callback incSecond = () -> { beforeResolve[1]++; };
            callback incThird = () -> { beforeResolve[2]++; };

            prom.subscribe(incFirst);
            prom.subscribe(incSecond);
            prom.subscribe(incThird);

            prom.resolve(10);
        } catch (Exception e) {
            Assert.fail();
        }
        assertArrayEquals("Subscribed callbacks didn't work", beforeResolve, shouldBe);

        try { prom.resolve(10); } catch (IllegalStateException e) {
            Assert.fail("this object is already resolved");
        }


    }

    @Test
    public void subscribe() throws Exception {


        int[] beforeResolve = {0, 0, 0};
        int[] shouldBeFirstStage = {1, 1, 1};
        int[] shouldBeSecondStage = {2,2,2};

        callback incFirst = () -> {
            beforeResolve[0]++;
        };
        callback incSecond = () -> {
            beforeResolve[1]++;
        };
        callback incThird = () -> {
            beforeResolve[2]++;
        };

        try {


            prom.subscribe(incFirst);
            prom.subscribe(incSecond);
            prom.subscribe(incThird);
        }
        catch (Exception e)
        {
            Assert.fail("Subscribe fail");
        }

        try
        {
            prom.resolve(12);

            assertArrayEquals("Subscribed callbacks didn't work", beforeResolve, shouldBeFirstStage);

            prom.subscribe(incFirst);
            prom.subscribe(incSecond);
            prom.subscribe(incThird);

            assertArrayEquals("Callbacks didn't work when Promise was already resolved", beforeResolve, shouldBeSecondStage);

        }
        catch (Exception e)
        {
            Assert.fail("Subscribe fail");
        }

    }
}


