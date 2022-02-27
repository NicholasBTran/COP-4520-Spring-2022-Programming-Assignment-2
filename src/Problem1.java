import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Nicholas Tran
// COP 4520 - Parallel Programming
// Spring 2022
// Problem 1: Minotaur�s Birthday Party

class Guest extends Thread {
    AtomicBoolean hasEaten = new AtomicBoolean(false);
    static AtomicBoolean emptyPlate = new AtomicBoolean(true);
    static Lock lock = new ReentrantLock();

    public void run() {
        // Check if the plate is empty, if it is not then eat the cupcake.
        if (hasEaten.get() == true)
            ; // Do nothing
        else if (emptyPlate.compareAndExchange(false, true) == false) {
            hasEaten.set(true);
        }
    }
}

// Leader thread keeps track of the total number of Guests guarnteed to have visited the
// labyrinth.
class leader extends Guest {
    AtomicInteger counter = new AtomicInteger(0);

    public void run() {
        // hasEaten.compareAndExchange(false, true);
        // Check if the plate is empty, if it is increment count and request a new cupcake.
        if (emptyPlate.compareAndExchange(true, false) == true)
            counter.getAndIncrement();
    }
}

public class Problem1 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        long counter = 0;
        int numGuests = 10000;
        ArrayList<Guest> threads = new ArrayList<>();
        Random random = new Random();
        int randomWithNextInt = random.nextInt();

        // Create leader thread
        leader leader = new leader();
        leader.setName("Leader");
        threads.add(leader);
        leader.hasEaten.set(true);

        // Create threads and add them to the list
        for (int i = 0; i < numGuests - 1; i++) {
            Guest t = new Guest();
            t.setName("Guest-" + i);
            t.hasEaten.set(false);
            threads.add(t);
        }

        // Run threads
        while (leader.counter.get() < numGuests) {
            // Randomly select threads (Guests) to run (enter labyrinth).
            randomWithNextInt = random.nextInt(numGuests);
            Thread thread = threads.get(randomWithNextInt);
            thread.run();

            try {
                // Wait for threads to finish (Guests to leave labyrinth).
                thread.join();
            } catch (Exception e) {
                System.out.println("[Exception]: " + e);
            }
        }

        // Check if every thread ran (Guest visited).
        counter = 0;
        for (Guest i : threads) {
            if (i.hasEaten.get() == true)
                counter++;
        }

        System.out.println("Number of Guests: " + numGuests);
        System.out.println("Leader counter: " + leader.counter.get());
        System.out.println("Number of Guests who entered labyrinth: " + counter);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Elapsed Time (ms): " + elapsedTime);
    }
}
