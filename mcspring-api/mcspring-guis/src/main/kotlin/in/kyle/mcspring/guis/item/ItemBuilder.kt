package `in`.kyle.mcspring.guis.item

import `in`.kyle.mcspring.chat.translateColorCodes
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.material.Dye

class ItemBuilder {

    var material = Material.AIR
    var amount = 1
    var name: String? = null
        set(value) {
            field = value?.translateColorCodes()
        }
    var skullOwner: String? = null
    var lore = listOf<String>()
        set(lines) {
            field = lines.map { it.translateColorCodes() }.toMutableList()
        }

    var dyeColor: DyeColor? = null

    fun skullOwner(name: String) {
        material = Material.PLAYER_HEAD
        skullOwner = name
    }

    fun lore(vararg lines: String) {
        this.lore = lines.toList()
    }

    fun build(): ItemStack {
        var itemStack= if (dyeColor != null) {
            createDyeItemStack()
        } else {
            ItemStack(material, amount)
        }
        tryAddItemName(itemStack)
        tryAddSkullMeta(itemStack)
        tryAddLore(itemStack)
        itemStack =
            resetItemFlags(itemStack)
        return itemStack
    }

    private fun tryAddLore(itemStack: ItemStack) {
        if (lore.isNotEmpty()) {
            val itemMeta = itemStack.itemMeta
            itemMeta!!.lore = lore.map { it.translateColorCodes() }
            itemStack.itemMeta = itemMeta
        }
    }

    private fun tryAddSkullMeta(itemStack: ItemStack) {
        if (skullOwner != null) {
            val skullMeta = itemStack.itemMeta as SkullMeta
            skullMeta.owner = skullOwner!!.trim { it <= ' ' }
            itemStack.itemMeta = skullMeta
        }
    }

    private fun tryAddItemName(itemStack: ItemStack) {
        if (name != null) {
            val itemMeta = itemStack.itemMeta
            itemMeta!!.setDisplayName(name)
            itemStack.itemMeta = itemMeta
        }
    }

    private fun createDyeItemStack(): ItemStack {
        val itemStack: ItemStack
        val dye = Dye(material)
        dye.color = dyeColor
        itemStack = dye.toItemStack(amount)
        return itemStack
    }

    companion object {
        fun create(lambda: ItemBuilder.() -> Unit): ItemStack {
            val builder = ItemBuilder()
            builder.lambda()
            return builder.build()
        }

        private fun resetItemFlags(item: ItemStack): ItemStack {
            if (item.hasItemMeta()) {
                val itemMeta = item.itemMeta
                itemMeta!!.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
                item.itemMeta = itemMeta
            }
            return item
        }
    }
}
