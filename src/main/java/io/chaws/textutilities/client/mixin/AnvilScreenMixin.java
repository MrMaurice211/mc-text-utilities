package io.chaws.textutilities.client.mixin;

import io.chaws.textutilities.TextUtilities;
import io.chaws.textutilities.utils.FormattingUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
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

//? if >=26.1.2 {
/*import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
*///? } else {
import net.minecraft.resources.ResourceLocation;
//? }

//? if >= 1.21.9 {
/*import net.minecraft.client.input.KeyEvent;
*///? }

@Environment(EnvType.CLIENT)
@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> {

    //? if < 26.1 {
    public AnvilScreenMixin(
            AnvilMenu handler,
            Inventory playerInventory,
            Component title,
            ResourceLocation texture
    ) {
        super(handler, playerInventory, title, texture);
    }
    //?} else {

    /*@Shadow
    @Final
    private static Identifier ANVIL_LOCATION;

    public AnvilScreenMixin(
            AnvilMenu handler,
            Inventory playerInventory,
            Component title
    ) {
        super(handler, playerInventory, title, ANVIL_LOCATION);
    }
    *///? }

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
        this.name.setFormatter((abc, def) ->
         //?} else {
        /*this.name.addFormatter((abc, def) ->
                *///?}
                Component.literal(abc).getVisualOrderText()
        );
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void inject(
            //? if <1.21.9 {
            int i,
            int j,
            int k,
            //?} else {
            /*KeyEvent event,
            *///?}
            CallbackInfoReturnable<Boolean> ci
    ) {
        if (!TextUtilities.getConfig().anvilFormattingEnabled) {
            return;
        }

        this.getAnvilScreen().setFocused(this.name);
    }

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
