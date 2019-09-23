package me.ztowne13.customcrates.logging;

import me.ztowne13.customcrates.crates.Crate;

/**
 * Created by ztowne13 on 6/12/16.
 */
public enum StatusLoggerEvent
{
    ACTION_ADD(true, "Actions", "Added action $?$ with tier $?$", ""),

    FIREWORK_ADD(true, "Fireworks", "Added firework $?$ with tier $?$", ""),

    FIREWORK_DATA_INVALIDCOLOR(false, "Fireworks", "Failed to completely load firework $?$",
            "$?$ is not a valid $?$ color."),

    FIREWORK_DATA_SUCCESS(true, "Fireworks", "Successfully loaded firework $?$", "NONE"),

    FIREWORK_DATA_PARTIALSUCCESS(false, "Fireworks", "Failed to completely load firework $?$", "$?$"),

    FIREWORK_DATA_FAILURE(false, "Fireworks", "Failed to completely load firework $?$",
            "Improperly formatted COLOR;COLOR, FADE;FADE, TRAIL, FLICKER, TYPE, POWER"),

    HOLOGRAM_ANIMATION_TYPE_FAILURE_NONEXISTENT(false, "Holograms", "Failed to load the hologram's animation.",
            "The hologram.animation.type value does not exist."),

    HOLOGRAM_ANIMATION_TYPE_FAILURE_INVALID(false, "Holograms", "Failed to load the hologram's animation.",
            "$?$ is not a valid Animation type."),

    HOLOGRAM_ANIMATION_SPEED_FAILURE_NONEXISTENT(false, "Holograms", "Failed to load the hologram's animation.",
            "The hologram.animation.speed value does not exist."),

    HOLOGRAM_ANIMATION_SPEED_FAILURE_INVALID(false, "Holograms", "Failed to load the hologram's animation.",
            "$?$ is not a valid number / speed."),

    HOLOGRAM_ANIMATION_PREFIXES_DISABLED(false, "Holograms",
            "Disabled hologram animation and changed the animation type to NULL.",
            "The prefixes are invalid and thus the animation won't work."),

    HOLOGRAM_ANIMATION_PREFIXES_NONEXISTENT(false, "Holograms", "Failed to load the hologram's animation.",
            "The hologram.animation.prefixes value does not exist."),

    HOLOGRAM_ANIMATION_PREFIXES_MISFORMATTED(false, "Holograms", "Failed to load the hologram's animation.",
            "The hologram.animation.prefixes value is not a properly formatted list."),

    HOLOGRAM_ADDLINE_SUCCESS(true, "Hologram", "Added hologram line $?$", "NONE"),

    @Deprecated
    HOLOGRAM_ADDLINE_FAIL_TOMANY(false, "Hologram", "Failed to add hologram line $?$",
            "Maximum lines for a hologram is: $?$"),

    HOLOGRAM_REWARD_HOLOGRAM(true, "Hologram", "Added the reward-hologram $?$", "NONE"),

    HOLOGRAM_REWARD_HOLOGRAM_DURATION_SUCCESS(true, "Hologram", "Added the reward-hologram's duration.", "NONE"),

    HOLOGRAM_REWARD_HOLOGRAM_DURATION_INVALID(true, "Hologram", "Failed to load the reward-hologram's duration",
            "It is not a valid integer."),

    HOLOGRAM_REWARD_HOLOGRAM_YOFFSET_SUCCESS(true, "Hologram", "Added the reward-hologram's Y-Offset.", "NONE"),

    HOLOGRAM_REWARD_HOLOGRAM_YOFFSET_INVALID(true, "Hologram", "Failed to load the reward-hologram's Y-Offset",
            "It is not a valid integer."),

    LUCKYCHEST_CHANCE_MISFORMATTED(false, "Lucky Chest", "Failed to load the 'chance' setting.",
            "Not properly formatted 'INTEGER/INTEGER'"),

    LUCKYCHEST_CHANCE_NONEXISTENT(false, "Lucky Chest", "The lucky chest 'chance' variable doesn't exist.", "NONE"),

    LUCKYCHEST_BLWL_INVALID(false, "Lucky Chest", "Failed to load the 'is-block-list-whitelist' setting.",
            "Not specified as 'true' or 'false'"),

    LUCKYCHEST_BLWL_NONEXISTENT(false, "Lucky Chest",
            "The lucky chest 'is-block-list-whitelist' variable doesn't and has assumed to be 'true'.", "NONE"),

    LUCKYCHEST_WORLD_INVALID(false, "Lucky Chest", "$?$ is not a valid world for the lucky chests to spawn in.", "NONE"),

    LUCKYCHEST_BLOCKLIST_INVALIDBLOCK(false, "Lucky Chest",
            "Failed to load the material '$?$' for the 'block-list' setting.",
            "Not a valid material such as DIRT."),

