import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: zr
 * @Description:
 * @Date: 8:49 2020/9/7
 * @Modified by:
 */
public class ThreadPool<E extends Runnable> implements ThreadPoolMethod<E> {

    private final int initSize;

    private final int maxSize;

    private final double scale;

    private final int maxJobSize;

    private final BlockingQueue<E> jobs;

    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());

    private int workerSize;

    private AtomicLong threadNum = new AtomicLong();

    public ThreadPool(Builder builder) {
        this.initSize = builder.initSize;
        this.maxSize = builder.maxSize;
        this.scale = builder.scale;
        this.maxJobSize = builder.maxJobSize;
        this.jobs = new ArrayBlockingQueue<E>(this.maxJobSize);
        initWorker(this.initSize);
    }

    private void initWorker(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker);
            thread.start();
        }
        this.workerSize += num;
    }

    private void checkWorkerFull() {
        // todo
    }

    @Override
    public void execute(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        synchronized (jobs) {
            try {
                jobs.offer(e, 10, TimeUnit.SECONDS);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            jobs.notify();
        }
        if (isQueueFull()){
            autoAddWorkers();
        }
    }

    private boolean isQueueFull(){
        return (double)(jobs.size() / maxJobSize) > 0.8;
    }


    @Override
    public void batchExecute(List<E> es) {
        if (es == null || es.isEmpty()) {
            throw new NullPointerException();
        }
        synchronized (jobs) {
            jobs.addAll(es);
            jobs.notify();
        }
    }

    @Override
    public void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }

    private void autoAddWorkers() {
        if (this.workerSize * this.scale <= this.maxSize) {
            initWorker((int) (this.workerSize * this.scale));
        } else {
            initWorker(this.maxSize - this.workerSize);
        }
    }

    private void addWorkers(int num) {
        if (this.workerSize + num <= this.maxSize) {
            initWorker(num);
        }
    }

    private void cleanBadThread(){
        // todo
    }

    private void autoRemoveWorker(){
        synchronized (workers){
            removeWorker(workers.size() - initSize);
        }
    }

    @Override
    public void removeWorker(int num) {
        synchronized (jobs) {
            if (num > this.workerSize) {
                throw new IllegalArgumentException("out of worker size");
            }
            for (int i = 0; i < num; i++) {
                Worker worker = workers.get(i);
                worker.shutdown();
                workers.remove(worker);
            }
            this.workerSize -= num;
        }
    }

    private void auto(){
        if (jobs.size() > workerSize && maxSize > workerSize ){

        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }

    @Override
    public int getWorkersSize() {
        return workerSize;
    }

    public class Worker implements Runnable {

        private volatile boolean running = true;

        public ThreadLocal threadLocal = new ThreadLocal();

        @Override
        public void run() {
            while (running) {
                E e = null;
                auto();
                synchronized (jobs) {
                    if (jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    try {
                        e = jobs.take();
                        threadLocal.set(jobs.size());
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    if (e != null) {
                        e.run();
                    }
                }
            }
        }

        public void shutdown() {
            running = false;
        }
    }

    public static class Builder {

        private int initSize = 2;
        private int maxSize = 10;
        private double scale = 1.5;
        private int maxJobSize = 200;

        public Builder() {
        }

        public Builder setInitSize(int initSize) {
            this.initSize = initSize;
            return this;
        }

        public Builder setMaxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder setScale(double scale) {
            this.scale = scale;
            return this;
        }

        public Builder setMaxJobSize(int maxJobSize) {
            this.maxJobSize = maxJobSize;
            return this;
        }

        public ThreadPool build() {
            return new ThreadPool(this);
        }
    }


}
