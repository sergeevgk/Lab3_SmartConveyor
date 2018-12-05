import conveyor.Manager;
import log.Log;

//TODO Adapters, Subscription;
//TODO ... Executors with different data types; different data 'blocks' (start, size, type);
//TODO ... Read block -> throw tokens to subsribers, block data, run subscribers.
//TODO ... 3 Lab 1 - 3 - 1 is ok; last gain from all via subscription.

public class Main {
    public static void main(String[] args){
        if (args[0] != null) {
            if (Log.init() != 0){
                return;
            }
            Log.logReport("Program started.");
            String fileName = args[0];
            Manager manager = new Manager(fileName);
            if (manager.openStreams() == 0) {
                if (manager.createWorkers() == 0) {
                    if (manager.introduceWorkers() == 0) {
                        manager.StartConveyor();
                    }
                }
                manager.closeStreams();
            }
            //log before exit to report about program'setConsumer work
            Log.logReport("Program finished.");
            Log.close();
        } else {
            Log.logReport("Missing command arguments.");
            Log.close();
            return;
        }
        return;
    }
}
