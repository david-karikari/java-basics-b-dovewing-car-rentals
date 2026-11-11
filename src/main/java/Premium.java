/** This is for premium and luxurious cars. It has ratively expensive rates and larger deposits */

public record Premium() implements VehicleClass {
    public String label() {
        return "PREMIUM";
    }



    /** This is the daily rate for renter going for Premium cars */
    public double dailyRate() {
        return 600.00;
    }



    /** This is the deposit made by casual renters who are going for premium cars */
    public double standardDeposit() {
        return 2000.00;
    }
}
