import java.util.*;

class Depot {
    private final String name;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Renter> renters = new ArrayList<>();
    private final List<Rental> history = new ArrayList<>();

    public Depot(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String name() {
        return name;
    }

    public List<Renter> renters() { return Collections.unmodifiableList(renters); }

    /** These are read-only and hence cannot be edited outside this class by any other code */
    public List<Vehicle> vehicles() {
        return Collections.unmodifiableList(vehicles);
    }
    public List<Rental> rentalHistory() {
        return Collections.unmodifiableList(history);
    }

    public void addVehicle(Vehicle v) {
        Objects.requireNonNull(v);

        for (Vehicle existing : vehicles) {
            if (existing.reg().equals(v.reg())) {
                throw new IllegalArgumentException("Duplicate registration numbers. Vehicle with registration ( " + v.reg() + " ) already exists.");
            }
        }

        vehicles.add(v);
    }

    public void addRenter(Renter r) {
        renters.add(Objects.requireNonNull(r));
    }

    /** Rent a vehicle to a renter for a number of days. The rules depend on the class of vehicle. */
    public Rental rent(Renter renter, Vehicle vehicle, int days) {
       Objects.requireNonNull(renter);
       Objects.requireNonNull(vehicle);

        /** The vehicle class supplies the price and deposit rules. */
       VehicleClass vc = vehicle.vehicleClass();
       double cost = vc.dailyRate() * days;
       double deposit = vc.deposit(renter.type(), days);
       Rental rental = new Rental(history.size() + 1, renter, vehicle, days, cost, deposit);

        vehicle.markRented(rental); // throws an exception if the car is not available

        renter.recordRental(vehicle.model());
       history.add(rental); // only reached if the rent succeeded

       return rental;
    }

    /** Returns a car. Throws if the car is not out. */
    public Rental returnVehicle(Vehicle vehicle) {
        return vehicle.markReturned();
    }


    /** Rentals currently out, taken from the cars' status. */
    public List<Rental> activeRentals() {
       List<Rental> active = new ArrayList<>();

       for (Vehicle v : vehicles) {
           if (v.status() instanceof VehicleStatus.Rented(Rental rental)) {
               active.add(rental);
           }
       }
       active.sort(Comparator.comparingInt(Rental::id)); // same order as before

       return active;
    }
}
