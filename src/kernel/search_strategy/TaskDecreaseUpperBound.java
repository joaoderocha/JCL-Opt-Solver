package kernel.search_strategy;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import javafx.util.Pair;
import user.delta_evaluation.DeltaEvaluationInterface;
import user.min_or_max_prune.MinOrMaxPruneInterface;
import user.pruning.PruningInterface;
import user.utils.JCLglobalVariablesAccess;

@SuppressWarnings("unchecked")
public class TaskDecreaseUpperBound {
	
	
	public void execute(String ith, String jth, String[] classes, String edge,String mom, String JCLv){
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(jth+":");
		
		JCL_facade jcl = JCL_FacadeImpl.getInstancePacu();
		
		@SuppressWarnings("unchecked")
		ObjectSet<String> vertices = (ObjectSet<String>) jcl.getValue("vertices").getCorrectResult();
		ObjectSet<String> remaining = new ObjectOpenHashSet<String>(vertices.size()-2);
		
		Object[] JCLvars = JCLglobalVariablesAccess.getVarsJCL(JCLv.split("\\;"));
		
		for(String i:vertices){
			if(!i.equals(ith) && !i.equals(jth)){				
				remaining.add(i);				
			}
		}	
		
		DeltaEvaluationInterface edgeCalc = (DeltaEvaluationInterface) loadInstance(edge);
		MinOrMaxPruneInterface momprune = (MinOrMaxPruneInterface) loadInstance(mom);
		PruningInterface[] instances = new PruningInterface[classes.length];
		for(int i=0; i<classes.length;i++)
			if(!classes[i].equals("null"))
				instances[i] = (PruningInterface) loadInstance(classes[i]);
			else instances[i] = null;
		
		if(!JCL_FacadeImpl.getInstanceLambari().containsGlobalVar("bestResultL")){
			JCL_FacadeImpl.getInstanceLambari().instantiateGlobalVar("bestResultL",(Pair<String,Double>) jcl.getValue("bestResult").getCorrectResult());
		} else updateLocalGlobal(momprune);		
		
		JCL_facade jclLambari = JCL_FacadeImpl.getInstanceLambari();

		traverse(jclLambari, instances, JCLvars, edgeCalc,momprune, vertices.size(), remaining, sb, ith, jth, jth, 0);
		
		sb=null;	
		
		//updateLocalGlobal();
	}
	
