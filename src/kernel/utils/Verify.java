package kernel.utils;



import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Verify {
	public String execute() {
		System.out.print(Thread.currentThread().getName() + "  ");
		File diretorio = new File("ResultsDir");
		File interno = new File(diretorio.getAbsolutePath()+"\\"+Thread.currentThread().getName());
		
		Properties properties = new Properties();
		try {
			if(!diretorio.exists()) {
				return "-1:0:0:0:0";
			}else {
				if(!interno.exists()) {
					return "-1:0:0:0:0";
				}else {
					properties.load(new FileInputStream(interno.getAbsolutePath()+"\\"+"Results.properties"));
					if(properties.getProperty("DONE").equals("true")) {
						return "1:0:0:0:0";
					}else {
						String permutacao = properties.getProperty("PERMUTATION"); // Qual foi a ultima permutação pesquisada
						String quantidade = properties.getProperty("AMOUNT"); // Qual foi a quantidade que falta
						String BestPath = properties.getProperty("BEST"); // melhor resultado
						String limite = properties.getProperty("BREAKPOINT"); // Quantidade que ele precisa alcançar
						return +0+":"+permutacao+":"+quantidade+":"+limite+":"+BestPath;
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			return "-1:0:0:0:0";
		}
	}
}
