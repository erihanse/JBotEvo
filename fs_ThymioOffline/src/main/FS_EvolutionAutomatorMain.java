package main;

import src.Main;

public class FS_EvolutionAutomatorMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String folder = "confs/";

		//navigation and obstacle avoidance
		//String file = "_thymio_online_nav_with_macro_autom.conf";
		
		//homing 1 robot
		//String file = "_thymio_online_homing_autom.conf";
		
		//aggregation 3 robots
		String file = "_thymio_online_agg_autom_2_60secs.conf";
		Main.main(new String[]{(folder+file)});

		
		/*********
		 * OLD EXPERIMENTATION
		 */
		//String file = "_thymionavobs_evo_autom.conf";
	//	String file = "_thymio_turn_autom.conf";
		
		//aggregation
		/*String file = "_thymio_online_agg_autom.conf";
		//Main.main(new String[]{(folder+file)});
		
		file = "_thymio_online_agg_autom_macro_std.conf";
		//Main.main(new String[]{(folder+file)});
		
		file = "_thymio_online_agg_autom_macro_specialisation.conf";
		Main.main(new String[]{(folder+file)});*/
		
		//homing 1 robot
		//String file = "_thymio_online_homing_autom.conf";
		//Main.main(new String[]{(folder+file)});
			
		//file = "_thymio_online_homing_autom_macro_std.conf";
		//Main.main(new String[]{(folder+file)});
				
		//String file = "_thymio_online_homing_autom_macro_specialisation_v2.conf";
		
		
		
		
		/*****
		 * scalability
		 */
		/*file = "_thymio_online_agg_autom_9robots.conf";
		//Main.main(new String[]{(folder+file)});
		
		file = "_thymio_online_agg_autom_macro_std_9robots.conf";
		//Main.main(new String[]{(folder+file)});
		
		file = "_thymio_online_agg_autom_macro_specialisation_9robots.conf";
		Main.main(new String[]{(folder+file)});
		
		file = "_thymio_online_agg_autom_macro_specialisation_6robots.conf";
		Main.main(new String[]{(folder+file)});*/
		
	}

}
