import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;

/**
 * Use Case 10: Booking Cancellation and Inventory Rollback.
 * <p>
 * Demonstrates safe cancellation with controlled rollback of room allocation,
 * inventory restoration, and booking history status updates.
 * </p>
 *
 * @author Thamaraikannan G
 * @version 10.0
 */
public class UC10BookingCancellation {

    private static final String APPLICATION_NAME = "Book My Stay App";
    private static final String APPLICATION_VERSION = "v10.0";

    public static void main(String[] args) {
        System.out.println("Welcome to " + APPLICATION_NAME + " - Use Case 10");
        System.out.println("Version: " + APPLICATION_VERSION);
        System.out.println();

        Map<String, Integer> inventorySeed = new HashMap<>();
        inventorySeed.put("Single Room", 1);
        inventorySeed.put("Double Room", 0);
        inventorySeed.put("Suite Room", 0);

        UC10InventoryService inventoryService = new UC10InventoryService(inventorySeed);
        UC10BookingHistory bookingHistory = new UC10BookingHistory();
        UC10RoomAllocationRegistry allocationRegistry = new UC10RoomAllocationRegistry();

        UC10Booking booking1 = new UC10Booking("RES-10001", "Anbu", "Single Room", "SI-501");
        UC10Booking booking2 = new UC10Booking("RES-10002", "Bala", "Double Room", "DO-601");
        UC10Booking booking3 = new UC10Booking("RES-10003", "Chitra", "Suite Room", "SU-701");

        bookingHistory.addConfirmedBooking(booking1);
        bookingHistory.addConfirmedBooking(booking2);
        bookingHistory.addConfirmedBooking(booking3);

        allocationRegistry.registerAllocatedRoomId(booking1.getRoomId());
        allocationRegistry.registerAllocatedRoomId(booking2.getRoomId());
        allocationRegistry.registerAllocatedRoomId(booking3.getRoomId());

        UC10CancellationService cancellationService = new UC10CancellationService(
                bookingHistory, inventoryService, allocationRegistry);

        System.out.println("Initial Inventory: " + inventoryService.getSnapshot());
        System.out.println("Initial Booking History:");
        bookingHistory.displayBookings();
        System.out.println();

        System.out.println("Cancellation Attempt 1 (Valid):");
        System.out.println(cancellationService.cancelBooking("RES-10002"));

        System.out.println();
        System.out.println("Cancellation Attempt 2 (Already Cancelled):");
        System.out.println(cancellationService.cancelBooking("RES-10002"));

        System.out.println();
        System.out.println("Cancellation Attempt 3 (Non-Existent):");
        System.out.println(cancellationService.cancelBooking("RES-19999"));

        System.out.println();
        System.out.println("Rollback Stack (Recently Released Room IDs): " + allocationRegistry.getReleasedRoomIdsSnapshot());
        System.out.println("Inventory After Cancellations: " + inventoryService.getSnapshot());
        System.out.println("Booking History After Cancellations:");
        bookingHistory.displayBookings();

        System.out.println();
        System.out.println("Application execution completed.");
    }
}

/**
 * Represents a booking in the lifecycle.
 */
class UC10Booking {

    private final String reservationId;
    private final String guestName;
    private final String roomType;
    private final String roomId;
    private String status;

    public UC10Booking(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.status = "CONFIRMED";
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getStatus() {
        return status;
    }

    public void markCancelled() {
        this.status = "CANCELLED";
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
 * Stores booking records for lookup and lifecycle updates.
 */
class UC10BookingHistory {

    private final Map<String, UC10Booking> bookingsById;

    public UC10BookingHistory() {
        this.bookingsById = new LinkedHashMap<>();
    }

    public void addConfirmedBooking(UC10Booking booking) {
        bookingsById.put(booking.getReservationId(), booking);
    }

    public UC10Booking findByReservationId(String reservationId) {
        return bookingsById.get(reservationId);
    }

    public void displayBookings() {
        for (UC10Booking booking : bookingsById.values()) {
            System.out.println(booking);
        }
    }
}

/**
 * Maintains inventory state and restoration operations.
 */
class UC10InventoryService {

    private final Map<String, Integer> availability;

    public UC10InventoryService(Map<String, Integer> initialAvailability) {
        this.availability = new HashMap<>(initialAvailability);
    }

    public void increment(String roomType) {
        availability.put(roomType, availability.getOrDefault(roomType, 0) + 1);
    }

    public Map<String, Integer> getSnapshot() {
        return new HashMap<>(availability);
    }
}

/**
 * Tracks allocated and released room IDs.
 */
class UC10RoomAllocationRegistry {

    private final Set<String> allocatedRoomIds;
    private final Stack<String> releasedRoomIds;

    public UC10RoomAllocationRegistry() {
        this.allocatedRoomIds = new HashSet<>();
        this.releasedRoomIds = new Stack<>();
    }

    public void registerAllocatedRoomId(String roomId) {
        allocatedRoomIds.add(roomId);
    }

    public boolean releaseAllocatedRoomId(String roomId) {
        if (!allocatedRoomIds.contains(roomId)) {
            return false;
        }
        allocatedRoomIds.remove(roomId);
        releasedRoomIds.push(roomId);
        return true;
    }

    public Stack<String> getReleasedRoomIdsSnapshot() {
        Stack<String> snapshot = new Stack<>();
        snapshot.addAll(releasedRoomIds);
        return snapshot;
    }
}

/**
 * Performs validated cancellation and ordered rollback steps.
 */
class UC10CancellationService {

    private final UC10BookingHistory bookingHistory;
    private final UC10InventoryService inventoryService;
    private final UC10RoomAllocationRegistry allocationRegistry;

    public UC10CancellationService(UC10BookingHistory bookingHistory,
                                   UC10InventoryService inventoryService,
                                   UC10RoomAllocationRegistry allocationRegistry) {
        this.bookingHistory = bookingHistory;
        this.inventoryService = inventoryService;
        this.allocationRegistry = allocationRegistry;
    }

    public String cancelBooking(String reservationId) {
        UC10Booking booking = bookingHistory.findByReservationId(reservationId);
        if (booking == null) {
            return "REJECTED: Reservation does not exist.";
        }
        if (!"CONFIRMED".equals(booking.getStatus())) {
            return "REJECTED: Reservation is already cancelled.";
        }

        boolean roomReleased = allocationRegistry.releaseAllocatedRoomId(booking.getRoomId());
        if (!roomReleased) {
            return "REJECTED: Room allocation state is inconsistent for rollback.";
        }

        inventoryService.increment(booking.getRoomType());
        booking.markCancelled();

        return "SUCCESS: Cancellation completed for " + reservationId
                + " and inventory restored.";
    }
}
