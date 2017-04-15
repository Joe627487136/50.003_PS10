package Week12;

import java.util.concurrent.atomic.AtomicInteger;

public class NonblockingCounter {
    private AtomicInteger value = new AtomicInteger(); 

    public int getValue() {
        return value.get();
    }

    public int increment() {
        int oldValue; 
        do{
          oldValue = value.get();
        } while (!value.compareAndSet(oldValue, oldValue + 1)); 
        return oldValue + 1;
    }
}
