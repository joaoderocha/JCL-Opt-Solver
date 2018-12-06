package user.load_input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
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
 * 1 -57.0 28.0
 * 2 54.0 -65.0
 * 3 46.0 79.0
 * 4 8.0 111.0
 * 5 -36.0 52.0
 * 6 -22.0 -76.0
 * 7 34.0 129.0
 * 8 74.0 6.0
 * 9 -6.0 -41.0
 * 10 21.0 45.0
 * 11 37.0 155.0
 * 12 -38.0 35.0
 * 13 -5.0 -24.0
 * 14 70.0 -74.
 * EOF
 */
public class CoordinatesXY extends LoadAbstract{
	//monta a tabela de pesos entre os vertices
	public void load(String filePath){
		super.load(filePath);
		try{	
			 JCL_facade jcl = JCL_FacadeImpl.getInstance();
			 Object2DoubleMap<String> distances = new Object2DoubleOpenHashMap<String>();
			 			
			 BufferedReader in = new BufferedReader(new FileReader(filePath));
		     String str = null;	  
		     		         
		     List<String> inputLimpos = new LinkedList<String>();
		     
		     double lower=0;
		     
	         //ler o arquivo
	         while ((str = in.readLine()) != null) {
	        	 if(!str.trim().equals("EOF")){
	        		 String[] inputDetalhes = str.split(" ");
	        		 StringBuilder sb = new StringBuilder();
	        		 for(String frag:inputDetalhes){
	        			 if(!frag.equals("")) sb.append(frag+":");
	        		 }
	        		 inputLimpos.add(sb.toString());
	        		 sb=null;
	        		 inputDetalhes=null;
	        	 }
	        	
	         }
	         in.close();
	         in=null;
	         
	        @SuppressWarnings("unchecked")
			ObjectSet<String> vertices = (ObjectSet<String>) jcl.getValue("vertices").getCorrectResult();
	         
	         double lowest = Double.MAX_VALUE;
	         /*
	          * transitar todo projeto para DOUBLE tirar todos floats para que aceite o permutation
	          */
	         //montando distancias	                  
	         for(String umaEntrada:inputLimpos){
	        	 String[] umaEntradaDetalhe = umaEntrada.split(":");
	        	 double menorD = Double.MAX_VALUE;
	        	 double maiorD = Double.MIN_VALUE;
	        	 vertices.add("$"+umaEntradaDetalhe[0]+"$");
	        	 for(String outraEntrada:inputLimpos){
	        		 String[] outraEntradaDetalhe = outraEntrada.split(":");
	        		 if(!umaEntradaDetalhe[0].equals(outraEntradaDetalhe[0])){
	        			 double dx = (Double.parseDouble(outraEntradaDetalhe[1])-Double.parseDouble(umaEntradaDetalhe[1]));
	        			 double dy = (Double.parseDouble(outraEntradaDetalhe[2])-Double.parseDouble(umaEntradaDetalhe[2]));
	        			 
	        			 double d = (double) Math.hypot(dx, dy);
	        			 
	        			 distances.put("$"+umaEntradaDetalhe[0]+"$:$"+outraEntradaDetalhe[0]+"$", d);	        			 
	        			 
	        			 if(d<menorD) menorD = d;
	        			 if(d>maiorD) maiorD = d;
	        			
	        		 }
	        		 outraEntradaDetalhe=null;
	        	 }
	        	 
	        	 distances.put("$"+umaEntradaDetalhe[0]+"$:$shorterD$", menorD);
	        	 distances.put("$"+umaEntradaDetalhe[0]+"$:$longerD$", maiorD);
	        	 
	        	 lower+=menorD;
	        	 if(lowest>menorD)
	        		 lowest=menorD;
	        	         	 
	        	 distances.put("$"+umaEntradaDetalhe[0]+"$:$X$", Float.parseFloat(umaEntradaDetalhe[1]));
	        	 distances.put("$"+umaEntradaDetalhe[0]+"$:$Y$", Float.parseFloat(umaEntradaDetalhe[2]));
	        	 umaEntradaDetalhe=null;
	         }	 
	         
	         lower+=lowest;
	         	                  	         
	         inputLimpos.clear();
	         inputLimpos=null;
	         
	         jcl.instantiateGlobalVar("numOfVertices", vertices.size());
	         
	         jcl.setValueUnlocking("vertices", vertices);
	         
	         jcl.setValueUnlocking("lower", lower);
	         
	         JCLglobalVariablesAccess.instantiateVarInJCL("distances", distances);
	       
	         System.out.println(vertices.toString());

	         
	         
	         
	         	        
		}catch (Exception e){
			e.printStackTrace();			
		}
		
	}

}
