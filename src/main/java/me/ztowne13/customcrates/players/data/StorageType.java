package me.ztowne13.customcrates.players.data;

public enum StorageType
{
    MYSQL(true),

    FLATFILE(true),

    PLAYERFILES(true);

    boolean enabled;

    StorageType(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
