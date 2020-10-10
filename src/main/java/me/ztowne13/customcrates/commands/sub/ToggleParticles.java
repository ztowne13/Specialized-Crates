package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;

public class ToggleParticles extends SubCommand {
    public ToggleParticles() {
        super("toggleparticles", 0, "", new String[]{"particles", "togleparticles", "toggleparticle", "tparticles"});
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args) {
        cc.setParticlesEnabled(!cc.isParticlesEnabled());
        if (cc.isParticlesEnabled()) {
            cmds.msgSuccess("Particles have been re-enabled!");
        } else {
            cmds.msgSuccess("Particles have been &2&lTEMPORARILY &adisabled. This particle toggle does NOT persist through reloads or restarts. If you truly want " +
                    "the particles to be disabled, considered changing the 'particle-view-distance' in the config.yml to 0.");
        }
        return true;
    }
}
