package net.benjimadness.triad.api.capabilities.fluid

import net.benjimadness.triad.registry.TriadFluids
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import kotlin.math.max
import kotlin.math.min

class BoilerFluidHandler(private val capacity: Int) : IFluidHandler {
    private val tanks = arrayListOf(
        FluidTank(capacity) {
            it.fluid == Fluids.WATER
        },
        FluidTank(capacity) {
            it.fluid == TriadFluids.STEAM
        }
    )
    override fun getTanks(): Int = tanks.size

    override fun getFluidInTank(tank: Int): FluidStack =
        tanks[tank].fluid

    fun getSteam(): FluidStack = getFluidInTank(1)
    fun getWater(): FluidStack = getFluidInTank(0)

    override fun getTankCapacity(tank: Int): Int = capacity

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean =
        tanks[tank].isFluidValid(stack)

    override fun fill(resource: FluidStack, action: FluidAction): Int {
        if (resource.isEmpty) return 0
        return when (resource.fluid) {
            Fluids.WATER -> fillWater(resource.amount, action)
            TriadFluids.STEAM -> fillSteam(resource.amount, action)
            else -> 0
        }
    }

    private fun fill(tank: Int, resource: FluidStack, action: FluidAction): Int {
        if (resource.isEmpty || !isFluidValid(tank, resource)) return 0
        val fluid = getFluidInTank(tank)
        if (!fluid.isEmpty && !FluidStack.isSameFluidSameComponents(fluid, resource)) return 0
        if (action.simulate()) {
            if (fluid.isEmpty) return min(capacity, resource.amount)
            return min(capacity - resource.amount, resource.amount)
        }
        if (fluid.isEmpty) {
            tanks[tank].fluid = resource.copyWithAmount(min(capacity, resource.amount))
            return tanks[tank].fluidAmount
        }
        var filled = capacity - fluid.amount
        if (resource.amount < filled) {
            tanks[tank].fluid.grow(resource.amount)
            filled = resource.amount
        }
        else tanks[tank].fluid.amount = capacity
        return filled
    }

    override fun drain(resource: FluidStack, action: FluidAction): FluidStack {
        if (resource.isEmpty) return FluidStack.EMPTY
        return when (resource.fluid) {
            Fluids.WATER -> drain(0, resource.amount, action)
            TriadFluids.STEAM -> drain(1, resource.amount, action)
            else -> FluidStack.EMPTY
        }
    }

    fun fillSteam(amount: Int, action: FluidAction): Int =
        fill(1, FluidStack(TriadFluids.STEAM, amount), action)

    fun fillWater(amount: Int, action: FluidAction): Int =
        fill(0, FluidStack(Fluids.WATER, amount), action)

    fun drainSteam(maxDrain: Int, action: FluidAction): FluidStack =
        drain(1, maxDrain, action)

    fun drainWater(maxDrain: Int, action: FluidAction): FluidStack =
        drain(0, maxDrain, action)

    override fun drain(maxDrain: Int, action: FluidAction): FluidStack =
        drain(0, maxDrain, action)

    private fun drain(tank: Int, maxDrain: Int, action: FluidAction): FluidStack {
        var drained = maxDrain
        val fluid = getFluidInTank(tank)
        if (fluid.amount < drained) {
            drained = fluid.amount
        }
        val stack = fluid.copyWithAmount(drained)
        if (action.execute() && drained > 0) tanks[tank].fluid.shrink(drained)
        return stack
    }

    fun isEmpty(tank: Int): Boolean = getFluidInTank(tank).isEmpty
    fun getSpace(tank: Int): Int = max(0, capacity - getFluidInTank(tank).amount)

    fun readFromNBT(lookupProvider: Provider, tag: CompoundTag): BoilerFluidHandler {
        tanks[0].readFromNBT(lookupProvider, tag.getCompound("Water"))
        tanks[1].readFromNBT(lookupProvider, tag.getCompound("Steam"))
        return this
    }

    fun writeToNBT(lookupProvider: Provider, tag: CompoundTag): CompoundTag {
        if (!tanks[0].isEmpty) {
            tag.put("Water", tanks[0].writeToNBT(lookupProvider, CompoundTag()))
        }
        if (!tanks[1].isEmpty) {
            tag.put("Steam", tanks[1].writeToNBT(lookupProvider, CompoundTag()))
        }
        return tag
    }
}