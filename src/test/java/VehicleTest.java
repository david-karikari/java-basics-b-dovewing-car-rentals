import com.dovewing.rentals.domain.Rental;
import com.dovewing.rentals.domain.Renter;
import com.dovewing.rentals.domain.Vehicle;
import com.dovewing.rentals.pricing.Economy;
import com.dovewing.rentals.pricing.RenterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {
    private Vehicle car;
    private Rental rental;

    @BeforeEach
    void setUp() {
        car = new Vehicle("Toyota Vitz", "GT-1234-20", new Economy());
        rental = new Rental(1, new Renter("Kofi", RenterType.CASUAL), car, 1, 150.0, 500.0);
    }

    @Test
    void newCarIsAvailable() {
        assertTrue(car.isAvailable());
        assertEquals("AVAILABLE", car.statusLabel());
    }

    @Test
    void rentingMakesTheCarRented() {
        car.markRented(rental);
        assertFalse(car.isAvailable());
        assertEquals("RENTED", car.statusLabel());
    }

    // The key business rule: a rented car cannot be rented again.
    @Test
    void rentedCarRejectsASecondRentalAndKeepsTheFirst() {
        car.markRented(rental);
        Rental other = new Rental(2, new Renter("Ama", RenterType.MEMBER), car, 2, 300.0, 0.0);
        assertThrows(IllegalStateException.class, () -> car.markRented(other));
        assertSame(rental, car.markReturned());   // still holds the first rental
    }

    @Test
    void returningMakesTheCarAvailableAgain() {
        car.markRented(rental);
        assertSame(rental, car.markReturned());
        assertTrue(car.isAvailable());
    }

    @Test
    void returningACarThatIsNotOutIsRejected() {
        assertThrows(IllegalStateException.class, () -> car.markReturned());
    }
}