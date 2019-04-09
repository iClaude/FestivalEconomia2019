package iclaude.festivaleconomia2019.ui.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotifyWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        // to implement
        return Result.success()
    }
}