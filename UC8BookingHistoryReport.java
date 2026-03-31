import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Use Case 8: Booking History and Reporting.
 * <p>
 * Demonstrates historical tracking of confirmed bookings using ordered storage
 * and report generation without mutating booking history.
 * </p>
 *
 * @author Thamaraikannan G
 * @version 8.0
 */
public class UC8BookingHistoryReport {

    private static final String APPLICATION_NAME = "Book My Stay App";
    private static final String APPLICATION_VERSION = "v8.0";

    public static void main(String[] args) {
        System.out.println("Welcome to " + APPLICATION_NAME + " - Use Case 8");
        System.out.println("Version: " + APPLICATION_VERSION);
        System.out.println();

        UC8BookingHistory bookingHistory = new UC8BookingHistory();

        bookingHistory.addConfirmedReservation(new UC8Reservation("RES-8001", "Nila", "Single Room", "SI-101", 2499.0));
        bookingHistory.addConfirmedReservation(new UC8Reservation("RES-8002", "Arjun", "Double Room", "DO-201", 3999.0));
        bookingHistory.addConfirmedReservation(new UC8Reservation("RES-8003", "Kavin", "Suite Room", "SU-301", 6999.0));
        bookingHistory.addConfirmedReservation(new UC8Reservation("RES-8004", "Meera", "Single Room", "SI-102", 2499.0));

        System.out.println("Booking History (Chronological / Insertion Order):");
        for (UC8Reservation reservation : bookingHistory.getAllReservations()) {
            System.out.println(reservation);
        }

        System.out.println();
        UC8BookingReportService reportService = new UC8BookingReportService();
        UC8BookingSummaryReport summaryReport = reportService.generateSummary(bookingHistory.getAllReservations());

        System.out.println("Booking Summary Report:");
        System.out.println("Total Confirmed Bookings: " + summaryReport.getTotalBookings());
        System.out.println("Total Revenue: " + summaryReport.getTotalRevenue());
        System.out.println("Room Type Distribution: " + summaryReport.getBookingsByRoomType());

        System.out.println();
        System.out.println("Reporting completed without modifying stored booking history.");
        System.out.println("Application execution completed.");
    }
}

/**
 * Represents a confirmed reservation entry.
 */
class UC8Reservation {

    private final String reservationId;
    private final String guestName;
    private final String roomType;
    private final String roomId;
    private final double amount;

    public UC8Reservation(String reservationId, String guestName, String roomType, String roomId, double amount) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.amount = amount;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "ReservationId=" + reservationId
                + ", Guest=" + guestName
                + ", RoomType=" + roomType
                + ", RoomId=" + roomId
                + ", Amount=" + amount;
    }
}

/**
 * Stores booking history as an ordered list.
 */
class UC8BookingHistory {

    private final List<UC8Reservation> confirmedReservations;

    public UC8BookingHistory() {
        this.confirmedReservations = new ArrayList<>();
    }

    public void addConfirmedReservation(UC8Reservation reservation) {
        confirmedReservations.add(reservation);
    }

    public List<UC8Reservation> getAllReservations() {
        return new ArrayList<>(confirmedReservations);
    }
}

/**
 * Generates reports from booking history data.
 */
class UC8BookingReportService {

    public UC8BookingSummaryReport generateSummary(List<UC8Reservation> reservations) {
        int totalBookings = reservations.size();
        double totalRevenue = 0.0;
        Map<String, Integer> bookingsByRoomType = new LinkedHashMap<>();

        for (UC8Reservation reservation : reservations) {
            totalRevenue += reservation.getAmount();
            bookingsByRoomType.put(
                    reservation.getRoomType(),
                    bookingsByRoomType.getOrDefault(reservation.getRoomType(), 0) + 1);
        }

        return new UC8BookingSummaryReport(totalBookings, totalRevenue, bookingsByRoomType);
    }
}

/**
 * Represents summary report output.
 */
class UC8BookingSummaryReport {

    private final int totalBookings;
    private final double totalRevenue;
    private final Map<String, Integer> bookingsByRoomType;

    public UC8BookingSummaryReport(int totalBookings, double totalRevenue, Map<String, Integer> bookingsByRoomType) {
        this.totalBookings = totalBookings;
        this.totalRevenue = totalRevenue;
        this.bookingsByRoomType = new LinkedHashMap<>(bookingsByRoomType);
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public Map<String, Integer> getBookingsByRoomType() {
        return new LinkedHashMap<>(bookingsByRoomType);
    }
}
