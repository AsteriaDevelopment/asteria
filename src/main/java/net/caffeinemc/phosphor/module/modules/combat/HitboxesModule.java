package net.caffeinemc.phosphor.module.modules.combat;

import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.settings.BooleanSetting;
import net.caffeinemc.phosphor.module.setting.settings.NumberSetting;

public class HitboxesModule extends Module {
    public final NumberSetting expand = new NumberSetting("Expansion", this, 0, 0, 1, 0.1);
    public final BooleanSetting render = new BooleanSetting("Enable Render", this, true);

    public HitboxesModule() {
        super("Hitboxes", "Expands hitboxes", Category.COMBAT);
    }
}
