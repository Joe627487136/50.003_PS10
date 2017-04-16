package Week12;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

class ValueLatch <T> {
    private T value = null;
    private final CountDownLatch done = new CountDownLatch(1);

    public boolean isSet() {
        return (done.getCount() == 0);
    }

    public synchronized void setValue(T newValue) {
        if (!isSet()) {
            value = newValue;
            done.countDown();
        }
    }

    public T getValue() throws InterruptedException {
        done.await();
        synchronized (this) {
            return value;
        }
    }
}

public class ConcurrentSlidingGame {	
	public static void main (String[] args) throws Exception { 
    	int[] initialBoardConfig = new int[] {2,1,5,3,6,0,7,8,4}; 
		ExecutorService exec = new ScheduledThreadPoolExecutor (100);
		ValueLatch<PuzzleNode> solution = new ValueLatch<PuzzleNode>();
		AtomicInteger taskCount = new AtomicInteger(0);
    	ConcurrentMap<String, Boolean> seen = new ConcurrentHashMap<String, Boolean>();

    	Search(exec, solution, taskCount, seen, new PuzzleNode(initialBoardConfig, null));
		
    	if (solution.getValue() == null) {
    		System.out.println ("No solution");
    	}
    	else {
    		System.out.println ("Solution Found");
    		for (int[] i: solution.getValue().getTrace()) {
        		System.out.println (toString(i));    			
    		}
    	}
    }
	
	public static void Search(final ExecutorService exec, final ValueLatch<PuzzleNode> solution, final AtomicInteger taskCount, final ConcurrentMap<String, Boolean> seen, final PuzzleNode node) {
    	taskCount.incrementAndGet();
    	exec.execute(new Runnable () {
			public void run() {
				if (solution.isSet() || seen.putIfAbsent(ConcurrentSlidingGame.toString(node.config), true) != null) {
					if (taskCount.decrementAndGet() == 0) {
						solution.setValue(null);
					}
					return;
				}
				
				if (isGoal(node.config)) {
					taskCount.decrementAndGet();
					solution.setValue(node);
					exec.shutdown();
				}
				else {
					for (int[] next: nextPositions(node.config)) {
						Search(exec, solution, taskCount, seen, new PuzzleNode(next, node));
					}
				}
			}    		
    	});
    }
	
    private static boolean isGoal (int[] boardConfig) {
    	return boardConfig[0] == 1 && boardConfig[1] == 2 && boardConfig[2] == 3 && boardConfig[3] == 4 &&
    			boardConfig[4] == 5 && boardConfig[5] == 6 && boardConfig[6] == 7 && boardConfig[7] == 8 && boardConfig[8] == 0;
    }

    private static List<int[]> nextPositions (int[] boardConfig) {
    	int emptySlot = -1;
    	
    	for (int i = 0; i < boardConfig.length; i++) {
    		if (boardConfig[i] == 0) {
    			emptySlot = i;
    			break;
    		}
    	}
    	
    	List<int[]> toReturn = new ArrayList<int[]>();
    	if (emptySlot != 2 && emptySlot != 5 && emptySlot != 8) {
    		int[] newConfig = boardConfig.clone(); 
    		newConfig[emptySlot]= newConfig[emptySlot+1];
    		newConfig[emptySlot+1]=0;
    		toReturn.add(newConfig);
    	}
    	if (emptySlot != 0 && emptySlot !=3 && emptySlot != 6) {
    		int[] newConfig = boardConfig.clone();     		
    		newConfig[emptySlot]=newConfig[emptySlot-1];
    		newConfig[emptySlot-1]=0;
    		toReturn.add(newConfig);
    	}
    	if (emptySlot != 6 && emptySlot != 7 && emptySlot != 8) {
    		int[] newConfig = boardConfig.clone();     		    		
    		newConfig[emptySlot]=newConfig[emptySlot+3];
    		newConfig[emptySlot+3]=0;
    		toReturn.add(newConfig);
    	}
    	if (emptySlot != 0 && emptySlot != 1 && emptySlot != 2) {
    		int[] newConfig = boardConfig.clone();     		    		
    		newConfig[emptySlot] = newConfig[emptySlot-3];
    		newConfig[emptySlot-3] = 0;
    		toReturn.add(newConfig);
    	}
    	
    	return toReturn;
    }
    
    private static String toString(int[] config) {
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < config.length; i++) {
    		sb.append(config[i]);
    	}
    	
    	return sb.toString();
    }
}