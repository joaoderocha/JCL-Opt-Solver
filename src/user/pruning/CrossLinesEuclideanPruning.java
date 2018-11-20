package user.pruning;

import java.awt.geom.Line2D;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import user.delta_evaluation.DeltaEvaluationInterface;

public class CrossLinesEuclideanPruning implements PruningInterface{
	
	//Object[] args = {jclVars, numOfVertices, remainingVertices, path, root, leaf, currentV, i, currentUpperBound, upperBound, edgeCalc};
		
	@Override
	public boolean prune(Object...args){
		
		Object[] JCLvars = (Object[]) args[0];
		StringBuilder path = (StringBuilder) args[3];
		String i = (String) args[7];
		@SuppressWarnings("unchecked")
		ObjectSet<String> remainingAux = (ObjectSet<String>) args[2];
		DeltaEvaluationInterface edgeCalc = (DeltaEvaluationInterface) args[10];
		if(remainingAux!=null) {
			String[] vertices = path.toString().split(":");
			
			double x1 = edgeCalc.get(i, "$X$", JCLvars);
			double y1 = edgeCalc.get(i, "$Y$", JCLvars);
			
			double[][] x3y3x4y4 = new double[vertices.length-1][4];
			
			for(int j=1; j<vertices.length;j++){
				x3y3x4y4[j-1][0] = edgeCalc.get(vertices[j-1], "$X$", JCLvars);
				x3y3x4y4[j-1][1] = edgeCalc.get(vertices[j-1], "$Y$", JCLvars);
				x3y3x4y4[j-1][2] = edgeCalc.get(vertices[j], "$X$", JCLvars);
				x3y3x4y4[j-1][3] = edgeCalc.get(vertices[j], "$Y$", JCLvars);
			}		
			
			for(String oneR:remainingAux){		
				
				double x2 = edgeCalc.get(oneR, "$X$", JCLvars);
				double y2 = edgeCalc.get(oneR, "$Y$", JCLvars);	
			
				for(double[] aux: x3y3x4y4)								
					if(Line2D.Float.linesIntersect(x1, y1, x2, y2, aux[0], aux[1], aux[2], aux[3])) {
						vertices = null;
						x3y3x4y4=null;
						return true;
					}
				
			}
			
			vertices = null;
			x3y3x4y4=null;
		}
		return false;
		
	}
	
	

}
