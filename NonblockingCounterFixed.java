package Week12;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

public class NonblockingCounterFixed {
    private AtomicStampedReference<AtomicInteger> value = new AtomicStampedReference<AtomicInteger>(new AtomicInteger(), 0); 

    public int getValue() {
        return value.getReference().get();
    }

    public int increment() {
        AtomicInteger oldref;
        int oldstamp;
        AtomicInteger newref;
		do{
        	oldref = value.getReference();
        	oldstamp = value.getStamp();       
        	newref = new AtomicInteger(oldref.get());
        	newref.incrementAndGet();
        } while (!value.compareAndSet(oldref, newref, oldstamp, oldstamp + 1)); 
        return oldref.get() + 1;
    }
}
