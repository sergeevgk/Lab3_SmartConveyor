package conveyor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public interface Executor {
    /**
     * @param inputStream input buffered stream
     * @return 0 if stream successfully set, -1 otherwise
     */
    int SetInput(BufferedInputStream inputStream);

    /**
     * @param outputStream output buffered stream
     * @return 0 if stream successfully set, -1 otherwise
     */
    int SetOutput(BufferedOutputStream outputStream);

    /**
     * @param ex conveyor.Executor instance, which consumes data provided by this conveyor.Executor
     */
    void SetConsumer(Executor ex);

    /**
     * @return 0 if algorithm completed successfully, -1 otherwise
     * <p>
     * runs conveyor.Executor work algorithm
     */
    int run();

    /**
     * @param input input data for consumer (provider doesn't use this method)
     *              <p>
     *              puts data from provider to its consumer
     */
    void put(Object input);

    void notifySubscribers();
    //String getCodeMode();
}