package user.delta_evaluation;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;

public class StaticCalculus implements DeltaEvaluationInterface{
	
	
	/**
	 * dummy constructor
	 */
	public StaticCalculus() {
		
	}

	/**
	 * Calculate standard implementation
	 */
	@Override
	public double calculate(String vertexOne, String vertexTwo, Object[] JCLvars) {
		@SuppressWarnings("unchecked")
		Object2DoubleMap<String> distances = (Object2DoubleMap<String>) JCLvars[0];
		return distances.get(vertexOne+":"+vertexTwo);
	}
	
	@Override
	public double get(String vertexOne, String vertexTwo, Object[] JCLvars) {
		return calculate(vertexOne, vertexTwo, JCLvars);
	}

	

}
