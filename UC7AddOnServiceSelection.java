import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use Case 7: Add-On Service Selection.
 * <p>
 * Demonstrates optional service attachment using reservation-to-services mapping
 * without modifying core booking or inventory state.
 * </p>
 *
 * @author Thamaraikannan G
 * @version 7.0
 */
public class UC7AddOnServiceSelection {

    private static final String APPLICATION_NAME = "Book My Stay App";
    private static final String APPLICATION_VERSION = "v7.0";

    public static void main(String[] args) {
        System.out.println("Welcome to " + APPLICATION_NAME + " - Use Case 7");
        System.out.println("Version: " + APPLICATION_VERSION);
        System.out.println();

        String reservationId1 = "RES-3001";
        String reservationId2 = "RES-3002";

        UC7AddOnServiceManager serviceManager = new UC7AddOnServiceManager();

        serviceManager.addService(reservationId1, new UC7AddOnService("Breakfast", 499.00));
        serviceManager.addService(reservationId1, new UC7AddOnService("Airport Pickup", 999.00));
        serviceManager.addService(reservationId1, new UC7AddOnService("Late Checkout", 299.00));

        serviceManager.addService(reservationId2, new UC7AddOnService("Spa Access", 1299.00));
        serviceManager.addService(reservationId2, new UC7AddOnService("Dinner Package", 899.00));

        printServicesForReservation(reservationId1, serviceManager);
        printServicesForReservation(reservationId2, serviceManager);

        System.out.println();
        System.out.println("Core booking and inventory state remain unchanged in this use case.");
        System.out.println("Application execution completed.");
    }

    private static void printServicesForReservation(String reservationId, UC7AddOnServiceManager serviceManager) {
        System.out.println("Reservation: " + reservationId);
        List<UC7AddOnService> services = serviceManager.getServices(reservationId);
        for (UC7AddOnService service : services) {
            System.out.println("- " + service.getServiceName() + " : " + service.getPrice());
        }
        System.out.println("Total Add-On Cost: " + serviceManager.calculateTotalAdditionalCost(reservationId));
        System.out.println("----------------------------------------");
    }
}

/**
 * Represents an optional service linked to a reservation.
 */
class UC7AddOnService {

    private final String serviceName;
    private final double price;

    public UC7AddOnService(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }
}

/**
 * Maintains reservation-to-services mapping.
 */
class UC7AddOnServiceManager {

    private final Map<String, List<UC7AddOnService>> reservationServices;

    public UC7AddOnServiceManager() {
        this.reservationServices = new HashMap<>();
    }

    public void addService(String reservationId, UC7AddOnService service) {
        reservationServices.computeIfAbsent(reservationId, key -> new ArrayList<>()).add(service);
    }

    public List<UC7AddOnService> getServices(String reservationId) {
        return new ArrayList<>(reservationServices.getOrDefault(reservationId, new ArrayList<>()));
    }

    public double calculateTotalAdditionalCost(String reservationId) {
        double total = 0.0;
        List<UC7AddOnService> services = reservationServices.getOrDefault(reservationId, new ArrayList<>());
        for (UC7AddOnService service : services) {
            total += service.getPrice();
        }
        return total;
    }
}
