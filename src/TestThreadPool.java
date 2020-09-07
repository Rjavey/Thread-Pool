import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: zr
 * @Description:
 * @Date: 10:09 2020/9/7
 * @Modified by:
 */
public class TestThreadPool {

    public static class Job implements Runnable{
        ThreadLocal threadLocal = new ThreadLocal();
        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("当前线程名称:"+Thread.currentThread().getName()+";"+"job被指执行了" + System.currentTimeMillis());
        }
    }

//    public static void main(String[] args) {
//        ThreadPool threadPool = new ThreadPool.Builder().setInitSize(2).setMaxSize(5).setMaxJobSize(100).build();
//        for (int i=0;i<10000;i++){
//            Job job = new TestThreadPool.Job();
//            threadPool.execute(job);
//        }
//
//    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for(int i = 0; i < 10000; i++) {
            Runnable synRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName());
                }
            };
            executorService.execute(synRunnable);
        }
    }
}
