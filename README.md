# COP-4520-Spring-2022-Programming-Assignment-2
Nicholas Tran

<ins>Problem 1: Minotaur’s Birthday Party</ins> <br>

To compile and run:<br>
javac Problem1.java<br>
java Problem1<br>

The strategy I used to let the Minotaur know that every guest entered the Minotaur’s labyrinth is similar to the strategy that was discussed in class about the prisoner’s dilemma with the light switch. The guests elect a leader among them to keep count of how many cupcakes were eaten. Guests will only eat a cupcake if there is one already there when they leave the labyrinth and they have not eaten a cupcake yet. The leader will be the one to request for new cupcakes, keeping track of when the number of cupcakes requested is equal to the number of guests at the party. Once equal, the leader announces to the Minotaur that every guest has entered the labyrinth.

In Java code, each guest is represented by a single thread with the number of guests being a hardcoded value of 10,000. Each guest has a flag for if they have eaten a cupcake and another class-wide flag is used to tell the guests if there is a cupcake there when leaving the labyrinth or if the plate is empty. Each thread is randomly picked from a list by a Java Random() object calling nextInt() to simulate the minotaur randomly picking guests. The leader thread has a counter to keep track of the number of guests who have eaten a cupcake, and that value is used as the conditional statement in a while loop to know when every guest is guaranteed to have entered the labyrinth.

I was initially trying to implement the CLH lock as described in the book as a queue-based lock would seem efficient, but I actually found somewhat better performance just using Java’s Reentrant lock class. My implementation with Java’s Reentrant locks seems to scale exponentially with the number of guests with about 10000 guests taking about 12.2 seconds to run, while 20000 guests takes about 55.3 seconds to run. Testing was done on UCF’s Eustis Server around 5 to 6 PM on 3/3/2022.<br>

<ins>Problem 2: Minotaur’s Crystal Vase</ins><br>

To compile and run:<br>
javac Problem2.java<br>
java Problem2<br>

Analysis of each strategy:<br>
In strategy 1, the disadvantages are clear in that it could result in high contention and there is no guarantee of progress. This is because many guests could try to enter into the showroom at the same time, and a particular guests is not guaranteed to be able to see the vase in a given period of time. The advantage is that the strategy is simple to implement – it is simply a first come first served basis.<br>

In strategy 2, the advantage is that guests would not waste time trying to get into the showroom when it is “BUSY.” The disadvantages are still the same as in strategy 1 in that there is no guarantee that a particular guest gets to see the vase.<br>

In strategy 3, the advantage is that guests are guaranteed first-come-first served access to the room, and that everyone who wants to see the room will eventually get to see the room.<br>

Going with strategy 3, I tried implementing a queue-based lock. Each guest gets to visit the vase room a certain number of times, and both the number of guests and visits per guests are specified by user input.<br>

I first implemented a CLH lock as described in the class textbook. 10000 guests with 10000 visits per guests runs in about 5.8 seconds. 20000 guests with 20000 visits per guests runs in about 23.5 seconds. 40000 guests with 40000 visits per guests runs in about 148.0 seconds.<br>

I then tried implementing a MCS lock from the class textbook and found some improvement in performance over the CLH lock. 10000 guests with 10000 visits per guests runs in about 5.5 seconds. 20000 guests with 20000 visits per guests runs in about 21.7 seconds. 40000 guests with 40000 visits per guests runs in about 98.3 seconds. Testing was done on UCF’s Eustis Server around 5 to 6 PM on 3/3/2022.<br>

