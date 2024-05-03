package net.benjimadness.triad.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Consumer

abstract class AbstractConduitBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractTriadBlockEntity(type, pos, state) {

        protected val outputs = HashSet<BlockPos>()

    private fun cacheOutputs() {
        if (outputs.isEmpty()) {
            traverse(worldPosition) { conduit ->
                for (dir in Direction.entries) {
                    val pos = conduit.blockPos.relative(dir)
                    val blockEntity = level!!.getBlockEntity(pos)
                    if (blockEntity != null && blockEntity !is AbstractConduitBlockEntity) {
                        addOutput(pos)
                    }
                }
            }
        }
    }

    private fun traverse(pos: BlockPos, consumer: Consumer<AbstractConduitBlockEntity>) {
        val traversed = HashSet<BlockPos>()
        traversed.add(pos)
        consumer.accept(this)
        traverse(pos, traversed, consumer)
    }

    private fun traverse(pos: BlockPos, traversed: HashSet<BlockPos>, consumer: Consumer<AbstractConduitBlockEntity>) {
        for (dir in Direction.entries) {
            val dirPos = pos.relative(dir)
            if (!traversed.contains(dirPos)) {
                traversed.add(dirPos)
                val blockEntity = level!!.getBlockEntity(dirPos)
                if (blockEntity != null && isSame(blockEntity)) {
                    val conduit = blockEntity as AbstractConduitBlockEntity
                    consumer.accept(conduit)
                    conduit.traverse(dirPos, traversed, consumer)
                }
            }
        }
    }

    override fun serverTick(level: Level, pos: BlockPos, blockEntity: AbstractTriadBlockEntity) {
        if (isNotEmpty()) {
            cacheOutputs()
            if (outputs.isNotEmpty()) {
                distribute()
            }
        }
    }

    fun markDirty() {
        traverse(worldPosition) { conduit -> conduit.outputs.clear() }
    }
    abstract fun isEmpty(): Boolean
    private fun isNotEmpty() = !isEmpty()
    abstract fun distribute()
    abstract fun isSame(blockEntity: BlockEntity): Boolean
    abstract fun addOutput(pos: BlockPos)
}