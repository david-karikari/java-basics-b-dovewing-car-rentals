import java.util.Objects;

/**
 * A car in the fleet. It owns its status and refuses any change
 * that would make it invalid, so it is never both available and rented.
 */

public final class Vehicle {
    private final String model;
    private final String reg;
    private final VehicleClass vehicleClass; /** This decides the rate and deposit */
    private VehicleStatus status = new VehicleStatus.Available();


  public Vehicle (String model, String reg, VehicleClass vehicleClass) {
      this.model = Objects.requireNonNull(model);
      this.reg = Objects.requireNonNull(reg);
      this.vehicleClass = Objects.requireNonNull(vehicleClass);
  }

  /** These methods are safe to share since values cannot be altered from the outside */
  public String model() {
      return model;
  }

  public String reg() {
      return reg;
  }

  public VehicleClass vehicleClass() {
      return vehicleClass;
  }

  public VehicleStatus status() {
      return status;
  }

  public boolean isAvailable() {
      return status instanceof VehicleStatus.Available;
  }

    /** The only way to rent this car. Rejects it if it is already out. */
  public void markRented(Rental rental) {
      if(!isAvailable()) {
          throw new IllegalStateException(model + " ( " + reg + " ) is already rented." );
      }

      status = new VehicleStatus.Rented(rental);
  }


    /** The only way to return this car. Rejects it if it is not out. */
  public Rental markReturned() {
      if (status instanceof VehicleStatus.Rented(Rental rental)) {
          status = new VehicleStatus.Available();
          return rental;
      }

      throw new IllegalStateException(model + " ( " + reg + " ) is not rented.");
  }

    /** Text for reports. No default branch, so a new status forces an update here. */
  public String statusLabel() {
      return switch (status) {
          case VehicleStatus.Available a -> "AVAILABLE";
          case VehicleStatus.Rented r -> "RENTED";
      };
  }
}
