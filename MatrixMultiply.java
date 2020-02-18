import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;

@SuppressWarnings("unchecked")
class MatrixMultiply {
    
    public static float[][] inputMatrix (int row, int column) {
        
        float input[][] = new float [row][column];
        Random random = new Random();
        
        for (int i=0; i<row;i++) {
            for( int j=0; j<column;j++) {
                input[i][j] = random.nextFloat();
            }
        }
        return input;
    }
    
    
    public static void main(String[] args) {

        int row = 2000;
        int column = 2000;
        int thread = 10;
        
        float a[][] = inputMatrix(row,column);
        float b[][] = inputMatrix(row,column);
        float c[][] = new float[row][column];
        
        long start = System.currentTimeMillis();
        float result[][] = matMult(a,b,c,row,column,row,thread);
        long end = System.currentTimeMillis();
        
        //System.out.println(Arrays.deepToString(result));
        System.out.println("\nNumber of row: " + row);
        System.out.println("Number of column: " + column);
        System.out.println("Thread NUmber: " + thread);
        System.out.println("Runtime: " + (end-start) + " ms");
    }
    public static float[][] matMult (float a[][], float b[][], float c[][], int m, int n, int p, int thnum) {
        
        ExecutorService service = Executors.newFixedThreadPool(thnum);
        Future<Float> answerMatrix[][] = new Future[m][p];
        Future<Float> answer;
        
        class MatrixMult implements Callable<Float> {
            private float a[][];
            private float b[][];
            private float c[][];
            private int i;
            private int j;
            private float s;

            public MatrixMult(float a[][],float b[][], float c[][], int i, int j) {
                this.a = a;
                this.b = b;
                this.c = c;
                this.i = i;
                this.j = j;
            }

            public Float call() {
                try {
                    for (int k = 0; k < n; k++) {
                        s = s + a[i][k] * b[k][j];
                    }    
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return s;
            }
        }
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                answer = service.submit(new MatrixMult(a,b,c,i,j));
                answerMatrix[i][j] = answer; 
            }
        }
        
        for (int i=0; i<m; i++) {
            for (int j=0; j<p; j++) {
                try {    
                    c[i][j] = answerMatrix[i][j].get();
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
                catch(ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return c;
    }
    
}