package board;

import java.util.HashMap;

import assets.Pair;

public class Cache {

    /**
     * Hash maped indexed by key (Zobrist), contaning pair, which
     * contains Depth as key, number of moves (or score) as value
     */
    private final HashMap<Long, Pair<Integer, Long>> storage; 

    public Cache() {
        storage = new HashMap<>();
    }

    /**
     * @param key hash of current board
     * @return Pair class if hash exists, null otherwise
     */
    public Pair<Integer, Long> contains(long key) {
        return storage.get(key);
    }

    /**
     * @param key hash of current board
     * @param value Pair class
     */
    public void insert(long key, Pair<Integer, Long> value) {
        storage.put(key, value);
    }

    /**
     * Deletes all entries from storage hashmap
     */
    public void clear() {
        storage.clear();
    }

}
