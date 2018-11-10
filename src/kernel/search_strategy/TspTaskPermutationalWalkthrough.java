package kernel.search_strategy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import interfaces.kernel.JCL_result;
import kernel.utils.TrotterJhonson;
import user.delta_evaluation.DeltaEvaluationInterface;
import user.evaluation.EvaluationInterface;

public class TspTaskPermutationalWalkthrough {
	static JCL_facade jcl = JCL_FacadeImpl.getInstance(); // instancia do cluster
	static JCL_facade jclL = JCL_FacadeImpl.getInstanceLambari();
	static boolean flag = true;
	//@SuppressWarnings({ "unused" })
	public void execute(BigInteger begin, BigInteger amount,Class<?> avaliacao,Class<?> edgeCalc,String[] pruning) {
		try {
			int timestop = (int) jcl.getValue("timeout").getCorrectResult();
			Logger logger = LogManager.getFormatterLogger(logger());
			if((boolean) jcl.getValue("log").getCorrectResult()) {
				logger = LogManager.getRootLogger();
			}
			
			logger.info(begin +"  "+ amount);
			String ThreadName = Thread.currentThread().getName();
			String filePath = criarDir(ThreadName);
			creatState(filePath,begin,amount);
			TrotterJhonson x = new TrotterJhonson(((int)jcl.getValue("numOfVertices").getCorrectResult())-1);
			logger.info("Started search from: " + begin + " until it's done with " + amount + " permutations on " + Thread.currentThread().getName());
			x.setPerm(x.TrotterJohnsonUnranking(begin),begin);
			double lowerBound = (double) jcl.getValue("lowerBound").getCorrectResult();
			BigInteger aux = new BigInteger("0");
			BigInteger porcentagem = amount.multiply(BigInteger.valueOf((int) jcl.getValue("Porcentagem").getCorrectResult()));
			porcentagem = porcentagem.divide(BigInteger.valueOf(100));
			if(!jclL.containsGlobalVar("flag")) {
				jclL.instantiateGlobalVar("flag", true);
			}
			flag = (boolean ) jclL.getValue("flag").getCorrectResult();
			List<Integer> bestlocalpath = null;
			double bestlocal;
			long tempoDecorrido = System.nanoTime(),tempoDelog=0;			
			Object[] dataStruct = user.utils.JCLglobalVariablesAccess.getVarsJCL();
			List<Integer> CasoTeste = x.getCurrentPermutation(); // pega permutacao
			double distancia = 0; // zera a distancia atual de busca
			int tam = CasoTeste.size();
			EvaluationInterface evaluation = (EvaluationInterface) avaliacao.newInstance();
			DeltaEvaluationInterface ec = (DeltaEvaluationInterface) edgeCalc.newInstance();
			/*PruningInterface[] instances = new PruningInterface[pruning.length];
			for(int i=0; i<pruning.length;i++) {
				if(!pruning[i].equals("null"))
					instances[i] = (PruningInterface) loadInstance(pruning[i]);
				else instances[i] = null;
			}*/
			//List<Boolean> worth = new BooleanArrayList();  
			
			for(int j=0;j<tam-1;j++) { // soma as distancias inicialmente
				distancia += ec.get(("$"+(CasoTeste.get(j))+"$"), ("$"+(CasoTeste.get(j+1))+"$"), dataStruct);
			}
			distancia += ec.get("$"+(CasoTeste.get(tam-1))+"$","$"+(tam+1)+"$",dataStruct);
			distancia += ec.get("$"+(CasoTeste.get(0))+"$","$"+(tam+1)+"$",dataStruct);
			bestlocal = distancia;
			
			aux = aux.add(BigInteger.ONE);
			while((aux.compareTo(amount) != 0) && flag ) {
				
				/*if(instances != null) {
					for(PruningInterface pr : instances) {
						worth.add( pr.prune(dataStruct,distancia,x.getCurrentPermutation()));
					}
				}
				
				if(!worth.contains(false) && worth != null) {*/
				distancia = evaluation.Evaluation(x,distancia,ec,dataStruct);
				
				if(aux.mod(porcentagem).compareTo(BigInteger.ZERO) == 0) {
					
					logger.warn("----------Estado atual------------");
					tempoDelog = (System.nanoTime()-tempoDecorrido)/1000000000;
					if(!flag) {
						logger.warn("Flag Alterada em: " + tempoDelog);
					}else {
						logger.warn("Flag Nao alterada em: " + tempoDelog);
					}
					
					logger.warn("PathValue: " + distancia);
					logger.warn("Path: " + x.getCurrentPermutation());
					logger.warn("-------------------------");
					updateState(filePath, false,new String( x.getRank().toString()),new String( aux.subtract(amount).toString()),new String( amount.toString()),new String(""+bestlocal));
					
				}
				if(timestop <=(System.nanoTime()-tempoDecorrido)/1000000000) {
					flag = false;
					List<Future<JCL_result>> lista = jcl.executeAll("TspTaskPermutationalWalkthrough","flagNotify");
					jclL.getAllResultBlocking(lista);
				}
				
				if(distancia < bestlocal) {
					if(distancia <= lowerBound) {
						bestlocal = distancia;
						bestlocalpath = CasoTeste;
						flag = false; 
						List<Future<JCL_result>> lista = jcl.executeAll("TspTaskPermutationalWalkthrough","flagNotify");
						jclL.getAllResultBlocking(lista);
						logger.warn(ThreadName+"   "+"New best Local: " + bestlocal);
						logger.warn("Found earlier on: " + ThreadName + " all threads should stop now...");
						
					}else {
						bestlocal = distancia;
						bestlocalpath = CasoTeste;
						logger.info(ThreadName+"  New best Local: " + bestlocal);
					}
				}
				
				aux = aux.add(BigInteger.ONE);	
				//worth.clear();
			}
		
			double aux2 = (double) jcl.getValueLocking("bestDistance").getCorrectResult();
			if(bestlocal <= aux2 ) { // compara o melhor resultado do cluster com o resultado obtido
				jcl.setValueUnlocking("path", bestlocalpath); // atualiza a melhor permutacao do cluster
				jcl.setValueUnlocking("bestDistance", bestlocal); // se melhor atualiza o valor do cluster
			}else {
				jcl.setValueUnlocking("bestDistance", aux2);
			}
			logger.info("   Resultado Final do core: "+bestlocal);
			updateState(filePath,true,"","","","");
			
		}catch(Exception e){
			System.out.println("Error inside SearchStrategyClass");
			e.printStackTrace();
		}
	}
	
