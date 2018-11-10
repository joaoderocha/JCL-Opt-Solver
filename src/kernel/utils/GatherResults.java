package kernel.utils;

import java.io.File;
import com.android.dx.util.FileUtils;
/**
 * Returns a String that represents the content of logfile that ends with .log inside an file called ResultsDir.
 * The MachineID argument is just a representation of the machine in cluster.
 * <p>
 * This method always return immediately after reading all lines inside the .log file.
 * 
 * @param MachineID  A number that represents the ID of a machine.
 * @return 			 the content of the .log file inside ResultsDir
 */
public class GatherResults {
	
	public String execute(int MachineID) {
		
		File diretorio = new File("ResultsDir");
		String conteudo = new String("MachineID: " +MachineID+"\n");
		try {
			if(diretorio.exists()) {
				for(File x : diretorio.listFiles()) {
					if(x.getName().endsWith(".log")) {
						conteudo += new String(FileUtils.readFile(x),"UTF-8");	
					}			
				}
			}
			return conteudo;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	
}
