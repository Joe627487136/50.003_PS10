package Week12;
class MultiThreadSearch {
	public static Boolean found = false;
	
	public static void main(String args[]){    
		int[] array = {1,2,3,4,5,6,7,8,9,10};
		int target = 6;
		
		Searcher searcher1 = new Searcher(array, 0, array.length/2, target);
		Searcher searcher2 = new Searcher(array, array.length/2, array.length, target);
		searcher1.start();
		searcher2.start();

		while (true) {		
			if (!MultiThreadSearch.found && !searcher1.isAlive() && !searcher2.isAlive()) {
				System.out.println("The element is not in the list.");
				break;
			}
			
			if (MultiThreadSearch.found) {
				if (searcher1.isAlive()) {
					searcher1.interrupt();
				}

				if (searcher2.isAlive()) {
					searcher2.interrupt();
				}

				System.out.println("The element is in the list.");
				break;
			}
		}
	}  
}  

class Searcher extends Thread {
	private int[] array;
	private int lower, upper, target;
	
	public Searcher (int[] array, int lower, int upper, int target) {
		this.array = array;
		this.lower = lower;
		this.upper = upper;
		this.target = target;
	}
	
	public void run(){  
		for (int i = lower; i < upper; i++) {
			
			if (isInterrupted()) {
				break;
			}
			
			if (array[i] == target) {
				MultiThreadSearch.found = true;
				break;
			}
		}  
		
		System.out.println("Done");
	}
}