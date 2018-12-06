package user.min_or_max_prune;

public class MinimizePrune implements MinOrMaxPruneInterface {
	/**
	 * return true whether current is bigger than newValue
	 */
	public boolean prune(double current, double newValue) {
		return (current<newValue);
	}

}
