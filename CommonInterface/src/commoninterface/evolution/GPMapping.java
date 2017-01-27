package commoninterface.evolution;

import java.io.Serializable;
import java.lang.reflect.Constructor;


import commoninterface.utils.CIArguments;


public abstract class GPMapping<G,C> implements Serializable {


	
	public static void setOutputDirectory(String out){
	}
	
	public GPMapping(CIArguments args){
	}

	public static GPMapping getGPMapping(CIArguments arguments){
		if (!arguments.getArgumentIsDefined("classname"))
			throw new RuntimeException("GPMapping 'classname' not defined: "+arguments.toString());

		String evaluationName = arguments.getArgumentAsString("classname");

		try {
			Constructor<?>[] constructors = Class.forName(evaluationName).getDeclaredConstructors();
			for (Constructor<?> constructor : constructors) {
				Class<?>[] params = constructor.getParameterTypes();
				if (params.length == 1 && params[0] == CIArguments.class) {
					return (GPMapping) constructor.newInstance(arguments);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		throw new RuntimeException("Unknown mapping: " + evaluationName);
	}

	public abstract C decode(G genome);

	public abstract G encode(C p);
}
