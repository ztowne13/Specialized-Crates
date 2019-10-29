package me.ztowne13.customcrates.interfaces.igc.inputmenus;

import me.ztowne13.customcrates.interfaces.igc.IGCMenu;

/**
 * Created by ztowne13 on 6/13/16.
 */
public abstract class InputMenuGUI
{
    InputMenu im;

    public InputMenuGUI(InputMenu im)
    {
        this.im = im;
    }

    public abstract void initMsg();

    public abstract void runFor(IGCMenu igcm, String s);


    public InputMenu getIm()
    {
        return im;
    }

    public void setIm(InputMenu im)
    {
        this.im = im;
    }
}
