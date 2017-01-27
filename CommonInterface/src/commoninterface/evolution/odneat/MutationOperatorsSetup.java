package commoninterface.evolution.odneat;

import java.util.Random;

import commoninterface.evolution.ALGDescriptor;


public class MutationOperatorsSetup {

	public ODNEATCompositeMutator setup(ALGDescriptor descriptor,
			Random random, ODNEATInnovationManager inManager) {
		ODNEATCompositeMutator compMut = new ODNEATCompositeMutator();
		//add link mutator
		setupAddLinkMutator(compMut, descriptor, random, inManager);

		//add node mutator
		setupAddNodeMutator(compMut, descriptor, random, inManager);

		//connection weight mutator
		setupConnectionWeightMutator(compMut, descriptor, random);
		
		compMut.setMutationProbability(descriptor.getPMutation());
		compMut.setRandom(random);

		return compMut;
	}

	

	


	
	private void setupConnectionWeightMutator(ODNEATCompositeMutator compMut,
			ALGDescriptor descriptor, Random random) {
		//double pWeightReplaced, double pToggle, double perturbMagnitude, double weightRange
		ODNEATConnectionWeightMutator weightMut = new ODNEATConnectionWeightMutator(descriptor.getPWeightReplaced(),
				descriptor.getPToggleLink(), descriptor.getMaxPerturb(), 
				descriptor.getWeightRange());
		weightMut.setMutationProbability(descriptor.getPWeightMutation());
		weightMut.setRandom(random);
		compMut.registerMutator(weightMut);

	}

	private void setupAddNodeMutator(ODNEATCompositeMutator compMut,
			ALGDescriptor descriptor, Random random,
			ODNEATInnovationManager inManager) {
		ODNEATAddNodeMutator nodeMut = new ODNEATAddNodeMutator(inManager);
		nodeMut.setMutationProbability(descriptor.getPAddNode());
		nodeMut.setRandom(random);

		compMut.registerMutator(nodeMut);
	}

	private void setupAddLinkMutator(ODNEATCompositeMutator compMut,
			ALGDescriptor descriptor, Random random,
			ODNEATInnovationManager inManager) {
		ODNEATAddLinkMutator linkMut = new ODNEATAddLinkMutator(descriptor.getWeightRange(), inManager);
		linkMut.setMutationProbability(descriptor.getPAddLink());
		linkMut.setRandom(random);
		compMut.registerMutator(linkMut);
	}

}
