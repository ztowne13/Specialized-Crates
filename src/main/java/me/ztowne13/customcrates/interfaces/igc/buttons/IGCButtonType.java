package me.ztowne13.customcrates.interfaces.igc.buttons;

public enum IGCButtonType
{
    SAVE,
    BACK,
    RELOAD,
    SAVE_AND_RELOAD,
    REWARD_FILTER;

    public IGCButton createInstance()
    {
        switch(this)
        {
            case SAVE:

                break;
            case RELOAD:

                break;
            case REWARD_FILTER:
                return new IGCButtonRewardFilter();
            case SAVE_AND_RELOAD:

                break;
            case BACK:
                return new IGCButtonBack();
        }
        return null;
    }
}
