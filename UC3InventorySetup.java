import java.util.HashMap;
import java.util.Map;

/**
 * Use Case 3: Centralized Room Inventory Management.
 * <p>
 * This program demonstrates centralized availability management using a
 * {@link HashMap} as a single source of truth.
 * </p>
 *
 * @author Thamaraikannan G
 * @version 3.0
 */
public class UC3InventorySetup {

    private static final String APPLICATION_NAME = "Book My Stay App";
    private static final String APPLICATION_VERSION = "v3.0";

    /**
     * Application entry point for Use Case 3.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("Welcome to " + APPLICATION_NAME + " - Use Case 3");
        System.out.println("Version: " + APPLICATION_VERSION);
        System.out.println();

        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        Map<String, Integer> initialAvailability = new HashMap<>();
        initialAvailability.put(singleRoom.getRoomType(), 12);
        initialAvailability.put(doubleRoom.getRoomType(), 8);
        initialAvailability.put(suiteRoom.getRoomType(), 4);

        RoomInventory roomInventory = new RoomInventory(initialAvailability);

        System.out.println("Current Inventory (Initial State)");
        roomInventory.displayInventory();

        System.out.println();
        System.out.println("Applying Controlled Inventory Updates...");
        roomInventory.updateAvailability(singleRoom.getRoomType(), 10);
        roomInventory.updateAvailability(doubleRoom.getRoomType(), 7);

        System.out.println();
        System.out.println("Availability Check:");
        System.out.println(singleRoom.getRoomType() + " => "
                + roomInventory.getAvailability(singleRoom.getRoomType()));
        System.out.println(doubleRoom.getRoomType() + " => "
                + roomInventory.getAvailability(doubleRoom.getRoomType()));
        System.out.println(suiteRoom.getRoomType() + " => "
                + roomInventory.getAvailability(suiteRoom.getRoomType()));

        System.out.println();
        System.out.println("Current Inventory (After Updates)");
        roomInventory.displayInventory();

        System.out.println();
        System.out.println("Application execution completed.");
    }
}

/**
 * Manages room availability using a centralized map.
 */
class RoomInventory {

    private final HashMap<String, Integer> availabilityByRoomType;

    /**
     * Creates inventory with initial room availability.
     *
     * @param initialAvailability room type to availability mapping
     */
    public RoomInventory(Map<String, Integer> initialAvailability) {
        this.availabilityByRoomType = new HashMap<>();
        for (Map.Entry<String, Integer> entry : initialAvailability.entrySet()) {
            validateAvailability(entry.getKey(), entry.getValue());
            this.availabilityByRoomType.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Gets availability count for a room type.
     *
     * @param roomType room type key
     * @return available count
     */
    public int getAvailability(String roomType) {
        if (!availabilityByRoomType.containsKey(roomType)) {
            throw new IllegalArgumentException("Unknown room type: " + roomType);
        }
        return availabilityByRoomType.get(roomType);
    }

    /**
     * Updates availability for a room type through a controlled method.
     *
     * @param roomType room type key
     * @param newAvailability updated availability value
     */
    public void updateAvailability(String roomType, int newAvailability) {
        if (!availabilityByRoomType.containsKey(roomType)) {
            throw new IllegalArgumentException("Unknown room type: " + roomType);
        }
        validateAvailability(roomType, newAvailability);
        availabilityByRoomType.put(roomType, newAvailability);
    }

    /**
     * Returns a copy of current inventory state.
     *
     * @return snapshot of room inventory
     */
    public Map<String, Integer> getCurrentAvailability() {
        return new HashMap<>(availabilityByRoomType);
    }

    /**
     * Displays current inventory state.
     */
    public void displayInventory() {
        Map<String, Integer> snapshot = getCurrentAvailability();
        for (Map.Entry<String, Integer> entry : snapshot.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }
    }

    private void validateAvailability(String roomType, int availability) {
        if (roomType == null || roomType.isBlank()) {
            throw new IllegalArgumentException("Room type must not be blank.");
        }
        if (availability < 0) {
            throw new IllegalArgumentException("Availability cannot be negative for: " + roomType);
        }
    }
}

/**
 * Abstract room model representing room characteristics.
 */
abstract class Room {

    private final String roomType;

    protected Room(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomType() {
        return roomType;
    }
}

/**
 * Single room type.
 */
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room");
    }
}

/**
 * Double room type.
 */
class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room");
    }
}

/**
 * Suite room type.
 */
class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room");
    }
}
