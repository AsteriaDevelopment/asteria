package net.caffeinemc.phosphor.module.modules.combat;

import net.caffeinemc.phosphor.module.Module;
import net.caffeinemc.phosphor.module.setting.settings.NumberSetting;

public class ReachModule extends Module {
    public final NumberSetting reach = new NumberSetting("Reach Expand", this, 3, 3, 6, 0.1);

    public ReachModule() {
        super("Reach", "Extends player's reach", Category.COMBAT);
    }
}
