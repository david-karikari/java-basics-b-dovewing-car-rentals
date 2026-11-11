/** All printing lives here, so the rules can be used without a console. */

public class ConsolePrinter {

    public void rented(Rental r) {
        System.out.println("Rented " + r.vehicle().model() + " (" + r.vehicle().reg() + ") to "
                + r.renter().name() + " for " + r.days() + " days, cost GHS " + r.cost()
                + ", deposit GHS " + r.deposit());
    }

    public void returned(Rental r) {
        System.out.println("Returned " + r.vehicle().model() + " (" + r.vehicle().reg() + ")");
    }
    public void report(Depot depot) {
        System.out.println();
        System.out.println("=== " + depot.name() + " - end of day ===");
        System.out.println("Fleet status:");
        for (Vehicle v : depot.vehicles()) {
            System.out.println("  " + v.model() + " (" + v.reg() + ", "
                    + v.vehicleClass().label() + "): " + v.statusLabel());
        }
        System.out.println("Active rentals:");
        for (Rental r : depot.activeRentals()) {
            System.out.println("  Rental #" + r.id() + ": " + r.vehicle().reg() + " -> "
                    + r.renter().name() + " (" + r.days() + " days)");
        }
    }
}
