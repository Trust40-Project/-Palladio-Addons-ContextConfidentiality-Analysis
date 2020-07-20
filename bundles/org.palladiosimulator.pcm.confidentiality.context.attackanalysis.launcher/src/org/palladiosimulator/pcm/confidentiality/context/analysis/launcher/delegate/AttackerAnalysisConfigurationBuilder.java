package org.palladiosimulator.pcm.confidentiality.context.analysis.launcher.delegate;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.pcm.confidentiality.context.analysis.launcher.constants.Constants;
import org.palladiosimulator.pcm.confidentiality.context.attackanalysis.execution.workflow.config.ContextAnalysisWorkflowConfig;

import de.uka.ipd.sdq.workflow.launchconfig.AbstractWorkflowBasedRunConfiguration;
import de.uka.ipd.sdq.workflow.launchconfig.AbstractWorkflowConfigurationBuilder;

/**
 * This class can build an Attacker analysis specific configuration objects out of a given Eclipse
 * Launch Configuration.
 * 
 * @author majuwa
 *
 */
public class AttackerAnalysisConfigurationBuilder extends AbstractWorkflowConfigurationBuilder {

    public AttackerAnalysisConfigurationBuilder(ILaunchConfiguration configuration, String mode) throws CoreException {
        super(configuration, mode);
    }

    @Override
    public void fillConfiguration(AbstractWorkflowBasedRunConfiguration configuration) throws CoreException {
        if (!configuration.getClass().equals(ContextAnalysisWorkflowConfig.class))
            throw new IllegalArgumentException("configuration is from type " + configuration.getClass() + ", but "
                    + ContextAnalysisWorkflowConfig.class + " expected");
        var config = (ContextAnalysisWorkflowConfig) configuration;
        config.setAdversaryModel(getURI(Constants.ADVERSARY_MODEL_LABEL.getConstant()));
        config.setContextModel(getURI(Constants.CONTEXT_MODEL_LABEL.getConstant()));
        config.setDataModel(getURI(Constants.DATA_MODEL_LABEL.getConstant()));
        config.setRepositoryModel(getURI(Constants.REPOSITORY_MODEL_LABEL.getConstant()));
        config.setAllocationModel(getURI(Constants.ALLOCATION_MODEL_LABEL.getConstant()));
        
    }
    
    private URI getURI(String pathValue) throws CoreException {
        return URI.createURI(getStringAttribute(pathValue)); 
    }

}