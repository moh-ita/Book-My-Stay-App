import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Use Case 11: Concurrent Booking Simulation (Thread Safety).
 * <p>
 * Demonstrates multi-threaded booking processing with synchronized critical
 * sections to prevent race conditions and double allocation.
 * </p>
 *
 * @author Thamaraikannan G
 * @version 11.0
 */
public class UC11ConcurrentBookingSimulation {

    private static final String APPLICATION_NAME = "Book My Stay App";
    private static final String APPLICATION_VERSION = "v11.0";

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Welcome to " + APPLICATION_NAME + " - Use Case 11");
        System.out.println("Version: " + APPLICATION_VERSION);
        System.out.println();

        Queue<UC11BookingRequest> sharedQueue = new ArrayDeque<>();
        sharedQueue.offer(new UC11BookingRequest("REQ-11001", "Asha", "Single Room"));
        sharedQueue.offer(new UC11BookingRequest("REQ-11002", "Banu", "Single Room"));
        sharedQueue.offer(new UC11BookingRequest("REQ-11003", "Chetan", "Double Room"));
        sharedQueue.offer(new UC11BookingRequest("REQ-11004", "Dinesh", "Suite Room"));
        sharedQueue.offer(new UC11BookingRequest("REQ-11005", "Esha", "Suite Room"));
        sharedQueue.offer(new UC11BookingRequest("REQ-11006", "Farhan", "Double Room"));

        Map<String, Integer> initialInventory = new HashMap<>();
        initialInventory.put("Single Room", 1);
        initialInventory.put("Double Room", 1);
        initialInventory.put("Suite Room", 1);

        UC11ConcurrentBookingProcessor processor = new UC11ConcurrentBookingProcessor(sharedQueue, initialInventory);
        List<String> resultLog = Collections.synchronizedList(new ArrayList<>());

        Runnable worker = () -> {
            while (true) {
                UC11AllocationResult result = processor.processNext();
                if (result == null) {
                    break;
                }
                resultLog.add(Thread.currentThread().getName() + " -> " + result);
            }
        };

        Thread t1 = new Thread(worker, "Worker-1");
        Thread t2 = new Thread(worker, "Worker-2");
        Thread t3 = new Thread(worker, "Worker-3");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("Concurrent Processing Results:");
        for (String log : resultLog) {
            System.out.println(log);
        }

        System.out.println();
        System.out.println("Allocated Room IDs: " + processor.getAllocatedRoomIdsSnapshot());
        System.out.println("Final Inventory: " + processor.getInventorySnapshot());
        System.out.println("Consistent state maintained without double allocation.");
        System.out.println("Application execution completed.");
    }
}

/**
 * Represents a booking request submitted by a guest.
 */
class UC11BookingRequest {

    private final String requestId;
    private final String guestName;
    private final String roomType;

    public UC11BookingRequest(String requestId, String guestName, String roomType) {
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
 * Allocation outcome for a request.
 */
class UC11AllocationResult {

    private final boolean confirmed;
    private final String requestId;
    private final String guestName;
    private final String roomType;
    private final String roomId;
    private final String reason;

    private UC11AllocationResult(boolean confirmed, String requestId, String guestName,
                                 String roomType, String roomId, String reason) {
        this.confirmed = confirmed;
        this.requestId = requestId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.reason = reason;
    }

    public static UC11AllocationResult confirmed(String requestId, String guestName,
                                                 String roomType, String roomId) {
        return new UC11AllocationResult(true, requestId, guestName, roomType, roomId, null);
    }

    public static UC11AllocationResult rejected(String requestId, String guestName,
                                                String roomType, String reason) {
        return new UC11AllocationResult(false, requestId, guestName, roomType, null, reason);
    }

    @Override
    public String toString() {
        if (confirmed) {
            return "CONFIRMED: RequestId=" + requestId
                    + ", Guest=" + guestName
                    + ", RoomType=" + roomType
                    + ", RoomId=" + roomId;
        }
        return "REJECTED: RequestId=" + requestId
                + ", Guest=" + guestName
                + ", RoomType=" + roomType
                + ", Reason=" + reason;
    }
}

/**
 * Processes shared queue and inventory in synchronized critical sections.
 */
class UC11ConcurrentBookingProcessor {

    private final Queue<UC11BookingRequest> requestQueue;
    private final Map<String, Integer> inventory;
    private final Set<String> allocatedRoomIds;
    private int sequence;

    public UC11ConcurrentBookingProcessor(Queue<UC11BookingRequest> requestQueue,
                                          Map<String, Integer> initialInventory) {
        this.requestQueue = requestQueue;
        this.inventory = new HashMap<>(initialInventory);
        this.allocatedRoomIds = new HashSet<>();
        this.sequence = 1;
    }

    public synchronized UC11AllocationResult processNext() {
        UC11BookingRequest request = requestQueue.poll();
        if (request == null) {
            return null;
        }

        int available = inventory.getOrDefault(request.getRoomType(), 0);
        if (available <= 0) {
            return UC11AllocationResult.rejected(
                    request.getRequestId(), request.getGuestName(), request.getRoomType(), "Insufficient inventory");
        }

        String roomId = generateUniqueRoomId(request.getRoomType());
        inventory.put(request.getRoomType(), available - 1);
        return UC11AllocationResult.confirmed(
                request.getRequestId(), request.getGuestName(), request.getRoomType(), roomId);
    }

    public synchronized Set<String> getAllocatedRoomIdsSnapshot() {
        return new HashSet<>(allocatedRoomIds);
    }

    public synchronized Map<String, Integer> getInventorySnapshot() {
        return new HashMap<>(inventory);
    }

    private String generateUniqueRoomId(String roomType) {
        String prefix = roomType.replace(" ", "").substring(0, 2).toUpperCase();
        String candidate;
        do {
            candidate = prefix + "-" + String.format("%03d", sequence++);
        } while (allocatedRoomIds.contains(candidate));
        allocatedRoomIds.add(candidate);
        return candidate;
    }
}
