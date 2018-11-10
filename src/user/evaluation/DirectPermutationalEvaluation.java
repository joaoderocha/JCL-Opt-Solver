package user.evaluation;

import java.util.List;

import kernel.utils.TrotterJhonson;
import user.delta_evaluation.DeltaEvaluationInterface;
import user.evaluation.EvaluationInterface;

public class DirectPermutationalEvaluation implements EvaluationInterface{
	@SuppressWarnings("unchecked")
	@Override
	public double Evaluation(Object... objects) {

		try {
			Object[] obj = (Object[]) objects[3];
			TrotterJhonson x = (TrotterJhonson) objects[0];
			List<Integer> posicao =	x.NextPermutation();
			List<Integer> CasoTeste = x.getCurrentPermutation();
			DeltaEvaluationInterface ec = (DeltaEvaluationInterface) objects[2].getClass().newInstance();
			int get0 = posicao.get(0);
			int get1 = posicao.get(1);
			int tam = CasoTeste.size();
			double distancia = (double) objects[1];
			if(get0 == tam-1 && get1 == tam-2) {
				distancia -= ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get1-1))+"$",obj);
				distancia -= ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (tam+1)+"$",obj);
				distancia += ec.get( "$"+(CasoTeste.get(get1-1)) +"$","$"+ (CasoTeste.get(get1))+"$",obj);
				distancia += ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (tam+1)+"$",obj);
			}else if(get1 == tam-1 && get0 == tam-2) {
				distancia -= ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (tam+1)+"$",obj);
				distancia -= ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get0-1))+"$",obj);
				distancia += ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (tam+1)+"$",obj);
				distancia += ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get0-1))+"$",obj);
			}else if(get0 == 1 && get1 == 0) {
				distancia -= ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get0+1))+"$",obj);
				distancia -= ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (tam+1)+"$",obj);
				distancia += ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (tam+1)+"$",obj);
				distancia += ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get0+1))+"$",obj);
			}else if(get0 == 0 && get1 == 1) {
				distancia -= ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (tam+1)+"$",obj);
				distancia -= ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get1+1))+"$",obj);
				distancia += ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (tam+1)+"$",obj);
				distancia += ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get1+1))+"$",obj);
			}else if(get0 > get1) {
				distancia -= ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get0+1))+"$",obj);
				distancia -= ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get1-1))+"$",obj);
				distancia += ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get1-1))+"$",obj);
				distancia += ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get0+1))+"$",obj);
			}else if(get1 > get0){
				distancia -= ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get1+1))+"$",obj);
				distancia -= ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get0-1))+"$",obj);
				distancia += ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get1+1))+"$",obj);
				distancia += ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get0-1))+"$",obj);
			}
			return distancia;
		}catch(Exception e) {
			System.out.println("Error inside EvaluationClass");
			e.printStackTrace();
			return 0;
		}
		
	}
}

/*if(get0 == tam-1 && get1 == tam-2) {
	distancia -= ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(0))+"$",obj);
	distancia -= ec.get( "$"+(CasoTeste.get(get1-1)) +"$","$"+ (CasoTeste.get(get0))+"$",obj);
	distancia += ec.get( "$"+(CasoTeste.get(get1-1)) +"$","$"+ (CasoTeste.get(get1))+"$",obj);
	distancia += ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(0))+"$",obj);
}else if(get1 == tam-1 && get0 == tam-2) {
	distancia -= ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(0))+"$",obj);
	distancia -= ec.get( "$"+(CasoTeste.get(get0-1)) +"$","$"+ (CasoTeste.get(get1))+"$",obj);
	distancia += ec.get( "$"+(CasoTeste.get(get0-1)) +"$","$"+ (CasoTeste.get(get0))+"$",obj);
	distancia += ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(0))+"$",obj);
}else if(get0 == 1 && get1 == 0) {
	distancia -= ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get0+1))+"$",obj);
	distancia -= ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(tam-1))+"$",obj);
	distancia += ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(tam-1))+"$",obj);
	distancia += ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get0+1))+"$",obj);
}else if(get0 == 0 && get1 == 1) {
	distancia -= ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(tam-1))+"$",obj);
	distancia -= ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get1+1))+"$",obj);
	distancia += ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(tam-1))+"$",obj);
	distancia += ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get1+1))+"$",obj);
}else if(get0 > get1) {
	distancia -= ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get0+1))+"$",obj);
	distancia -= ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get1-1))+"$",obj);
	distancia += ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get1-1))+"$",obj);
	distancia += ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get0+1))+"$",obj);
}else if(get1 > get0){
	distancia -= ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get1+1))+"$",obj);
	distancia -= ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get0-1))+"$",obj);
	distancia += ec.get( "$"+(CasoTeste.get(get1)) +"$","$"+ (CasoTeste.get(get1+1))+"$",obj);
	distancia += ec.get( "$"+(CasoTeste.get(get0)) +"$","$"+ (CasoTeste.get(get0-1))+"$",obj);
}
return distancia;*/
