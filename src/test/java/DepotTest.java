import com.dovewing.rentals.domain.Depot;
import com.dovewing.rentals.domain.Rental;
import com.dovewing.rentals.domain.Renter;
import com.dovewing.rentals.domain.Vehicle;
import com.dovewing.rentals.pricing.Economy;
import com.dovewing.rentals.pricing.Premium;
import com.dovewing.rentals.pricing.RenterType;
import com.dovewing.rentals.pricing.VehicleClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DepotTest {
    private Depot depot;
    private Vehicle vitz;    // economy
    private Vehicle prado;   // premium
    private Renter kofi;     // casual
    private Renter ama;      // member

    @BeforeEach
    void setUp() {
        depot = new Depot("Test com.dovewing.rentals.domain.Depot");
        vitz = new Vehicle("Toyota Vitz", "GT-1234-20", new Economy());
        prado = new Vehicle("Toyota Prado", "GR-5678-22", new Premium());
        kofi = new Renter("Kofi Mensah", RenterType.CASUAL);
        ama = new Renter("Ama Owusu", RenterType.MEMBER);
        depot.addVehicle(vitz);
        depot.addVehicle(prado);
        depot.addRenter(kofi);
        depot.addRenter(ama);
    }

    @Test
    void casualRenterPaysCostAndDeposit() {
        Rental r = depot.rent(kofi, vitz, 3);
        assertEquals(450.0, r.cost(), 0.001);
        assertEquals(500.0, r.deposit(), 0.001);
    }

    @Test
    void memberHasTheDepositWaived() {
        Rental r = depot.rent(ama, prado, 2);
        assertEquals(1200.0, r.cost(), 0.001);
        assertEquals(0.0, r.deposit(), 0.001);
    }

    @Test
    void rentedCarCannotBeRentedAgain() {
        depot.rent(kofi, vitz, 3);
        assertThrows(IllegalStateException.class, () -> depot.rent(ama, vitz, 2));
    }

    // A rejected rental must leave nothing behind.
    @Test
    void rejectedRentalIsNotRecordedAnywhere() {
        depot.rent(kofi, vitz, 3);
        assertThrows(IllegalStateException.class, () -> depot.rent(ama, vitz, 2));
        assertEquals(1, depot.rentalHistory().size());
        assertEquals(1, depot.activeRentals().size());
        assertTrue(ama.history().isEmpty());
    }

    @Test
    void carCanBeRentedAgainAfterItIsReturned() {
        depot.rent(kofi, vitz, 1);
        depot.returnVehicle(vitz);
        Rental second = depot.rent(ama, vitz, 2);
        assertEquals(2, second.id());
        assertFalse(vitz.isAvailable());
    }

    @Test
    void returningACarThatIsNotOutIsRejected() {
        assertThrows(IllegalStateException.class, () -> depot.returnVehicle(vitz));
    }

    @Test
    void returnClosesOnlyThatCarsRental() {
        depot.rent(kofi, vitz, 3);
        depot.rent(ama, prado, 2);
        depot.returnVehicle(prado);
        List<Rental> active = depot.activeRentals();
        assertEquals(1, active.size());
        assertEquals("GT-1234-20", active.get(0).vehicle().reg());
    }

    @Test
    void duplicateRegistrationIsRejected() {
        Vehicle copy = new Vehicle("Other", "GT-1234-20", new Economy());
        assertThrows(IllegalArgumentException.class, () -> depot.addVehicle(copy));
    }

    // Proves the extension story: a new class needs no changes to other code.
    @Test
    void newVehicleClassPlugsInWithoutChangingAnythingElse() {
        VehicleClass van = new VehicleClass() {
            public String label()           { return "VAN"; }
            public double dailyRate()       { return 400.0; }
            public double standardDeposit() { return 1500.0; }
            @Override
            public double deposit(RenterType type, int days) {
                return standardDeposit();   // this van has no member waiver
            }
        };
        Vehicle transit = new Vehicle("Ford Transit", "GR-9999-26", van);
        depot.addVehicle(transit);

        Rental r = depot.rent(ama, transit, 2);   // Ama is a member
        assertEquals(800.0, r.cost(), 0.001);
        assertEquals(1500.0, r.deposit(), 0.001);
    }
}