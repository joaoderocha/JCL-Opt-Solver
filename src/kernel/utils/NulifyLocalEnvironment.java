package kernel.utils;


import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import javafx.util.Pair;

public class NulifyLocalEnvironment {
	public NulifyLocalEnvironment() {
		// TODO Auto-generated constructor stub
	}
	public void execute() {
		JCL_facade jclLambari = JCL_FacadeImpl.getInstanceLambari();
		
		
		if(jclLambari.containsGlobalVar("bestResultL")){
			Pair<String,Double> x = new Pair<String,Double>("",Double.MAX_VALUE);
			System.err.println(jclLambari.setValueUnlocking("bestResultL", x));
		}
		
	}
}
