package kernel.utils;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;

public class CleanLocalEnvironment {
	
	public CleanLocalEnvironment(){
		
	}
	
	public void execute(){
		JCL_facade jclLambari = JCL_FacadeImpl.getInstanceLambari();
		
		if(jclLambari.containsGlobalVar("BestResultL")){
			System.err.println(jclLambari.deleteGlobalVar("BestResultL"));
		}
		if(jclLambari.containsGlobalVar("lowerL")){
			jclLambari.deleteGlobalVar("lowerL");
		}
		if(jclLambari.containsGlobalVar("pathL")){
			jclLambari.deleteGlobalVar("pathL");
		}
	}

}
