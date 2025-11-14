package io.chaws.textutilities.client.mixin;

import io.chaws.textutilities.TextUtilities;
import io.chaws.textutilities.utils.FormattingUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
//? if >=1.21.9 {
import net.minecraft.client.input.KeyEvent;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> {

    public AnvilScreenMixin(
            AnvilMenu handler,
            Inventory playerInventory,
            Component title,
            ResourceLocation texture
    ) {
        super(handler, playerInventory, title, texture);
    }

    @Shadow
    private EditBox name;

    @Unique
    private AnvilScreen getAnvilScreen() {
        return ((AnvilScreen) (Object) this);
    }

    @Inject(method = "subInit", at = @At(value = "TAIL"))
    protected void subInit(CallbackInfo ci) {
        // Defaults to: OrderedText.styledForwardsVisitedString(string, Style.EMPTY);
        //? if <1.21.9 {
         /*this.name.setFormatter((abc, def) ->
        *///?} else {
        this.name.addFormatter((abc, def) ->
        //?}
                Component.literal(abc).getVisualOrderText()
        );
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void inject(
            //? if <1.21.9 {
            /*int i,
            int j,
            int k,
            *///?} else {
            KeyEvent event,
            //?}
            CallbackInfoReturnable<Boolean> ci
    ) {
        if (!TextUtilities.getConfig().anvilFormattingEnabled) {
            return;
        }

        this.getAnvilScreen().setFocused(this.name);
    }

    // FIXME: This type has changed, need to fix...
    // @Inject(method = "onSlotUpdate", at = @At(value = "TAIL"))
    // private void onSlotUpdate(
    // 	ScreenHandler handler,
    // 	int slotId,
    // 	ItemStack stack,
    // 	CallbackInfo ci
    // ) {
    // 	if (!TextUtilities.getConfig().anvilFormattingEnabled) {
    // 		return;
    // 	}

    // 	if (slotId != 0) {
    // 		return;
    // 	}

    // 	var displayElement = stack.getSubNbt(ItemStack.DISPLAY_KEY);
    // 	if (displayElement == null) {
    // 		return;
    // 	}

    // 	var nameElement = displayElement.get(ItemStack.NAME_KEY);
    // 	if (nameElement == null) {
    // 		return;
    // 	}

    // 	var json = nameElement.asString();
    // 	var text = Text.Serialization.fromJson(json);
    // 	if (text == null) {
    // 		return;
    // 	}

    // 	var sb = new StringBuilder();
    // 	text.visit(
    // 		(StringVisitable.StyledVisitor<String>) (style, asString) -> {
    // 			var color = style.getColor();
    // 			if (color != null) {
    // 				var formatting = Formatting.byName(color.getName());
    // 				if (formatting != null) {
    // 					sb.append(formatting);
    // 				}
    // 			}

    // 			if (style.isBold()) {
    // 				sb.append(Formatting.BOLD);
    // 			}

    // 			if (style.isItalic()) {
    // 				sb.append(Formatting.ITALIC);
    // 			}

    // 			if (style.isUnderlined()) {
    // 				sb.append(Formatting.UNDERLINE);
    // 			}

    // 			if (style.isStrikethrough()) {
    // 				sb.append(Formatting.STRIKETHROUGH);
    // 			}

    // 			if (style.isObfuscated()) {
    // 				sb.append(Formatting.OBFUSCATED);
    // 			}

    // 			if (style.isEmpty()) {
    // 				sb.append(Formatting.RESET);
    // 			}

    // 			sb.append(asString);
    // 			return Optional.empty();
    // 		},
    // 		Style.EMPTY
    // 	);

    // 	var formattedName = sb.toString();
    // 	this.nameField.setText(formattedName);
    // }

    //	@ModifyArg(method = "onRenamed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
    //	private Packet onRenamed_sendPacket(Packet packet) {
    //		if (!(packet instanceof RenameItemC2SPacket renameItemC2SPacket)) {
    //			return packet;
    //		}
    //
    //		var name = renameItemC2SPacket.getName();
    //		name = FormattingUtils.replaceBuiltInPrefixWithConfiguredPrefix(name);
    //		return new RenameItemC2SPacket(name);
    //	}

    @ModifyVariable(method = "onNameChanged", at = @At("HEAD"), argsOnly = true)
    private String onNameChanged(String name) {
        if (!TextUtilities.getConfig().anvilFormattingEnabled) {
            return name;
        }

        return FormattingUtils.replaceBuiltInPrefixWithConfiguredPrefix(name);
    }

    @ModifyArg(
            method = "slotChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/EditBox;setValue(Ljava/lang/String;)V"
            )
    )
    private String slotChanged_EditBox_setValue(String name) {
        if (!TextUtilities.getConfig().anvilFormattingEnabled) {
            return name;
        }

        return FormattingUtils.replaceConfiguredPrefixWithBuiltInPrefix(name);
    }

}
