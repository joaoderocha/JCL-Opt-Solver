package mainpkg;


import kernel.mode.KernelMode;
import kernel.utils.LoadClass;

public class Main {

	public static void main(String[] args) {
		
		
			KernelMode interf = (KernelMode) LoadClass.loadInstance("mode");
			interf.execute();
			
		
		
	}

}
