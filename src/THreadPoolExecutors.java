import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.*;

/**
 * @Author: zr
 * @Description:
 * @Date: 8:42 2020/9/8
 * @Modified by:
 */
public class THreadPoolExecutors {

    public static void main(String[] args) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-pool-%d").build();
        //Common Thread Pool
        ExecutorService pool = new ThreadPoolExecutor(5, 200,
             0L, TimeUnit.MILLISECONDS,
             new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 100; i++) {
            pool.execute(()-> System.out.println(Thread.currentThread().getName()));
        }

        pool.shutdown();//gracefully shutdown
    }

}