    LUCKYCHEST_BLOCKLIST_INVALID(false, "Lucky Chest", "Failed to load the 'block-list' setting.",
            "Not a proper STRING list."),

    LUCKYCHEST_BLOCKLIST_NONEXISTENT(false, "Lucky Chest",
            "The lucky chest 'block-list' variable doesn't exist and will allow ALL blocks for Lucky Chests.", "NONE"),

    LUCKYCHEST_NOVALUES(false, "Lucky Chest", "This crate is a lucky chest but does is not properly set up.",
            "Does not contain any of the required 'lucky-chest' settings."),

    PARTICLE_ADD_SUCCESS(true, "Particles", "Added particle $?$", ""),

    PARTICLE_INVALID(false, "Particles", "Particle string '$?$' is invalid", "Particle effect '$?$' is invalid"),

    PARTICLE_STRING_INVALID(false, "Particles", "Particle string '$?$' is invalid", "NONE"),

    PARTICLE_ANIMATION_INVALID(false, "Particles",
            "The particle animation '$?$' is invalid and will thus not have an animation.", "NONE"),

    PARTICLE_ANIMATION_COLOR_INVALID(false, "Particles",
            "The particle animation '$?$''s R/G/B colors are invalid and will thus not have an color.", "NONE"),

    REWARD_ADD_SUCCESS(true, "Rewards", "Added reward $?$", "NONE"),

    REWARD_NONEXISTENT(false, "Rewards", "The 'rewards' path does not exist in the config", "NONE"),

    REWARD_CHANCE_NONEXISTENT(false, "Rewards", "Failed to load CHANCE for reward: $?$",
            "The 'chance' value does not exist."),

    REWARD_NAME_NONEXISTENT(false, "Rewards", "Failed to load NAME for reward: $?$", "The 'name' value does not exist."),

    REWARD_RARITY_NONEXISTENT(false, "Rewards", "Failed to load RARITY for reward: $?$",
            "The 'rarity' value does not exist."),

    REWARD_ITEM_NONEXISTENT(false, "Rewards", "Failed to load ITEM for reward: $?$",
            "The 'item' value does not exist or is misconfigured."),

    REWARD_COMMAND_INVALID(false, "Rewards", "Failed to load COMMANDS for reward: $?$",
            "The 'commands' value does not exist or is not a valid list."),

    REWARD_ENCHANT_INVALID(false, "Rewards", "Failed to load specified enchantments for reward $?$", "$?$"),

    REWARD_POTION_INVALID(false, "Rewards", "Failed to load specified potion for reward $?$",
            "'$?$' is not formatted potiontype;duration;amplifier, potiontype is not a valid potion type, or duration/amplifier are not valid numbers."),

    REWARD_AMOUNT_INVALID(false, "Rewards", "Failed to load amount for reward $?$", "It is not a valid number."),

    SOUND_NONEXISTENT(false, "Sounds", "The sound value: $?$ is improperly setup.", "Sound '$?$' is invalid."),

    SOUND_PITCH_INVALID(false, "Sounds", "Failed to load pitch for sound $?$", "$?$ is not a valid number."),

    SOUND_PITCH_NONEXISTENT(false, "Sounds", "Failed to load pitch for sound $?$", "The PITCH value does not exist."),

    SOUND_VOLUME_INVALID(false, "Sounds", "Failed to load volume for sound $?$", "$?$ is not a valid number."),

    SOUND_VOLUME_NONEXISTENT(false, "Sounds", "Failed to load volume for sound $?$", "The VOLUME value does not exist."),

    SOUND_ADD_SUCCESS(true, "Sounds", "Successfully loaded the sound $?$", "NONE"),

    SOUND_ADD_IMPROPER_SETUP(false, "Sounds", "The sound value: $?$ is not set up properly.",
            "It does not have a valid SOUND, PITCH, or VOLUME"),

    MULTICRATEINVENTORY_OBJECTS_INVALID(false, "MultiCrate Inventory", "Failed to load the MultiCrate object: $?$", "$?$"),

    MULTICRATEINVENTORY_OBJECTS_MISCONFIGURED(false, "MultiCrate Inventory", "The MultiCrate object list is invalid.",
            "Missing the 'gui.objects' value or the list is misconfigured."),

    MULTICRATEINVENTORY_ROW_MISCONFIGURED(false, "MultiCrate Inventory", "The 'gui.row' value is misconfigured",
            "Missing the 'gui.objects' value or the list is misconfigured."),

    MULTICRATEINVENTORY_NONEXISTENT(false, "MultiCrate Inventory", "Both the 'gui.row' and 'gui.objects' value are missing",
            "The values are nonexistent"),

    SETTINGS_CRATE_LORE_ADDLINE_SUCCESS(true, "Settings", "Added line to the crate's lore: $?$", "NONE"),

