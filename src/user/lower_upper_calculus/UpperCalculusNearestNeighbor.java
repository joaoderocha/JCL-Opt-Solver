package user.lower_upper_calculus;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;
import kernel.utils.LoadClass;

public class UpperCalculusNearestNeighbor implements UpperLowerCalculusInterface{
	//an example of a distributed way to perform the previous upper/lower
	//calculus. Off course, we adopt JCL to distribute tasks over a cluster.
	public void execute(){
		
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		jcl.register(TaskNearestNeighbor.class, "TaskNearestNeighbor");		
		List<Future<JCL_result>> tickets = new LinkedList<Future<JCL_result>>();
		@SuppressWarnings("unchecked")
		Set<String> vertices = (Set<String>) jcl.getValue("vertices").getCorrectResult();
		String edge = LoadClass.loadString("deltaevaluation");
		String jclVars = LoadClass.loadString("vars");
		TaskNearestNeighbor x = new TaskNearestNeighbor();
		for(String aux:vertices){
			Object[] args = {aux, "nulo", edge, jclVars};
			tickets.add(jcl.execute("TaskNearestNeighbor", args));
			//x.execute(aux, "nulo", edge, jclVars);
		}
		
		
		for(Future<JCL_result> aux:tickets)
			try {
				aux.get();
				jcl.removeResult(aux);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		tickets.clear();
		tickets = null;
	}

}
