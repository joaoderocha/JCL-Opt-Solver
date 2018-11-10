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
 * @author Joubert
 *
 *	Standard implementation of LoadInterface class.
 *
 *	Main objective is to read data input, register and instantiate it on cluster.
 *	
 *
 *	Input file example:
 * 0  8 50 31 12 48 36  2  5 39 10
 * 8  0 38  9 33 37 22  6  4 14 32
 *50 38  0 11 55  1 23 46 41 17 52
 *31  9 11  0 44 13 16 19 25 18 42
 *12 33 55 44  0 54 53 30 28 45  7
 *48 37  1 13 54  0 26 47 40 24 51
 *36 22 23 16 53 26  0 29 35 34 49
 * 2  6 46 19 30 47 29  0  3 27 15
 * 5  4 41 25 28 40 35  3  0 20 2
 *39 14 17 18 45 24 34 27 20  0 4
 *10 32 52 42  7 51 49 15 21 43  0
 *EOF
 */
public class DistanceMatrix extends LoadAbstract{
	
	//monta a tabela de pesos entre os vertices
	public void load(String filePath){
		super.load(filePath);
		try{
			 Object2ObjectMap<String, Float> distances = new Object2ObjectOpenHashMap<String, Float>();
	 			
			 BufferedReader in = new BufferedReader(new FileReader(filePath));
		     String str = null;	  
		     		         
		     float[][] matrix = null;
		     int linha = 0;
		     float lower=0;
		     int matrixSize = 0;
		     //ler o arquivo
	         while ((str = in.readLine()) != null) {
	        	 if(!str.trim().equals("EOF")){
	        		 String[] inputDetalhes = str.split(" ");
	        		 if(matrix==null){
	        			 matrixSize = 0;
	        			 for(String frag:inputDetalhes){
		        			 if(!frag.equals("")) matrixSize++;
		        		 }
	        			 System.out.println("dimensão da matriz: " + matrixSize);
	        			 matrix = new float[matrixSize][matrixSize];
	        		 }
	        		 int coluna =0;
	        		 for(String frag:inputDetalhes){
	        			 if(!frag.equals("")) {
	        				 matrix[linha][coluna] = Float.parseFloat(frag);
	        				 coluna++;	        				 
	        			 }
	        		 }
	        		 
	        		 linha++;
	        	 }
	         }
	         
	         in.close();
	         in=null;
	         
	         JCL_facade jcl = JCL_FacadeImpl.getInstance();
	         
	         @SuppressWarnings("unchecked")
			 ObjectSet<String> vertices = (ObjectSet<String>) jcl.getValue("vertices").getCorrectResult();
	         jcl.instantiateGlobalVar("numOfVertices", matrixSize);
	         float lowest = Float.MAX_VALUE;
	        
	         //montando distancias	                  
	         for (int i=0; i<matrix.length;i++){
	        	 float menorD = Float.MAX_VALUE;
	        	 float maiorD = Float.MIN_VALUE;
	        	 vertices.add("$"+(i+1)+"$");
	        	 
	        	 for(int j=0; j<matrix.length; j++){
	        		 if(i!=j){
	        			 distances.put("$"+(i+1)+"$:$"+(j+1)+"$", matrix[i][j]);
	        			 if(matrix[i][j]<menorD) menorD = matrix[i][j];
	        			 if(matrix[i][j]>maiorD) maiorD = matrix[i][j];
	        			 
	        		 }
	        	 }	        	 

	        	 distances.put("$"+(i+1)+"$:$shorterD$", menorD);
	        	 distances.put("$"+(i+1)+"$:$longerD$", maiorD);
	        	 
	        	 lower+=menorD;
	        	 if(lowest>menorD)
	        		 lowest=menorD;	        	         	 
	        	 
	         }	         
	         
	         lower+=lowest; 	         
	         	         
	         jcl.setValueUnlocking("vertices", vertices);
	         
	         jcl.setValueUnlocking("lower", lower);
	         
	         JCLglobalVariablesAccess.instantiateVarInJCL("distances", distances);
	         
	         for(String k:distances.keySet()){
	        	 System.out.println("Key: " + k + " || value: " + distances.get(k));
	         }
		}catch (Exception e){
			
		}
	}

}
