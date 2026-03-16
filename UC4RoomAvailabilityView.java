import java.util.HashMap;
import java.util.Map;

/**
 * Use Case 4: Room Availability Visibility.
 * <p>
 * This use case exposes current room availability to the user from a centralized
 * inventory source, without accepting booking requests or performing allocations.
 * </p>
 *
 * @author Thamaraikannan G
 * @version 4.0
 */
public class UC4RoomAvailabilityView {

    private static final String APPLICATION_NAME = "Book My Stay App";
    private static final String APPLICATION_VERSION = "v4.0";

    public static void main(String[] args) {
        System.out.println("Welcome to " + APPLICATION_NAME + " - Use Case 4");
        System.out.println("Version: " + APPLICATION_VERSION);
        System.out.println();

        Map<String, Integer> initialAvailability = new HashMap<>();
        initialAvailability.put("Single Room", 12);
        initialAvailability.put("Double Room", 8);
        initialAvailability.put("Suite Room", 4);

        UC4RoomInventory inventory = new UC4RoomInventory(initialAvailability);

        System.out.println("Current Room Availability:");
        inventory.displayAvailability();

        System.out.println();
        System.out.println("Quick Availability Lookup:");
        System.out.println("Single Room => " + inventory.getAvailability("Single Room"));
        System.out.println("Double Room => " + inventory.getAvailability("Double Room"));
        System.out.println("Suite Room => " + inventory.getAvailability("Suite Room"));

        System.out.println();
        System.out.println("No booking requests or inventory mutations are performed in this use case.");
        System.out.println("Application execution completed.");
    }
}

/**
 * Centralized inventory component for read-focused availability access.
 */
class UC4RoomInventory {

    private final HashMap<String, Integer> availabilityByRoomType;

    public UC4RoomInventory(Map<String, Integer> initialAvailability) {
        this.availabilityByRoomType = new HashMap<>(initialAvailability);
    }

    public int getAvailability(String roomType) {
        if (!availabilityByRoomType.containsKey(roomType)) {
            throw new IllegalArgumentException("Unknown room type: " + roomType);
        }
        return availabilityByRoomType.get(roomType);
    }

    public Map<String, Integer> getAllAvailability() {
        return new HashMap<>(availabilityByRoomType);
    }

    public void displayAvailability() {
        for (Map.Entry<String, Integer> entry : getAllAvailability().entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }
    }
}
