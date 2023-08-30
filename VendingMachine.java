import java.util.*;


class Coin {
    public int value;

    Coin(int value) {
        this.value = value;
    }
}

class OneCoin extends Coin {
    OneCoin() {
        super(1);
    }
}

class TwoCoin extends Coin {
    TwoCoin() {
        super(2);
    }
}

class FiveCoin extends Coin {
    FiveCoin() {
        super(5);
    }
}

class TenCoin extends Coin {
    TenCoin() {
        super(10);
    }
}

class Item {
    int code;
    String name;
    int price;

    Item(String name, int code, int price) {
        this.name = name;
        this.code = code;
        this.price = price;
    }

    public int getCode() {
        return code;
    }
}

class CoinsBucket {
    List<Coin> bucket;

    CoinsBucket() {
        bucket = new ArrayList<>();
    }

    public void addCoins(List<Coin> coins) {
        bucket.addAll(coins);
    }
}

class Inventory {
    Map<Integer, Item> itemToCodeMap;
    Map<Integer, Integer> itemToCountMap;

    Inventory() {
        itemToCodeMap = new HashMap<>();
        itemToCountMap = new HashMap<>();
    }

    public void fillInventory(HashMap<Item, Integer> items) {
        for(Map.Entry<Item, Integer> entry : items.entrySet()) {
            Item item = entry.getKey();
            itemToCodeMap.put(item.getCode(), item);
            itemToCountMap.put(item.getCode(), entry.getValue());
        }
    }

    public boolean isItemAvailable(int code) {
        return itemToCountMap.get(code) != null && itemToCountMap.get(code) > 0;
    }

    public Item getItem(int code) {
        return itemToCodeMap.get(code);
    }

    public void removeItem(int code) {
        int count = itemToCountMap.get(code);
        itemToCountMap.put(code, count-1);
    }
}

interface State {
    void fillInventory(HashMap<Item, Integer> items);
    void startButtonPress();
    void selectItem(int code);
    void acceptCoins(List<Coin> coins);
    void dispanceItem();
}

class IdleState implements State {
    Machine context;

    IdleState(Machine context) {
        this.context = context;
        context.selectedItem = null;
        context.insertedCoints.clear();
    }

    public void startButtonPress() {
        context.setState(new SelectProductState(context));
    }

    public void fillInventory(HashMap<Item, Integer> items) {
        context.getInventory().fillInventory(items);
    }

    @Override
    public void selectItem(int code) {
        System.out.println("Invalid call");
    }

    @Override
    public void acceptCoins(List<Coin> coins) {
        System.out.println("Invalid call");
    }

    @Override
    public void dispanceItem() {
        System.out.println("Invalid call");
    }
}

class SelectProductState implements State {
    Machine context;

    SelectProductState(Machine context) {
        this.context = context;
    }
    
    @Override
    public void startButtonPress() {
        System.out.println("Invalid call");
    }

    @Override
    public void selectItem(int code) {
        Item item = context.getInventory().getItem(code);

        if(item == null) {
            System.out.println("Invalid item code, enter again");
            return;
        }

        if (context.getInventory().isItemAvailable(code)) {
            context.selectedItem = item;
            context.setState(new AcceptCoinsState(context));
        } else {
            System.out.println("Item out of stock");
            return;
        }
    }

    @Override
    public void acceptCoins(List<Coin> coins) {
        System.out.println("Invalid call");
    }

    @Override
    public void dispanceItem() {
        System.out.println("Invalid call");
    }

    @Override
    public void fillInventory(HashMap<Item, Integer> items) {
        System.out.println("Invalid call");
    }
}

class AcceptCoinsState implements State {
    Machine context;

    AcceptCoinsState(Machine context) {
        this.context = context;
    }
    
    @Override
    public void startButtonPress() {
        System.out.println("Invalid call");
    }

    @Override
    public void selectItem(int code) {
        System.out.println("Invalid call");
    }

    @Override
    public void acceptCoins(List<Coin> coins) {
        int price = context.selectedItem.price;
        int enterPrice = 0;

        for(Coin coin : coins) {
            enterPrice += coin.value;
        }

        if(enterPrice >= price) {
             context.insertedCoints.addAll(coins);
             context.setState(new DispanceItemState(context));
        } else {
            System.out.println("Insufficient coins entered.");
        }
    }

    @Override
    public void dispanceItem() {
        System.out.println("Invalid call");
    }

    @Override
    public void fillInventory(HashMap<Item, Integer> items) {
        System.out.println("Invalid call");
    }
}

class DispanceItemState implements State {
    Machine context;

    DispanceItemState(Machine context) {
        this.context = context;
        dispanceItem();
    }

    @Override
    public void fillInventory(HashMap<Item, Integer> items) {
        System.out.println("Invalid call");
    }

    @Override
    public void startButtonPress() {
        System.out.println("Invalid call");
    }

    @Override
    public void selectItem(int code) {
        System.out.println("Invalid call");
    }

    @Override
    public void acceptCoins(List<Coin> coins) {
        System.out.println("Invalid call");
    }

    @Override
    public void dispanceItem() {
        context.coinsBucket.addCoins(context.insertedCoints);
        context.inventory.removeItem(context.selectedItem.getCode());
        context.setState(new IdleState(context));
        System.out.println("Please collect your item");
    }
}

class Machine {
    List<Coin> insertedCoints;
    Item selectedItem;
    State machineState;
    Inventory inventory;
    CoinsBucket coinsBucket;

    Machine() {
        insertedCoints = new ArrayList<>();
        inventory = new Inventory();
        coinsBucket = new CoinsBucket();
        setState(new IdleState(this));
    }

    public void setState(State state) {
        this.machineState = state;
    }

    public State getState() {
        return machineState;
    }

    public Inventory getInventory() {
        return inventory;
    }
}


class VendingMachine {
    public static void main(String[] args) {
        Machine machine = new Machine();

        //Filling inventory
        Item coce = new Item("Coce", 101, 10);
        Item Sprite = new Item("Sprite", 103, 20);
        Item mango = new Item("mango", 104, 5);
        HashMap<Item, Integer> map = new HashMap<>();
        map.put(coce, 10);
        map.put(Sprite, 20);
        map.put(mango, 50);

        machine.getState().fillInventory(map);

        machine.getState().startButtonPress();
        machine.getState().selectItem(104);

        Coin coin51 = new Coin(5);
        Coin coin52 = new Coin(5);
        machine.getState().acceptCoins(new ArrayList<Coin>(Arrays.asList(coin51, coin52)));
    }
}