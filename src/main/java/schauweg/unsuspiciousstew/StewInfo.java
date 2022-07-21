package schauweg.unsuspiciousstew;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
//import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;

import java.util.List;

public class StewInfo {

    static MinecraftClient minecraft = MinecraftClient.getInstance();

    private static String ticksToSeconds(int duration){
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
    public static void onInjectTooltip(Object stackIn, List<Text> list) {
        ItemStack stack = (ItemStack) stackIn;
        if (stack != null && (stack.getItem() == Items.SUSPICIOUS_STEW)){
            NbtCompound tag = stack.getNbt();
            if (tag != null) {
                NbtList effects = tag.getList("Effects", 10);
                int effectsCount = effects.size();

                for (int i = 0; i < effectsCount; i++) {
                    tag = effects.getCompound(i);
                    int duration = tag.getInt("EffectDuration");
                    StatusEffect effect = StatusEffect.byRawId(tag.getByte("EffectId"));
                    list.add(Text.translatable(effect.getTranslationKey())
                            .append(" "+ticksToSeconds(duration))
                            .setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
                }
            }
        }
    }

}