    SETTINGS_CRATE_ENCHANTMENT_ADD_SUCCESS(true, "Settings", "Added enchantment $?$ to the crate", "NONE"),

    SETTINGS_CRATE_ENCHANTMENT_ADD_FAILURE(false, "Settings", "Failed to load the 'crate.enchantment' value.", "$?$"),

    SETTINGS_CRATE_POTION_ADD_FAILURE(false, "Settings", "Failed to load the 'crate.potion-effects' value.", "$?$"),

    SETTINGS_CRATE_GLOW_FAILURE(false, "Settings", "Failed to load the 'crate.glow' value.", "$?$"),

    SETTINGS_CRATE_AMOUNT_FAILURE(false, "Settings", "Failed to load the 'crate.amount' value.", "$?$"),

    SETTINGS_CRATE_SUCCESS(true, "Settings", "Set up crate item.", "NONE"),

    SETTINGS_CRATE_FAILURE_DISABLE(false, "Settings", "Failed to load the crate settings for the crate... disabling", "$?$"),

    SETTINGS_CRATE_FAILURE(false, "Settings", "Failed to load the crate item.", "$?$"),

    SETTINGS_KEY_LORE_ADDLINE(true, "Settings", "Added line to the key's lore: $?$", "NONE"),

    SETTINGS_KEY_ENCHANTMENT_ADD_SUCCESS(true, "Settings", "Added enchantment $?$ to the key", "NONE"),

    SETTINGS_KEY_ENCHANTMENT_ADD_FAILURE(false, "Settings", "Failed to load the 'key.enchantment' value.", "$?$"),

    SETTINGS_KEY_POTION_ADD_FAILURE(false, "Settings", "Failed to load the 'key.potion-effects' value.", "$?$"),

    SETTINGS_KEY_GLOW_FAILURE(false, "Settings", "Failed to load the 'key.glow' value.", "$?$"),

    SETTINGS_KEY_AMOUNT_FAILURE(false, "Settings", "Failed to load the 'key.amount' value.", "$?$"),

    SETTINGS_KEY_SUCCESS(true, "Settings", "Set up key item.", "NONE"),

    SETTINGS_KEY_REQUIRE_NONEXISTENT(false, "Settings", "Failed to load the 'key.require' value",
            "The 'key.require' value does not exist."),

    SETTINGS_KEY_FAILURE_DISABLE(false, "Settings", "Failed to load the key settings for the crate... disabling the crate",
            "$?$"),

    SETTINGS_KEY_FAILURE(false, "Settings", "Failed to load the key item.", "$?$"),

    SETTINGS_OBTAINMETHOD_SUCCESS(true, "Settings", "Loaded the 'obtain-method' value.", "NONE"),

    SETTINGS_OBTAINMETHOD_INVALID(false, "Settings", "Failed to load the 'obtain-method' value.",
            "$?$ is not a valid ObtainType."),

    SETTINGS_OBTAINMETHOD_NONEXISTENT(false, "Settings", "The 'obtain-method' value does not exist.", "NONE"),

    SETTINGS_ANIMATION_SUCCESS(true, "Settings", "Loaded the 'open.crate-animation' value.", "NONE"),

    SETTINGS_ANIMATION_INVALID(false, "Settings", "Failed to load the 'open.crate-animation' value.",
            " $?$ is not a valid type."),

    SETTINGS_ANIMATION_NONEXISTENT(false, "Settings", "The 'open.crate-animation' value does not exist.", "NONE"),

    SETTINGS_COOLDOWN_SUCCESS(true, "Settings", "Loaded the 'cooldown' value", "NONE"),

    SETTINGS_COOLDOWN_INVALID(false, "Settings", "Failed to load the 'cooldown' value.",
            "It is not of a valid integer (number) type."),

    SETTINGS_DISPLAYTYPE_SUCCESS(true, "Settings", "Loaded the 'display.type' crate value.", "NONE"),

    SETTINGS_DISPLAYTYPE_NONEXISTENT(false, "Settings",
            "Failed to find a crate display type, settings default to block and re-loading values.",
            "Value does not exist."),

    SETTINGS_DISPLAYTYPE_INVALID(false, "Settings", "Failed to load the 'display.type' value.",
            "$?$ is not BLOCK, MOB, or NPC"),

    SETTINGS_DISPLAYTYPE_FAIL_NOCITIZENS(false, "Settings", "Failed to set the display type to NPC / MOB",
            "The 'Citizens' plugin is not installed."),

    SETTINGS_DISPLAYTYPE_CREATURE_SUCCESS(true, "Settings", "Loaded the 'display.creature' value.", "NONE"),

    SETTINGS_DISPLAYTYPE_CREATURETYPE_INVALID(false, "Settings", "Failed to load the 'display.creature' value.",
            "$?$ is not a valid, or usable, creature type."),

