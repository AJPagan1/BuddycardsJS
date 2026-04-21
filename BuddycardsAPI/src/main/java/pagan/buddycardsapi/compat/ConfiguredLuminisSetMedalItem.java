package pagan.buddycardsapi.compat;

import pagan.buddycardsapi.medal.ConfiguredMedalType;
import com.wildcard.buddycards.core.BuddycardSet;
import com.wildcard.buddycards.item.LuminisSetMedalItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ConfiguredLuminisSetMedalItem extends LuminisSetMedalItem {
    private final ConfiguredMedalType configuredType;

    public ConfiguredLuminisSetMedalItem(ConfiguredMedalType configuredType, BuddycardSet set, Item.Properties properties) {
        super(configuredType, set, properties);
        this.configuredType = configuredType;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal("Effect: " + configuredType.effectKey() + " " + configuredType.level()));
    }
}
