import java.util.ArrayList;
import java.util.LinkedList;

/**
 * HashTable class implements a hash table with dynamic resizing for storing
 */
public class HashTable {
    public ArrayList<LinkedList<Host>> indexes;
    public int capacity;
    public int size;
    private static final double LOAD_FACTOR = 0.75;

    /**
     * Constructor of HashTable objects
     */
    public HashTable(int initialCapacity) {
        this.capacity = initialCapacity;
        this.size = 0;
        this.indexes = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            indexes.add(new LinkedList<>());
        }
    }

    /**
     * Hash function
     */
    private int hash(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    /**
     * Inserts a host into the hash table and resizes if necessary
     */
    public void put(Host value) {
        if ((double) size / capacity >= LOAD_FACTOR) {
            resize();
        }
        String key = value.name;
        int index = hash(key);
        LinkedList<Host> chain = indexes.get(index);
        chain.add(value);
        size++;
    }

    /**
     * Retrieves a host from the hash table by key
     */
    public Host get(String key) {
        int index = hash(key);
        LinkedList<Host> chain = indexes.get(index);
        for (Host h : chain) {
            if (h.name.equals(key)) {
                return h;
            }
        }
        return null;
    }

    /**
     * Resizes the hash table to double capacity, finds the next prime, and rehashes
     * elements
     */
    private void resize() {
        int newCapacity = getNextPrime(capacity * 2);
        ArrayList<LinkedList<Host>> newIndexes = new ArrayList<>(newCapacity);
        for (int i = 0; i < newCapacity; i++) {
            newIndexes.add(new LinkedList<>());
        }
        for (LinkedList<Host> chain : indexes) {
            for (Host host : chain) {
                String key = host.name;
                int newIndex = Math.abs(key.hashCode()) % newCapacity;
                newIndexes.get(newIndex).add(host);
            }
        }
        this.indexes = newIndexes;
        this.capacity = newCapacity;
    }

    /**
     * Finds the next prime number greater than or equal to input
     */
    private int getNextPrime(int input) {
        int counter = input;
        while (true) {
            if (isPrime(counter)) {
                return counter;
            }
            counter++;
        }
    }

    /**
     * Checks if a number is prime
     */
    private boolean isPrime(int num) {
        if (num <= 1)
            return false;
        if (num <= 3)
            return true;
        if (num % 2 == 0 || num % 3 == 0)
            return false;
        for (int i = 5; i * i <= num; i = i + 6) {
            if (num % i == 0 || num % (i + 2) == 0)
                return false;
        }
        return true;
    }

    /**
     * gathers all hosts from the hash table
     */
    public ArrayList<Host> gather() {
        ArrayList<Host> list = new ArrayList<>();
        for (LinkedList<Host> chain : indexes) {
            list.addAll(chain);
        }
        return list;
    }
}
