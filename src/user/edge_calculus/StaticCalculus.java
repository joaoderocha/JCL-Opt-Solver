package user.edge_calculus;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

public class StaticCalculus implements EdgeCalculusInterface{
	
	
	/**
	 * dummy constructor
	 */
	public StaticCalculus() {
		
	}

	/**
	 * Calculate standard implementation
	 */
	@Override
	public float calculate(String vertexOne, String vertexTwo, Object[] JCLvars) {
		@SuppressWarnings("unchecked")
		Object2ObjectMap<String, Float> distances = (Object2ObjectMap<String, Float>) JCLvars[0];
		return distances.get(vertexOne+":"+vertexTwo);
	}
	
	@Override
	public float get(String vertexOne, String vertexTwo, Object[] JCLvars) {
		return calculate(vertexOne, vertexTwo, JCLvars);
	}

}
