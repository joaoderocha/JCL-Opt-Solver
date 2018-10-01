package kernel.utils;

import implementations.sm_kernel.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;

public class CleanLocalEnvironment {
	
	public CleanLocalEnvironment(){
		
	}
	
	public void execute(){
		JCL_facade jclLambari = JCL_FacadeImpl.getInstance();
		
		if(jclLambari.containsGlobalVar("upperL")){
			System.err.println(jclLambari.deleteGlobalVar("upperL"));
		}
		if(jclLambari.containsGlobalVar("lowerL")){
			jclLambari.deleteGlobalVar("lowerL");
		}
		if(jclLambari.containsGlobalVar("pathL")){
			jclLambari.deleteGlobalVar("pathL");
		}
	}

}
