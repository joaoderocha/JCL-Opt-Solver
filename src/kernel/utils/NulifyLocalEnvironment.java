package kernel.utils;

import implementations.sm_kernel.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;

public class NulifyLocalEnvironment {
	public NulifyLocalEnvironment() {
		// TODO Auto-generated constructor stub
	}
	public void execute() {
		JCL_facade jclLambari = JCL_FacadeImpl.getInstance();
		
		if(jclLambari.containsGlobalVar("upperL")){
			System.err.println(jclLambari.setValueUnlocking("upperL", null));
		}
		if(jclLambari.containsGlobalVar("lowerL")){
			System.out.println(jclLambari.setValueUnlocking("lowerL", null));
			
		}
		if(jclLambari.containsGlobalVar("pathL")){
			System.out.println(jclLambari.setValueUnlocking("lowerL", null));
		}
	}
}