    SETTINGS_DISPLAYTYPE_CREATURETYPE_NONEXISTENT(false, "Settings", "Failed to load the 'display.creature' value.",
            "It does not exist while type is set to MOB."),

    SETTINGS_DISPLAYTYPE_DISPLAYNAME_SUCCESS(true, "Settings", "Loaded the 'display.name' value.", "NONE"),

    SETTINGS_DISPLAYTYPE_DISPLAYNAME_NONEXISTENT(false, "Settings", "Failed to load the 'display.name' value.",
            "It does not exist while type is set to NPC"),

    SETTINGS_INVENTORYNAME_SUCCESS(true, "Settings", "Loaded the 'inventory-name' value", "NONE"),

    SETTINGS_INVENTORYNAME_NONEXISTENT(false, "Settings", "Failed to load the 'inventory-name' value.",
            "It does not exist. You may want to add 'inventory-name' followed by the crate's inventory's name, but it is not required."),

    SETTINGS_INVENTORYNAME_INVALID(false, "Settings", "Failed to load the 'inventory-name' value.",
            "The name is longer than 32 characters (Automatically shortening)."),

    SETTINGS_PERMISSION_SUCCESS(true, "Settings", "Loaded the 'permission' value", "NONE"),

    SETTINGS_AUTOCLOSE_SUCCESS(true, "Settings", "Loaded the 'auto-close' value", "NONE"),

    SETTINGS_HOLOGRAMOFFSET_SUCCESS(true, "Settings", "Loaded the 'hologram-offset' value", ""),

    SETTINGS_HOLOGRAMOFFSET_FAILURE(false, "Settings", "The hologram-offset value failed to load",
            "The value is not a valid double (number)."),

    ANIMATION_ENCLOSEMENT_INVNAME_SUCCESS(true, "Animation", "Successfully loaded the enclose animation animation inv-name.",
            "NONE"),

    ANIMATION_ENCLOSEMENT_INVNAME_INVALID(false, "Animation", "Failed to load the enclose animation inv-name value.",
            "The inv-name value does not exist."),

    ANIMATION_ENCLOSEMENT_INVROWS_SUCCESS(true, "Animation",
            "Successfully loaded the enclose animation animation inventory-rows.", ""),

    ANIMATION_ENCLOSEMENT_INVROWS_INVALID(false, "Animation", "Failed to load the enclose animation inventory-rows.",
            "It is either nonexistent or not a valid number."),

    ANIMATION_ENCLOSEMENT_FILLBLOCK_SUCCESS(true, "Animation", "Successfully loaded the enclose animation fill-block value.",
            ""),

    ANIMATION_ENCLOSEMENT_FILLBLOCK_INVALID(false, "Animation", "Failed to load the enclose animation fill-block value.",
            "$?$"),

    ANIMATION_ENCLOSEMENT_UPDATESPEED_SUCCESS(true, "Animation", "Successfully loaded the enclose animation update-speed.",
            ""),

    ANIMATION_ENCLOSEMENT_UPDATESPEED_INVALID(false, "Animation", "Failed to load the enclose animation update-speed.",
            "It is either nonexistent or not a valid number."),

    ANIMATION_ENCLOSEMENT_REWARDAMOUNT_SUCCESS(true, "Animation", "Successfully loaded the enclose animation reward-amount.",
            ""),

    ANIMATION_ENCLOSEMENT_REWARDAMOUNT_INVALID(false, "Animation", "Failed to load the enclose animation reward-amount.",
            "It is either nonexistent or not a valid number."),

    ANIMATION_ENCLOSEMENT_TICKSOUND_SOUND_SUCCESS(true, "Animation", "Loaded the enclose animation tick-sound SOUND.",
            "NONE"),

    ANIMATION_ENCLOSEMENT_TICKSOUND_SOUND_FAILURE(false, "Animation", "Failed to load the enclose tick-sound.",
            "The sound specified is not a valid sound, or doesn't exist."),

    ANIMATION_ENCLOSEMENT_TICKSOUND_VOLUMEPITCH_FAILURE(false, "Animation",
            "Failed to load the enclose animation VOLUME and PITCH.",
            "The values do not exist. (NOTE: These values are non-critical)"),

    ANIMATION_ENCLOSEMENT_TICKSOUND_VOLUME_SUCCESS(true, "Animation", "Loaded the enclose tick-sound VOLUME.", "NONE"),

    ANIMATION_ENCLOSEMENT_TICKSOUND_VOLUME_INVALID(false, "Animation",
            "Failed to load the enclose animation tick-sound VOLUME.", "$?$ is not a valid number."),

    ANIMATION_ENCLOSEMENT_TICKSOUND_PITCH_SUCCESS(true, "Animation", "Loaded the enclose tick-sound PITCH.", "NONE"),

