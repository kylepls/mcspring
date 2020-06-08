@file:Suppress("LocalVariableName")

package `in`.kyle.mcspring.guis.invenotry

import com.google.common.base.Preconditions
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * A inventory helper that allows you to change the title of the current
 * opened inventory.
 * https://gist.githubusercontent.com/Cybermaxke/7f0a315aea70c9d62535/raw/545b01be4234422e81b1ce0a9606083c261906ba/InventoryTitleHelper
 */
object InventoryTitleHelper {
    // Methods
    private var m_Player_GetHandle: Method? = null
    private var m_PlayerConnection_sendPacket: Method? = null
    private var m_CraftChatMessage_fromString: Method? = null
    private var m_EntityPlayer_updateInventory: Method? = null

    // Fields
    private var f_EntityPlayer_playerConnection: Field? = null
    private var f_EntityPlayer_activeContainer: Field? = null
    private var f_Container_windowId: Field? = null

    // Constructors
    private var c_PacketOpenWindow: Constructor<*>? = null

    // The version of the server (nms version like v1_5_R3)
    private var nms_version: String? = null
    private var nms_package: String? = null
    private var crb_package: String? = null

    /**
     * Sends a new inventory title to the client.
     *
     * @param player the player
     * @param title the new title
     */
    fun sendInventoryTitle(player: Player, title: String) {
        Preconditions.checkNotNull(player, "player")
        try {
            sendInventoryTitle0(
                player,
                title
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendInventoryTitle0(player: Player, title: String) {
        val inventory = player.openInventory.topInventory
        if (m_Player_GetHandle == null) {
            m_Player_GetHandle = player.javaClass.getMethod("getHandle")
        }
        val nms_EntityPlayer = m_Player_GetHandle!!.invoke(player)
        if (f_EntityPlayer_playerConnection == null) {
            f_EntityPlayer_playerConnection =
                nms_EntityPlayer.javaClass.getField("playerConnection")
        }
        val nms_PlayerConnection =
            f_EntityPlayer_playerConnection!![nms_EntityPlayer]
        if (f_EntityPlayer_activeContainer == null) {
            f_EntityPlayer_activeContainer = nms_EntityPlayer.javaClass.getField("activeContainer")
        }
        val nms_Container = f_EntityPlayer_activeContainer!![nms_EntityPlayer]
        if (f_Container_windowId == null) {
            f_Container_windowId = nms_Container.javaClass.getField("windowId")
        }
        val windowId = f_Container_windowId!!.getInt(nms_Container)
        val version = nmsVersion
        if (version!!.startsWith("v1_5_") || version.startsWith("v1_6_")) {
            sendPacket15a16a17(
                nms_PlayerConnection,
                nms_EntityPlayer,
                nms_Container,
                windowId,
                inventory,
                title,
                false
            )
        } else if (version.startsWith("v1_7_")) {
            sendPacket15a16a17(
                nms_PlayerConnection,
                nms_EntityPlayer,
                nms_Container,
                windowId,
                inventory,
                title,
                true
            )
        } else if (version == "v1_8_R1" || version == "v1_8_R2") {
            sendPacket18(
                nms_PlayerConnection,
                nms_EntityPlayer,
                nms_Container,
                windowId,
                inventory,
                title
            )
        }
    }

    private fun sendPacket15a16a17(
        nms_playerConnection: Any,
        nms_EntityPlayer: Any,
        nms_Container: Any,
        windowId: Int,
        inventory: Inventory,
        title: String,
        flag: Boolean
    ) {
        var title: String? = title
        if (c_PacketOpenWindow == null) {
            c_PacketOpenWindow = if (flag) {
                findNmsClass("PacketPlayOutOpenWindow")
                    .getConstructor(
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    String::class.java,
                    Int::class.javaPrimitiveType,
                    Boolean::class.javaPrimitiveType
                )
            } else {
                findNmsClass("Packet100OpenWindow")
                    .getConstructor(
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    String::class.java,
                    Int::class.javaPrimitiveType,
                    Boolean::class.javaPrimitiveType
                )
            }
        }
        val id: Int
        val size: Int
        when (inventory.type) {
            InventoryType.ANVIL -> {
                id = 8
                size = 9
            }
            InventoryType.BEACON -> {
                id = 7
                size = 1
            }
            InventoryType.BREWING -> {
                id = 5
                size = 4
            }
            InventoryType.CRAFTING -> return
            InventoryType.CREATIVE -> return
            InventoryType.DISPENSER -> {
                id = 3
                size = 9
            }
            InventoryType.DROPPER -> {
                id = 10
                size = 9
            }
            InventoryType.ENCHANTING -> {
                id = 4
                size = 9
            }
            InventoryType.ENDER_CHEST, InventoryType.CHEST -> {
                id = 0
                size = inventory.size
            }
            InventoryType.FURNACE -> {
                id = 2
                size = 2
            }
            InventoryType.HOPPER -> {
                id = 9
                size = 5
            }
            InventoryType.MERCHANT -> {
                id = 6
                size = 3
            }
            InventoryType.PLAYER -> return
            InventoryType.WORKBENCH -> {
                id = 1
                size = 9
            }
            else -> return
        }
        if (title != null && title.length > 32) {
            title = title.substring(0, 32)
        }
        if (m_EntityPlayer_updateInventory == null) {
            m_EntityPlayer_updateInventory =
                nms_EntityPlayer.javaClass.getMethod("updateInventory",
                    findNmsClass("Container")
                )
        }
        val packet = c_PacketOpenWindow!!.newInstance(
            windowId,
            id,
            title ?: "",
            size,
            true
        )
        sendPacket(
            nms_playerConnection,
            packet
        )
        m_EntityPlayer_updateInventory!!.invoke(nms_EntityPlayer, nms_Container)
    }

    private fun sendPacket18(
        nms_playerConnection: Any,
        nms_EntityPlayer: Any,
        nms_Container: Any,
        windowId: Int,
        inventory: Inventory,
        title: String
    ) {
        if (c_PacketOpenWindow == null) {
            c_PacketOpenWindow =
                findNmsClass("PacketPlayOutOpenWindow")
                    .getConstructor(
                    Int::class.javaPrimitiveType,
                    String::class.java,
                        findNmsClass("IChatBaseComponent"),
                    Int::class.javaPrimitiveType
                )
        }
        val id: String
        var size = 0
        when (inventory.type) {
            InventoryType.ANVIL -> id = "minecraft:anvil"
            InventoryType.BEACON -> id = "minecraft:beacon"
            InventoryType.BREWING -> id = "minecraft:brewing_stand"
            InventoryType.CRAFTING -> return
            InventoryType.CREATIVE -> return
            InventoryType.DISPENSER -> id = "minecraft:dispenser"
            InventoryType.DROPPER -> id = "minecraft:dropper"
            InventoryType.ENCHANTING -> id = "minecraft:enchanting_table"
            InventoryType.ENDER_CHEST, InventoryType.CHEST -> {
                id = "minecraft:chest"
                size = inventory.size
            }
            InventoryType.FURNACE -> id = "minecraft:furnace"
            InventoryType.HOPPER -> id = "minecraft:hopper"
            InventoryType.MERCHANT -> {
                id = "minecraft:villager"
                size = 3
            }
            InventoryType.PLAYER -> return
            InventoryType.WORKBENCH -> id = "minecraft:crafting_table"
            else -> return
        }
        if (m_CraftChatMessage_fromString == null) {
            m_CraftChatMessage_fromString =
                findCrbClass("util.CraftChatMessage")
                    .getMethod("fromString", String::class.java)
        }
        if (m_EntityPlayer_updateInventory == null) {
            m_EntityPlayer_updateInventory =
                nms_EntityPlayer.javaClass.getMethod("updateInventory",
                    findNmsClass("Container")
                )
        }
        val nms_title =
            (m_CraftChatMessage_fromString!!.invoke(null, title) as Array<Any?>)[0]
        val nms_packet = c_PacketOpenWindow!!.newInstance(windowId, id, nms_title, size)
        sendPacket(
            nms_playerConnection,
            nms_packet
        )
        m_EntityPlayer_updateInventory!!.invoke(nms_EntityPlayer, nms_Container)
    }

    private fun sendPacket(playerConnection: Any, packet: Any) {
        if (m_PlayerConnection_sendPacket == null) {
            m_PlayerConnection_sendPacket =
                playerConnection.javaClass.getMethod("sendPacket",
                    findNmsClass("Packet")
                )
        }
        m_PlayerConnection_sendPacket!!.invoke(playerConnection, packet)
    }

    private val nmsVersion: String?
        get() {
            if (nms_version == null) {
                nms_version =
                    Bukkit.getServer().javaClass.getPackage().name.replace("org.bukkit.craftbukkit.", "")
            }
            return nms_version
        }

    private val nmsPackage: String?
        get() {
            if (nms_package == null) {
                nms_package = "net.minecraft.server.$nmsVersion"
            }
            return nms_package
        }

    private val crbPackage: String?
        get() {
            if (crb_package == null) {
                crb_package = "org.bukkit.craftbukkit.$nmsVersion"
            }
            return crb_package
        }

    private fun findNmsClass(name: String): Class<*> {
        return Class.forName("$nmsPackage.$name")
    }

    private fun findCrbClass(name: String): Class<*> {
        return Class.forName("$crbPackage.$name")
    }
}
