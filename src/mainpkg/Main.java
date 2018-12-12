package mainpkg;


import java.io.File;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import kernel.mode.KernelMode;
import kernel.utils.LoadClass;

public class Main {

	public static void main(String[] args) {
		
		
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		
		File[] jars = {new File("dependency/tudo.jar")};
		
		String[] nickName = LoadClass.loadString("searchstrategy").split("\\.");
			
		jcl.register(jars,nickName[nickName.length-1], true);
		
			KernelMode interf = (KernelMode) LoadClass.loadInstance("mode");
			interf.execute();
			
		
		
	}

}
