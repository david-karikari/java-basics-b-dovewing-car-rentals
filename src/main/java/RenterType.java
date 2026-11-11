/**
 * The kinds of renter Dovewing has.
 * Each type knows whether its deposit is waived, so the rule lives
 * with the renter and not inside the vehicle code.
 */


public enum RenterType {
    CASUAL(false),
//    Members have their deposit waived
    MEMBER(true);

    private final boolean waivesDeposit;

    RenterType(boolean waivesDeposit) { this.waivesDeposit = waivesDeposit; }

    public boolean waivesDeposit() { return waivesDeposit; }
}