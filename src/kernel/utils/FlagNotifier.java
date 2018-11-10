package kernel.utils;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;

public class FlagNotifier {
	public void execute() {
		JCL_facade jclL = JCL_FacadeImpl.getInstanceLambari();
		jclL.setValueUnlocking("flag", false);
		System.out.println("executei...");
	}
}
