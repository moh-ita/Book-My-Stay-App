import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Use Case 5: Booking Request (First-Come-First-Served).
 * <p>
 * Demonstrates fair request intake using a queue that preserves arrival order.
 * This use case only captures booking intent and does not perform room allocation
 * or inventory mutation.
 * </p>
 *
 * @author Thamaraikannan G
 * @version 5.0
 */
public class UC5BookingRequestQueue {

    private static final String APPLICATION_NAME = "Book My Stay App";
    private static final String APPLICATION_VERSION = "v5.0";

    public static void main(String[] args) {
        System.out.println("Welcome to " + APPLICATION_NAME + " - Use Case 5");
        System.out.println("Version: " + APPLICATION_VERSION);
        System.out.println();

        UC5RequestQueue requestQueue = new UC5RequestQueue();

        requestQueue.submitRequest(new UC5Reservation("REQ-1001", "Arun", "Single Room", 2));
        requestQueue.submitRequest(new UC5Reservation("REQ-1002", "Meena", "Suite Room", 1));
        requestQueue.submitRequest(new UC5Reservation("REQ-1003", "Rahul", "Double Room", 3));
        requestQueue.submitRequest(new UC5Reservation("REQ-1004", "Divya", "Single Room", 1));

        System.out.println("Queued Booking Requests (Arrival Order / FIFO):");
        for (UC5Reservation reservation : requestQueue.getQueuedRequests()) {
            System.out.println(reservation);
        }

        System.out.println();
        System.out.println("Next Request Ready for Allocation: " + requestQueue.peekNextRequest().getRequestId());
        System.out.println("No room allocation or inventory updates are performed in this use case.");
        System.out.println();
        System.out.println("Application execution completed.");
    }
}

/**
 * Represents a guest booking request.
 */
class UC5Reservation {

    private final String requestId;
    private final String guestName;
    private final String roomType;
    private final int nights;

    public UC5Reservation(String requestId, String guestName, String roomType, int nights) {
        this.requestId = requestId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
    }

    public String getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return "RequestId=" + requestId
                + ", Guest=" + guestName
                + ", RoomType=" + roomType
                + ", Nights=" + nights;
    }
}

/**
 * Queue component that captures booking requests in arrival order.
 */
class UC5RequestQueue {

    private final Queue<UC5Reservation> pendingRequests;

    public UC5RequestQueue() {
        this.pendingRequests = new LinkedList<>();
    }

    public void submitRequest(UC5Reservation reservation) {
        pendingRequests.offer(reservation);
    }

    public UC5Reservation peekNextRequest() {
        UC5Reservation next = pendingRequests.peek();
        if (next == null) {
            throw new IllegalStateException("No booking requests in queue.");
        }
        return next;
    }

    public List<UC5Reservation> getQueuedRequests() {
        return new ArrayList<>(pendingRequests);
    }
}
