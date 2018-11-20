package user.evaluation;

import java.util.List;

import user.delta_evaluation.DeltaEvaluationInterface;

public class CompleteEvaluation implements EvaluationInterface {

	@Override
	public double Evaluation(Object... objects) {
		DeltaEvaluationInterface ec = (DeltaEvaluationInterface) objects[0];
		@SuppressWarnings("unchecked")
		List<Integer> CasoTeste = (List<Integer>) objects[2];
		int tam =(int) objects[1];
		double distancia = 0;
		Object[] dataStruct = (Object[]) objects[3];
		
		for(int j=0;j<tam-1;j++) { // soma as distancias inicialmente
			distancia += ec.get(("$"+(CasoTeste.get(j))+"$"), ("$"+(CasoTeste.get(j+1))+"$"), dataStruct);
		}
		distancia += ec.get("$"+(CasoTeste.get(tam-1))+"$","$"+(tam+1)+"$",dataStruct);
		distancia += ec.get("$"+(CasoTeste.get(0))+"$","$"+(tam+1)+"$",dataStruct);
		return distancia;
	}

}
