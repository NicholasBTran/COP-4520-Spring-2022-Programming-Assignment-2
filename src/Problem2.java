import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

// Nicholas Tran
// COP 4520 - Parallel Programming
// Spring 2022
// Problem 2: Minotaur’s Crystal Vase

// An implemenation of a type of queue lock based on the third strategy described in the problem.
// Source: Class textbook "The Art of Multiprocessor Programming", Morgan Kaufmann (2020)
class CLHLock implements Lock {
    AtomicReference<QNode> tail;
    ThreadLocal<QNode> myPred;
    ThreadLocal<QNode> myNode;

    public CLHLock() {
        tail = new AtomicReference<QNode>(new QNode());
        myNode = new ThreadLocal<QNode>() {
            public QNode initialValue() {
                return new QNode();
            }
        };
        myPred = new ThreadLocal<QNode>() {
            protected QNode initialValue() {
                return null;
            }
        };
    }

    public void lock() {
        QNode qnode = myNode.get();
        qnode.locked = true;
        QNode pred = tail.getAndSet(qnode);
        myPred.set(pred);
        while (pred.locked) ;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {
        QNode qnode = myNode.get();
        qnode.locked = false;
        myNode.set(myPred.get());
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    class QNode {
        volatile boolean locked = false;
    }
}

class NewThread extends Thread {
    static Lock mutex = new CLHLock();
    static long counter = 0;
    static ArrayList<String> entryList = new ArrayList<>();

    public void run() {
        // When a thread obtains the lock, increment the counter and add the name to the entry list.
        mutex.lock();
        try {
            counter++;
            entryList.add(this.getName());
        } finally {
            mutex.unlock();
        }
    }
}

public class Problem2 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        final int numGuests = 100000;
        final int multiplier = 10;
        int numEntries = numGuests * multiplier;
        ArrayList<NewThread> threads = new ArrayList<>();
        ArrayList<String> queueList = new ArrayList<>();
        Random random = new Random();
        int randomWithNextInt;

        // Create threads and add them to the list
        for (int i = 0; i < numGuests; i++) {
            NewThread t = new NewThread();
            t.setName("Guest-" + i);
            threads.add(t);
        }

        // Run threads for a while: until the number of vase sightings is equal to 10*numGuests
        while (NewThread.counter < numEntries) {
            randomWithNextInt = random.nextInt(numGuests);
            Thread thread = threads.get(randomWithNextInt);
            thread.run();
            queueList.add(thread.getName());
        }

        // A simple check to see if Guests who queue to see the vase, actual get to see the vase.
        // Compare the Queue List and the Entry List to see if the order was preserved.
        boolean listsMatch = true;
        for (int i = 0; i < queueList.size(); i++) {
            if (!queueList.get(i).equals(NewThread.entryList.get(i))) {
                // Set listsMatch flag to false if the lists are not equivalent.
                listsMatch = false;
                break;
            }
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("No more guests are waiting to see the vase!");
        System.out.println("Elapsed Time (ms): " + elapsedTime);
        System.out.println("Number of Guests: " + numGuests);
        System.out.println("Expected Vase Sightings: " + numEntries);
        System.out.println("Actual Vase Sightings: " + NewThread.counter);
        System.out.println("Lists Match: " + listsMatch);
    }
}
