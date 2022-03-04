// Nicholas Tran
// COP 4520 - Parallel Programming
// Spring 2022
// Problem 1: Minotaur's Birthday Party

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Guest extends Thread {
    static Lock mutex = new ReentrantLock();
    boolean hasEaten = false;
    static boolean cupcake = true;

    public void start() {
        // Get lock, which simulates guests entering the maze one at a time.
        mutex.lock();
        try {
            // Check if the plate is empty, if it is not then eat the cupcake.
            if (!hasEaten && cupcake) {
                cupcake = false;
                hasEaten = true;
            }
        } finally {
            mutex.unlock();
        }
    }
}

// Leader thread keeps track of the total number of Guests guaranteed to have visited the
// labyrinth.
class leader extends Guest {
    int counter = 1;

    public leader() {
        super();
        this.hasEaten = true;
    }

    public void start() {
        // The counter represents the number of guests guaranteed to have entered the labyrinth.
        // Check if the plate is empty, if it is increment counter and request a new cupcake.
        if (!cupcake) {
            cupcake = true;
            counter++;
        }
    }
}

public class Problem1 {
    public static void main(String[] args) {
        long startTime, stopTime, elapsedTime;
        long counter;
        int numGuests;
        ArrayList<Guest> threads = new ArrayList<>();
        Random random = new Random();

        // Read input from user
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter how many guests (integer):");
        numGuests = keyboard.nextInt();
        keyboard.close();

        // Start timer
        startTime = System.currentTimeMillis();

        // Create leader thread
        leader leader = new leader();
        leader.setName("Leader");
        threads.add(leader);

        // Create threads and add them to the list
        for (int i = 0; i < numGuests - 1; i++) {
            Guest t = new Guest();
            t.setName("Guest-" + i);
            threads.add(t);
        }

        // Run threads
        while (leader.counter < numGuests) {
            // Randomly select threads (Guests) to run (enter labyrinth).
            Thread thread = threads.get(random.nextInt(numGuests));
            thread.start();
        }

        // Count how many guests have eaten a cupcake/entered the labyrinth.
        counter = 0;
        for (Guest i : threads) {
            if (i.hasEaten) counter++;
        }

        if (counter == numGuests)
            System.out.println("\nAll the guests have entered the labyrinth!\n");
        else
            System.out.println("\nUh-oh, some guests didn't enter the labyrinth =(\n");
        System.out.println("Number of Guests: " + numGuests);
        System.out.println("Leader counter: " + leader.counter);
        System.out.println("Number of Guests who entered labyrinth: " + counter);
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Elapsed Time (ms): " + elapsedTime);
    }
}
