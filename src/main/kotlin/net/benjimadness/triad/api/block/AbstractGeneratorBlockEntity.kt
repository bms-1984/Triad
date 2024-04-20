package net.benjimadness.triad.api.block

import net.benjimadness.triad.block.TriadBlockStateProperties
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler
import kotlin.math.min

abstract class AbstractGeneratorBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    BlockEntity(type, pos, state) {
        companion object {
            private const val MAX_TRANSFER = 10
            private const val CAPACITY = 10000
            const val INPUT_SLOT = 0
        }
    private val items = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }
        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (amount >= getStackInSlot(INPUT_SLOT).count) progress = 0
            return super.extractItem(slot, amount, simulate)
        }
    }
    val itemHandler: IItemHandler by lazy { items }
    private val energy = EnergyStorage(CAPACITY, MAX_TRANSFER)
    val energyStorage: IEnergyStorage by lazy { energy }
    var isRunning = false
    var progress = 0

    open fun serverTick(level: Level, pos: BlockPos, blockEntity: AbstractGeneratorBlockEntity) {
        if (isPowered(level)) {
            isRunning = true
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.RUNNING, true), Block.UPDATE_CLIENTS)
        }
        else {
            isRunning = false
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.RUNNING, false), Block.UPDATE_CLIENTS)
        }
        if (energyStorage.energyStored >= energyStorage.maxEnergyStored) {
            isRunning = false
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.RUNNING, false), Block.UPDATE_CLIENTS)
        }
        generate(level, pos)
        distribute(level, pos)
        setChanged()
    }

    abstract fun generate(level: Level, pos: BlockPos)

    open fun distribute(level: Level, pos: BlockPos) {
        if (energy.energyStored > 0) {
            for (dir in Direction.entries) {
                val dirEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(dir), null)
                if (dirEnergy != null && dirEnergy.canReceive()) {
                    val received = dirEnergy.receiveEnergy(min(energy.energyStored, MAX_TRANSFER), false)
                    energy.extractEnergy(received, false)
                }
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.putInt("Progress", progress)
        tag.put("Items", items.serializeNBT())
        tag.put("Energy", energy.serializeNBT())
        saveClientData(tag)
    }

    private fun saveClientData(tag: CompoundTag) {
        tag.putBoolean("IsRunning", isRunning)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        if (tag.contains("Progress")) progress = tag.getInt("Progress")
        if (tag.contains("Items")) items.deserializeNBT(tag.getCompound("Items"))
        if (tag.contains("Energy")) tag.get("Energy")?.let { energy.deserializeNBT(it) }
        loadClientData(tag)
    }

    private fun loadClientData(tag: CompoundTag) {
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

    abstract fun getTotalTime(): Int
    abstract fun isPowered(level: Level): Boolean
    abstract fun isFuel(stack: ItemStack): Boolean
}