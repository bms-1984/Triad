package net.benjimadness.triad.api.block

import net.benjimadness.triad.api.block.entity.AbstractTriadBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

abstract class AbstractTriadEntityBlock(properties: Properties, private val blockEntity: KClass<out BlockEntity>) :
Block(properties), EntityBlock{
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = blockEntity.primaryConstructor!!.call(pos, state)
    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> =
        BlockEntityTicker { lLevel, pos, _, lBlockEntity ->
            if (lLevel.isClientSide) return@BlockEntityTicker
            else if (lBlockEntity is AbstractTriadBlockEntity) lBlockEntity.serverTick(lLevel, pos, lBlockEntity)
        }
}