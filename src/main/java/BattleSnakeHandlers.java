
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BattleSnakeHandlers {

	private static final String TAUNT[] = { "Doot", "Doot doot", "Doot doot doot" };

	private Board board = null;

	public Object handleStart(Map<String, Object> requestBody) {
		// Load board
		board = Board.getBoard(requestBody.get("game_id"), requestBody.get("width"), requestBody.get("height"));

		Snake.initMe();
		// Create snake
		Map<String, Object> responseObject = new HashMap<String, Object>();
		responseObject.put("name", Snake.MY_NAME);
		responseObject.put("color", Snake.MY_COLOR);
		responseObject.put("head_url", Snake.MY_HEAD);
		responseObject.put("taunt", Snake.MY_TAUNT);
		return responseObject;
	}

	public Object handleMove(Map<String, Object> requestBody) {
		// Load snakes
		ArrayList<Map<String, Object>> snakesInfo = (ArrayList<Map<String, Object>>) requestBody.get("snakes");
		for (Map<String, Object> snakeInfo : snakesInfo) {
			Snake.updateSnake(snakeInfo);
		}
		// load board data
		board.turn++;
		board.updateBoard((ArrayList<ArrayList<Map<String, Object>>>) requestBody.get("board"));
		board.updateFood((ArrayList<ArrayList<Integer>>) requestBody.get("food"));

		// Handle move
		Map<String, Object> responseObject = new HashMap<String, Object>();
		int[] move = board.getBestMove();
		if (move != null) {
			System.out.println(Arrays.toString(move));
			responseObject.put("taunt", getTaunt());
			if (move[0] == -1) {
				responseObject.put("move", "left");
			} else if (move[0] == 1) {
				responseObject.put("move", "right");
			}else if(move[1] == 1){
				responseObject.put("move", "down");
			}else if(move[1] == -1){
				responseObject.put("move", "up");
			}
		}else{
			responseObject.put("taunt", "Stuck doot doot");
		}
		
		return responseObject;
	}

	public Object handleEnd(Map<String, Object> requestBody) {
		// No response required
		Map<String, Object> responseObject = new HashMap<String, Object>();
		return responseObject;
	}

	private static String getTaunt() {
		return TAUNT[(int) (Math.random() * 3)];
	}
}
