/**
 * The status of a vehicle. It is always exactly one of these two values,
 * so a car can never be both available and rented.
 * "sealed" means no other status can be added without changing this file,
 * and the compiler will warn about any code that forgets to handle a new one.
 */

public sealed interface VehicleStatus {

    /** This shows that a car is available and can be rented */
    record Available() implements VehicleStatus {}

    /** This shows that a car is out. It carries the rental, so Dovewing cars know who has it */
    record Rented(Rental rental) implements VehicleStatus {}
}
