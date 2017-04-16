package Week12;

//import Week12.StripedMapSolution.Node;

public class StripedMapSolution {
	//synchronization policy: buckets[n] guarded by locks[n%N_LOCKS]
	private static final int N_LOCKS = 16;
	private Node[] buckets;
	private final Object[] locks;

	public StripedMapSolution (int numBuckets) {
		buckets = new Node[numBuckets];
		locks = new Object[N_LOCKS];

		for (int i = 0; i < N_LOCKS; i++) {
			locks[i] = new Object();
		}
	}

    public Object put(Object key, Object value) {
        int hash = hash(key);
        synchronized (locks[hash % N_LOCKS]) {
            for (Node m = buckets[hash]; m != null; m = m.next) {
                if (m.key.equals(key)) {
                    Object oldValue = m.value;
                    m.value = value;
                    return oldValue;
                }
            }
            buckets[hash] = new Node(key,value,buckets[hash]);
        }
        return null;
    }

	public Object get (Object key) {
		//todo: get the item with the given key in the map
        int hash = hash(key);
        synchronized (locks[hash % N_LOCKS]) {
            for (Node m = buckets[hash]; m != null; m = m.next) {
                if (m.key.equals(key)) {
                    return m.value;
                }
            }
        }
		return null;
	}

	private final int hash (Object key) {
		return Math.abs(key.hashCode() % buckets.length);
	}

	public void clear () {
		//todo: remove all objects in the map
        buckets = new Node[buckets.length];
	}

	public int size () {
		//todo: count the number of elements in the map
		int length = 0;
        for (int i = 0; i < N_LOCKS; i++) {
            synchronized (locks[i]) {
                for (int j = i; j < buckets.length; j += N_LOCKS) {
                    if (j >= buckets.length) {
                        break;
                    }
                    for (Node m = buckets[j]; m != null; m = m.next) {
                        length++;
                    }
                }
            }
        }
        return length;
	}

    class Node {
        Node next;
        Object key;
        Object value;
        Node(Object key, Object value, Node next) {
            this.next = next;
            this.key = key;
            this.value = value;
        }
    }
}