    ANIMATION_ENCLOSEMENT_TICKSOUND_PITCH_INVALID(false, "Animation", "Failed to load the enclose tick-sound PITCH.",
            "$?$ + is not a valid number."),

    ANIMATION_DISCOVER_INVNAME_SUCCESS(true, "Animation", "Successfully loaded the discover animation animation inv-name.",
            "NONE"),

    ANIMATION_DISCOVER_INVNAME_INVALID(false, "Animation", "Failed to load the discover animation inv-name value.",
            "The inv-name value does not exist."),

    ANIMATION_DISCOVER_MINREWARDS_SUCCESS(true, "Animation", "Successfully loaded the discover animation min-rewards value.",
            ""),

    ANIMATION_DISCOVER_MINREWARDS_INVALID(false, "Animation", "Failed to loaded the discover animation min-rewards value.",
            "It is either nonexistent or an invalid number."),

    ANIMATION_DISCOVER_MAXREWARDS_SUCCESS(true, "Animation", "Successfully loaded the discover animation max-rewards value.",
            ""),

    ANIMATION_DISCOVER_MAXREWARDS_INVALID(false, "Animation", "Failed to loaded the discover animation max-rewards value.",
            "It is either nonexistent or an invalid number."),

    ANIMATION_DISCOVER_INVROWS_SUCCESS(true, "Animation", "Successfully loaded the discover animation inventory-rows value.",
            ""),

    ANIMATION_DISCOVER_INVROWS_INVALID(false, "Animation", "Failed to loaded the discover animation inventory-rows value.",
            "It is either nonexistent or an invalid number."),

    ANIMATION_DISCOVER_RANDDISPLAYLOCATION_SUCCESS(true, "Animation",
            "Successfully loaded the discover animation random-display-duration value.", ""),

    ANIMATION_DISCOVER_RANDDISPLAYLOCATION_INVALID(false, "Animation",
            "Failed to load the discover animation random-display-duration value.",
            "It is either nonexistent or an invalid number."),

    ANIMATION_DISCOVER_COUNT_SUCCESS(true, "Animation", "Successfully loaded the discover animation count value.", ""),

    ANIMATION_DISCOVER_COUNT_INVALID(false, "Animation", "Failed to load the discover animation count value.",
            "It is either nonexistent or an invalid true / false value."),

    ANIMATION_DISCOVER_COVERBLOCK_MATERIAL_INVALID(false, "Animation", "Failed to load the discover animation cover-block.",
            "$?$ is an invalid material."),

    ANIMATION_DISCOVER_COVERBLOCK_DURABILITY_INVALID(false, "Animation",
            "Failed to load the durability for the discover animation cover-block", "$?$ is an invalid number."),

    ANIMATION_DISCOVER_COVERBLOCK_INVALID(false, "Animation", "Failed to load the discover animation cover-block value.",
            "It is nonexistent or not formatted MATERIAL;DURABILITY"),

    ANIMATION_DISCOVER_COVERBLOCK_SUCCESS(true, "Animation", "Successfully loaded the discover animation cover-block value.",
            ""),

    ANIMATION_DISCOVER_REWARDBLOCK_MATERIAL_INVALID(false, "Animation", "Failed to load the discover animation reward-block.",
            "$?$ is an invalid material."),

    ANIMATION_DISCOVER_REWARDBLOCK_DURABILITY_INVALID(false, "Animation",
            "Failed to load the durability for the discover animation reward-block", "$?$ is an invalid number."),

    ANIMATION_DISCOVER_REWARDBLOCK_INVALID(false, "Animation", "Failed to load the discover animation reward-block value.",
            "It is nonexistent or not formatted MATERIAL;DURABILITY"),

    ANIMATION_DISCOVER_REWARDBLOCK_SUCCESS(true, "Animation", "Successfully loaded the discover animation reward-block value.",
            ""),

    ANIMATION_DISCOVER_TICKSOUND_SOUND_SUCCESS(true, "Animation",
            "Successfully loaded the discover animation tick-sound SOUND", ""),

    ANIMATION_DISCOVER_TICKSOUND_SOUND_FAILURE(false, "Animation", "Failed to loud the discover animation tick-sound SOUND",
            "The sound is invalid."),

    ANIMATION_DISCOVER_TICKSOUND_VOLUME_SUCCESS(true, "Animation",
            "Successfully loaded the discover animation tick-sound VOLUME", ""),

    ANIMATION_DISCOVER_TICKSOUND_VOLUME_INVALID(false, "Animation",
            "Failed to load the discover animation tick-sound VOLUME", "$?$ is not a valid number."),

    ANIMATION_DISCOVER_TICKSOUND_PITCHVOL_INVALID(false, "Animation",
            "Failed to load the discover animation tick-sound VOLUME and PITCH",
            "The sound is not formatted SOUND, VOLUME, PITCH"),

