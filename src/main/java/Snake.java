import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Snake {

	public static final String MY_NAME = "Skull Trumpet";
	public static final String MY_COLOR = "#877319";
	public static final String MY_HEAD = "http://i.imgur.com/S5bS2zS.png";
	public static final String MY_TAUNT = "Doot doot";

	public static Snake ME;
	public static final Map<String, Snake> SNAKE_MAP = new HashMap<String, Snake>();
	
	private String name, color, head, taunt;
	public ArrayList<ArrayList<Integer>> body;
	public int x, y;

	public Snake(String name, String color, String head, String taunt) {
		this.name = name;
		this.color = color;
		this.head = head;
		this.taunt = taunt;
		this.x = -1;
		this.y = -1;
	}
	
	public static void initMe(){
		Snake.ME = new Snake(MY_NAME, MY_COLOR, MY_HEAD, MY_TAUNT);
		SNAKE_MAP.put(MY_NAME, Snake.ME);
	}

	public static Snake getSnake(Object name, Object color, Object head, Object taunt) {
		return new Snake((String) name, (String) color, (String) head, (String) taunt);
	}

	public static void updateSnake(Map<String, Object> snakeInfo) {
		String name = (String) snakeInfo.get("name");
		Snake snake = SNAKE_MAP.get(name);
		if (snake == null) {
			snake = Snake.getSnake(name, snakeInfo.get("color"), snakeInfo.get("head_url"), snakeInfo.get("taunt"));
			SNAKE_MAP.put(name, snake);
		}
		snake.body = (ArrayList<ArrayList<Integer>>) (snakeInfo.get("coords"));
		snake.x = snake.body.get(0).get(0);
		snake.y = snake.body.get(0).get(1);
	}
}