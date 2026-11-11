package com.dovewing.rentals.pricing;

/**
 * The rules for one class of vehicle (economy, premium, and later vans).
 * Each class decides its own daily rate and deposit. To add a new class,
 * write a new implementation of this interface. Nothing else needs to change.
 */

public interface VehicleClass {

    /** Name shown in reports, eg "ECONOMY" */
    String label();

    /** Price per day in GHS */
    double dailyRate();

    /** Deposit a casual renter pays in GHS */
    double standardDeposit();

    /**
     * Deposit for this renter. By default members pay nothing and everyone
     * else pays the standard deposit. A class with different rules
     * (for example a van) can override this method.
     * The days value is not used by the default rule, but is passed in
     * so an overriding rule can use it.
     */

    default double deposit(RenterType renterType, int days) {
        return renterType.waivesDeposit() ? 0 : standardDeposit();
    }
}
