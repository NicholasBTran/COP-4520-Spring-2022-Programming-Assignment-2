import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Nicholas Tran
// COP 4520 - Parallel Programming
// Spring 2022
// Problem 1: Minotaur's Birthday Party

class Guest extends Thread {
    AtomicBoolean hasEaten = new AtomicBoolean(false);
    static AtomicBoolean emptyPlate = new AtomicBoolean(true);
    static Lock mutex = new ReentrantLock();

    public void run() {
        // Get lock
        mutex.lock();
        try {
            // Check if the plate is empty, if it is not then eat the cupcake.
            if (!hasEaten.get() && !emptyPlate.compareAndExchange(false, true)) hasEaten.set(true);
        } finally {
            mutex.unlock();
        }
    }
}

// Leader thread keeps track of the total number of Guests guaranteed to have visited the
// labyrinth.
class leader extends Guest {
    AtomicInteger counter = new AtomicInteger(0);

    public void run() {
        // leader.counter should increment only when a new guest is guaranteed to have entered a labyrinth.
        // Check if the plate is empty, if it is increment count and request a new cupcake.
        if (emptyPlate.compareAndExchange(true, false)) {
            counter.getAndIncrement();
        }
    }
}

public class Problem1 {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        long counter;
        int numGuests = 10000;
        int randomWithNextInt;
        ArrayList<Guest> threads = new ArrayList<>();
        Random random = new Random();

        // Create leader thread
        leader leader = new leader();
        leader.setName("Leader");
        threads.add(leader);
        leader.hasEaten.set(true);

        // Create threads and add them to the list
        for (int i = 0; i < numGuests - 1; i++) {
            Guest t = new Guest();
            t.setName("Guest-" + i);
            threads.add(t);
        }

        // Run threads
        while (leader.counter.get() < numGuests) {
            // Randomly select threads (Guests) to run (enter labyrinth).
            randomWithNextInt = random.nextInt(numGuests);
            Thread thread = threads.get(randomWithNextInt);
            thread.run();
        }

        // Check if every thread ran (Guest visited).
        counter = 0;
        for (Guest i : threads) {
            if (i.hasEaten.get()) counter++;
        }

        System.out.println("All the guests have entered the labyrinth!");
        System.out.println("Number of Guests: " + numGuests);
        System.out.println("Leader counter: " + leader.counter.get());
        System.out.println("Number of Guests who entered labyrinth: " + counter);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Elapsed Time (ms): " + elapsedTime);
    }
}
