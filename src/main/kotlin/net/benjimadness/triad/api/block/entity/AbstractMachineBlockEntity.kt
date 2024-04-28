package net.benjimadness.triad.api.block.entity

import net.benjimadness.triad.api.util.MiscUtil.getLeverOrientation
import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractMachineBlockEntity (type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractTriadBlockEntity(type, pos, state) {
        private var isRunning = false

    override fun serverTick(level: Level, pos: BlockPos, blockEntity: AbstractTriadBlockEntity) {
        level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER, level.getBlockState(pos).getLeverOrientation(level, pos)), Block.UPDATE_CLIENTS)
        if (shouldRun()) {
            isRunning = true
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.RUNNING, true), Block.UPDATE_CLIENTS)
        }
        else {
            isRunning = false
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.RUNNING, false), Block.UPDATE_CLIENTS)
        }
        execute()
        setChanged()
    }

    abstract fun execute()

    override fun saveAdditional(tag: CompoundTag, registry: Provider) {
        super.saveAdditional(tag, registry)
        saveClientData(tag)
    }

    open fun saveClientData(tag: CompoundTag) {
        tag.putBoolean("IsRunning", isRunning)
    }

    override fun loadAdditional(tag: CompoundTag, registry: Provider) {
        super.loadAdditional(tag, registry)
        loadClientData(tag)
    }

    open fun loadClientData(tag: CompoundTag) {
        if (tag.contains("IsRunning")) isRunning = tag.getBoolean("IsRunning")
    }

    override fun getUpdateTag(registry: Provider): CompoundTag {
        val tag = super.getUpdateTag(registry)
        saveClientData(tag)
        return tag
    }

    override fun handleUpdateTag(tag: CompoundTag, registry: Provider) {
        loadClientData(tag)
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? =
        ClientboundBlockEntityDataPacket.create(this)

    override fun onDataPacket(network: Connection, packet: ClientboundBlockEntityDataPacket, registry: Provider) {
        handleUpdateTag(packet.tag, registry)
    }

    open fun shouldRun(): Boolean = isFueled()
    abstract fun isFueled(): Boolean
}