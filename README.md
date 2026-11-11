# Dovewing Car Rentals: Fleet and Rental Tracker

A small Java program the Dovewing depot uses to track its fleet and its rentals. This version is a rebuild of the original single-file program. It does the same job, but it is organised so that it is safe to change, and a car can never be both available and rented.

## Running it

You need JDK 21. There are no other dependencies.

**With Maven:**

```bash
mvn compile
mvn exec:java -Dexec.mainClass=RentalTracker
```

**Or directly with the JDK (macOS, Linux or Git Bash):**

```bash
javac -d target/classes src/main/java/*.java
java -cp target/classes RentalTracker
```

The program runs a short demo: it rents two cars, returns one, tries to rent the first car a second time, and prints an end-of-day report.

**To run the tests:**

```bash
mvn test
```

## How the code is organised

All files are in `src/main/java`, one class per file.

| File | What it does |
|---|---|
| `RentalTracker` | The demo. Sets up the depot and runs a normal day. |
| `Depot` | Holds the fleet, the renters and the rental history. Handles renting and returning. |
| `Vehicle` | A car. Owns its status and refuses any change that would make it invalid. |
| `VehicleStatus` | Sealed type with two values: `Available` and `Rented` (which carries the rental). |
| `VehicleClass` | Interface for the rules of a vehicle class: daily rate and deposit. |
| `Economy`, `Premium` | The two current vehicle classes. |
| `Renter` | A customer, with a name, a type and a rental history. |
| `RenterType` | Enum: `CASUAL` or `MEMBER`. Knows whether the deposit is waived. |
| `Rental` | An immutable record of one rental. |
| `ConsolePrinter` | All the printing, kept apart from the rules. |

## Design notes

### 1. A car's status is one value

The old program kept a text status on the car and also a separate list of rentals, and nothing tied them together. That is how a car could be rented twice.

Now the status is a sealed type with exactly two options. `Available` carries nothing. `Rented` carries the rental. There is no second flag, and a rental does not have its own "active" state. The list of active rentals in the report is worked out from the cars that are currently rented, so there is only one place that holds the truth.

I used a sealed interface here because the code switches on the status and the set of states is fixed. If a third state is ever added, the compiler will point to every place that needs updating.

### 2. The car guards its own status

`Vehicle.status` is private. The only ways to change it are `markRented` and `markReturned`, and both check the current state first:

- Renting a car that is not available throws an `IllegalStateException`.
- Returning a car that is not rented throws an `IllegalStateException`.

I put the check inside `Vehicle` and not in `Depot`, so the rule holds no matter who calls it. If someone writes new code next year that rents a car, they cannot get around it.

I chose exceptions because the old program failed silently. A rejected rental is now impossible to miss, and nothing is half recorded: if the rent fails, no history entry and no rental are created.

### 3. Each vehicle class owns its own rules

`VehicleClass` is an interface with a daily rate, a standard deposit and a `deposit(renterType, days)` method. `Economy` and `Premium` implement it. The original code had the same pricing branch copied once for each class. Now `Depot.rent()` has one path and asks the vehicle's class for the numbers.

In the original, each car carried its own rate and deposit. I moved them to the class, because the business describes them as a property of the class, and a van will bring its own rules. The demo output is the same, because the Vitz is economy (150 and 500) and the Prado is premium (600 and 2000).

I kept `VehicleClass` as a normal interface, not a sealed one. Nothing in the code switches on the class, and the owner wants to add more classes, so it should stay open.

### 4. The deposit waiver belongs to the renter type

Members do not pay a deposit. That is a fact about the renter, not the car, so it lives in `RenterType`. The default `deposit` method in `VehicleClass` uses it, so the waiver is written once and not once per class. A class with different rules can override that method.

### 5. Enums and types instead of text

Renter type is now an enum and vehicle class is a type. A spelling mistake is a compile error now, where before it failed silently.

### 6. Encapsulation

All fields are private. `Depot` returns read-only views of its lists. A `Rental` points to the real car and renter instead of copying their names, and it is a record so it cannot change after it is made. Duplicate registrations are rejected when a car is added to the fleet.

### 7. Printing is separate

The old `rent()`, `returnVehicle()` and `printReport()` printed their own text. Now the rules return results, and `ConsolePrinter` prints them. The text is identical to the original.

### 8. Two small choices

