package edu.kit.ipd.sdq.kamp4attack.core;

import java.util.stream.Collectors;

import org.palladiosimulator.pcm.confidentiality.attackerSpecification.attackSpecification.CredentialAttack;

import edu.kit.ipd.sdq.kamp.propagation.AbstractChangePropagationAnalysis;
import edu.kit.ipd.sdq.kamp4attack.core.changepropagation.AssemblyChange;
import edu.kit.ipd.sdq.kamp4attack.core.changepropagation.ContextChanges;
import edu.kit.ipd.sdq.kamp4attack.core.changepropagation.ResourceChange;
import edu.kit.ipd.sdq.kamp4attack.model.modificationmarks.KAMP4attackModificationmarks.CredentialChange;
import edu.kit.ipd.sdq.kamp4attack.model.modificationmarks.KAMP4attackModificationmarks.KAMP4attackModificationmarksFactory;

//TODO This is probably the interesting component.

/**
 * The change propagation analysis of KAMP4APS 1. determines a seed population of affected
 * components (resp. provided roles) 2. calculates in iterations: a) inter-component propagation b)
 * intra-component propagation 3. generates internal modification marks for affected elements
 * 
 * - elements which were already part of a seed population are not further investigated
 * 
 * 
 * @author Sandro Koch
 *
 */
public class AttackPropagationAnalysis implements AbstractChangePropagationAnalysis<BlackboardWrapper> {

    private CredentialChange changePropagationDueToCredential;

    @Override
    public void runChangePropagationAnalysis(BlackboardWrapper board) {
        // Setup
        this.changePropagationDueToCredential = KAMP4attackModificationmarksFactory.eINSTANCE.createCredentialChange();

        // prepare

        createInitialStructure(board);

        // Calculate
        do {
            changePropagationDueToCredential.setChanged(false);
            calculateAndMarkResourcePropagation(board);
            calculateAndMarkToAssemblyPropagation(board);
            calculateAndMarkToContextPropagation(board);
            
        } while (changePropagationDueToCredential.isChanged());

        
        
        
     

    }

    
    private void calculateAndMarkToContextPropagation(BlackboardWrapper board) {
        var change = new ContextChanges(board);
        change.calculateContextToResourcePropagation(changePropagationDueToCredential);
        change.calculateContextToAssemblyPropagation(changePropagationDueToCredential);
    }
    
    private void calculateAndMarkToAssemblyPropagation(BlackboardWrapper board) {
        var change = new AssemblyChange(board);
        change.calculateAssemblyToContextPropagation(changePropagationDueToCredential);
        change.calculateAssemblyToResourcePropagation(changePropagationDueToCredential);
    }
    
    private void calculateAndMarkResourcePropagation(BlackboardWrapper board) {
        var change = new ResourceChange(board);
        change.calculateResourceToAssemblyPropagation(changePropagationDueToCredential);
        change.calculateResourceToContextPropagation(changePropagationDueToCredential);
    }


    /**
     * Creates the initial propagation steps from the seed modification
     * @param board
     */
    private void createInitialStructure(BlackboardWrapper board) {
        var repository = board.getModificationMarkRepository();
        var seedModification = repository.getSeedModifications();
        var attackers = seedModification.getAttackcomponent();
        if (attackers == null)
            throw new IllegalStateException("No seed modification found");
        
        repository.getChangePropagationSteps().clear();
        
        for (var attacker : attackers) {
            var localAttacker = attacker.getAffectedElement();
            for( var capabilities: localAttacker.getCapabilities()){
                if(capabilities instanceof CredentialAttack) {
                    var credentialAttack = (CredentialAttack) capabilities;
                    
                    var contextChange = credentialAttack.getContexts().stream().map(context -> {
                        var change = KAMP4attackModificationmarksFactory.eINSTANCE.createContextChange();
                        change.setAffectedElement(context);
                        return change;
                    }).collect(Collectors.toList());
                    changePropagationDueToCredential.getContextchange().addAll(contextChange);
                }
            }
            var affectedRessourcesList = localAttacker.getCompromisedResources().stream().map(resource -> {
                var change = KAMP4attackModificationmarksFactory.eINSTANCE.createCompromisedResource();
                change.setAffectedElement(resource);
                return change;
            }).collect(Collectors.toList());
            
            changePropagationDueToCredential.getCompromisedresource().addAll(affectedRessourcesList);
            
            var affectedComponentsList = localAttacker.getCompromisedComponents().stream().map(assemblyComponent -> {
                var change = KAMP4attackModificationmarksFactory.eINSTANCE.createCompromisedAssembly();
                change.setAffectedElement(assemblyComponent);
                return change;
            }).collect(Collectors.toList());
            
            changePropagationDueToCredential.getCompromisedassembly().addAll(affectedComponentsList);
        }
        board.getModificationMarkRepository().getChangePropagationSteps().add(changePropagationDueToCredential);
    }



}