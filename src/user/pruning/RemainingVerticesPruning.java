package user.pruning;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import user.delta_evaluation.DeltaEvaluationInterface;

public class RemainingVerticesPruning implements PruningInterface{

	//Object[] args = {jclVars, numOfVertices, remainingAux, path, root, leaf, current, i, currentUpperBound, upperBound, edgeCalc};
	
	@Override
	public boolean prune(Object... args) {
		Object[] JCLvars = (Object[]) args[0];
		@SuppressWarnings("unchecked")
		ObjectSet<String> remainingAux = (ObjectSet<String>) args[2];
		float upperBound = (float) args[9];
		float currentUpperBound = (float) args[8];
		DeltaEvaluationInterface edgeCalc = (DeltaEvaluationInterface) args[10];
		
		for(String vertex:remainingAux){
			currentUpperBound+=edgeCalc.get(vertex, "$shorterD$", JCLvars);
			if(currentUpperBound>upperBound)
				return true;			
		}
						
		return false;
	}

}
