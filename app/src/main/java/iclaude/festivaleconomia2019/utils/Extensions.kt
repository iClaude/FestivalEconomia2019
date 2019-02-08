package iclaude.festivaleconomia2019.utils

import androidx.databinding.ObservableBoolean

fun ObservableBoolean.hasSameValue(other: ObservableBoolean) = get() == other.get()