import java.util.List;

/**
 * @Author: zr
 * @Description:
 * @Date: 9:17 2020/9/7
 * @Modified by:
 */
public interface ThreadPoolMethod<E extends Runnable> {

    void batchExecute(List<E> eList);

    void execute(E e);

    void shutdown();

    void removeWorker(int num);

    int getJobSize();

    int getWorkersSize();
}
