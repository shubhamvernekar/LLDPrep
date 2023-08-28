import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class Player {
  String color;
  String id;

  Player(String id, String color) {
    this.id = id;
    this.color = color;
  }

  public String getId() {
    return this.id;
  }
}

class Dice {
  int numberOfDice;

  Dice(int n) {
    this.numberOfDice = n;
  }

  public int rollDice() {
    int result = 0;

    for(int i = 0; i < numberOfDice; i++) {
      result += ThreadLocalRandom.current().nextInt(1, 6 + 1);
    }
    return result;
  }
}

class Snake {
  int headp;
  int tailp;
  String id;

  Snake(String id, int headp, int tailp) {
    this.id = id;
    this.headp = headp;
    this.tailp = tailp;
  }

  public int getTail() {
    return tailp;
  }
}

class Ladder {
  int headp;
  int tailp;
  String id;

  Ladder(String id, int headp, int tailp) {
    this.id = id;
    this.headp = headp;
    this.tailp = tailp;
  }

  public int getTail() {
    return tailp;
  }
}

enum MoveStatus {
  INVALID,
  VALID,
  WIN
}

class Board {
  String board[];
  Map<String, Snake> snakes;
  Map<String, Ladder> ladders;
  Map<String, Integer> playerPositions;

  Board(int length) {
    board = new String[length];
    snakes = new HashMap<>();
    ladders = new HashMap<>();
    playerPositions = new HashMap<>();
  }

  public boolean addSnake(int s, int e) {
    if(s <= e || s >= board.length || e < 1) return false;

    String cellContent = board[s];
    if(cellContent != null && cellContent.charAt(0) == 'L') return false;

    String snakeId = "S+" + UUID.randomUUID().toString();
    Snake snake = new Snake(snakeId, s, e);
    snakes.put(snakeId, snake);

    return true;
  }

  public boolean addLadder(int s, int e) {
    if(s >= e || s < 1 || e >= board.length) return false;

    String cellContent = board[s];
    if(cellContent != null && cellContent.charAt(0) == 'S') return false;

    String ladderId = "L+" + UUID.randomUUID().toString();
    Ladder ladder = new Ladder(ladderId, s, e);
    ladders.put(ladderId, ladder);

    return true;
  }

  public MoveStatus makeMove(int moveBy, Player player) {
    int currpos = playerPositions.getOrDefault(player.getId(), 0);
    int newpos = currpos + moveBy;

    if(newpos >= board.length) return MoveStatus.INVALID;

    String cellContent = board[newpos];
    if(cellContent != null) {
      if(cellContent.charAt(0) == 'L') {
        Ladder ladder = ladders.get(cellContent);
        newpos = ladder.getTail();
        System.out.println("Climbe ladder");
      } else if(cellContent.charAt(0) == 'S') {
        Snake snake = snakes.get(cellContent);
        System.out.println("Eatten by snake");
        newpos = snake.getTail();
      }
    }

    playerPositions.put(player.getId(), newpos);

    System.out.println("new position = " + newpos);

    if(newpos == board.length-1) return MoveStatus.WIN;

    return MoveStatus.VALID;
  }

}


class Game {
  Board board;
  Queue<Player> playesQueue;
  Scanner sc;
  Dice dice;
  String colors[] = new String[]{"Red", "Blue", "Green", "Orange", "Black"};

  Game() {
    sc = new Scanner(System.in);
    playesQueue = new LinkedList<>();

    System.out.println("Enter no of dices");
    int n = sc.nextInt();
    dice = new Dice(n);
    configBoard();
  }

  private void configBoard() {
    System.out.println("enter Board size");
    int n = sc.nextInt();
    board = new Board(n);
    System.out.println("Enter no of Playes");
    int pn = sc.nextInt();

    int ppi = 1;
    while(pn > 0) {
      Player player = new Player("Player " + ppi++, colors[pn]);
      playesQueue.add(player);
      pn--;
    }

    System.out.println("Enter no of Snakes");
    int sn = sc.nextInt();
    while(sn > 0) {
      System.out.println("Enter snake head index");
      int sh = sc.nextInt();
      System.out.println("Enter snake tail index");
      int st = sc.nextInt();

      if(!board.addSnake(sh, st)) {
        System.out.println("Invalid inputs Enter Again");
        continue;
      }
      sn--;
    }

    System.out.println("Enter no of Ladders");
    int ln = sc.nextInt();
    while(ln > 0) {
      System.out.println("Enter ladder start index");
      int lh = sc.nextInt();
      System.out.println("Enter ladder end index");
      int lt = sc.nextInt();

      if(!board.addLadder(lh, lt)) {
        System.out.println("Invalid inputs Enter Again");
        continue;
      }
      ln--;
    }

    System.out.println("press enter to start game");
    sc.nextLine();

    startGame();
  }

  public void startGame() {
    while(playesQueue.size() > 1) {
      Player player = playesQueue.poll();
      
      System.out.println("Press Enter for " + player.getId() + "'s dice roll");
      sc.nextLine();

      int rolldiceno = dice.rollDice();
      System.out.println("Dice roll = " + rolldiceno);
      MoveStatus status = board.makeMove(rolldiceno, player);
      
      if(status == MoveStatus.WIN) {
        System.out.println("You " + player.getId() + "Win");
      } else {
        playesQueue.offer(player);
      }
    }
  }
}

class SnakeAndLadder {
  public static void main(String[] args) {
    Game game = new Game();
  }
}