    ANIMATION_DISCOVER_TICKSOUND_PITCH_SUCCESS(true, "Animation",
            "Successfully loaded the discover animation tick-sound PITCH", ""),

    ANIMATION_DISCOVER_TICKSOUND_PITCH_INVALID(false, "Animation", "Failed to load the discover animation tick-sound PITCH",
            "$?$ is not a valid number."),

    ANIMATION_DISCOVER_CLICKSOUND_SOUND_SUCCESS(true, "Animation",
            "Successfully loaded the discover animation click-sound SOUND", ""),

    ANIMATION_DISCOVER_CLICKSOUND_SOUND_FAILURE(false, "Animation",
            "Failed to loud the discover animation click-sound SOUND", "The sound is invalid."),

    ANIMATION_DISCOVER_CLICKSOUND_VOLUME_SUCCESS(true, "Animation",
            "Successfully loaded the discover animation click-sound VOLUME", ""),

    ANIMATION_DISCOVER_CLICKSOUND_VOLUME_INVALID(false, "Animation",
            "Failed to load the discover animation click-sound VOLUME", "$?$ is not a valid number."),

    ANIMATION_DISCOVER_CLICKSOUND_PITCHVOL_INVALID(false, "Animation",
            "Failed to load the discover animation click-sound VOLUME and PITCH",
            "The sound is not formatted SOUND, VOLUME, PITCH"),

    ANIMATION_DISCOVER_CLICKSOUND_PITCH_SUCCESS(true, "Animation",
            "Successfully loaded the discover animation click-sound PITCH", ""),

    ANIMATION_DISCOVER_CLICKSOUND_PITCH_INVALID(false, "Animation",
            "Failed to load the discover animation click-sound PITCH", "$?$ is not a valid number."),

    ANIMATION_DISCOVER_UNCOVERSOUND_SOUND_SUCCESS(true, "Animation",
            "Successfully loaded the discover animation uncover-sound SOUND", ""),

    ANIMATION_DISCOVER_UNCOVERSOUND_SOUND_FAILURE(false, "Animation",
            "Failed to loud the discover animation uncover-sound SOUND", "The sound is invalid."),

    ANIMATION_DISCOVER_UNCOVERSOUND_VOLUME_SUCCESS(true, "Animation",
            "Successfully loaded the discover animation uncover-sound VOLUME", ""),

    ANIMATION_DISCOVER_UNCOVERSOUND_VOLUME_INVALID(false, "Animation",
            "Failed to load the discover animation uncover-sound VOLUME", "$?$ is not a valid number."),

    ANIMATION_DISCOVER_UNCOVERSOUND_PITCHVOL_INVALID(false, "Animation",
            "Failed to load the discover animation uncover-sound VOLUME and PITCH",
            "The sound is not formatted SOUND, VOLUME, PITCH"),

    ANIMATION_DISCOVER_UNCOVERSOUND_PITCH_SUCCESS(true, "Animation",
            "Successfully loaded the discover animation uncover-sound PITCH", ""),

    ANIMATION_DISCOVER_UNCOVERSOUND_PITCH_INVALID(false, "Animation",
            "Failed to load the discover animation uncover-sound PITCH", "$?$ is not a valid number."),

    ANIMATION_CSGO_TICKSOUND_SOUND_SUCCESS(true, "Animation", "Loaded the CSGO animation tick-sound SOUND.", "NONE"),

    ANIMATION_CSGO_TICKSOUND_SOUND_FAILURE(false, "Animation", "Failed to load the CSGO tick-sound.",
            "The sound specified is not a valid sound, or doesn't exist."),

    ANIMATION_CSGO_TICKSOUND_VOLUMEPITCH_FAILURE(false, "Animation", "Failed to load the CSGO animation VOLUME and PITCH.",
            "The values do not exist. (NOTE: These values are non-critical)"),

    ANIMATION_CSGO_TICKSOUND_VOLUME_SUCCESS(true, "Animation", "Loaded the CSGO tick-sound VOLUME.", "NONE"),

    ANIMATION_CSGO_TICKSOUND_VOLUME_INVALID(false, "Animation", "Failed to load the CSGO animation tick-sound VOLUME.",
            "$?$ is not a valid number."),

    ANIMATION_CSGO_TICKSOUND_PITCH_SUCCESS(true, "Animation", "Loaded the CSGO tick-sound PITCH.", "NONE"),

    ANIMATION_CSGO_TICKSOUND_PITCH_INVALID(false, "Animation", "Failed to load the CSGO tick-sound PITCH.",
            "$?$ + is not a valid number."),

    ANIMATION_CSGO_IDBLOCK_SUCCESS(true, "Animation", "Loaded the CSGO identifier-block.", "NONE"),

    ANIMATION_CSGO_IDBLOCK_INVALID(false, "Animations", "Failed to load the CSGO identifier-block.",
            "$?$ is an invalid material or the byte value is not a number."),

