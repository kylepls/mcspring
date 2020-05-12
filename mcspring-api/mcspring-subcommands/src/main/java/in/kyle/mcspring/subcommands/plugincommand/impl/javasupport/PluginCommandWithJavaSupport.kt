package `in`.kyle.mcspring.subcommands.plugincommand.impl.javasupport

import `in`.kyle.mcspring.subcommands.plugincommand.impl.PluginCommandWith

interface PluginCommandWithJavaSupport : PluginCommandWith {

    override fun withInt(errorMessage: String) = super.withInt { errorMessage }

    override fun withDouble(errorMessage: String) = super.withDouble { errorMessage }

    override fun withPlayer(errorMessage: String) = super.withPlayer { errorMessage }

    override fun withWorld(errorMessage: String) = super.withWorld { errorMessage }

    override fun withXYZInt(errorMessage: String) = super.withXYZInt { errorMessage }

    override fun <T> withMap(options: Map<String, T>, errorMessage: String) = withMap(options) { errorMessage }

    override fun withAny(options: Collection<String>, errorMessage: String) = withAny(options) { errorMessage }

}
