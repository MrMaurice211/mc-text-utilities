package io.chaws.textutilities.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ChatComponent.class)
public class ChatComponentMixin {
	@Inject(method = "getWidth(D)I", at = @At("HEAD"), cancellable = true)
	private static void getWidth(double widthOption, CallbackInfoReturnable<Integer> ci) {
		ci.setReturnValue(Mth.floor(widthOption * 480.0 + 40.0));
	}

	@Inject(method = "getHeight(D)I", at = @At("HEAD"), cancellable = true)
	private static void getHeight(double heightOption, CallbackInfoReturnable<Integer> ci) {
		ci.setReturnValue(Mth.floor(heightOption * 360.0 + 20.0));
	}
}
