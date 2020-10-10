package me.ztowne13.customcrates.crates.types.display.npcs;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;

public class IdentifierTrait extends Trait {
    @Persist(value = "isCrate")
    boolean isCrate;

    public IdentifierTrait() {
        super("IdentifierTrait");
        isCrate = true;
    }
    // the default value of @Persist saves the value under the field name (in this case, 'myVariable').

    public boolean isCrate() {
        return isCrate;
    }
}
