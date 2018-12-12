package kernel.mode;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
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
import user.load_input.LoadInterface;
import user.lower_upper_calculus.UpperLowerCalculusInterface;


public class BtMultipleBranchParallelism implements KernelMode{
	
	public BtMultipleBranchParallelism(){
		
	}
	
	public void execute(){
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
		Pair<String,Double> best = (Pair<String,Double>)jcl.getValue("bestResult").getCorrectResult();
		System.out.println("Finished to find a valid graph path to be used as upper and lower bounds");
		System.out.println("upper bound: " + best.getValue());
		System.out.println("lower bound: " + jcl.getValue("lower").getCorrectResult());
		System.out.println("path: "+ best.getKey());
		System.out.println("end of upper/lower bound values calculus... ");
		
		@SuppressWarnings("unchecked")
		ObjectSet<String> vertices = (ObjectSet<String>) jcl.getValue("vertices").getCorrectResult();
		List<Future<JCL_result>> tickets = new LinkedList<Future<JCL_result>>();
		String[] nickName = LoadClass.loadString("searchstrategy").split("\\.");
		jcl.register(LoadClass.loadClass("searchstrategy"), nickName[nickName.length-1]);
		String[] pruning = LoadClass.loadString("pruning").split(";");
		System.out.println("Distributing arguments...");
		traverse(vertices, pruning, tickets, nickName, jcl);	
		
		tickets = null;
		vertices = null;
		
		System.out.println();
		System.out.println("elapsed time (sec): " + (System.nanoTime()-time)/1000000000);
		System.out.println("best path value: " + ((Pair<String,Double>)jcl.getValue("bestResult").getCorrectResult()).getValue());
		System.out.println("best path: " + ((Pair<String,Double>)jcl.getValue("bestResult").getCorrectResult()).getKey());
		
		jcl.register(CleanLocalEnvironment.class, "CleanLocalEnvironment");
		Object[] args=null;	
		
		tickets = jcl.executeAll("CleanLocalEnvironment", args);
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
		tickets = null;
		
		jcl.cleanEnvironment();
		
		jcl.destroy();
	}
	
	private void traverse(ObjectSet<String> vertices, String[] pruning, List<Future<JCL_result>> tickets, String[] nickName, JCL_facade jcl){
		Set<String> restrictions = new HashSet<String>();		
		String edge = LoadClass.loadString("deltaevaluation");
		String jclVars = LoadClass.loadString("vars");
		String mom = LoadClass.loadString("minormax");
		System.out.println("Executing method...");
		TaskDecreaseUpperBound obj = new TaskDecreaseUpperBound();
		for(String i:vertices){
			
			for(String j:vertices){ 
				
				if(!j.equals(i) && !restrictions.contains(j)){	
						
						Object[] args = {i,j, pruning, edge,mom, jclVars};
						// startar thread para debug
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
