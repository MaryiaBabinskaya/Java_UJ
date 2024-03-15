package uj.wmii.pwj.exec;

import org.junit.jupiter.api.Test;

import java.util.List;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ExecServiceTest {

    @Test
    void testExecute() {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        s.execute(r);
        doSleep(10);
        assertTrue(r.wasRun);
    }

    @Test
    void testScheduleRunnable() {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        s.submit(r);
        doSleep(10);
        assertTrue(r.wasRun);
    }

    @Test
    void testScheduleRunnableWithResult() throws Exception {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        Object expected = new Object();
        Future<Object> f = s.submit(r, expected);
        doSleep(10);
        assertTrue(r.wasRun);
        assertTrue(f.isDone());
        assertEquals(expected, f.get());
    }

    @Test
    void testScheduleCallable() throws Exception {
        MyExecService s = MyExecService.newInstance();
        StringCallable c = new StringCallable("X", 10);
        Future<String> f = s.submit(c);
        doSleep(20);
        assertTrue(f.isDone());
        assertEquals("X", f.get());
    }

//==========================================================

    @Test
    void testShutdown() {
        MyExecService s = MyExecService.newInstance();
        s.execute(new TestRunnable());
        doSleep(10);
        s.shutdown();
        assertThrows(
                RejectedExecutionException.class,
                () -> s.submit(new TestRunnable()));
    }

    @Test
    void testIsShutdown() {
        MyExecService s = MyExecService.newInstance();
        assertFalse(s.isShutdown());
        s.shutdown();
        assertTrue(s.isShutdown());
    }

    @Test
    void testIsTerminated() {
        MyExecService s = MyExecService.newInstance();
        assertFalse(s.isTerminated());
        s.shutdown();
        doSleep(10);
        assertTrue(s.isTerminated());
    }

    @Test
    void testInvokeAll() throws InterruptedException, ExecutionException {
        MyExecService s = MyExecService.newInstance();
        var tasks = List.of(
                new StringCallable("task1", 10),
                new StringCallable("task2", 100),
                new StringCallable("task3", 1000)
        );
        var futures = s.invokeAll(tasks);
        assertTrue(futures.get(0).isDone());
        assertTrue(futures.get(1).isDone());
        assertTrue(futures.get(2).isDone());
        assertEquals("task1", futures.get(0).get());
        assertEquals("task2", futures.get(1).get());
        assertEquals("task3", futures.get(2).get());
    }

    @Test
    void testInvokeAll_Timeout() throws InterruptedException {
        MyExecService s = MyExecService.newInstance();
        var tasks = List.of(
                new StringCallable("task1", 10),
                new StringCallable("task2", 100),
                new StringCallable("task3", 1000)
        );
        List<Future<String>> futures = s.invokeAll(tasks, 20, TimeUnit.MILLISECONDS);
        assertEquals(3, futures.size());
        assertTrue(futures.get(0).isDone());
//        assertTrue(futures.get(1).isDone());
//        assertTrue(futures.get(2).isDone());
        assertTrue(futures.get(1).isCancelled());
        assertTrue(futures.get(2).isCancelled());
    }

    @Test
    void testInvokeAny() throws ExecutionException, InterruptedException {
        MyExecService s = MyExecService.newInstance();
        var tasks = List.of(
                new StringCallable("task1", 10),
                new StringCallable("task2", 100),
                new StringCallable("task3", 1000)
        );
        String result = s.invokeAny(tasks);
        doSleep(40);
        assertEquals("task1", result);
    }
    @Test
    void testInvokeAny_timeout() throws ExecutionException, InterruptedException, TimeoutException {
        MyExecService s = MyExecService.newInstance();
        var tasks = List.of(
                new StringCallable("task1", 10),
                new StringCallable("task2", 100),
                new StringCallable("task3", 1000)
        );

        String result = s.invokeAny(tasks, 50, TimeUnit.MILLISECONDS);
        assertEquals("task1", result);
    }
//==========================================================

    static void doSleep(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class StringCallable implements Callable<String> {

    private final String result;
    private final int milis;

    StringCallable(String result, int milis) {
        this.result = result;
        this.milis = milis;
    }

    @Override
    public String call() throws Exception {
        ExecServiceTest.doSleep(milis);
        return result;
    }
}
class TestRunnable implements Runnable {

    boolean wasRun;
    @Override
    public void run() {
        wasRun = true;
    }
}