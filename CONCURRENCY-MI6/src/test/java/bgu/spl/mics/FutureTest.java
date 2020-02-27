package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {

    Future<Object> future;

    @BeforeEach
    public void setUp() {
        future = new Future<>();
    }

    @Test
    public void get(){
        Object i = new Object();
        future.resolve(i);
        assertEquals(future.get(),i,"get() method doesn't work as expected or the object is not resolved properly");
    }

    @Test
    public void resolved(){
        Object i = new Object();
        future.resolve(i);
        assertEquals(future.get(),i,"get() method doesn't work as expected or the object is not resolved properly");
    }
    @Test
    public void isDone(){
        Object i = new Object();
        future.resolve(i);
        assertTrue(future.isDone(),"After resolving the object, it is steel set to false (suppose to be true)");
    }

    @Test
    public void get2() {
        Object i = future.get(3,TimeUnit.SECONDS);
        assertNull(i, "get2() method doesn't return null even if there is no result");
    }
}