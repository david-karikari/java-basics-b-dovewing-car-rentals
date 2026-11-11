import java.util.*;

/** This is the customer class defined as Renter */
public final class Renter {
    private final String name;

    /** This type determines whether the deposit is waived or not */
    private final RenterType type;

    private final List<String> history = new ArrayList<>();



    public Renter(String name, RenterType type) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }

    public String name() {
        return name;
    }

    public RenterType type() {
        return type;
    }

    /** This returns an unmodifiable list which sets the history list to read only so that only verified rental records can be added to the history */
    public List<String> history() {
        return Collections.unmodifiableList(history);
    }

    public void recordRental(String model) {
        history.add("Rented " + model);
    }

}
