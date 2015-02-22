import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Board {

	public static String NAME;
	public static int WIDTH, HEIGHT;

	public int turn;

	public SnakeBlock[][] board;
	public GridPoint[] food;

	public Board(String name, int width, int height) {
		this.NAME = name;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.turn = 0;
		this.board = new SnakeBlock[WIDTH][HEIGHT];
	}

	public void updateBoard(ArrayList<ArrayList<Map<String, Object>>> boardInfo) {
		for (int x = 0; x < boardInfo.size(); x++) {
			ArrayList<Map<String, Object>> column = boardInfo.get(x);
			for (int y = 0; y < column.size(); y++) {
				Map<String, Object> square = column.get(y);
				String name = (String) square.get("snake");
				if (name != null) {
					// If we have a block there, but not in our view of the board
					if (board[x][y] == null) {
						board[x][y] = new SnakeBlock(Snake.SNAKE_MAP.get(name), turn);
					}
				} else {
					// If we used to have a block there, but it's gone
					if (board[x][y] != null) {
						board[x][y] = null;
					}
				}
			}
		}
	}

	public int[] getBestMove() {
		SnakeBlock[][] backup = new SnakeBlock[Board.WIDTH][Board.HEIGHT];
		for(int x=0; x<Board.WIDTH; x++){
			for(int y=0; y<Board.HEIGHT; y++){
				backup[x][y] = board[x][y];
			}
		}
		int[] lastBest = null;
		// First go through from CurPos -> Food in order of closest point to farthest
		for (GridPoint point : food) {
			MoveResult firstMoveResult = getFirstMove(Snake.ME.x, Snake.ME.y, point.x, point.y, null, 0);
			if (firstMoveResult != null) {
				lastBest = firstMoveResult.move;
				// Then go through from Food -> NextFood from farthest point to closest
				// This is to make sure we don't accidentally box ourself from greedily going after the first
				for (int tail = food.length - 1; tail >= 0; tail--) {
					if (food[tail] == point) {
						// There was no move after CurPos->Food that doesn't lead us to be trapped
						// We may be dead, but the least precise calculation is the path to the farthest food
						// This is because we don't know how enemy snakes are going to behave
						// Therefore, we return the longest path in a sort of stalling tactic and hope a survival chance opens up
						return lastBest;
					}
					MoveResult firstMoveAfter = getFirstMove(point.x, point.y, food[tail].x, food[tail].y, null, firstMoveResult.steps);
					if (firstMoveAfter != null) {
						// This means we found an efficient path CurPos->Food that also has an escape Food->NextFood
						return firstMoveResult.move;
					}
				}
			}
		}
		board = backup;
		
		// if there was no move, just try something...
		if(lastBest == null){
			if(Snake.ME.x+1 < Board.WIDTH && board[Snake.ME.x+1][Snake.ME.y] == null){
				return new int[]{1, 0};
			}else if(Snake.ME.x > 0 && board[Snake.ME.x-1][Snake.ME.y] == null){
				return new int[]{-1, 0};
			}else if(Snake.ME.y+1 < Board.HEIGHT && board[Snake.ME.x][Snake.ME.y+1] == null){
				return new int[]{0, 1};
			}else{
				return new int[]{0, -1};
			}
		}
		return lastBest;
	}

	public boolean expiresBefore(int x, int y, int turn) {
		if (x < 0 || x >= Board.WIDTH || y < 0 || y >= Board.HEIGHT) {
			return false;
		}
		return SnakeBlock.expiresBefore(board[x][y], turn);
	}

	public MoveResult getFirstMove(int fromX, int fromY, int toX, int toY, int[] firstStep, int steps) {
		// update the board
		board[fromX][fromY] = new SnakeBlock(Snake.ME, turn+steps);
		// Try and avoid paths that are too long
		if (steps > Board.WIDTH + Board.HEIGHT || steps > 100) {
			System.out.println("No more steps");
			// no more steps.
			// We return the first step because our tree is probably too deep for recovery
			// since the next branch up of this recursive tree has a step count really high also
			// and will reach this branch really fast.
			// We know the first step can at least get us to survive for a long amount of steps.
			return new MoveResult(firstStep, steps);
		}
		if (fromX == toX && fromY == toY) {
			return new MoveResult(firstStep, steps);
		}

		int[] dist = new int[] { toX - fromX, toY - fromY };
		MoveResult pathFound = null;

		// First try going directly to the target
		if (dist[0] > 0 && expiresBefore(fromX + 1, fromY, turn + steps)) {
			if (firstStep == null) {
				firstStep = new int[] { 1, 0 };
			}
			pathFound = getFirstMove(fromX + 1, fromY, toX, toY, firstStep, steps + 1);
		} else if (dist[0] < 0 && expiresBefore(fromX - 1, fromY, turn + steps)) {
			if (firstStep == null) {
				firstStep = new int[] { -1, 0 };
			}
			pathFound = getFirstMove(fromX - 1, fromY, toX, toY, firstStep, steps + 1);
		}
		if (pathFound != null) {
			return pathFound;
		}
		if (dist[1] > 0 && expiresBefore(fromX, fromY + 1, turn + steps)) {
			if (firstStep == null) {
				firstStep = new int[] { 0, 1 };
			}
			pathFound = getFirstMove(fromX, fromY + 1, toX, toY, firstStep, steps + 1);
		} else if (dist[1] < 0 && expiresBefore(fromX, fromY - 1, turn + steps)) {
			if (firstStep == null) {
				firstStep = new int[] { 0, -1 };
			}
			pathFound = getFirstMove(fromX, fromY - 1, toX, toY, firstStep, steps + 1);
		}
		if (pathFound != null) {
			return pathFound;
		}

		// Else, try moving in the opposite direction or a direction that's counter-intuitive
		if (dist[0] <= 0 && expiresBefore(fromX + 1, fromY, turn + steps)) {
			if (firstStep == null) {
				firstStep = new int[] { 1, 0 };
			}
			pathFound = getFirstMove(fromX + 1, fromY, toX, toY, firstStep, steps + 1);
		} else if (dist[0] >= 0 && expiresBefore(fromX - 1, fromY, turn + steps)) {
			if (firstStep == null) {
				firstStep = new int[] { -1, 0 };
			}
			pathFound = getFirstMove(fromX - 1, fromY, toX, toY, firstStep, steps + 1);
		}
		if (pathFound != null) {
			return pathFound;
		}
		if (dist[1] <= 0 && expiresBefore(fromX, fromY + 1, turn + steps)) {
			if (firstStep == null) {
				firstStep = new int[] { 0, 1 };
			}
			pathFound = getFirstMove(fromX, fromY + 1, toX, toY, firstStep, steps + 1);
		} else if (dist[1] >= 0 && expiresBefore(fromX, fromY - 1, turn + steps)) {
			if (firstStep == null) {
				firstStep = new int[] { 0, -1 };
			}
			pathFound = getFirstMove(fromX, fromY - 1, toX, toY, firstStep, steps + 1);
		}
		if (pathFound != null) {
			return pathFound;
		}

		// No path found so remove the position from the board
		board[fromX][fromY] = null;
		return null;
	}

	public void updateFood(ArrayList<ArrayList<Integer>> boardInfo) {
		this.food = new GridPoint[boardInfo.size()];
		for (int i = 0; i < boardInfo.size(); i++) {
			ArrayList<Integer> pointInfo = boardInfo.get(i);
			food[i] = new GridPoint(pointInfo.get(0), pointInfo.get(1));
		}
		Arrays.sort(food);
	}

	public static Board getBoard(Object name, Object width, Object height) {
		return new Board((String) name, (Integer) width, (Integer) height);
	}
}
