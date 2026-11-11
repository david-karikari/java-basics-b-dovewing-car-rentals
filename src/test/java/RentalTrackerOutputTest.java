import com.dovewing.rentals.app.RentalTracker;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RentalTrackerOutputTest {

    @Test
    void demoOutputMatchesTheExpectedText() {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            RentalTracker.main(new String[0]);
        } finally {
            System.setOut(original);   // always put the real console back
        }

        String actual = buffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        String expected = String.join("\n",
                "Rented Toyota Vitz (GT-1234-20) to Kofi Mensah for 3 days, cost GHS 450.0, deposit GHS 500.0",
                "Rented Toyota Prado (GR-5678-22) to Ama Owusu for 2 days, cost GHS 1200.0, deposit GHS 0.0",
                "Returned Toyota Prado (GR-5678-22)",
                "Could not rent: Toyota Vitz (GT-1234-20) is already rented",
                "",
                "=== Dovewing Car Rentals - end of day ===",
                "Fleet status:",
                "  Toyota Vitz (GT-1234-20, ECONOMY): RENTED",
                "  Toyota Prado (GR-5678-22, PREMIUM): AVAILABLE",
                "Active rentals:",
                "  com.dovewing.rentals.domain.Rental #1: GT-1234-20 -> Kofi Mensah (3 days)",
                "");
        assertEquals(expected, actual);
    }
}