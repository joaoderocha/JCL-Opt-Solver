package user.load_input;

import implementations.dm_kernel.user.JCL_FacadeImpl;
import interfaces.kernel.JCL_facade;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import javafx.util.Pair;
/**
 * 
 * @author Joao
 *Abstract class used to guarantee essentials implementations used on kernel
 */
public abstract class LoadAbstract implements LoadInterface{
	
	public void load(String filePath){
		JCL_facade jcl = JCL_FacadeImpl.getInstance();
		jcl.instantiateGlobalVar("upper", new Double(Double.MAX_VALUE));
		jcl.instantiateGlobalVar("lower", new Double(Double.MIN_VALUE));
		jcl.instantiateGlobalVar("path", "");
		Pair<String,Double> x = new Pair<String,Double>("", Double.MAX_VALUE);
		jcl.instantiateGlobalVar("bestResult",x);
		ObjectSet<String> vertices = new ObjectOpenHashSet<String>();
		jcl.instantiateGlobalVar("vertices", vertices);
		x= null;
	}

}
