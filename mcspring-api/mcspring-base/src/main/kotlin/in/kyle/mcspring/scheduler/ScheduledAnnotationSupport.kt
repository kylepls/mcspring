package `in`.kyle.mcspring.scheduler

import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.Trigger
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.ListenableFuture
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.ScheduledFuture

@Lazy
@Component
@ConditionalOnBean(Plugin::class)
internal class ScheduledAnnotationSupport(
        private val scheduler: SchedulerService
) : ThreadPoolTaskScheduler() {

    override fun schedule(task: Runnable, trigger: Trigger): ScheduledFuture<*> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return super.schedule(wrapSync(task), trigger)
    }

    override fun schedule(task: Runnable, startTime: Date): ScheduledFuture<*> {
        return super.schedule(wrapSync(task), startTime)
    }

    override fun scheduleAtFixedRate(task: Runnable, startTime: Date, period: Long): ScheduledFuture<*> {
        return super.scheduleAtFixedRate(wrapSync(task), startTime, period)
    }

    override fun scheduleAtFixedRate(task: Runnable, period: Long): ScheduledFuture<*> {
        return super.scheduleAtFixedRate(wrapSync(task), period)
    }

    override fun scheduleWithFixedDelay(task: Runnable, startTime: Date, delay: Long): ScheduledFuture<*> {
        return super.scheduleWithFixedDelay(wrapSync(task), startTime, delay)
    }

    override fun scheduleWithFixedDelay(task: Runnable, delay: Long): ScheduledFuture<*> {
        return super.scheduleWithFixedDelay(wrapSync(task), delay)
    }

    private fun wrapSync(task: Runnable): Runnable {
        return WrappedRunnable(scheduler, task)
    }

    data class WrappedRunnable(val scheduler: SchedulerService, val runnable: Runnable) : Runnable {
        override fun run() {
            scheduler.syncTask(runnable)
        }
    }
}
