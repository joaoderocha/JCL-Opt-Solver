package user.lower_upper_calculus;

import java.util.HashSet;
import java.util.Set;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import javafx.util.Pair;
import user.delta_evaluation.DeltaEvaluationInterface;
import user.utils.JCLglobalVariablesAccess;

public class TaskNearestNeighbor{
	
	public TaskNearestNeighbor(){
		
	}
	
	//find a path using the Nearest Neighbor from one vertex of a graph
	public void execute (String ith, String jth, String edgeWeightClass, String JCLv){
		StringBuilder sb = new StringBuilder();
		
		sb.append(ith+":");
		
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		
		@SuppressWarnings("unchecked")
		ObjectSet<String> vertices = (ObjectSet<String>) jcl.getValue("vertices").getCorrectResult();
		Set<String> remaining = new HashSet<String>(vertices.size());
		
		for(String aux:vertices){
			if(!aux.equals(ith)){
				remaining.add(aux);				
			}
		}
		Object[] JCLvars = JCLglobalVariablesAccess.getVarsJCL(JCLv.split("\\;"));
		DeltaEvaluationInterface edgeCalc = (DeltaEvaluationInterface) loadInstance(edgeWeightClass);
		
		traverse(edgeCalc, JCLvars, vertices.size(), ith, remaining, sb, ith, 0, 1);
		
		sb=null;		
	}
	
	@SuppressWarnings("unchecked")
	private void traverse(DeltaEvaluationInterface edgeCalc, Object[] JCLvars, int numOfVertices, String vertex, Set<String> remaining, StringBuilder path, String current, double distance, int level){
		if(level!=numOfVertices){
			
			String index = null;
			double d = Double.MAX_VALUE;
			
			for(String next:remaining){
				
				double currentDistance = edgeCalc.calculate(current, next, JCLvars);
			
				if(currentDistance<d){
					index=next;
					d=currentDistance;
				}
				
			}			
			
			remaining.remove(index);
						
			double currentDistance = distance + edgeCalc.calculate(current, index, JCLvars);
			path.append(index+":");
			
			traverse(edgeCalc, JCLvars, numOfVertices, vertex, remaining, path, index, currentDistance, level+1);
			
		}else{
			distance += edgeCalc.calculate(current, vertex, JCLvars);
			Pair<String,Double> best = (Pair<String,Double>) JCL_FacadeImpl.getInstance().getValue("bestResult").getCorrectResult();
			double bestDistance = best.getValue();//(double) JCL_FacadeImpl.getInstance().getValue("upper").getCorrectResult();
			if(bestDistance>distance){
				
				best = (Pair<String,Double>) JCL_FacadeImpl.getInstance().getValueLocking("bestResult").getCorrectResult();
				bestDistance = best.getValue();//(double) JCL_FacadeImpl.getInstance().getValueLocking("upper").getCorrectResult();
				if(bestDistance>distance){
					//JCL_FacadeImpl.getInstance().setValueUnlocking("path", path.toString());
					//JCL_FacadeImpl.getInstance().setValueUnlocking("upper", distance);
					best = null;
					best = new Pair<String,Double>(path.toString(),distance);
					
					JCL_FacadeImpl.getInstance().setValueUnlocking("bestResult", best);
				}else{
					JCL_FacadeImpl.getInstance().setValueUnlocking("bestResult", best);
				}	
			}								
		}
	}
	
	private Object loadInstance(String oneClass){
		
		try {
			return Class.forName(oneClass).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 	
		
	}

}
