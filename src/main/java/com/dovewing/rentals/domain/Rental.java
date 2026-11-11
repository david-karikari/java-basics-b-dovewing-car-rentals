package com.dovewing.rentals.domain;

/**
 * One rental. A record, so it cannot change after it is made.
 * It points to the real renter and car, not copies of their names.
 */
public record Rental (int id, Renter renter, Vehicle vehicle, int days, double cost, double deposit) {}
