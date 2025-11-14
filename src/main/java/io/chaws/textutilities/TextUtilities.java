package io.chaws.textutilities;

import io.chaws.textutilities.config.TextUtilitiesConfig;
import io.chaws.textutilities.handlers.ClickThroughHandler;
import io.chaws.textutilities.handlers.SignEditHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class TextUtilities implements ModInitializer {

	public static TextUtilitiesConfig getConfig() {
		return AutoConfig.getConfigHolder(TextUtilitiesConfig.class).getConfig();
	}

	@Override
	public void onInitialize() {
		AutoConfig.register(TextUtilitiesConfig.class, Toml4jConfigSerializer::new);
		SignEditHandler.initialize();
		ClickThroughHandler.initialize();
	}

}