    ANIMATION_CSGO_IDBLOCK_NONEXISTENT(false, "Animation", "Failed to load the CSGO identifier-block value.",
            "The identifier-block value does not exist."),

    ANIMATION_CSGO_FINALTICKLENGTH_SUCCESS(true, "Animation", "Loaded the CSGO final-crate-tick-length value.", "NONE"),

    ANIMATION_CSGO_FINALTICKLENGTH_INVALID(false, "Animation", "Failed to load the CSGO final-crate-tick-length.",
            "The value does not exist or is not a valid number."),

    ANIMATION_CSGO_GLASSUPDATE_SUCCESS(true, "Animation", "Loaded the CSGO tile-update-ticks", "NONE"),

    ANIMATION_CSGO_GLASSUPDATE_INVALID(false, "Animation", "Failed to load the CSGO tile-update-ticks",
            "The value is not a valid number or is nonexistent."),

    ANIMATION_CSGO_CLOSESPEED_SUCCESS(true, "Animation", "Loaded the CSGO close-speed", "NONE"),

    ANIMATION_CSGO_CLOSESPEED_INVALID(false, "Animation", "Failed to load the CSGO close-speed",
            "The value is not a valid number or is nonexistent."),

    ANIMATION_CSGO_TICKSPEED_SUCCESS(true, "Animation", "Loaded the CSGO tick-speed-per-run value.", "NONE"),

    ANIMATION_CSGO_TICKSPEED_INVALID(false, "Animation", "Failed to load the CSGO tick-speed-per-run value.",
            "It value does not exist or is not a valid number."),

    ANIMATION_CSGO_FILLERBLOCK_MATERIAL_INVALID(false, "Animation", "Failed to load the $?$ CSGO filler-block material.",
            "That material does not exist."),

    ANIMATION_CSGO_FILLERBLOCK_MATERIAL_SUCCESS(true, "Animation", "Loaded the $?$ CSGO filler-block material.", "NONE"),

    ANIMATION_CSGO_FILLERBLOCK_ITEM_INVALID(false, "Animation", "Failed to load the CSGO filler-block item: $?$",
            "It doesn't have a valid MATERIAL, or ID, or is not formatted MATERIAL;ID"),

    ANIMATION_CSGO_FILLERBLOCK_NONEXISTENT(false, "Animation", "Failed to load the CSGO filler-blocks.",
            "The filler-blocks value does not exist."),

    ANIMATION_ROULETTE_INVENTORYNAME_SUCCESS(true, "Animation", "Loaded the roulette animation default inventory name: $?$",
            "NONE"),

    ANIMATION_ROULETTE_INVENTORYNAME_NONEXISTENT(false, "Animation", "Failed to load the roulette animation inventory name.",
            "The 'inv-name' value is either missing or the value is misformatted."),

    ANIMATION_ROULETTE_TICKSOUND_SOUND_SUCCESS(true, "Animation", "Loaded the Roulette animation tick-sound SOUND.", "NONE"),

    ANIMATION_ROULETTE_TICKSOUND_SOUND_FAILURE(false, "Animation", "Failed to load the Roulette tick-sound.",
            "The sound specified is not a valid sound, or doesn't exist."),

    ANIMATION_ROULETTE_TICKSOUND_VOLUMEPITCH_FAILURE(false, "Animation",
            "Failed to load the Roulette animation VOLUME and PITCH.",
            "The values do not exist. (NOTE: These values are non-critical)"),

    ANIMATION_ROULETTE_TICKSOUND_VOLUME_SUCCESS(true, "Animation", "Loaded the Roulette tick-sound VOLUME.", "NONE"),

    ANIMATION_ROULETTE_TICKSOUND_VOLUME_INVALID(false, "Animation",
            "Failed to load the Roulette animation tick-sound VOLUME.", "$?$ is not a valid number."),

    ANIMATION_ROULETTE_TICKSOUND_PITCH_SUCCESS(true, "Animation", "Loaded the Roulette tick-sound PITCH.", "NONE"),

    ANIMATION_ROULETTE_TICKSOUND_PITCH_INVALID(false, "Animation", "Failed to load the Roulette tick-sound PITCH.",
            "$?$ + is not a valid number."),

    ANIMATION_ROULETTE_FINALTICKLENGTH_SUCCESS(true, "Animation", "Loaded the roulette final-crate-tick-length value.",
            "NONE"),

    ANIMATION_ROULETTE_FINALTICKLENGTH_INVALID(false, "Animation", "Failed to load the roulette final-crate-tick-length.",
            "The value does not exist or is not a valid number."),

    ANIMATION_ROULETTE_GLASSUPDATE_SUCCESS(true, "Animation", "Loaded the Roulette tile-update-ticks", "NONE"),

