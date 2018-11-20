package user.pruning;

public class LowerThanUpperPruning implements PruningInterface {
	
	
	//if(currentUpperBound<upperBound)
	//return false if dont want to execute
	@Override
	public boolean prune(Object... args) {
		return !((double)args[8] < (double)args[9]);
	}

}
