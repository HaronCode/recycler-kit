package ru.haroncode.aquarius.core.base.strategies.results

import ru.haroncode.aquarius.core.base.strategies.DifferStrategy
import ru.haroncode.aquarius.core.observer.DataSourceObserver

internal class ChangeRangeResult(
    private val position: Int,
    private val count: Int,
    private val payload: Any? = null
) : DifferStrategy.Result {

    override fun dispatchUpdatesTo(dataSourceObserver: DataSourceObserver) {
        dataSourceObserver.onItemRangeChanged(position, count, payload)
    }
}
