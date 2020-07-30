package `in`.kyle.mcspring.scheduler

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

/**
 * Convenience methods for Bukkit scheduling
 * @see [BukkitScheduler]
 */
@Lazy
@Service
class SchedulerService(
        private val scheduler: BukkitScheduler,
        private val plugin: Plugin
) {
    /**
     * @see BukkitScheduler.runTaskAsynchronously
     */
    fun asyncTask(task: Runnable): BukkitTask {
        return scheduler.runTaskAsynchronously(plugin, task)
    }

    /**
     * @see BukkitScheduler.runTask
     */
    fun syncTask(task: Runnable): BukkitTask {
        return scheduler.runTask(plugin, task)
    }

    /**
     * @see BukkitScheduler.runTaskLaterAsynchronously
     */
    fun asyncDelayedTask(task: Runnable, delayTicks: Long): BukkitTask {
        return scheduler.runTaskLaterAsynchronously(plugin, task, delayTicks)
    }

    /**
     * @see BukkitScheduler.scheduleSyncDelayedTask
     */
    fun syncDelayedTask(task: Runnable, delayTicks: Long): BukkitTask {
        return scheduler.runTaskLater(plugin, task, delayTicks)
    }

    /**
     * @see BukkitScheduler.runTaskTimerAsynchronously
     */
    fun asyncRepeatingTask(task: Runnable, delayTicks: Long, periodTicks: Long): BukkitTask {
        return scheduler.runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks)
    }

    /**
     * @see BukkitScheduler.runTaskTimer
     */
    fun syncRepeatingTask(task: Runnable, delayTicks: Long, periodTicks: Long): BukkitTask {
        return scheduler.runTaskTimer(plugin, task, delayTicks, periodTicks)
    }

    /**
     * @see BukkitScheduler.cancelTask
     */
    fun cancelTask(task: BukkitTask) {
        scheduler.cancelTask(task.taskId)
    }

    /**
     * @see BukkitScheduler.isCurrentlyRunning
     */
    fun isCurrentlyRunning(task: BukkitTask): Boolean {
        return scheduler.isCurrentlyRunning(task.taskId)
    }

    /**
     * @see BukkitScheduler.isQueued
     */
    fun isQueued(task: BukkitTask): Boolean {
        return scheduler.isQueued(task.taskId)
    }
}
