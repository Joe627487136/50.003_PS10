package Week12;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Apply SPMD (Single Program, Multiple Data) design pattern for concurrent programming to parallelize the program which 
 * approximates $\pi$ by integrating the following formula $4/(1+x^2 )$. Hint: In the SPMD design pattern, all threads 
 * run the same program, operating on different data.
 */
public class Exercise1 {
	public static void main(String[] args) throws Exception {
		final int NTHREADS = 5;
        final CountDownLatch latch = new CountDownLatch(NTHREADS-1);
		ExecutorService exec = Executors.newFixedThreadPool(NTHREADS - 1);
		final double [] ab={0,0.25,0.5,0.75,1};
		final double[] workinterval = new double[2];
		final double[] sum = new double[4];
        double sum1=0;
        Runnable task1 = new Runnable() {
                public void run() {
                    try {
                        workinterval[0] = ab[0];
                        workinterval[1] = ab[1];
                        sum[0] = integrateT(workinterval);
                        latch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        Runnable task2 = new Runnable() {
            public void run() {
                try {
                    workinterval[0] = ab[1];
                    workinterval[1] = ab[2];
                    sum[1] = integrateT(workinterval);
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable task3 = new Runnable() {
            public void run() {
                try {
                    workinterval[0] = ab[2];
                    workinterval[1] = ab[3];
                    sum[2] = integrateT(workinterval);
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable task4 = new Runnable() {
            public void run() {
                try {
                    workinterval[0] = ab[3];
                    workinterval[1] = ab[4];
                    sum[3] = integrateT(workinterval);
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        exec.execute(task1);
        exec.execute(task2);
        exec.execute(task3);
        exec.execute(task4);

        try{
            latch.await();
        }catch (InterruptedException E) {
            System.out.println("interrupt");
        }
        for (double i : sum) {
            sum1 += i;
        }
        System.out.println(sum1);
        exec.shutdown();
    }

	public static double f(double x) {
		return 4.0 / (1 + x * x);
	}

	// the following does numerical integration using Trapezoidal rule.
	public static double integrate(double a, double b) {
		int N = 10000; // preciseness parameter
		double h = (b - a) / (N - 1); // step size
		double sum = 1.0 / 2.0 * (f(a) + f(b)); // 1/2 terms

		for (int i = 1; i < N - 1; i++) {
			double x = a + h * i;
			sum += f(x);
		}

		return sum * h;
	}
	public static double integrateT (double[] interval) {
		double integrateanswer;
		integrateanswer = integrate(interval[0],interval[1]);
		System.out.println("Done");
		return integrateanswer;
	}

}
