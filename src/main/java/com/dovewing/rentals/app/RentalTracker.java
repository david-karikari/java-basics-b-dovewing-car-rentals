package com.dovewing.rentals.app;

import com.dovewing.rentals.domain.Depot;
import com.dovewing.rentals.domain.Renter;
import com.dovewing.rentals.domain.Vehicle;
import com.dovewing.rentals.pricing.Economy;
import com.dovewing.rentals.pricing.Premium;
import com.dovewing.rentals.pricing.RenterType;

/** The demo: a normal day, plus the double rental the old program allowed. */

public class RentalTracker {

    public static void main(String[] args) {
        Depot d = new Depot("Dovewing Car Rentals");
        ConsolePrinter out = new ConsolePrinter();

        Vehicle vitz = new Vehicle("Toyota Vitz", "GT-1234-20", new Economy());
        Vehicle prado = new Vehicle("Toyota Prado", "GR-5678-22", new Premium());
        d.addVehicle(vitz);
        d.addVehicle(prado);

        Renter kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        Renter ama = new Renter("Ama Owusu", RenterType.MEMBER);
        d.addRenter(kofi);
        d.addRenter(ama);

        // a normal day
        out.rented(d.rent(kofi, vitz, 3));
        out.rented(d.rent(ama, prado, 2));
        out.returned(d.returnVehicle(prado));

        // The Vitz is already out with Kofi, so this is now rejected.
        try {
            out.rented(d.rent(ama, vitz, 2));
        } catch (IllegalStateException e) {
            System.out.println("Could not rent: " + e.getMessage());
        }

        out.report(d);
    }
}