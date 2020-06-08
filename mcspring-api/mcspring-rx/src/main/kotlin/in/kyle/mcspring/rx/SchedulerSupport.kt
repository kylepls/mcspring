package `in`.kyle.mcspring.rx

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

private val asyncSchedulers = mutableMapOf<Plugin, BukkitScheduler>()
private val syncSchedulers = mutableMapOf<Plugin, BukkitScheduler>()

fun Plugin.asyncScheduler(): BukkitScheduler {
    return asyncSchedulers.getOrPut(this) {
        BukkitScheduler(this, true)
    }
}

fun Plugin.syncScheduler(): BukkitScheduler {
    return syncSchedulers.getOrPut(this) {
        BukkitScheduler(this, false)
    }
}

class BukkitScheduler(private val plugin: Plugin, private val async: Boolean) : Scheduler() {

    override fun createWorker() = BukkitWorker(plugin, async)

    class BukkitWorker(private val plugin: Plugin, private val async: Boolean) : Worker() {

        private val composite = CompositeDisposable()

        override fun isDisposed() = composite.isDisposed

        override fun schedule(run: Runnable, initialDelay: Long, unit: TimeUnit): Disposable {
            val initialTicks = timeUnitToBukkitTicks(initialDelay, unit)
            val bukkitTask = bukkitSchedule(run, initialTicks)

            return Disposable.fromRunnable {
                bukkitTask.cancel()
            }.apply { composite.add(this) }
        }

        override fun dispose() = composite.dispose()

        override fun schedulePeriodically(run: Runnable, initialDelay: Long, period: Long, unit: TimeUnit): Disposable {
            val initialTicks = timeUnitToBukkitTicks(initialDelay, unit)
            val periodTicks = timeUnitToBukkitTicks(period, unit)
            val bukkitTask = bukkitSchedulePeriodically(run, initialTicks, periodTicks)

            return Disposable.fromRunnable {
                bukkitTask.cancel()
            }.apply { composite.add(this) }
        }

        private fun timeUnitToBukkitTicks(delayTime: Long, timeUnit: TimeUnit): Long {
            return (timeUnit.toMillis(delayTime) * 0.02).roundToLong()
        }

        private fun bukkitSchedulePeriodically(runnable: Runnable, delay: Long, period: Long): BukkitTask {
            val scheduler = plugin.server.scheduler
            return if (async) {
                scheduler.runTaskTimerAsynchronously(plugin, runnable, delay, period)
            } else {
                scheduler.runTaskTimer(plugin, runnable, delay, period)
            }
        }

        private fun bukkitSchedule(runnable: Runnable, delay: Long): BukkitTask {
            val scheduler = plugin.server.scheduler
            return if (async) {
                scheduler.runTaskLaterAsynchronously(plugin, runnable, delay)
            } else {
                scheduler.runTaskLater(plugin, runnable, delay)
            }
        }
    }
}
