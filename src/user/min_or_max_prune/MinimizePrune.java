package user.min_or_max_prune;

public class MinimizePrune implements MinOrMaxPruneInterface {

	public boolean prune(double current, double newValue) {
		return current<newValue;
	}

}
