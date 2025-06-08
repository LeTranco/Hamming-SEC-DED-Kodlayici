import java.util.HashMap;

public class Memory {
    private static HashMap<Integer, int[]> memory = new HashMap<>();

    public static void save(int address, int[] data) {
        memory.put(address, data.clone());
    }

    public static int[] read(int address) {
        return memory.get(address);
    }

    public static boolean exists(int address) {
        return memory.containsKey(address);
    }
}