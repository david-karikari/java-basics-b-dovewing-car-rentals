/** This is for everyday cars. It has Chepaer rates and smaller deposits */

public record Economy() implements VehicleClass {
    public String label() {
        return "ECONOMY";
    }

    /** Returns GHS 150.00, that should be paid daily by the renter for economy cars */
    public double dailyRate() {
        return 150.00;
    }

    /** This is the initial deposit on economy cars by casual renters */
    public double standardDeposit() {
        return 500.00;
    }
}
