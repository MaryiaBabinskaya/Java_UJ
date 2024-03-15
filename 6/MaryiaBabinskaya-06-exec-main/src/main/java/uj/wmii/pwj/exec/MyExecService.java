package uj.wmii.pwj.exec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class MyExecService implements ExecutorService {

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final List<Thread> workerThreads = new ArrayList<>();
    private boolean isShutdown = false;

    static MyExecService newInstance() {
        return new MyExecService();
    }

    @Override
    public void shutdown() {
        isShutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        isShutdown = true;
        for (Thread workerThread : workerThreads) {
            workerThread.interrupt();
        }
        taskQueue.clear();
        return new ArrayList<>();
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public boolean isTerminated() {
        return isShutdown && workerThreads.isEmpty();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        boolean terminated = false;
        final long deadline = System.nanoTime() + unit.toNanos(timeout);
        long SLEEP_TIME = 100;
        while (!(terminated = isTerminated()) && System.nanoTime() < deadline)
            Thread.sleep(SLEEP_TIME);
        return terminated;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if(isShutdown) {
            throw new RejectedExecutionException();
        }
        else {
            FutureTask<T> futureTask = new FutureTask<>(task);
            execute(futureTask);
            return futureTask;
        }
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if(isShutdown){
            throw new RejectedExecutionException();
        }
        else{
            FutureTask<T> futureTask = new FutureTask<T>(task, result);
            execute(futureTask);
            return futureTask;
        }
    }

    @Override
    public Future<?> submit(Runnable task) {
        return submit(task, null);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        if (isShutdown) throw new RejectedExecutionException();
        if(tasks == null) throw new NullPointerException();
        List<Future<T>> listFuture = new ArrayList<>();
        for (Callable<T> task : tasks) {
            listFuture.add(submit(task));
        }
        for(Future<T> futures : listFuture) {
            try {
                futures.get();
            }
            catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return listFuture;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        if (isShutdown) throw new RejectedExecutionException();
        if(tasks == null || unit == null) throw new NullPointerException();
        List<Future<T>> listFuture = new ArrayList<>();
        for (Callable<T> task : tasks) {
            listFuture.add(submit(task));
        }
        for(var future : listFuture) {
            try {
                future.get(timeout, unit);
            }
            catch(ExecutionException e) {
                throw new RuntimeException(e);
            }
            catch(TimeoutException ignored) {
                future.cancel(true);
            }
        }
        return listFuture;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        List<Future<T>> listFuture = new ArrayList<>();
        for (Callable<T> task : tasks) {
            listFuture.add(submit(task));
        }
        for (Future<T> future : listFuture) {
            try {
                return future.get();
            } catch (Exception e){
                future.cancel(true);
            }
        }
        throw new ExecutionException(null);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        List<Future<T>> listFuture = new ArrayList<>();
        for (Callable<T> task : tasks) {
            listFuture.add(submit(task));
        }
        for (Future<T> future : listFuture) {
            try {
                return future.get(timeout, unit);
            }
            catch(TimeoutException e) {
                future.cancel(true);
            }
        }
        throw new ExecutionException("", new Exception());
    }

    @Override
    public void execute(Runnable command) {
        if(command == null) throw new NullPointerException();
        if(isShutdown) {
            throw new RejectedExecutionException();
        }
        else {
            var thread = new Thread(() -> {
                command.run();
                workerThreads.remove(Thread.currentThread());
            });
            workerThreads.add(thread);
            thread.start();
        }
    }
}