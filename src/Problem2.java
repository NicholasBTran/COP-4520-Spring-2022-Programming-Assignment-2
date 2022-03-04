// Nicholas Tran
// COP 4520 - Parallel Programming
// Spring 2022
// Problem 2: Minotaur’s Crystal Vase

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

// An implementation of the Mellor-Crummey and Scott MCS lock.
// Source: Class textbook "The Art of Multiprocessor Programming", Morgan Kaufmann (2020)
class MCSLock implements Lock {
    AtomicReference<QNode> tail;
    ThreadLocal<QNode> myNode;

    public MCSLock() {
        tail = new AtomicReference<>(null);
        myNode = ThreadLocal.withInitial(QNode::new);
    }

    public void lock() {
        QNode qnode = myNode.get();
        QNode pred = tail.getAndSet(qnode);
        if (pred != null) {
            qnode.locked = true;
            pred.next = qnode;
            // wait until predecessor gives up the lock
            while (qnode.locked) {
            }
        }
    }

    @Override
    public void lockInterruptibly() {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        return false;
    }

    public void unlock() {
        QNode qnode = myNode.get();
        if (qnode.next == null) {
            if (tail.compareAndSet(qnode, null))
                return;
            // wait until successor fills in its next field
            while (qnode.next == null) {
            }
        }
        qnode.next.locked = false;
        qnode.next = null;
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    class QNode {
        volatile boolean locked = false;
        volatile QNode next = null;
    }
}

class ViewVase extends Thread {
    static Lock mutex = new MCSLock();
    static long counter = 0;
    static int viewsPerGuest = 10000;
    int numViews = 0;

    public void start() {
        // Each guest views the vase the same number of times.
        while (numViews < viewsPerGuest) {
            // Try to get the lock and enter the vase viewing room.
            mutex.lock();
            try {
                // Keep track of how many times the vase is viewed.
                counter++;
                numViews++;
            } finally {
                mutex.unlock();
            }
        }
    }

    public static void setViewsPerGuest(int n) {
        viewsPerGuest = n;
    }
}

public class Problem2 {

    public static void main(String[] args) {
        int numGuests;
        int numEntries;
        long startTime, stopTime, elapsedTime;
        ArrayList<ViewVase> threads = new ArrayList<>();

        // Read input from user
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter how many guests (integer):");
        numGuests = keyboard.nextInt();
        System.out.println("Enter how many times a guest should visit the vase room (integer):");
        ViewVase.setViewsPerGuest(keyboard.nextInt());
        numEntries = numGuests * ViewVase.viewsPerGuest;
        keyboard.close();

        // Start timer
        startTime = System.currentTimeMillis();

        // Create threads and add them to the list
        for (int i = 0; i < numGuests; i++) {
            ViewVase t = new ViewVase();
            t.setName("Guest-" + i);
            threads.add(t);
        }

        for (Thread t : threads) {
            t.start();
        }

        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("\nNo more guests are waiting to see the vase!\n");
        System.out.println("Number of Guests: " + numGuests);
        System.out.println("Number of sightings per guest: " + ViewVase.viewsPerGuest);
        System.out.println("Expected Vase Viewings: " + numEntries);
        System.out.println("Actual Vase Viewings: " + ViewVase.counter);
        System.out.println("Elapsed Time (ms): " + elapsedTime);
    }
}
