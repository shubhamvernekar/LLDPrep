import java.util.*;

class Player {
  String id;
  Player(String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }
}

enum TurnStatus {
  WIN,
  INVALID,
  VALID,
  DRAW
}

class Board {
  int[][] board;
  Map<Integer, String> oxmap;
  int turn;
  int filledCells;

  Board(int row) {
    board = new int[row][row];
    oxmap = new HashMap<>();
    oxmap.put(-1, "O");
    oxmap.put(1, "X");
    oxmap.put(0, " ");
    turn = -1;
    filledCells = 0;
  }

  public void printBoard() {
    for(int i = 0; i < board.length; i++) {
      System.out.println();
      for(int j = 0; j < board[0].length; j++) {
        System.out.print(oxmap.get(board[i][j]) + "_");
      }
    }
    System.out.println();
  }

  public TurnStatus makeTurn(int i, int j) {
    if(i < 0 || i >= board.length || j < 0 || j >= board.length || board[i][j] != 0) return TurnStatus.INVALID;
    board[i][j] = turn;
    filledCells++;
    if(turn == -1) turn = 1;
    else turn = -1;

    int s = 1, v = 1, d1 = 1, d2 = 1;

    for(int p = 0; p < board.length; p++) {
      if(board[i][j] != board[p][j]) s = -1;
      if(board[i][j] != board[i][p]) v = -1;
      if(board[i][j] != board[p][p]) d1 = -1;
      if(board[i][j] != board[p][board.length - p-1]) d2 = -1;
    }
    if(s == 1 || v == 1 || d1 == 1 || d2 == 1) return TurnStatus.WIN;

    if(filledCells == board.length * board.length) return TurnStatus.DRAW;

    return TurnStatus.VALID;
  } 
}

class Game {
  Board board;
  Player[] players;
  int turn = 0;

  Game(Player[] players) {
    board = new Board(3);
    this.players = new Player[]{players[0], players[1]};
    startGame();
  }

  private void startGame() {
    Scanner sc= new Scanner(System.in);

    System.out.println("Press enter to start game");
    sc.nextLine();

    while(true) {
      board.printBoard();
      System.out.println("Player " + players[turn].getId() + " turn");
      System.out.println("Enter x position");
      int x = sc.nextInt();
      System.out.println("Enter y position");
      int y = sc.nextInt();

      TurnStatus status = board.makeTurn(x, y);

      board.printBoard();

      if(status == TurnStatus.DRAW) {
        System.out.println("Game is Draw");
        break;
      } else if(status == TurnStatus.WIN) {
        System.out.println("Player " + players[turn].getId() + " Win game");
        break;
      } else if(status == TurnStatus.INVALID) {
        System.out.println("Invalid move enter valid position");
      } else if(status == TurnStatus.VALID) {
        turn = turn == 0 ? 1 : 0;
      }
    }
    sc.close();
  }
}

class TicTacToe {
  public static void main(String[] args) {
    Player[] players = new Player[2];
    players[0] = new Player("player1");
    players[1] = new Player("player2");

    Game game =new Game(players);
  }
}
