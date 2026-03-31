/**
 * Use Case 2: Basic Room Types and Static Availability.
 * <p>
 * This program demonstrates abstraction, inheritance, polymorphism, encapsulation,
 * and static availability representation for a simple hotel booking domain.
 * </p>
 *
 * @author Thamaraikannan G
 * @version 2.0
 */
public class UC2RoomInitialization {

    private static final String APPLICATION_NAME = "Book My Stay App";
    private static final String APPLICATION_VERSION = "v2.0";

    /**
     * Application entry point for Use Case 2.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("Welcome to " + APPLICATION_NAME + " - Use Case 2");
        System.out.println("Version: " + APPLICATION_VERSION);
        System.out.println();

        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        int singleRoomAvailability = 12;
        int doubleRoomAvailability = 8;
        int suiteRoomAvailability = 4;

        printRoomWithAvailability(singleRoom, singleRoomAvailability);
        printRoomWithAvailability(doubleRoom, doubleRoomAvailability);
        printRoomWithAvailability(suiteRoom, suiteRoomAvailability);

        System.out.println();
        System.out.println("Application execution completed.");
    }

    private static void printRoomWithAvailability(Room room, int availability) {
        System.out.println("Room Type      : " + room.getRoomType());
        System.out.println("Beds           : " + room.getNumberOfBeds());
        System.out.println("Size (sq. ft.) : " + room.getRoomSizeSqFt());
        System.out.println("Price/Night    : " + room.getPricePerNight());
        System.out.println("Availability   : " + availability);
        System.out.println("Description    : " + room.getDescription());
        System.out.println("----------------------------------------");
    }
}

/**
 * Abstract representation of a room.
 * Defines common state and behavior shared across all room types.
 */
abstract class Room {

    private final String roomType;
    private final int numberOfBeds;
    private final int roomSizeSqFt;
    private final double pricePerNight;

    protected Room(String roomType, int numberOfBeds, int roomSizeSqFt, double pricePerNight) {
        this.roomType = roomType;
        this.numberOfBeds = numberOfBeds;
        this.roomSizeSqFt = roomSizeSqFt;
        this.pricePerNight = pricePerNight;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNumberOfBeds() {
        return numberOfBeds;
    }

    public int getRoomSizeSqFt() {
        return roomSizeSqFt;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public abstract String getDescription();
}

/**
 * Concrete room type representing a single room.
 */
class SingleRoom extends Room {

    public SingleRoom() {
        super("Single Room", 1, 180, 2499.00);
    }

    @Override
    public String getDescription() {
        return "Ideal for solo travelers.";
    }
}

/**
 * Concrete room type representing a double room.
 */
class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double Room", 2, 280, 3999.00);
    }

    @Override
    public String getDescription() {
        return "Suitable for couples or two guests.";
    }
}

/**
 * Concrete room type representing a suite room.
 */
class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite Room", 3, 450, 6999.00);
    }

    @Override
    public String getDescription() {
        return "Premium space with enhanced comfort.";
    }
}
