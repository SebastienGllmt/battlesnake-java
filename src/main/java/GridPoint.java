
public class GridPoint implements Comparable<GridPoint>{

	public int x, y;
	
	public GridPoint(int x, int y){
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(GridPoint o) {
		
		int ThisPointDistToSnake = Math.abs(x - Snake.ME.x) + Math.abs(y - Snake.ME.y);
		int OtherPointDistToSnake = Math.abs(o.x - Snake.ME.x) + Math.abs(o.y - Snake.ME.y);
		
		if(ThisPointDistToSnake < OtherPointDistToSnake){
			return -1;
		}else if(ThisPointDistToSnake == OtherPointDistToSnake){
			return 0;
		}else{
			return 1;
		}
	}
	
}
