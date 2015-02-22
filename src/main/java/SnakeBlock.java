
public class SnakeBlock {

	public Snake placer;
	public int turnPlaced;
	
	public SnakeBlock(Snake placer, int turnPlaced){
		this.placer = placer;
		this.turnPlaced = turnPlaced;
	}
	
	public int getExpiryTurn(){
		return turnPlaced + placer.body.size() - 1;
	}
	
	public static boolean expiresBefore(SnakeBlock block, int turn){
		if(block == null){
			return true;
		}else{
			return block.getExpiryTurn() <= turn;
		}
	}
}
