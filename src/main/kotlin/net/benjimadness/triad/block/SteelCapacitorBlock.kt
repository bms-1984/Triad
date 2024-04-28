package net.benjimadness.triad.block

import net.benjimadness.triad.api.block.AbstractCapacitorBlock
import net.minecraft.world.level.block.entity.BlockEntity
import kotlin.reflect.KClass

class SteelCapacitorBlock(properties: Properties, blockEntity: KClass<out BlockEntity>) :
AbstractCapacitorBlock(properties, blockEntity)