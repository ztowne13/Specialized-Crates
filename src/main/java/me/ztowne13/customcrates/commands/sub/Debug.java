package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.items.NBTTagBuilder;
import me.ztowne13.customcrates.interfaces.items.attributes.BukkitGlowEffect;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Debug extends SubCommand {
    boolean debug = true;

    public Debug() {
        super("debug", 1, "");
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args) {
        if (cmds.getCmdSender() instanceof Player) {
            Player player = (Player) cmds.getCmdSender();

            if (debug) {
                if (args.length == 1) {
                    ChatUtils.msgInfo(player, "Commands: iteminfo[tostring,-{-nolore,-ignoreexcluded,-stripcolor}], applytag[{tag id}], glow[-, asbuilder]");
                    return true;
                }

                if (args[1].equalsIgnoreCase("iteminfo")) {
                    boolean noLoreArg = false,
                            ignoreExcludedArg = false,
                            stripColorArg = false;

                    for (int i = 0; i < args.length; i++) {
                        if (args[i].equalsIgnoreCase("-nolore")) {
                            noLoreArg = true;
                        } else if (args[i].equalsIgnoreCase("-ignoreexcluded")) {
                            ignoreExcludedArg = true;
                        } else if (args[i].equalsIgnoreCase("-stripcolor")) {
                            stripColorArg = true;
                        }
                    }

                    ItemStack stack = player.getItemInHand();
                    ItemMeta im = stack.getItemMeta();

                    if (args.length == 3 && args[2].equalsIgnoreCase("tostring")) {
                        cmds.msg("toString(): " + stack.toString());
                        cmds.msg("im.toString(): " + im.toString());
                    }

                    cmds.msg("Type: " + stack.getType().name());

                    if (!noLoreArg) {
                        cmds.msg("Lore:");
                        for (String s : im.getLore()) {
                            if (stripColorArg) {
                                cmds.getCmdSender().sendMessage("- " + ChatUtils.fromChatColor(s));
                            } else {
                                cmds.msg("- " + s);
                            }
                        }
                    }


                    cmds.msg("Enchants: ");
                    if (VersionUtils.Version.v1_13.isServerVersionOrLater()) {
                        for (Enchantment ench : im.getEnchants().keySet())
                            cmds.msg("- " + ench.getKey().getKey() + " lvl " + im.getEnchants().get(ench));
                    }

                    cmds.msg("Data: " + stack.getDurability());
                    cmds.msg("NBT Tags:");
                    for (String s : NBTTagBuilder.getFrom(stack, ignoreExcludedArg))
                        cmds.msg("- " + s);

                } else if (args[1].equalsIgnoreCase("applytag")) {
                    String tag = args[2];
                    String id = args[3];
                    String combined = tag + " " + id;

                    player.setItemInHand(NBTTagBuilder.applyTo(player.getItemInHand(), combined));
                    cmds.msgSuccess("Added tag '" + combined + "'");
                } else if (args[1].equalsIgnoreCase("glow")) {
                    if (args.length > 2) {
                        if (args[2].equalsIgnoreCase("asbuilder")) {
                            ItemBuilder builder = new ItemBuilder(player.getItemInHand());
                            builder.setGlowing(true);
                            ItemBuilder dupedBuilder = new ItemBuilder(builder);
                            player.setItemInHand(dupedBuilder.get());
                            cmds.msg("Added glow effect via itembuilder");
                        }
                    } else {
                        new BukkitGlowEffect(player.getItemInHand()).apply();
                        cmds.msg("Added glow effect");
                    }
                }
            } else {
                ChatUtils.msgError(player, "This command is disabled in non-development builds.");
            }
        }
        return false;
    }
}
