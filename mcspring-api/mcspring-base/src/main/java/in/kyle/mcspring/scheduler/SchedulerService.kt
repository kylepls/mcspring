package `in`.kyle.mcspring.scheduler

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Lazy
@Service
@ConditionalOnBean(Plugin::class)
class SchedulerService(
        private val scheduler: BukkitScheduler,
        private val plugin: Plugin
) {
    fun asyncTask(task: Runnable): BukkitTask {
        return scheduler.runTaskAsynchronously(plugin, task)
    }

    fun syncTask(task: Runnable): BukkitTask {
        return scheduler.runTask(plugin, task)
    }

    fun asyncDelayedTask(task: Runnable, delayTicks: Long): BukkitTask {
        return scheduler.runTaskLaterAsynchronously(plugin, task, delayTicks)
    }

    fun syncDelayedTask(task: Runnable, delayTicks: Long): BukkitTask {
        return scheduler.runTaskLater(plugin, task, delayTicks)
    }

    fun asyncRepeatingTask(task: Runnable, delayTicks: Long, periodTicks: Long): BukkitTask {
        return scheduler.runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks)
    }

    fun syncRepeatingTask(task: Runnable, delayTicks: Long, periodTicks: Long): BukkitTask {
        return scheduler.runTaskTimer(plugin, task, delayTicks, periodTicks)
    }

    fun cancelTask(task: BukkitTask) {
        scheduler.cancelTask(task.taskId)
    }

    fun isCurrentlyRunning(task: BukkitTask): Boolean {
        return scheduler.isCurrentlyRunning(task.taskId)
    }

    fun isQueued(task: BukkitTask): Boolean {
        return scheduler.isQueued(task.taskId)
    }
}
