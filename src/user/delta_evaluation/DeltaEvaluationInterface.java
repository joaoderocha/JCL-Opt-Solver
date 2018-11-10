package user.delta_evaluation;
/**
 * 
 * @author Joao
 * 
 * This interface is used to give user access to their own data structures
 * 	
 * 
 * 
 */
public interface DeltaEvaluationInterface{
	
	/**
	 * This method is used when user wants to calculate value from two arguments inside their own data structure.
	 * @param vertexOne usually the first argument of the data structure
	 * @param vertexTwo second argument usually the second argument of the data structure
	 * @param JCLvars abstraction of all user data structures
	 * @return float value calculated from user function
	 */
	//performs an edge calculus on-the-fly
	public float calculate(String vertexOne, String vertexTwo, Object[] JCLvars);
	/**
	 * This method is used to simply retrieve from user data structure float value
	 * @param vertexOne usually the first argument of the data structure
	 * @param vertexTwo second argument usually the second argument of the data structure
	 * @param JCLvars abstraction of all user data structures
	 * @return float value inside user data structure
	 */
	//do not perform an edge calculus. instead, it uses the existing edge weight value
	public float get(String vertexOne, String vertexTwo, Object[] JCLvars);
	/**
	 * Perform a quick test wether the actual solution has gone further or below the last valid solution
	 * @param current value of the current solution
	 * @param newValue value of the last solution
	 * @return boolean value wich should represent wether the first value is bigger or lower than second value
	 */
	public boolean prune(float current,float newValue);

}
