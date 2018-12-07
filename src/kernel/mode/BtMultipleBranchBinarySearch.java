package kernel.mode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import javafx.util.Pair;
import kernel.search_strategy.TaskDecreaseUpperBound;
import kernel.utils.CleanLocalEnvironment;
import kernel.utils.LoadClass;
import kernel.utils.NulifyLocalEnvironment;
import user.load_input.LoadInterface;
import user.lower_upper_calculus.UpperLowerCalculusInterface;

public class BtMultipleBranchBinarySearch implements KernelMode {

	@Override
	public void execute() {
		/*
		 * Load input
		 */
		long time = System.nanoTime();
		LoadInterface mountDistances = (LoadInterface)LoadClass.loadInstance("load");
		mountDistances.load("input.txt");
		System.out.println("Finished to mount the edges weights of a graph...");
		
		/*
		 * pre calculus
		 */
		System.out.println("Starting calculating lower/upper bounds...");
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		UpperLowerCalculusInterface upperLowerCalculus = (UpperLowerCalculusInterface)LoadClass.loadInstance("upperlower");
		upperLowerCalculus.execute();
		Pair<String,Double> bestResult = (Pair<String,Double>) jcl.getValue("bestResult").getCorrectResult();
		Pair<String,Double> aux = null;
		System.out.println("Finished to find a valid graph path to be used as upper and lower bounds");
		System.out.println("upper bound: " + bestResult.getValue());
		System.out.println("lower bound: " + jcl.getValue("lower").getCorrectResult());
		System.out.println("path: "+ bestResult.getKey());
		System.out.println("end of upper/lower bound values calculus... ");
		
		@SuppressWarnings("unchecked")
		ObjectSet<String> vertices = (ObjectSet<String>) jcl.getValue("vertices").getCorrectResult();
		List<Future<JCL_result>> tickets = new LinkedList<Future<JCL_result>>();
		String[] nickName = LoadClass.loadString("searchstrategy").split("\\.");
		jcl.register(LoadClass.loadClass("searchstrategy"), nickName[nickName.length-1]);
		String[] pruning = LoadClass.loadString("pruning").split(";");
		String edge = LoadClass.loadString("deltaevaluation");
		String mom = LoadClass.loadString("minormax");
		String jclVars = LoadClass.loadString("vars");
		System.out.println("Distributing arguments...");
		
		
		Object[] args = null;
		int runs =1;
		
		double upperAux = bestResult.getValue();
		
		while((double) jcl.getValue("lower").getCorrectResult()<=((Pair<String,Double>) jcl.getValue("bestResult").getCorrectResult()).getValue()){
			System.out.println("Binary search strategy - round: " + runs);
			if (runs==1){
				double lowerAux = (double) jcl.getValue("lower").getCorrectResult();
				double pivot = (upperAux+lowerAux)/2;
				aux = new Pair<String,Double>("",pivot);
				jcl.setValueUnlocking("bestResult", aux);
				System.out.println("Begin search from lowerBound: "+lowerAux+" to UpperBound:"+pivot);
				jcl.register(LoadClass.loadClass("searchstrategy"), nickName[nickName.length-1]);
				traverse(vertices, pruning, edge,mom, jclVars, tickets, nickName, jcl);
				aux = null;
				aux = (Pair<String,Double>) jcl.getValue("bestResult").getCorrectResult();
				if(pivot==aux.getValue()){ 
					jcl.setValueUnlocking("lower", pivot);
					aux = null;
					aux = new Pair<String,Double>("",(upperAux+pivot)/2);
					jcl.setValueUnlocking("bestResult", aux);
					
					
					jcl.register(NulifyLocalEnvironment.class, "NulifyLocalEnvironment");
					tickets = jcl.executeAll("NulifyLocalEnvironment", args);
					jcl.getAllResultBlocking(tickets);
					
					tickets.clear();
				}else break;
			}else{
				
				double pivot = ((Pair<String,Double>) jcl.getValue("bestResult").getCorrectResult()).getValue();
				double lowerAux = (double) jcl.getValue("lower").getCorrectResult();
				System.out.println("Begin search from lowerBound: "+lowerAux+" to UpperBound:"+pivot);
				jcl.register(LoadClass.loadClass("searchstrategy"), nickName[nickName.length-1]);
				traverse(vertices, pruning, edge,mom, jclVars, tickets, nickName, jcl);
				aux = null;
				aux = (Pair<String,Double>) jcl.getValue("bestResult").getCorrectResult();
				System.out.println(pivot +" aux: "+ aux.getValue());
				if(pivot==aux.getValue()){ 
					if(Math.abs(pivot - ((upperAux+pivot)/2)) < 1) {
						jcl.setValueUnlocking("lower", pivot);
						aux = null;
						aux = new Pair<String,Double>("",pivot+1);
						jcl.setValueUnlocking("bestValue", aux);
					}else {
						jcl.setValueUnlocking("lower", pivot);
						aux = null;
						aux = new Pair<String,Double>("",(upperAux+pivot)/2);
						jcl.setValueUnlocking("bestResult", aux);
					}
					
					
								
					
					
					jcl.register(NulifyLocalEnvironment.class, "NulifyLocalEnvironment");
					tickets = jcl.executeAll("NulifyLocalEnvironment", args);
					jcl.getAllResultBlocking(tickets);
					
					tickets.clear();
				}else break;
			}
			System.out.println((double) jcl.getValue("lower").getCorrectResult());
			System.out.println(((Pair<String,Double>) jcl.getValue("bestResult").getCorrectResult()).getValue());
			runs++;
		}	
		
		tickets = null;
		vertices = null;
		aux = (Pair<String,Double>) jcl.getValue("bestResult").getCorrectResult();
		System.out.println(aux.getValue());
		System.out.println(bestResult.getValue());
		if(!aux.getKey().equals(""))
			bestResult = aux;
		aux = null;
		System.out.println();
		System.out.println("elapsed time (sec): " + (System.nanoTime()-time)/1000000000);
		System.out.println("best path value: " + bestResult.getValue());
		System.out.println("best path: " + bestResult.getKey());
		jcl.register(CleanLocalEnvironment.class,"CleanLocalEnvironment");
		tickets = jcl.executeAll("CleanLocalEnvironment", args);
		jcl.getAllResultBlocking(tickets);
		tickets.clear();
		tickets = null;
		
		jcl.cleanEnvironment();
		
		jcl.destroy();
		
	}
	
	private void traverse(ObjectSet<String> vertices, String[] pruning,String edge,String mom,String jclVars, List<Future<JCL_result>> tickets, String[] nickName, JCL_facade jcl){
		Set<String> restrictions = new HashSet<String>();		
		TaskDecreaseUpperBound obj = new TaskDecreaseUpperBound();
		
		for(String i:vertices){
			
			for(String j:vertices){ 
				
				if(!j.equals(i) && !restrictions.contains(j)){	
					
					Object[] args = {i,j, pruning, edge,mom, jclVars};
					tickets.add(jcl.execute(nickName[nickName.length-1], args));			
					//obj.execute(i,j, pruning, edge,mom, jclVars);
				}							
			}
			restrictions.add(i);						
		}
		
		restrictions.clear();
		restrictions = null;
		
		for(Future<JCL_result> f:tickets)
			try {
				f.get();
				jcl.removeResult(f);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		tickets.clear();
	}
	
	

}
