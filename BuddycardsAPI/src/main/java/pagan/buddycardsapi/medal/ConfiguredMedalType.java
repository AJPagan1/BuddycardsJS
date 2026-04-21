package pagan.buddycardsapi.medal;

import com.wildcard.buddycards.item.IMedalTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public final class ConfiguredMedalType implements IMedalTypes {
    private final MobEffect effect;
    private final int amplifier;
    private final int duration;

    public ConfiguredMedalType(MobEffect effect, int level, int duration) {
        this.effect = effect;
        this.amplifier = Math.max(0, level - 1);
        this.duration = Math.max(40, duration);
    }

    @Override
    public void applyEffect(Player player, int ignored) {
        player.addEffect(new MobEffectInstance(effect, duration, amplifier, true, false, true));
    }

    public String effectKey() { return BuiltInRegistries.MOB_EFFECT.getKey(effect).toString(); }
    public int level() { return amplifier + 1; }

    public static MobEffect resolveEffect(String rawName) {
        if (rawName == null || rawName.isBlank()) throw new IllegalArgumentException("Medal effect name cannot be blank");
        String normalized = rawName.trim().toLowerCase().replace(' ', '_').replace('-', '_');
        ResourceLocation exact = ResourceLocation.tryParse(normalized.contains(":") ? normalized : "minecraft:" + normalized);
        if (exact != null && BuiltInRegistries.MOB_EFFECT.containsKey(exact)) return BuiltInRegistries.MOB_EFFECT.get(exact);
        for (ResourceLocation key : BuiltInRegistries.MOB_EFFECT.keySet()) if (key.getPath().equals(normalized)) return BuiltInRegistries.MOB_EFFECT.get(key);
        throw new IllegalArgumentException("Unknown mob effect '" + rawName + "'. Use any registered effect name or id, including modded effects, such as Strength, Fire Resistance, minecraft:strength, or modid:custom_effect.");
    }
}
