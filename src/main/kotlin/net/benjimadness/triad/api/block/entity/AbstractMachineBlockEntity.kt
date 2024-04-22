package net.benjimadness.triad.api.block.entity

import net.benjimadness.triad.api.util.MiscUtil.getLeverOrientation
import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractMachineBlockEntity (type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    BlockEntity(type, pos, state) {
        private var isRunning = false

    open fun serverTick(level: Level, pos: BlockPos, blockEntity: AbstractMachineBlockEntity) {
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

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        saveClientData(tag)
    }

    open fun saveClientData(tag: CompoundTag) {
        tag.putBoolean("IsRunning", isRunning)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        loadClientData(tag)
    }

    open fun loadClientData(tag: CompoundTag) {
        if (tag.contains("IsRunning")) isRunning = tag.getBoolean("IsRunning")
    }

    override fun getUpdateTag(): CompoundTag {
        val tag = super.getUpdateTag()
        saveClientData(tag)
        return tag
    }

    override fun handleUpdateTag(tag: CompoundTag) {
        loadClientData(tag)
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? =
        ClientboundBlockEntityDataPacket.create(this)

    override fun onDataPacket(network: Connection, packet: ClientboundBlockEntityDataPacket) {
        if (packet.tag != null)
            handleUpdateTag(packet.tag!!)
    }

    open fun shouldRun(): Boolean = isFueled()
    abstract fun isFueled(): Boolean
}