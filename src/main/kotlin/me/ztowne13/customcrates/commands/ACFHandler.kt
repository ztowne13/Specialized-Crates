package me.ztowne13.customcrates.commands

import ch.jalu.configme.SettingsManager
import me.ztowne13.customcrates.SpecialisedCrates
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class ACFHandler(private val plugin: SpecialisedCrates, private val commandManager: PaperCommandManager) {
    val languages = mutableListOf<String>()

    fun load() {
        commandManager.usePerIssuerLocale(true, false)
        commandManager.enableUnstableAPI("help")

        loadLang()
        loadContexts(plugin.guildHandler, plugin.arenaHandler)
        loadCompletions(plugin.guildHandler, plugin.arenaHandler)
        loadConditions(plugin.guildHandler)
        loadDI()

        commandManager.commandReplacements.addReplacement("guilds", plugin.settingsHandler.mainConf.getProperty(PluginSettings.PLUGIN_ALIASES))
        commandManager.commandReplacements.addReplacement("syntax", plugin.settingsHandler.mainConf.getProperty(PluginSettings.SYNTAX_NAME))

        loadCommands()
        loadCompletionCache()
    }

    fun loadLang() {
        languages.clear()
        plugin.dataFolder.resolve("languages").listFiles()?.filter {
            it.extension.equals("yml", true)
        }?.forEach {
            val locale = Locale.forLanguageTag(it.nameWithoutExtension)

            commandManager.addSupportedLanguage(locale)
            commandManager.locales.loadYamlLanguageFile(it, locale)
            languages.add(it.nameWithoutExtension)
        }
        commandManager.locales.defaultLocale = Locale.forLanguageTag(plugin.settingsHandler.mainConf.getProperty(PluginSettings.MESSAGES_LANGUAGE))
    }

    private fun loadContexts(guildHandler: GuildHandler, arenaHandler: ArenaHandler) {
        commandManager.commandContexts.registerIssuerAwareContext(Guild::class.java) { c ->
            val guild: Guild = (if (c.hasFlag("other")) {
                guildHandler.getGuild(c.popFirstArg())
            } else {
                guildHandler.getGuild(c.player)
            })
                ?: throw InvalidCommandArgument(Messages.ERROR__NO_GUILD)
            guild
        }
        commandManager.commandContexts.registerContext(Arena::class.java) { c -> arenaHandler.getArena(c.popFirstArg()).get() }
    }

    private fun loadConditions(guildHandler: GuildHandler) {
        commandManager.commandConditions.addCondition(Guild::class.java, "perm") { c, exec, value ->
            if (value == null) {
                return@addCondition
            }
            val player = exec.player
            val guild = guildHandler.getGuild(player)
            if (!guild.memberHasPermission(player, c.getConfigValue("perm", "SERVER_OWNER"))) {
                throw InvalidPermissionException()
            }
        }
        commandManager.commandConditions.addCondition(Guild::class.java, "NotMaxedAllies") { c, exec, value ->
            if (value == null) {
                return@addCondition
            }
            val player = exec.player
            val guild = guildHandler.getGuild(player)
            if (guild.allies.size >= guild.tier.maxAllies) {
                throw ExpectationNotMet(Messages.ALLY__MAX_ALLIES)
            }
        }
        commandManager.commandConditions.addCondition(Player::class.java, "NoGuild") { c, exec, value ->
            if (value == null) {
                return@addCondition
            }
            val player = exec.player
            if (guildHandler.getGuild(player) != null) {
                throw ExpectationNotMet(Messages.ERROR__ALREADY_IN_GUILD)
            }
        }
        commandManager.commandConditions.addCondition("NotMigrating") {
            if (guildHandler.isMigrating) {
                throw ExpectationNotMet(Messages.ERROR__MIGRATING)
            }
        }
    }

    private fun loadCompletions(guildHandler: GuildHandler, arenaHandler: ArenaHandler) {
        commandManager.commandCompletions.registerCompletion("online") { Bukkit.getOnlinePlayers().map { it.name } }
        commandManager.commandCompletions.registerCompletion("invitedTo") { c -> guildHandler.getInvitedGuilds(c.player) }
        commandManager.commandCompletions.registerCompletion("joinableGuilds") { c -> guildHandler.getJoinableGuild(c.player) }
        commandManager.commandCompletions.registerCompletion("guilds") { guildHandler.guildNames }
        commandManager.commandCompletions.registerCompletion("arenas") { arenaHandler.getArenas().map { it.name } }
        commandManager.commandCompletions.registerStaticCompletion("locations") { listOf("challenger", "defender") }
        commandManager.commandCompletions.registerCompletion("languages") { languages.sorted() }
        commandManager.commandCompletions.registerStaticCompletion("sources") { listOf("JSON", "MYSQL", "SQLITE", "MARIADB") }

        commandManager.commandCompletions.registerAsyncCompletion("members") { c ->
            val guild = guildHandler.getGuild(c.player) ?: return@registerAsyncCompletion emptyList()
            guild.members.mapNotNull { guildHandler.lookupCache[it.uuid] ?: it.name }
        }

        commandManager.commandCompletions.registerCompletion("claimed") { c ->
            val guild = guildHandler.getGuild(c.player) ?: return@registerCompletion emptyList<String>()
            return@registerCompletion mutableListOf("this", "all")
        }

        commandManager.commandCompletions.registerCompletion("members-admin") { c ->
            val guild = c.getContextValue(Guild::class.java, 1) ?: return@registerCompletion emptyList()
            guild.members.mapNotNull { guildHandler.lookupCache[it.uuid] ?: it.name }
        }
        commandManager.commandCompletions.registerCompletion("allyInvites") { c ->
            val guild = guildHandler.getGuild(c.player) ?: return@registerCompletion null
            if (!guild.hasPendingAllies()) {
                return@registerCompletion null
            }
            guild.pendingAllies.mapNotNull { guildHandler.getNameById(it) }
        }
        commandManager.commandCompletions.registerCompletion("allies") { c ->
            val guild = guildHandler.getGuild(c.player) ?: return@registerCompletion null
            if (!guild.hasAllies()) {
                return@registerCompletion null
            }
            guild.allies.mapNotNull { guildHandler.getNameById(it) }
        }
        commandManager.commandCompletions.registerCompletion("activeCodes") { c ->
            val guild = guildHandler.getGuild(c.player) ?: return@registerCompletion null
            if (guild.codes == null) {
                return@registerCompletion null
            }
            guild.codes.map { it.id }
        }
        commandManager.commandCompletions.registerCompletion("vaultAmount") { c ->
            val guild = guildHandler.getGuild(c.player) ?: return@registerCompletion null
            if (guild.vaults == null) {
                return@registerCompletion null
            }
            val list = guildHandler.vaults[guild] ?: return@registerCompletion null
            (1 until list.size).map(Any::toString)
        }
    }

    private fun loadCommands() {
        ZISScanner().getClasses(Guilds::class.java, "me.glaremasters.guilds.commands").asSequence()
            .filter { BaseCommand::class.java.isAssignableFrom(it) }
            .forEach { commandManager.registerCommand(it.newInstance() as BaseCommand) }
    }

    private fun loadCompletionCache() {
        val handler = plugin.guildHandler

        handler.guilds.forEach { guild ->
            guild.members.filterNotNull().forEach { member ->
                handler.lookupCache.putIfAbsent(member.uuid, member.name)
            }
        }
    }

    private fun loadDI() {
        commandManager.registerDependency(GuildHandler::class.java, plugin.guildHandler)
        commandManager.registerDependency(SettingsManager::class.java, plugin.settingsHandler.mainConf)
        commandManager.registerDependency(ActionHandler::class.java, plugin.actionHandler)
        commandManager.registerDependency(Economy::class.java, plugin.economy)
        commandManager.registerDependency(Permission::class.java, plugin.permissions)
        commandManager.registerDependency(CooldownHandler::class.java, plugin.cooldownHandler)
        commandManager.registerDependency(ArenaHandler::class.java, plugin.arenaHandler)
        commandManager.registerDependency(ChallengeHandler::class.java, plugin.challengeHandler)
        commandManager.registerDependency(DatabaseAdapter::class.java, plugin.database)
    }
}