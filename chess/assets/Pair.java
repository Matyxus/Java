package assets;

public class Pair<K, V> {

    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return first entry of pair
     */
    public K getKey() {
        return key;
    }

    /**
     * @return second entry of pair
     */
    public V getValue() {
        return value;
    }
    
}