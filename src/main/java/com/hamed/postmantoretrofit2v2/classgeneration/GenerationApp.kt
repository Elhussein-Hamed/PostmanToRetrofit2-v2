package com.hamed.postmantoretrofit2v2.classgeneration

import com.robohorse.robopojogenerator.appModule
import com.robohorse.robopojogenerator.coreModule
import com.robohorse.robopojogenerator.generatorModule
import com.robohorse.robopojogenerator.models.GenerationModel
import com.robohorse.robopojogenerator.models.ProjectModel
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class GenerationApp : KoinComponent {

    private var controller: GeneratePOJOActionController

    init {
        GlobalContext.getOrNull() ?: startKoin {
            modules(appModule + generatorModule + coreModule)
        }

        controller = GeneratePOJOActionController(GlobalContext.get().get())
    }

    fun generate(model: GenerationModel, projectModel: ProjectModel) {
        controller.generate(model, projectModel)
    }


}