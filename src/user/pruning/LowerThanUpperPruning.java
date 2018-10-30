package user.pruning;

public class LowerThanUpperPruning implements PruningInterface {
	
	
	//if(currentUpperBound<upperBound)
	//return false if dont want to execute
	@Override
	public boolean prune(Object... args) {
		return !((float)args[8] < (float)args[9]);
	}

}
