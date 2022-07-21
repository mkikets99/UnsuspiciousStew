package schauweg.unsuspiciousstew.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.*;
//import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import schauweg.unsuspiciousstew.StewInfo;

import java.util.List;

import static net.minecraft.client.gui.DrawableHelper.fill;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int heldItemTooltipFade;

    @Shadow
    private ItemStack currentStack;

    @Shadow
    private int scaledWidth;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Shadow
    private int scaledHeight;

    private String ticksToSeconds(int duration){
        duration /= 20;
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        seconds = duration % 60;
        minutes = (duration / 60) % 60;
        hours = duration / 60 / 60;
        String sHours = (hours!=0)? hours+":":"";
        String sMin = ((minutes<=9&&minutes!=0)? "0"+minutes:""+minutes)+":";
        String sSec = (seconds<=9)? "0"+seconds:""+seconds;
        return sHours + sMin + sSec;
    }
    @Inject(at = @At("HEAD"), method = "renderHeldItemTooltip")
    public void onInjectTooltip(MatrixStack matrixStack, CallbackInfo info) {

        this.client.getProfiler().push("selectedItemName");
        if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
            MutableText mutableText = (Text.literal("")).append(this.currentStack.getName()).formatted(this.currentStack.getRarity().formatting);
            if (this.currentStack.hasCustomName()) {
                mutableText.formatted(Formatting.ITALIC);
            }

            int mainItemNameWidth = this.getTextRenderer().getWidth(mutableText);
            int j = (this.scaledWidth - mainItemNameWidth) / 2;
            int hotbarOffset = this.scaledHeight - 59;
            if (!this.client.interactionManager.hasStatusBars()) {
                hotbarOffset += 14;
            }

            int opacity = (int)((float)this.heldItemTooltipFade * 256.0F / 10.0F);
            if (opacity > 255) {
                opacity = 255;
            }

            if (opacity > 0) {
                matrixStack.push();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int var10001 = j - 2;
                int var10002 = hotbarOffset - 2;
                int var10003 = j + mainItemNameWidth + 2;
                this.getTextRenderer().getClass();
                fill(matrixStack, var10001, var10002, var10003, hotbarOffset + 9 + 2, this.client.options.getTextBackgroundColor(0));
                if (currentStack.getItem() == Items.SUSPICIOUS_STEW){
                    NbtCompound tag = currentStack.getNbt();
                    if (tag != null) {
                        NbtList effects = tag.getList("Effects", 10);
                        int effectsCount = effects.size();
                        for (int i = 0; i < effectsCount; i++) {
                            tag = effects.getCompound(i);
                            int duration = tag.getInt("EffectDuration");
                            StatusEffect effect = StatusEffect.byRawId(tag.getByte("EffectId"));
                            String time = (this.ticksToSeconds(duration));
                            Text completeText = Text.translatable(effect.getTranslationKey()).append(" "+time);
                            j = (this.scaledWidth - getTextRenderer().getWidth(completeText)) / 2;
                            this.getTextRenderer().drawWithShadow(matrixStack, completeText, (float)j, (float)hotbarOffset-(i*14)-14, 13421772 + (opacity << 24));
                        }
                    }
                }
                RenderSystem.disableBlend();
                matrixStack.pop();
            }
        }

    }


}