	public static void flagNotify() {
		flag = false;
	}
	
	public static String criarDir(String ThreadName) {
		try {
			File diretorio = new File("ResultsDir");
			File interno = new File(diretorio.getAbsolutePath()+"\\"+ThreadName);

			if(diretorio.exists()) {
				diretorio.delete();
			}
			if(!diretorio.exists()) {
				if(diretorio.mkdir()) {
					if(!interno.exists()) {
						if(interno.mkdir()) {
							return interno.getAbsolutePath();
							
						}else {
							return "";
						}
					}else {
						return interno.getAbsolutePath();
						
					}
				}else {
					System.err.println("Erro ao criar diretorio 1");
					return "";
				}
			}else {
				if(!interno.exists()) {
					if(interno.mkdir()) {
						return interno.getAbsolutePath();
						
					}else {
						return "";
					}
				}else {
					return interno.getAbsolutePath();
					
				}
			}
			
			}catch (Exception e) {
				System.err.println("Erro ao criar result files  2");
				return "";
			}
	}
	
	@SuppressWarnings("rawtypes")
	LoggerContext logger() {
		ConfigurationBuilder< BuiltConfiguration > builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setStatusLevel( Level.INFO);
		builder.setConfigurationName("RollingBuilder");
		// create a console appender
		AppenderComponentBuilder appenderBuilder = builder.newAppender("stdout", "CONSOLE").addAttribute("target",
		    ConsoleAppender.Target.SYSTEM_OUT);
		appenderBuilder.add(builder.newLayout("PatternLayout")
		    .addAttribute("pattern", "%d [%t] %-5level: %msg%n"));
		builder.add( appenderBuilder );
		// create a rolling file appender
		LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
		    .addAttribute("pattern", "%d [%t] %-5level: %msg%n");
		ComponentBuilder triggeringPolicy = builder.newComponent("Policies")
		    .addComponent(builder.newComponent("TimeBasedTriggeringPolicy").addAttribute("Interval", "2"))
		    .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "1M"));
		appenderBuilder = builder.newAppender("rolling", "RollingFile")
		    .addAttribute("fileName", "ResultsDir/rolling.log")
		    .addAttribute("filePattern", "ResultsDir/archive/rolling-%d{MM-dd-yy}.log.gz")
		    .add(layoutBuilder)
		    .addComponent(triggeringPolicy);
		builder.add(appenderBuilder);

		// create the new logger
		builder.add( builder.newLogger( "TestLogger", Level.INFO )
		    .add( builder.newAppenderRef( "rolling" ) )
		    .add(builder.newAppenderRef( "stdout" ))
		    .addAttribute( "additivity", false ) );

		builder.add( builder.newRootLogger( Level.DEBUG )
		    .add(builder.newAppenderRef("stdout")));
		return Configurator.initialize(builder.build());
	}

	@SuppressWarnings("deprecation")
	public static void updateState(String filepath,boolean done,String permutation,String amount,String breakpoint,String best) {
		try {
			Properties properties = new Properties();
			FileInputStream in = new FileInputStream(filepath+"\\"+"Results.properties");
			properties.load(new FileInputStream(filepath+"\\"+"Results.properties"));
			in.close();
			FileOutputStream out = new FileOutputStream(filepath+"\\"+"Results.properties");
			properties.setProperty("DONE", done? "true" : "false");// terminou ou nao
			properties.setProperty("PERMUTATION", permutation); // onde começa
			properties.setProperty("AMOUNT", amount); // quanto falta
			properties.setProperty("BEST", best); // melhor resultado encontrado
			properties.setProperty("BREAKPOINT", breakpoint); // fim
			properties.save(out, null);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void creatState(String filepath,BigInteger perm,BigInteger qnt) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(filepath+"\\"+"Results.properties"));
		
		out.write("DONE = "+false);
		out.newLine();
		out.write("PERMUTATION = "+perm);
		out.newLine();
		out.write("AMOUNT = "+qnt);
		out.newLine();
		out.write("BEST = "+0);
		out.newLine();
		out.write("BREAKPOINT = "+0);
		out.close();
	}	
}