package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.datagen.MarioverseBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

//@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class RegistryEventHandlers {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(
                event.includeClient(),
                new MarioverseBlockStateProvider(output, existingFileHelper)
        );
    }

}
