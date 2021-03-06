package ru.haroncode.aquarius.core.async

import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.haroncode.aquarius.core.BaseNotifier
import ru.haroncode.aquarius.core.Differ
import ru.haroncode.aquarius.core.Notifier
import ru.haroncode.aquarius.core.RenderAdapter
import ru.haroncode.aquarius.core.ViewTypeSelector
import ru.haroncode.aquarius.core.clicker.Clicker
import ru.haroncode.aquarius.core.helper.RenderItemTouchHelperCallback
import ru.haroncode.aquarius.core.observer.AdapterDataListUpdateCallback
import ru.haroncode.aquarius.core.observer.DataListUpdateCallback
import ru.haroncode.aquarius.core.renderer.BaseRenderer
import kotlin.reflect.KClass

class AsyncRenderAdapter<T : Any>(
    itemCallback: DiffUtil.ItemCallback<T>,
    itemIdSelector: (T) -> Long,
    touchHelperCallback: RenderItemTouchHelperCallback,
    viewTypeSelector: ViewTypeSelector<KClass<out T>>,
    clickers: SparseArrayCompat<Clicker<*, out RecyclerView.ViewHolder>>,
    renderers: SparseArrayCompat<BaseRenderer<out T, *, out RecyclerView.ViewHolder>>
) : RenderAdapter<T>(
    itemIdSelector = itemIdSelector,
    viewTypeSelector = object : ViewTypeSelector<T> {
        override fun createViewTypeFor(item: T): Int = viewTypeSelector.createViewTypeFor(item::class)

        override fun viewTypeFor(item: T): Int = viewTypeSelector.viewTypeFor(item::class)
    },
    clickers = clickers,
    renderers = renderers,
    touchHelperCallback = touchHelperCallback
) {

    private val adapterObserver: DataListUpdateCallback = AdapterDataListUpdateCallback(this)

    override val differ: Differ<T> = AsyncDiffer(itemCallback, adapterObserver)

    override val notifier: Notifier<T> = BaseNotifier(differ, adapterObserver)

    override fun onItemDismiss(position: Int, direction: Int) {
        val newList = differ.currentList.toMutableList().apply { removeAt(position) }
        differ.submitList(newList)
    }

    private class AsyncDiffer<T : Any>(
        itemCallback: DiffUtil.ItemCallback<T>,
        dataListUpdateCallback: DataListUpdateCallback
    ) : Differ<T> {

        private val asyncDiffer = AsyncListDiffer(
            dataListUpdateCallback,
            AsyncDifferConfig.Builder(itemCallback).build()
        )

        override val currentList: List<T>
            get() = asyncDiffer.currentList

        override fun submitList(items: List<T>) {
            asyncDiffer.submitList(items)
        }
    }
}