- **Ids:** I removed the unused ids on `Vehicle` and `Renter` and the static counters. They were never shown or used. Rental ids now come from the size of the rental history, so a rejected rental never uses up a number.
- **Money:** it is still `double`. Switching to `BigDecimal` would change the printed output (for example `450.00` instead of `450.0`), and the brief asks for the same behaviour. See known limits below.

## Adding the commercial van

The van needs one new file and nothing else changes:

```java
public record CommercialVan() implements VehicleClass {
    public String label()           { return "VAN"; }
    public double dailyRate()       { return 400.00; }   // example numbers
    public double standardDeposit() { return 1500.00; }

    @Override
    public double deposit(RenterType renterType, int days) {
        // the van's own deposit rule goes here
        return standardDeposit();
    }
}
```

Then a van is created with `new Vehicle("Ford Transit", "GR-9999-26", new CommercialVan())`. `Depot`, `Vehicle`, `Renter` and `ConsolePrinter` stay as they are. The numbers above are examples only and would come from the owner.

**What the old code would have forced me to touch:** a new `"VAN"` text value and a third copy of the pricing branch inside `Depot.rent()`, in the same method that handles economy and premium. If I missed any place that depends on the category, the van would get a free rental on a car that still showed as available, and every edit risked breaking economy and premium again.

## What changed from my original sketch

My sketch showed `Rented` carrying the renter. While building I made it carry the whole rental instead. That way the rental itself is the single record of who has the car, for how long and at what price, and the "active rentals" list can be taken straight from the cars with no separate state to keep in sync.

I also added `ConsolePrinter`, which was not in the first sketch. It came from the problem of rules and printing being mixed together (diagnosis 3.3).

## Behaviour compared with the original

The output of the original program is in [`docs/initial-output.txt`](docs/initial-output.txt). The output of the rebuilt program is in [`docs/final-output.txt`](docs/final-output.txt).

Every line is the same except for what follows from the one intended change: the second rental of the Vitz (to Ama, while it is out with Kofi) is now rejected. In the new output that shows as a "Could not rent" line, Ama's history has no entry for it, and the report shows one active rental instead of two.

## Tests

The tests use JUnit 5 and live in `src/test/java`. Run them with `mvn test`, or from IntelliJ by right-clicking the `src/test/java` folder.

| Test file | What it checks |
|---|---|
| `VehicleTest` | **The status rule.** A new car is available. Renting makes it rented. A rented car rejects a second rental and keeps the first one. Returning makes it available again. Returning a car that is not out is rejected. |
| `DepotTest` | **Pricing and deposits.** A casual renter pays the cost and the deposit (Vitz, 3 days: GHS 450 and GHS 500). A member's deposit is waived (Prado, 2 days: GHS 1200 and GHS 0). |
| `DepotTest` | **Rejected rentals leave no trace.** After a rejected rental there is no new history entry, no extra active rental and no entry in the renter's history. A car can be rented again after it is returned, returning one car does not close another car's rental, and a duplicate registration is rejected. |
| `DepotTest` | **The van plug-in test.** It creates a new vehicle class with its own deposit rule (no member waiver), adds a car of that class and rents it. Nothing else in the code changes, which is how the real commercial van will be added. |
| `RentalTrackerOutputTest` | **The whole demo output.** It runs the program and compares the printed text with the expected text, so any change in behaviour makes it fail. |

There are 15 tests in total. When I first ran the output test it failed because I had written the "Could not rent" message with extra spaces and a full stop. I fixed the message in `Vehicle`, so the test did its job.

## The extra improvement

`Depot.rent()` now rejects a number of days that is zero or negative. The original accepted it and produced a zero or negative cost. This is the one other place where behaviour differs, and only for invalid input.

## Known limits

- **Money is a `double`.** This is fine for the current numbers, but it could cause rounding problems if discounts or tax are added. The next step would be `BigDecimal` with an agreed output format.
- **Test coverage is focused, not complete.** The tests cover the status rule, pricing, deposits, rejected rentals and the van plug-in. They do not yet cover every input check, such as null values, or the `Renter` history list on its own.
- **Default package.** I kept every class in the default package so the run command stays the same as the original.

## Project documents

- [`DIAGNOSIS.md`](DIAGNOSIS.md): the written diagnosis and the planned structure, committed before any code changed.
- [`docs/`](docs/): the sketches and the before and after program output.