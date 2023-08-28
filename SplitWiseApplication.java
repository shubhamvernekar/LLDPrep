import java.util.*;

class UserTally {
    double balance;
    HashMap<User, Double> balanceMap;

    UserTally() {
        balanceMap = new HashMap<>();

    }

    public void addAmount(User user, double amount) {
        balance += amount;
        balanceMap.put(user, balanceMap.getOrDefault(user, 0.0) + amount);
    }

    public void printUserTally() {
        System.out.println("Your total balance = " + balance);
        System.out.println("-----------");
        for(Map.Entry<User, Double> entry : balanceMap.entrySet()) {
            if (entry.getValue() < 0) {
                System.out.println("You have to pay " + (entry.getValue() * -1) + " to " + entry.getKey().getUserId());
            } else {
                System.out.println("You have to get " + (entry.getValue()) + " from " + entry.getKey().getUserId());
            }
        }
    }
}

class User {
    String userid;
    UserTally tally;

    User(String id) {
        this.userid = id;
        tally = new UserTally();
    }

    public String getUserId() {
        return this.userid;
    }

    public UserTally getUserTally() {
        return this.tally;
    }
}

class Split {
    String splitId;
    double amount;
    User createdBy;
    HashMap<User, Double> amountSplit;

    Split(String id, double amount, User createdBy, HashMap<User, Double> amountSplit) {
        this.splitId = id;
        this.amount = amount;
        this.createdBy = createdBy;
        this.amountSplit = amountSplit;
    }
}

class SplitService {
    HashMap<User, List<Split>> userToSplitMap;

    SplitService() {
        userToSplitMap = new HashMap<>();
    }

    public Split createSplit(User createdBy, List<User> users, double amount, SplitType type) {
        SplitCalculatorFactory factory = new SplitCalculatorFactory();
        SplitCalculator calculator = factory.getSplitCalculator(type);

        if(calculator == null) return null;

        Split split = calculator.calculateSplit(createdBy, users, amount);

        if(!userToSplitMap.containsKey(createdBy)) userToSplitMap.put(createdBy, new ArrayList<Split>());
        userToSplitMap.get(createdBy).add(split);
        return split;
    }
}


enum SplitType {
    EQUAL,
    SPECIFIC
}

class SplitCalculatorFactory {
    public SplitCalculator getSplitCalculator(SplitType type) {
        SplitCalculator calculator = null;
        switch(type) {
            case EQUAL: calculator = new SplitCalculatorEqual();
            break;
            case SPECIFIC: calculator = new SplitCalculatorSpecific();
            break;
        }
        return calculator;
    }
}

interface SplitCalculator {
    Split calculateSplit(User createdBy, List<User> users, double amount);
}

class SplitCalculatorEqual implements SplitCalculator{
    public Split calculateSplit(User createdBy, List<User> users, double amount) {
        double amountAfterDiv = amount / users.size();
        UserTally myTally = createdBy.getUserTally();
        HashMap<User, Double> amountSplit = new HashMap<>();
        amountSplit.put(createdBy, amountAfterDiv);

        for(User o : users) {
            if(o == createdBy) continue;

            UserTally hisTally = o.getUserTally();
            hisTally.addAmount(createdBy, amountAfterDiv * -1);
            myTally.addAmount(o, amountAfterDiv);
            amountSplit.put(o, amountAfterDiv);
        }

        return new Split(UUID.randomUUID().toString(), amount, createdBy, amountSplit);
    }
}

class SplitCalculatorSpecific implements SplitCalculator{
    public Split calculateSplit(User createdBy, List<User> users, double amount) {
        Scanner sc = new Scanner(System.in);

        double[] useramounts = new double[users.size()];
        boolean accept = false;

        while(!accept) {
            int sum = 0;

            for(int i = 0; i < users.size(); i++) {
                System.out.println("Enter Amount for " + users.get(i).getUserId());
                double d = sc.nextDouble();
                useramounts[i] = d;
                sum += d;
            }

            if(amount == sum) accept=true;
        }

        HashMap<User, Double> amountSplit = new HashMap<>();
        UserTally myTally = createdBy.getUserTally();

        for(int i = 0; i < users.size(); i++) {
            amountSplit.put(users.get(i), useramounts[i]);

            if(users.get(i) == createdBy) continue;

            UserTally hisTally = users.get(i).getUserTally();
            hisTally.addAmount(createdBy, useramounts[i] * -1);
            myTally.addAmount(users.get(i), useramounts[i]);
        }

        return new Split(UUID.randomUUID().toString(), amount, createdBy, amountSplit);
    }
}

class SplitWiseApplication {
    public static void main(String[] args) {
        User user1 = new User("user1");
        User user2 = new User("user2");
        User user3 = new User("user3");

        SplitService splitService = new SplitService();
        
        splitService.createSplit(user1, new ArrayList<User>(Arrays.asList(user1, user2, user3)), 90, SplitType.EQUAL);

        System.out.println("printing user1's tally");
        user1.getUserTally().printUserTally();

        splitService.createSplit(user2, new ArrayList<User>(Arrays.asList(user1, user2, user3)), 150, SplitType.EQUAL);

        System.out.println("printing user2's tally");
        user2.getUserTally().printUserTally();

        splitService.createSplit(user3, new ArrayList<User>(Arrays.asList(user1, user2, user3)), 90, SplitType.SPECIFIC);

        System.out.println("printing user3's tally");
        user3.getUserTally().printUserTally();

        System.out.println("printing all users tally");
        user1.getUserTally().printUserTally();
        user2.getUserTally().printUserTally();
        user3.getUserTally().printUserTally();
    }
}