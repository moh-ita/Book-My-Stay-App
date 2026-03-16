import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Use Case 6: Reservation Confirmation and Room Allocation.
 * <p>
 * Demonstrates FIFO request processing, unique room assignment, and immediate
 * inventory synchronization to prevent double-booking.
 * </p>
 *
 * @author Thamaraikannan G
 * @version 6.0
 */
public class UC6ReservationConfirmation {

    private static final String APPLICATION_NAME = "Book My Stay App";
    private static final String APPLICATION_VERSION = "v6.0";

    public static void main(String[] args) {
        System.out.println("Welcome to " + APPLICATION_NAME + " - Use Case 6");
        System.out.println("Version: " + APPLICATION_VERSION);
        System.out.println();

        Queue<UC6ReservationRequest> requestQueue = new ArrayDeque<>();
        requestQueue.offer(new UC6ReservationRequest("REQ-2001", "Anitha", "Single Room"));
        requestQueue.offer(new UC6ReservationRequest("REQ-2002", "Bharath", "Double Room"));
        requestQueue.offer(new UC6ReservationRequest("REQ-2003", "Charan", "Suite Room"));
        requestQueue.offer(new UC6ReservationRequest("REQ-2004", "Deepa", "Suite Room"));

        Map<String, Integer> initialInventory = new HashMap<>();
        initialInventory.put("Single Room", 2);
        initialInventory.put("Double Room", 1);
        initialInventory.put("Suite Room", 1);

        UC6InventoryService inventoryService = new UC6InventoryService(initialInventory);
        UC6BookingService bookingService = new UC6BookingService(inventoryService);

        System.out.println("Initial Inventory: " + inventoryService.getSnapshot());
        System.out.println();

        while (!requestQueue.isEmpty()) {
            UC6ReservationConfirmationResult result = bookingService.confirmNext(requestQueue);
            System.out.println(result);
        }

        System.out.println();
        System.out.println("Allocated Room IDs by Type: " + bookingService.getAllocatedRoomIdsByType());
        System.out.println("Final Inventory: " + inventoryService.getSnapshot());
        System.out.println();
        System.out.println("Application execution completed.");
    }
}

/**
 * Represents a queued reservation request.
 */
class UC6ReservationRequest {

    private final String requestId;
    private final String guestName;
    private final String roomType;

    public UC6ReservationRequest(String requestId, String guestName, String roomType) {
        this.requestId = requestId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

/**
 * Maintains centralized inventory state.
 */
class UC6InventoryService {

    private final Map<String, Integer> availability;

    public UC6InventoryService(Map<String, Integer> initialAvailability) {
        this.availability = new HashMap<>(initialAvailability);
    }

    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    public void decrement(String roomType) {
        int current = getAvailability(roomType);
        if (current <= 0) {
            throw new IllegalStateException("No inventory available for: " + roomType);
        }
        availability.put(roomType, current - 1);
    }

    public Map<String, Integer> getSnapshot() {
        return new HashMap<>(availability);
    }
}

/**
 * Handles reservation confirmation and room allocation logic.
 */
class UC6BookingService {

    private final UC6InventoryService inventoryService;
    private final Set<String> globallyAllocatedRoomIds;
    private final Map<String, Set<String>> allocatedRoomIdsByType;
    private int sequence;

    public UC6BookingService(UC6InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        this.globallyAllocatedRoomIds = new HashSet<>();
        this.allocatedRoomIdsByType = new HashMap<>();
        this.sequence = 1;
    }

    public UC6ReservationConfirmationResult confirmNext(Queue<UC6ReservationRequest> requestQueue) {
        UC6ReservationRequest request = requestQueue.poll();
        if (request == null) {
            throw new IllegalStateException("No pending requests to confirm.");
        }

        int availability = inventoryService.getAvailability(request.getRoomType());
        if (availability <= 0) {
            return UC6ReservationConfirmationResult.rejected(
                    request.getRequestId(), request.getGuestName(), request.getRoomType(), "Insufficient inventory");
        }

        String allocatedRoomId = generateUniqueRoomId(request.getRoomType());
        allocatedRoomIdsByType.computeIfAbsent(request.getRoomType(), key -> new HashSet<>()).add(allocatedRoomId);
        inventoryService.decrement(request.getRoomType());

        return UC6ReservationConfirmationResult.confirmed(
                request.getRequestId(), request.getGuestName(), request.getRoomType(), allocatedRoomId,
                inventoryService.getAvailability(request.getRoomType()));
    }

    public Map<String, Set<String>> getAllocatedRoomIdsByType() {
        Map<String, Set<String>> snapshot = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : allocatedRoomIdsByType.entrySet()) {
            snapshot.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return snapshot;
    }

    private String generateUniqueRoomId(String roomType) {
        String prefix = roomType.replace(" ", "").substring(0, 2).toUpperCase();
        String candidate;
        do {
            candidate = prefix + "-" + String.format("%03d", sequence++);
        } while (globallyAllocatedRoomIds.contains(candidate));

        globallyAllocatedRoomIds.add(candidate);
        return candidate;
    }
}

/**
 * Represents confirmation output for a reservation request.
 */
class UC6ReservationConfirmationResult {

    private final boolean confirmed;
    private final String requestId;
    private final String guestName;
    private final String roomType;
    private final String roomId;
    private final String reason;
    private final int remainingInventory;

    private UC6ReservationConfirmationResult(boolean confirmed, String requestId, String guestName,
                                             String roomType, String roomId, String reason, int remainingInventory) {
        this.confirmed = confirmed;
        this.requestId = requestId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.reason = reason;
        this.remainingInventory = remainingInventory;
    }

    public static UC6ReservationConfirmationResult confirmed(String requestId, String guestName,
                                                             String roomType, String roomId, int remainingInventory) {
        return new UC6ReservationConfirmationResult(true, requestId, guestName, roomType, roomId, null,
                remainingInventory);
    }

    public static UC6ReservationConfirmationResult rejected(String requestId, String guestName,
                                                            String roomType, String reason) {
        return new UC6ReservationConfirmationResult(false, requestId, guestName, roomType, null, reason, -1);
    }

    @Override
    public String toString() {
        if (confirmed) {
            return "CONFIRMED: RequestId=" + requestId
                    + ", Guest=" + guestName
                    + ", RoomType=" + roomType
                    + ", RoomId=" + roomId
                    + ", Remaining=" + remainingInventory;
        }
        return "REJECTED: RequestId=" + requestId
                + ", Guest=" + guestName
                + ", RoomType=" + roomType
                + ", Reason=" + reason;
    }
}
