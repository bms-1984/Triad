package net.benjimadness.triad.api.util

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

object ComponentUtil {
    private fun Component.add(vararg components: Component): Component {
        val mutableComponents = components.map { MutableComponent.create(it.contents) }
        mutableComponents.drop(1).forEach { mutableComponents.first().append(it) }
        return MutableComponent.create(this.contents).append(mutableComponents.first())
    }

    fun combine(vararg components: Component): Component = components.first().add(*components.drop(1).toTypedArray())

}