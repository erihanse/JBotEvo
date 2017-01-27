package commoninterface.evolution;

import java.util.ArrayList;
import java.util.Iterator;



public class SerialisationHelper {
	
	public static final double NODE = 0d, LINK = 1d, FEATURE = 2d;


	  public static synchronized ArrayList<ODNEATGene> deserialize(String ser) {
		  
		   String[] split = ser.split(",");
	        ArrayList<Double> stuff = new ArrayList<Double>();
	        for (String s : split) {
	            stuff.add(Double.parseDouble(s));
	        }

	        ArrayList<ODNEATGene> genes = new ArrayList<ODNEATGene>();
	        Iterator<Double> iter = stuff.iterator();
	        while (iter.hasNext()) {
	            double type = iter.next();
	            if (type == NODE) {
	                int id = (int) (double) iter.next();
	                double sigF = iter.next();
	                int t = (int) (double) iter.next();
	                double bias = iter.next();
	                //HIDDEN (OTHER FORMAT)
	                if(t == 0)
	                	t = ODNEATNodeGene.HIDDEN;
	                //output
	                else if(t == 1)
	                	t = ODNEATNodeGene.OUTPUT;
	                //input
	                else if(t == 2)
	                	t = ODNEATNodeGene.INPUT;
	                //ADJUST
	                id--;
	                ODNEATNodeGene n = new ODNEATNodeGene(id, t);
	                n.setBias(bias);
	                genes.add(n);
	            } else if (type == LINK) {
	                boolean enabled = iter.next() == 1d;
	                int from = (int) (double) iter.next();
	                int to = (int) (double) iter.next();
	                double weight = iter.next();
	                //adjust
	                from--;
	                to--;
	                genes.add(new ODNEATLinkGene(0, enabled, from, to, weight));
	            } else if(type == FEATURE) {
	                double weight = iter.next();
	                int innov = iter.next().intValue();
	                //genes.add(new NEATFeatureGene(innov, weight));
	            }

	        }
	       return genes;
	    }
}
