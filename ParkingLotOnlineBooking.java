import java.util.*;

/*
Parking Space
    Gate
    Set<Spots>

Spot
Gate
    Set<Spots> - in ascending order of there distance from gate
Booking Service
    book the spot, check there availablilty
Receipt
*/

enum SpotTypes {
    SMALL,
    MEDIUM,
    LARGE
}

abstract class Spot {
    SpotTypes type;
    String id;
    String location;
    String spaceId;

    Spot(SpotTypes type, String id, String location, String spaceId) {
        this.type = type;
        this.id = id;
        this.location = location;
        this.spaceId = spaceId;
    }

    public abstract String getId();
    public abstract String getSpaceId();
    public abstract SpotTypes getType();
}

class SpotSmall extends Spot {
    SpotSmall(String id, String location, String spaceId) {
        super(SpotTypes.SMALL, id, location, spaceId);
    }

    public String getId() {
        return this.id;
    }

    public String getSpaceId() {
        return this.spaceId;
    }

    public SpotTypes getType() {
        return this.type;
    }
}

class SpotMedium extends Spot {
    SpotMedium(String id, String location, String spaceId) {
        super(SpotTypes.MEDIUM, id, location, spaceId);
    }

    public String getId() {
        return this.id;
    }
    public String getSpaceId() {
        return this.spaceId;
    }

    public SpotTypes getType() {
        return this.type;
    }
}

class SpotLarge extends Spot {
    SpotLarge(String id, String location, String spaceId) {
        super(SpotTypes.LARGE, id, location, spaceId);
    }

    public String getId() {
        return this.id;
    }
    public String getSpaceId() {
        return this.spaceId;
    }

    public SpotTypes getType() {
        return this.type;
    }
}


class ParkingSpace {
    String id;
    List<Spot> spots;
    Booking booking;

    ParkingSpace(String id, List<Spot> spots) {
        booking = new Booking();
        this.spots = new ArrayList<Spot>();
        this.id = id;
        for(Spot s : spots) {
            this.spots.add(s);
        }
    }

    public List<Spot> getAllSpots() {
        return new ArrayList<Spot>(spots);
    }

    public Receipt book(Spot spot, int startTimestamp, int endTimestamp) {
        return booking.bookSpot(spot, startTimestamp, endTimestamp);
    }

    public List<Spot> getAvailableSpots(int startTimestamp, int endTimestamp, SpotTypes type) {
        List<Spot> bookedSpots = booking.getBookedSpots(startTimestamp, endTimestamp, type);
        List<Spot> allSpots = new ArrayList<Spot>(spots);
        allSpots.removeAll(bookedSpots);

        return allSpots;
    }
 
}


class Receipt {
    String receiptId;
    String spotId;
    String spaceId;
    int startTimestamp;
    int endTimestamp;
    double price;
    boolean paid;

    Receipt(String receiptId, String spotId, String spaceId, int startTimestamp, int endTimestamp) {
        this.receiptId = receiptId;
        this.spaceId = spaceId;
        this.spotId = spotId;
        this.endTimestamp = endTimestamp;
        this.startTimestamp = startTimestamp;
    }

    public String getSpotId() {
        return this.spotId;
    }

    public int[] getTiming() {
        return new int[]{startTimestamp, endTimestamp};
    }

    public void setBill(double price, boolean paid) {
        this.price = price;
        this.paid = paid;
    }

}

class Booking {
    Map<Spot, List<Receipt>> bookedSpots;

    Booking() {
        bookedSpots = new HashMap<Spot, List<Receipt>>();
    }

    public Receipt bookSpot(Spot spot, int startTimestamp, int endTimestamp) {
        if(!isAvailable(spot, startTimestamp, endTimestamp)) return null;

        Receipt receipt = new Receipt(UUID.randomUUID().toString(), spot.getId(), spot.getSpaceId(), startTimestamp, endTimestamp);
        if(BillingService.GetInstance().processBill(receipt)) {
            if(!bookedSpots.containsKey(spot)) bookedSpots.put(spot, new ArrayList<Receipt>());
            bookedSpots.get(spot).add(receipt);
        } else {
            return null;
        }

        return receipt;
    }

