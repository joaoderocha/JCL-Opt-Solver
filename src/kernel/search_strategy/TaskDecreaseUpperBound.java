package kernel.search_strategy;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import user.edge_calculus.EdgeCalculusInterface;
import user.pruning.PruningInterface;
import user.utils.JCLglobalVariablesAccess;


public class TaskDecreaseUpperBound {
	
	public void execute(String ith, String jth, String[] classes, String edge, String JCLv){
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(jth+":");
		
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		
		@SuppressWarnings("unchecked")
		ObjectSet<String> vertices = (ObjectSet<String>) jcl.getValue("vertices").getCorrectResult();
		ObjectSet<String> remaining = new ObjectOpenHashSet<String>(vertices.size()-2);
		
		Object[] JCLvars = JCLglobalVariablesAccess.getVarsJCL(JCLv.split("\\;"));
		
		for(String i:vertices){
			if(!i.equals(ith) && !i.equals(jth)){				
				remaining.add(i);				
			}
		}	
		
		PruningInterface[] instances = new PruningInterface[classes.length];
		for(int i=0; i<classes.length;i++)
			if(!classes[i].equals("null"))
				instances[i] = (PruningInterface) loadInstance(classes[i]);
			else instances[i] = null;
		
		if(!JCL_FacadeImpl.getInstanceLambari().containsGlobalVar("upperL")){
			JCL_FacadeImpl.getInstanceLambari().instantiateGlobalVar("upperL", jcl.getValue("upper").getCorrectResult());
			JCL_FacadeImpl.getInstanceLambari().instantiateGlobalVar("pathL", jcl.getValue("path").getCorrectResult());
		} else updateLocalGlobal();		
		
		JCL_facade jclLambari = JCL_FacadeImpl.getInstanceLambari();
		
		EdgeCalculusInterface edgeCalc = (EdgeCalculusInterface) loadInstance(edge);
		
		traverse2(jclLambari, instances, JCLvars, edgeCalc, vertices.size(), remaining, sb, ith, jth, jth, 0);
		
		sb=null;	
		
		//updateLocalGlobal();
	}
	
	private void updateLocalGlobal(){
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		float upperBound = (float) jcl.getValue("upper").getCorrectResult();
		float localUpperBound = (float) JCL_FacadeImpl.getInstanceLambari().getValue("upperL").getCorrectResult();
		if(upperBound>localUpperBound){
			upperBound = (float) jcl.getValueLocking("upper").getCorrectResult();
			if(upperBound>localUpperBound){
				jcl.setValueUnlocking("path", JCL_FacadeImpl.getInstanceLambari().getValue("pathL").getCorrectResult());
				jcl.setValueUnlocking("upper", localUpperBound);
			}else jcl.setValueUnlocking("upper", upperBound);
		}else if (upperBound<localUpperBound){
			localUpperBound = (float) JCL_FacadeImpl.getInstanceLambari().getValueLocking("upperL").getCorrectResult();
			if(upperBound<localUpperBound){
				JCL_FacadeImpl.getInstanceLambari().setValueUnlocking("pathL", jcl.getValue("path").getCorrectResult());
				JCL_FacadeImpl.getInstanceLambari().setValueUnlocking("upperL", upperBound);
			}else JCL_FacadeImpl.getInstanceLambari().setValueUnlocking("upperL", localUpperBound);
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
	
	protected void traverse(JCL_facade jcl, PruningInterface[] instances, Object[] JCLvars, EdgeCalculusInterface edgeCalc, int numOfVertices, ObjectSet<String> remaining, StringBuilder path, String root, String leaf, String current, float distance){
		if(!remaining.isEmpty()){
			
			for(String i:remaining){
				
				float currentUpperBound = distance + edgeCalc.calculate(current, i, JCLvars);	
				float upperBound = (float) jcl.getValue("upperL").getCorrectResult();				
															
				if(currentUpperBound<upperBound){
					
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
						traverse(jcl, instances, JCLvars, edgeCalc, numOfVertices, remainingAux, currentPath, root, leaf, i, currentUpperBound);
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
			
			float upperBound = (float) jcl.getValue("upperL").getCorrectResult();
			
			if(upperBound>distance){
				upperBound = (float) jcl.getValueLocking("upperL").getCorrectResult();
				if(upperBound>distance){
					jcl.setValueUnlocking("pathL", path.toString());
					jcl.setValueUnlocking("upperL", distance);	
					
					updateLocalGlobal();
					
					System.out.println("partial result: " + distance + " " + path.toString());
					
				}else jcl.setValueUnlocking("upperL", upperBound);	
			}			
			
		}		
		
	}	
	protected void traverse2(JCL_facade jcl, PruningInterface[] instances, Object[] JCLvars, EdgeCalculusInterface edgeCalc, int numOfVertices, ObjectSet<String> remaining, StringBuilder path, String root, String leaf, String current, float distance)
	{
		if(!remaining.isEmpty()) {
			
			for(String i:remaining) {
				
				float Datual = distance + edgeCalc.calculate(current, i, JCLvars);
				float upperBound = (float) jcl.getValue("upperL").getCorrectResult();
				boolean flag = true;
				ObjectSet<String> remainingAux = new ObjectOpenHashSet<String>(remaining.size()-1);
				for(String j:remaining){
					if(!j.equals(i)){
						remainingAux.add(j);
					}	
				}
				Object[] args = {JCLvars, numOfVertices, remainingAux, path, root, leaf, current, i, Datual, upperBound, edgeCalc};
				for(PruningInterface x : instances) {
					if(x!=null && flag) {
						if(x.prune(args)) {
							
							flag = false;
							remainingAux.clear();
							remainingAux = null;
						}
					}
				}
				if(flag) {
					StringBuilder currentPath = new StringBuilder();
					currentPath.append(path);
					currentPath.append(i+":");
					if(remainingAux!=null) {
						traverse2(jcl,instances,JCLvars,edgeCalc,numOfVertices,remainingAux,currentPath,root,leaf,i,Datual);
					}else {
						remainingAux.clear();
						traverse2(jcl,instances,JCLvars,edgeCalc,numOfVertices,remainingAux,currentPath,root,leaf,i,Datual);
					}
					currentPath=null;
					if(remainingAux!=null){
						remainingAux.clear();
						remainingAux = null;
					}
				}
				i=null;
			}
		}else {
			distance += edgeCalc.calculate(current, root, JCLvars);
			distance += edgeCalc.calculate(root, leaf, JCLvars);
			path.append(root);
			
			float upperBound = (float) jcl.getValue("upperL").getCorrectResult();
			
			if(upperBound>distance){
				upperBound = (float) jcl.getValueLocking("upperL").getCorrectResult();
				if(upperBound>distance){
					jcl.setValueUnlocking("pathL", path.toString());
					jcl.setValueUnlocking("upperL", distance);	
					
					updateLocalGlobal();
					
					System.out.println("partial result: " + distance + " " + path.toString());
					
				}else jcl.setValueUnlocking("upperL", upperBound);	
			}			
		}
	}
}