    ANIMATION_ROULETTE_GLASSUPDATE_INVALID(false, "Animation", "Failed to load the Roulette tile-update-ticks",
            "The value is not a valid number or is nonexistent."),

    ANIMATION_ROULETTE_TICKSPEED_SUCCESS(true, "Animation", "Loaded the roulette tick-speed-per-run value.", "NONE"),

    ANIMATION_ROULETTE_TICKSPEED_INVALID(false, "Animation", "Failed to load the roulette tick-speed-per-run value.",
            "It value does not exist or is not a valid number."),

    ANIMATION_ROULETTE_RANDOMBLOCK_MATERIAL_NONEXISTENT(false, "Animation",
            "Failed to load the $?$ roulette random-block material.", "That material does not exist."),

    ANIMATION_ROULETTE_RANDOMBLOCK_MATERIAL_SUCCESS(true, "Animation", "Loaded the $?$ roulette random-block material.",
            "NONE"),

    ANIMATION_ROULETTE_RANDOMBLOCK_ITEM_INVALID(false, "Animation", "Failed to load the roulette random-block item: $?$",
            "It doesn't have a valid MATERIAL, or ID, or is not formatted MATERIAL;ID"),

    ANIMATION_ROULETTE_RANDOMBLOCK_NONEXISTENT(false, "Animation", "Failed to load the roulette random-blocks.",
            "The random-blocks value does not exist."),

    ANIMATION_OPENCHEST_CHEST_OPEN_DURATION_INVALID(false, "Animation",
            "Failed to load the openchest chest-open-duration value.", "It is either nonexistent or an invalid number."),

    ANIMATION_OPENCHEST_CHEST_OPEN_DURATION_SUCCESS(true, "Animation",
            "Successfully loaded the chestopen chest-open-duration value.", "NONE"),

    ANIMATION_OPENCHEST_EARLY_REWARD_SUCCESS(true, "Animation",
            "Successfully loaded the open chest early-reward-hologram value.", "NONE"),

    ANIMATION_OPENCHEST_EARLY_REWARD_INVALID(false, "Animation",
            "Failed to load the open chest early-reward-hologram value.",
            "It is either nonexistent or not a valid true/false value."),

    ANIMATION_OPENCHEST_REWARD_HOLO_DELAY_SUCCESS(true, "Animation",
            "Successfully loaded the open chest reward-holo-delay value.", "NONE"),

    ANIMATION_OPENCHEST_REWARD_HOLO_DELAY_INVALID(false, "Animation",
            "Failed to load the open chest reward-holo-delay value.",
            "It is either nonexistent or not a valid decimal value."),

    ANIMATION_OPENCHEST_ATTACH_TO_SUCCESS(true, "Animation",
            "Successfully loaded the open chest reward-holo-attach-to-item value.", "NONE"),

    ANIMATION_OPENCHEST_ATTACH_TO_INVALID(false, "Animation",
            "Failed to load the open chest reward-holo-attach-to-item value.",
            "It is either nonexistent or not a valid true/false value."),

    ANIMATION_OPENCHEST_EARLY_OPEN_ACTIONS_SUCCESS(true, "Animation",
            "Successfully loaded the open chest early-open-actions value.", "NONE"),

    ANIMATION_OPENCHEST_EARLY_OPEN_ACTIONS_INVALID(false, "Animation",
            "Faield to load the open chest early-open-actions value.",
            "It is either nonexistent or not a valid true/false value.");

    boolean success;
    String event, cause, section;

    StatusLoggerEvent(boolean success, String section, String event, String cause)
    {
        this.success = success;
        this.event = event;
        this.section = section;

        if (cause.equalsIgnoreCase(""))
        {
            this.cause = "NONE";
        }
        else
        {
            this.cause = cause;
        }
    }

    public void log(Crate crate)
    {
        log(crate.getCs().getSl(), new String[]{});
    }

    public void log(Crate crate, String[] args)
    {
        log(crate.getCs().getSl(), args);
    }

    public void log(StatusLogger sl)
    {
        log(sl, new String[]{});
    }

    public void log(StatusLogger sl, String[] args)
    {
        String eventDup = event, causeDup = cause;
        int i = 0;
        while (eventDup.indexOf("$?$") != -1)
        {
            int x = eventDup.indexOf("$?$");
            eventDup = eventDup.substring(0, x) + args[i] +
                    (x + 3 < eventDup.length() ? eventDup.substring(x + 3, eventDup.length()) : "");

            i++;
        }

        while (causeDup.indexOf("$?$") != -1)
        {
            int x = causeDup.indexOf("$?$");
            causeDup = causeDup.substring(0, x) + args[i] +
                    (x + 3 < causeDup.length() ? causeDup.substring(x + 3, causeDup.length()) : "");
            i++;
        }

        sl.addEvent(success, section, eventDup, causeDup);
    }
}
