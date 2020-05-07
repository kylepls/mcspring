package `in`.kyle.mcspring.subcommands.plugincommand.javacompat

import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommandWith

interface PluginCommandWithJavaSupport : PluginCommandWith {

    fun withInt(errorMessage: String) = super.withInt { errorMessage }

    fun withDouble(errorMessage: String) = super.withDouble { errorMessage }

    fun withPlayer(errorMessage: String) = super.withPlayer { errorMessage }

    fun withWorld(errorMessage: String) = super.withWorld { errorMessage }

    fun withXYZInt(errorMessage: String) = super.withXYZInt { errorMessage }

    fun <T> withMap(options: Map<String, T>, errorMessage: String) = with({ options[it] }, { errorMessage })

    fun withAny(options: Collection<String>, errorMessage: String) = with({ options.associateBy { it } }, { errorMessage })

}
