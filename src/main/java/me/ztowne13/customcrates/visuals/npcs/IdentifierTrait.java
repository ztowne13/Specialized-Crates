package me.ztowne13.customcrates.visuals.npcs;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;

public class IdentifierTrait extends Trait
{
    public IdentifierTrait()
    {
        super("IdentifierTrait");
        isCrate = true;
    }
    @Persist(value="isCrate")
    boolean isCrate = false; // the default value of @Persist saves the value under the field name (in this case, 'myVariable').

    public boolean isCrate()
    {
        return isCrate;
    }
}
