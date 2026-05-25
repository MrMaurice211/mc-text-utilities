package io.chaws.textutilities.client.handlers;

import com.google.common.collect.ImmutableList;
import io.chaws.textutilities.TextUtilities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

//? if >=1.21.9 {
/*import net.minecraft.client.input.CharacterEvent;
*///? }

@Environment(EnvType.CLIENT)
public class FormatButtonsHandler {

    private static final ImmutableList<ChatFormatting> colorFormattings = ImmutableList.of(
            ChatFormatting.BLACK,
            ChatFormatting.DARK_GRAY,
            ChatFormatting.DARK_BLUE,
            ChatFormatting.BLUE,
            ChatFormatting.DARK_GREEN,
            ChatFormatting.GREEN,
            ChatFormatting.DARK_AQUA,
            ChatFormatting.AQUA,
            ChatFormatting.DARK_RED,
            ChatFormatting.RED,
            ChatFormatting.DARK_PURPLE,
            ChatFormatting.LIGHT_PURPLE,
            ChatFormatting.GOLD,
            ChatFormatting.YELLOW,
            ChatFormatting.GRAY,
            ChatFormatting.WHITE
    );

    private static final ImmutableList<ChatFormatting> modifierFormattings = ImmutableList.of(
            ChatFormatting.BOLD,
            ChatFormatting.ITALIC,
            ChatFormatting.UNDERLINE,
            ChatFormatting.STRIKETHROUGH,
            ChatFormatting.OBFUSCATED,
            ChatFormatting.RESET
    );

    public static void initialize() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) ->
                onScreenOpened(screen)
        );
    }

    private static void onScreenOpened(Screen screen) {
        var config = TextUtilities.getConfig();

        // TODO: Make the x and y offset of the screens configurable.
        var xOffsetFromCenter = 0;
        var yOffset = 0;

        if (screen instanceof SignEditScreen ||
                screen instanceof HangingSignEditScreen) {
            if (!config.signFormattingEnabled) {
                return;
            }

            xOffsetFromCenter += 50;
            yOffset += 70;
        } else if (screen instanceof BookEditScreen) {
            if (!config.bookFormattingEnabled) {
                return;
            }

            xOffsetFromCenter += 70;
            yOffset += 20;
        } else if (screen instanceof AnvilScreen) {
            if (!config.anvilFormattingEnabled) {
                return;
            }

            xOffsetFromCenter += 85;
            yOffset += (screen.height / 2) - 80;
        } else {
            // Not a supported screen.
            return;
        }

        var colorButtons = getFormatButtons(
                screen,
                colorFormattings,
                (screen.width / 2) - (120 + xOffsetFromCenter),
                yOffset,
                4
        );

        var modifierButtons = getFormatButtons(
                screen,
                modifierFormattings,
                (screen.width / 2) + (xOffsetFromCenter),
                yOffset,
                6
        );

        //? if <26.1.2 {
        var screenButtons = Screens.getButtons(screen);
        //? } else {
        /*var screenButtons = Screens.getWidgets(screen);
         *///? }
        screenButtons.addAll(colorButtons);
        screenButtons.addAll(modifierButtons);
    }

    private static List<Button> getFormatButtons(
            Screen screen,
            List<ChatFormatting> formats,
            int x,
            int yOffset,
            int rows
    ) {
        List<Button> list = new ArrayList<>();
        var i = 0;
        var gap = 0;
        var buttonSize = 20;

        for (var formatting : formats) {
            var buttonX = x + (i / rows + 1) * (buttonSize + gap);
            var buttonY = i % rows * (buttonSize + gap) + yOffset;

            list.add(
                    getFormatButton(
                            screen,
                            buttonX,
                            buttonY,
                            buttonSize,
                            buttonSize,
                            formatting
                    )
            );

            i++;
        }

        return list;
    }

    private static Button getFormatButton(
            Screen screen,
            int buttonX,
            int buttonY,
            int buttonWidth,
            int buttonHeight,
            ChatFormatting formatting
    ) {

        String beautifiedFormattingName = beatifyEnumName(formatting.getName());
        String tooltip = formatting.toString().concat(beautifiedFormattingName);
        String label = formatting.toString().concat("⬛");
        int buttonFinalWidth = buttonWidth;

        if (formatting.isFormat() || formatting == ChatFormatting.RESET) {
            buttonFinalWidth *= 4;
            label = formatting.toString().concat(beautifiedFormattingName);
            tooltip = label;
        }

        return Button
                .builder(
                        Component.literal(label),
                        cod -> {
                            typeChar(screen, formatting);

                            // Fixes https://github.com/ChristopherHaws/mc-text-utilities/issues/104
                            if (formatting == ChatFormatting.RESET && screen instanceof AnvilScreen) {
                                typeChar(screen, ChatFormatting.WHITE);
                            }

                        }
                )
                .pos(buttonX, buttonY)
                .size(buttonFinalWidth, buttonHeight)
                .tooltip(Tooltip.create(Component.literal(tooltip)))
                .build();
    }

    private static void typeChar(Screen screen, ChatFormatting formatting) {
        char prefixChar = ChatFormatting.PREFIX_CODE;
        char formattingChar = formatting.getChar();
        //? if >=26.1.2 {
        /*screen.charTyped(new CharacterEvent(prefixChar));
        screen.charTyped(new CharacterEvent(formattingChar));
        *///? } elif <1.21.9 {
         screen.charTyped(prefixChar, 0);
         screen.charTyped(formattingChar, 0);
        //? } else {
        /*screen.charTyped(new CharacterEvent(prefixChar, 0));
        screen.charTyped(new CharacterEvent(formattingChar, 0));
        *///? }

    }

    private static String beatifyEnumName(String name) {
        String[] split = name.split("_");
        String concat = String.join(" ", split);
        // Uppercase just the first letter of all the string
        return concat.substring(0, 1).toUpperCase() + concat.substring(1);
    }

}