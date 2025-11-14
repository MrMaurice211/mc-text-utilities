package io.chaws.textutilities.mixin;

import io.chaws.textutilities.TextUtilities;
import net.minecraft.ChatFormatting;
import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StringUtil.class)
public class StringUtilMixin {

    @Inject(method = "isAllowedChatCharacter", at = @At("HEAD"), cancellable = true)
    private static void isAllowedChatCharacter(
             char c,
            CallbackInfoReturnable<Boolean> cir) {
        if (TextUtilities.getConfig().formattingDisabled()) {
            return;
        }

        // Allow for items and signs to contain the formatting code prefix
        if (c == ChatFormatting.PREFIX_CODE) {
            cir.setReturnValue(true);
        }

    }

}
