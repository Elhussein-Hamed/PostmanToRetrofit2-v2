package com.hamed.postmantoretrofit2v2.classgeneration

import com.robohorse.robopojogenerator.GenerationDelegate
import com.robohorse.robopojogenerator.models.GenerationModel
import com.robohorse.robopojogenerator.models.ProjectModel

class GeneratePOJOActionController(
    private val generationDelegate: GenerationDelegate
) {

    fun generate(model: GenerationModel, projectModel: ProjectModel) {
        generationDelegate.runGenerationTask(model, projectModel)
    }
}