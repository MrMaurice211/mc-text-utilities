package io.chaws.textutilities.client.mixin;

import io.chaws.textutilities.utils.FormattingUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(EditBox.class)
public class EditBoxMixin {
	@Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Ljava/lang/String;substring(I)Ljava/lang/String;", ordinal = 1))
	private String appendFormatting(String string, int i) {
		var strings = FormattingUtils.splitWithFormatting(string, i);

		return FormattingUtils.getLastFormattingCodes(strings.getA(), 2)
			.concat(strings.getB());
	}
}