	protected void traverse(JCL_facade jcl, PruningInterface[] instances, Object[] JCLvars, DeltaEvaluationInterface edgeCalc,MinOrMaxPruneInterface mom, int numOfVertices, ObjectSet<String> remaining, StringBuilder path, String root, String leaf, String current, double distance){
		if(!remaining.isEmpty()){
			
			for(String i:remaining){
				
				double currentUpperBound = distance + edgeCalc.calculate(current, i, JCLvars);
				double upperBound = ((Pair<String,Double>) jcl.getValue("bestResultL").getCorrectResult()).getValue();				
															
				if(mom.prune(currentUpperBound, upperBound)){
					
					ObjectSet<String> remainingAux = new ObjectOpenHashSet<String>(remaining.size()-1);
					for(String j:remaining){
						if(!j.equals(i)){
							remainingAux.add(j);
						}
					}
					
					Object[] args = {JCLvars, numOfVertices, remainingAux, path, root, leaf, current, i, currentUpperBound, upperBound, edgeCalc};
					
					for(PruningInterface pruning: instances)
						if(pruning!=null){
							if(pruning.prune(args)) {
								remainingAux.clear();
								remainingAux=null;
							}
						}
					
					StringBuilder currentPath = new StringBuilder();
					currentPath.append(path);
					currentPath.append(i+":");
					if(remainingAux!=null)				
						traverse(jcl, instances, JCLvars, edgeCalc,mom, numOfVertices, remainingAux, currentPath, root, leaf, i, currentUpperBound);
					currentPath = null;	
					if(remainingAux!=null){
						remainingAux.clear();
						remainingAux = null;
					}
				}	
				
				i=null;								
			}
			
		}else{
		
			distance += edgeCalc.calculate(current, root, JCLvars);
			distance += edgeCalc.calculate(root, leaf, JCLvars);
			path.append(root);
			
			Pair<String,Double> upperBound = (Pair<String,Double>) jcl.getValue("bestResultL").getCorrectResult();
			
			if(mom.prune(distance, upperBound.getValue())){
				
				upperBound = (Pair<String,Double>) jcl.getValueLocking("bestResultL").getCorrectResult();
				
				if(mom.prune(distance,upperBound.getValue())){
					/*jcl.setValueUnlocking("pathL", path.toString());
					jcl.setValueUnlocking("upperL", distance);	*/
					upperBound = null;
					upperBound = new Pair<String,Double>(path.toString(),distance);
					jcl.setValueUnlocking("bestResultL", upperBound);
					
					System.out.println("partial result: " + distance + " " + path.toString());
					updateLocalGlobal(mom);
					
					
					
				}else jcl.setValueUnlocking("bestResultL", upperBound);	
			}			
			
		}		
		
	}	
	@SuppressWarnings("unchecked")
	private void updateLocalGlobal(MinOrMaxPruneInterface edge){
		JCL_facade jcl = JCL_FacadeImpl.getInstancePacu();	
		Pair<String,Double> globalUpperBound = (Pair<String,Double>) jcl.getValue("bestResult").getCorrectResult(); // valor global
		Pair<String,Double> localUpperBound = (Pair<String,Double>) JCL_FacadeImpl.getInstanceLambari().getValue("bestResultL").getCorrectResult();// valor local
		if(edge.prune(localUpperBound.getValue(), globalUpperBound.getValue())){ // valor local é menor que global || global desatualizado
			globalUpperBound =  (Pair<String,Double>) jcl.getValueLocking("bestResult").getCorrectResult(); // travo valor global
			if(edge.prune(localUpperBound.getValue(),globalUpperBound.getValue() )) { // valor local continua menor que global
				jcl.setValueUnlocking("bestResult", localUpperBound); // se continua menor, libera valor global para local
			}else jcl.setValueUnlocking("bestResult", globalUpperBound); // caso contrario , libera com valor global
		}else if(edge.prune(globalUpperBound.getValue(), localUpperBound.getValue())) { // valor global e menor que local || Local desatualizado
			localUpperBound =  (Pair<String,Double>) JCL_FacadeImpl.getInstanceLambari().getValueLocking("bestResultL").getCorrectResult(); // travo valor local
			if(edge.prune(globalUpperBound.getValue(), localUpperBound.getValue())) { // valor global continua menor que o local
				JCL_FacadeImpl.getInstanceLambari().setValueUnlocking("bestResultL",globalUpperBound); // libera local com valor global
			}else JCL_FacadeImpl.getInstanceLambari().setValueUnlocking("bestResultL",localUpperBound); // libera local com valor local
		}
		/*double upperBound = (double) jcl.getValue("upper").getCorrectResult();
		double localUpperBound = (double) JCL_FacadeImpl.getInstanceLambari().getValue("upperL").getCorrectResult();
		if(!edge.prune(upperBound, localUpperBound)){
			upperBound = (double) jcl.getValueLocking("upper").getCorrectResult();
			if(!edge.prune(upperBound, localUpperBound)){
				jcl.setValueUnlocking("path", JCL_FacadeImpl.getInstanceLambari().getValue("pathL").getCorrectResult());
				jcl.setValueUnlocking("upper", localUpperBound);
			}else jcl.setValueUnlocking("upper", upperBound);
		}else if (edge.prune(upperBound, localUpperBound)){
			localUpperBound = (double) JCL_FacadeImpl.getInstanceLambari().getValueLocking("upperL").getCorrectResult();
			if(edge.prune(upperBound, localUpperBound)){
				JCL_FacadeImpl.getInstanceLambari().setValueUnlocking("pathL", jcl.getValue("path").getCorrectResult());
				JCL_FacadeImpl.getInstanceLambari().setValueUnlocking("upperL", upperBound);
			}else JCL_FacadeImpl.getInstanceLambari().setValueUnlocking("upperL", localUpperBound);
		}*/
		
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