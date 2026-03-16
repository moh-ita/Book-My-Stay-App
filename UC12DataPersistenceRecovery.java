import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use Case 12: Data Persistence and System Recovery.
 * <p>
 * Demonstrates serialization-based persistence and safe recovery of inventory
 * and booking history across application restarts.
 * </p>
 *
 * @author Thamaraikannan G
 * @version 12.0
 */
public class UC12DataPersistenceRecovery {

    private static final String APPLICATION_NAME = "Book My Stay App";
    private static final String APPLICATION_VERSION = "v12.0";

    public static void main(String[] args) {
        System.out.println("Welcome to " + APPLICATION_NAME + " - Use Case 12");
        System.out.println("Version: " + APPLICATION_VERSION);
        System.out.println();

        Path stateFile = Paths.get("usecase12_system_state.ser");
        UC12PersistenceService persistenceService = new UC12PersistenceService();

        UC12SystemState stateToPersist = createCurrentState();

        System.out.println("Saving current system state...");
        boolean saveSuccess = persistenceService.saveState(stateFile, stateToPersist);
        System.out.println("Save status: " + saveSuccess);

        System.out.println();
        System.out.println("Simulating application restart...");

        UC12SystemState recoveredState = persistenceService.loadState(stateFile);
        if (recoveredState == null) {
            System.out.println("Recovery fallback: using safe empty state.");
            recoveredState = UC12SystemState.empty();
        }

        System.out.println();
        System.out.println("Recovered Inventory: " + recoveredState.getInventory());
        System.out.println("Recovered Booking History:");
        for (UC12BookingRecord bookingRecord : recoveredState.getBookingHistory()) {
            System.out.println(bookingRecord);
        }

        System.out.println();
        System.out.println("System continues operating safely after recovery.");
        System.out.println("Application execution completed.");
    }

    private static UC12SystemState createCurrentState() {
        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);

        List<UC12BookingRecord> bookingHistory = new ArrayList<>();
        bookingHistory.add(new UC12BookingRecord("RES-12001", "Ishaan", "Single Room", "SI-901", "CONFIRMED"));
        bookingHistory.add(new UC12BookingRecord("RES-12002", "Jaya", "Double Room", "DO-902", "CONFIRMED"));
        bookingHistory.add(new UC12BookingRecord("RES-12003", "Kriti", "Suite Room", "SU-903", "CONFIRMED"));

        return new UC12SystemState(inventory, bookingHistory);
    }
}

/**
 * Serializable system snapshot.
 */
class UC12SystemState implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, Integer> inventory;
    private final List<UC12BookingRecord> bookingHistory;

    public UC12SystemState(Map<String, Integer> inventory, List<UC12BookingRecord> bookingHistory) {
        this.inventory = new HashMap<>(inventory);
        this.bookingHistory = new ArrayList<>(bookingHistory);
    }

    public static UC12SystemState empty() {
        return new UC12SystemState(new HashMap<>(), new ArrayList<>());
    }

    public Map<String, Integer> getInventory() {
        return new HashMap<>(inventory);
    }

    public List<UC12BookingRecord> getBookingHistory() {
        return new ArrayList<>(bookingHistory);
    }
}

/**
 * Serializable booking record for persistence.
 */
class UC12BookingRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String reservationId;
    private final String guestName;
    private final String roomType;
    private final String roomId;
    private final String status;

    public UC12BookingRecord(String reservationId, String guestName, String roomType, String roomId, String status) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReservationId=" + reservationId
                + ", Guest=" + guestName
                + ", RoomType=" + roomType
                + ", RoomId=" + roomId
                + ", Status=" + status;
    }
}

/**
 * Handles save and restore operations.
 */
class UC12PersistenceService {

    public boolean saveState(Path path, UC12SystemState state) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            outputStream.writeObject(state);
            return true;
        } catch (IOException exception) {
            System.out.println("Persistence save failed: " + exception.getMessage());
            return false;
        }
    }

    public UC12SystemState loadState(Path path) {
        if (!Files.exists(path)) {
            System.out.println("Persistence file not found. Starting with safe default state.");
            return null;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            Object data = inputStream.readObject();
            if (data instanceof UC12SystemState) {
                return (UC12SystemState) data;
            }
            System.out.println("Invalid persistence format. Starting with safe default state.");
            return null;
        } catch (IOException | ClassNotFoundException exception) {
            System.out.println("Persistence recovery failed: " + exception.getMessage());
            System.out.println("Starting with safe default state.");
            return null;
        }
    }
}
