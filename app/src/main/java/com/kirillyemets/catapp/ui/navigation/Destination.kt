package com.kirillyemets.catapp.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder


abstract class Destination(name: String, val args: List<NamedNavArgument> = emptyList()) {
    val destination: String = name + argsString()

    private fun argsString(): String = when {
        args.isEmpty() -> ""
        args.size == 1 && !args.single().argument.isNullable -> "/{${args.single().name}}"
        else -> args.fold("?") { acc, (name, _) ->
            "${acc}$name={$name}&"
        }.dropLast(1)
    }

    protected fun routeWithArgs(vararg args: Pair<String, Any?>): String {
        return when {
            this.args.isEmpty() -> destination
            args.size == 1 && this.args.size == 1 && !this.args.single().argument.isNullable -> {
                val (name, value) = args.single()
                if (value != null) destination.replace("{${name}}", value.toString())
                else destination.replace("/{${name}}","")
            }

            else -> args.fold(destination) { acc, (name, value) ->
                if (value == null) {
                    acc.replace(Regex("$name=\\{$name\\}&?"), "")
                } else {
                    acc.replace("{$name}", value.toString())
                }.dropLastWhile { it == '&' || it == '?' }
            }
        }
    }

    open fun navGraph(navGraphBuilder: NavGraphBuilder) {}
}