    private boolean isAvailable(Spot spot, int startTimestamp, int endTimestamp) {
        if(!bookedSpots.containsKey(spot.getId())) return true;

        List<Receipt> receipts = bookedSpots.get(spot.getId());

        for(Receipt r : receipts) {
            int[] bookingTiming = r.getTiming();
            if((startTimestamp >= bookingTiming[0] && startTimestamp <= bookingTiming[1]) || (endTimestamp >= bookingTiming[0] && endTimestamp <= bookingTiming[1])  || (startTimestamp <= bookingTiming[0] && endTimestamp >= bookingTiming[1])) return false;
        }

        return true;
    }

    public List<Spot> getBookedSpots(int startTimestamp, int endTimestamp, SpotTypes type) {
        List<Spot> booked = new ArrayList<Spot>();

        for(Map.Entry<Spot, List<Receipt>> entry : bookedSpots.entrySet()) {
            Spot spot = entry.getKey();
            for(Receipt r : entry.getValue()) {
                int[] bookingTiming = r.getTiming();
                if(spot.getType() == type && ((startTimestamp >= bookingTiming[0] && startTimestamp <= bookingTiming[1]) || (endTimestamp >= bookingTiming[0] && endTimestamp <= bookingTiming[1]) || (startTimestamp <= bookingTiming[0] && endTimestamp >= bookingTiming[1]))) {
                    booked.add(spot);
                    break;
                }
            }
        }
        return booked;
    }
}


class BillingService {
    private static BillingService instance;
    private BillingStrategy billStartegy;

    private BillingService() {}

    static BillingService GetInstance() {
        if(instance == null) {
            instance = new BillingService();
        }

        return instance;
    }

    public void setStrategy(BillingStrategy strategy) {
        this.billStartegy = strategy;
    }

    public boolean processBill(Receipt receipt) {
        if(billStartegy == null) return false;
        double amount = 10.22;  // Will be pulled from config base on slot type.

        if(!billStartegy.processBill(amount)) return false;

        receipt.setBill(10.22, true);
        return true;
    }
}

class BillingStrategy {
    PaymentGW paymentStrategy;

    public void setStrategy(PaymentGW obj) {
        this.paymentStrategy = obj;
    }

    public boolean processBill(double amount) {
        return paymentStrategy.processPlayment(amount);
    }
}

interface PaymentGW {
    boolean processPlayment(double amount);
}

class PaymentGWCard implements PaymentGW {
    String cardNo;

    public boolean processPlayment(double amount) {
        return true;
    }
}

class PaymentGWPaypal implements PaymentGW {
    String paypalId;

    public boolean processPlayment(double amount) {
        return true;
    }
}

class ParkingLotOnlineBooking {
    public static void main(String[] args) {
        PaymentGW paymentGW = new PaymentGWCard();
        BillingStrategy billingStrategy = new BillingStrategy();
        billingStrategy.setStrategy(paymentGW);
        BillingService.GetInstance().setStrategy(billingStrategy);

        Spot spot1 = new SpotSmall("spot1", "1,1", "space1");
        Spot spot2 = new SpotMedium("spot2", "2,2", "space1");
        Spot spot3 = new SpotLarge("spot3", "3,3,1", "space1");
        List<Spot> spots = new ArrayList<Spot>();
        spots.add(spot1);
        spots.add(spot2);
        spots.add(spot3);

        ParkingSpace space = new ParkingSpace("space1", spots);

        System.out.println("Available Spots");
        for(Spot spot : space.getAvailableSpots(1, 10, SpotTypes.SMALL)) {
            System.out.println(spot.getId());
        }

        Receipt r1 = space.book(spot1, 1, 10);
        if(r1 != null) {
            System.out.println("Spot booked");
            System.out.print(" " + r1.getSpotId() + " " + r1.getTiming()[0] + " " + r1.getTiming()[0]);
        }

        System.out.println("Available Spots");
        for(Spot spot : space.getAvailableSpots(5, 10, SpotTypes.SMALL)) {
            System.out.println(spot.getId());
        }

    }
}