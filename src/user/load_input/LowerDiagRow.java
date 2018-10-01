package user.load_input;

import java.io.BufferedReader;
import java.io.FileReader;
import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import user.utils.JCLglobalVariablesAccess;
/**
 * 
 * @author Joao
 *
 *	Standard implementation of LoadInterface class.
 *
 *	Main objective is to read data input, register and instantiate it on cluster.
 *
 *	Example:
 *	0
 *83
 *0
 *93
 *40
 *0
 *129
 *53
 *42
 *0
 *133
 *62
 *42
 *11
 *0
 *EOF
 */
public class LowerDiagRow extends LoadAbstract {

	public void load(String filePath) {

		super.load(filePath);
		try {
			JCL_facade jcl = JCL_FacadeImpl.getInstance();
			Object2ObjectMap<String,Float> distances = new Object2ObjectOpenHashMap<String,Float>();
			 BufferedReader in = new BufferedReader(new FileReader("input.txt"));
		     String str = null;	  
		     int linha = 0;
		     int coluna = 0;
		     float lower=0;
		     float lowest = Float.MAX_VALUE;
		     int matrixSize = 0;
		     float aux=0;
		     @SuppressWarnings("unchecked")
			ObjectSet<String> vertices = (ObjectSet<String>) jcl.getValue("vertices").getCorrectResult();
		      //ler o arquivo
	         while ((str = in.readLine()) != null) {
	        	 if(!str.trim().equals("EOF")){
	        		 String[] inputDetalhes = str.split(" ");	 
   			 for(String frag:inputDetalhes){
   				 if(!frag.isEmpty()) {
   					aux = Float.parseFloat(frag);
	        			 	if(aux == 0) { 
	        			 		matrixSize++;
	        			 		vertices.add("$"+(linha+1)+"$");
	        			 		linha++;
	        			 		coluna = 0;
	        			 	}else {
	        			 		distances.put("$"+(linha+1)+"$:$"+(coluna+1)+"$",aux);
	        			 		distances.put("$"+(coluna+1)+"$:$"+(linha+1)+"$",aux);
	        			 		coluna++;
	        			 		
	        			 	}
   				 		}
	        		 }
	        	 }
	         }
	         in.close();
	         in=null;
	         float aux2;
	         
	         for(int i=1;i<=matrixSize;i++) {
	        	 float maiorD = Float.MIN_VALUE;
			     float menorD = Float.MAX_VALUE;
	        	 for(int j = 1;j<matrixSize;j++) {
	        		if(i!=j) {
	        			aux2 = distances.get("$"+i+"$:$"+j+"$");
	        			if(aux2 > maiorD) maiorD = aux2;
			 			if(aux2 < menorD) menorD = aux2;
	        		}
	        	 }
	        	 distances.put("$"+(i)+"$:$shorterD$", menorD);
	        	 distances.put("$"+(i)+"$:$longerD$", maiorD);
	        	 
	        	 
	       
	        	 
	        	 lower+=menorD;
	        	 if(lowest>menorD)
	        		 lowest=menorD;
	         }
	         
	         lower+=lowest;
	         System.out.println(matrixSize);
	         
	         jcl.instantiateGlobalVar("numOfVertices", matrixSize);
			
	         jcl.setValueUnlocking("vertices", vertices);
	         
	         jcl.setValueUnlocking("lower", lower);
	        
	         
	         
	         
	         JCLglobalVariablesAccess.instantiateVarInJCL("distances", distances);
	          
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
