package io.chaws.textutilities.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.chaws.textutilities.config.TextUtilitiesConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

//? if <26.1 {
import me.shedaniel.autoconfig.AutoConfig;
//? } else {
 /*import me.shedaniel.autoconfig.AutoConfigClient;
*///? }

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
        //? if <26.1.2 {
		 return parent -> AutoConfig.getConfigScreen(TextUtilitiesConfig.class, parent).get();
        //? } else {
        /*return parent -> AutoConfigClient.getConfigScreen(TextUtilitiesConfig.class, parent).get();
        *///? }
	}
}
