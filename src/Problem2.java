import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

class NewThread extends Thread {
    AtomicBoolean hasEaten = new AtomicBoolean(false);
    static AtomicBoolean emptyPlate = new AtomicBoolean(false);

    public void run() {
    }
}

public class Problem2 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        int numGuests = 10000;
        ArrayList<NewThread> threads = new ArrayList<>();
        Random random = new Random();
        int randomWithNextInt = random.nextInt();

        // Create threads and add them to the list
        for (int i = 0; i < numGuests - 1; i++) {
            NewThread t = new NewThread();
            t.setName("NewThread-" + i);
            t.hasEaten.set(false);
            threads.add(t);
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Elapsed Time (ms): " + elapsedTime);

    }

}
