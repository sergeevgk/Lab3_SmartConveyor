package conveyor;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface Executor {

    /**
     * Types that provider can transmit
     */
    enum APPROPRIATE_TYPES {  CHAR, BYTE, DOUBLE }

    /**
     * The function for reading the configuration file of the executor.
     * @param config - name of configuration file
     * @return error code
     */
    int setConfig(String config);

    /**
     * The function for saving input stream
     * @param input - opened input stream
     */
    void setInput(DataInputStream input);

    /**
     * The function for saving output stream
     * @param output - opened output stream
     */
    void setOutput(DataOutputStream output);

    /**
     * Consumer's method which is called to give provider information about consumer's data borders
     * @param startPos
     * @param length
     */
    void subscribe(Executor consumer ,int startPos, int length);

    /**
     * The provider calls the consumer's methods 'getConsumedTypes', 'setAdapter' and retains the reference to the consumer.
     * @param consumer - reference to the consumer
     */
    int setConsumer(Executor consumer);

    /**
     * Consumer returns types that he can get
     * @return array of these types
     */
    APPROPRIATE_TYPES[] getConsumedTypes();

    /**
     * The provider calls the consumer's method to set inner class
     * @param provider - reference to the provider
     * @param adapter - reference to the inner class of the provider
     * @param type - appropriate type of connection between provider and consumer
     */
    void setAdapter(Executor provider, Object adapter, APPROPRIATE_TYPES type);

    /**
     * The function for running the first executor
     * @return error code
     */
    int run();

    /**
     * Provider calls this method of consumer when ends processing his data
     * @param provider - reference to the provider
     * @return error code
     */
    int put(Executor provider);
}

// error code: 0 - success, another - error
