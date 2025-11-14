package io.chaws.textutilities.mixin;

import io.chaws.textutilities.TextUtilities;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatFormatting.class)
public class FormattingMixin {

    @Inject(at = @At("HEAD"), method = "stripFormatting", cancellable = true)
    private static void strip(@Nullable String string, CallbackInfoReturnable<@Nullable String> ci) {
        if (TextUtilities.getConfig().formattingDisabled()) {
            return;
        }

        // Don't strip the formatting codes out of strings
        ci.setReturnValue(string);
    }

}
