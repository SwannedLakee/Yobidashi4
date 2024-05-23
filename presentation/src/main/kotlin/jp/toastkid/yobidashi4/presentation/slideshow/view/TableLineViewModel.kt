package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import jp.toastkid.yobidashi4.presentation.markdown.TableSortService

class TableLineViewModel {

    private var lastSorted = -1 to false

    private val tableData: MutableState<List<List<Any>>> =  mutableStateOf(emptyList())

    private val headerCursorOn = mutableStateOf(false)

    private val tableSortService = TableSortService()

    fun tableData() = tableData.value

    fun start(table: List<List<Any>>) {
        tableData.value = table
    }

    private fun sort(
        lastSortOrder: Boolean,
        index: Int,
        articleStates: MutableState<List<List<Any>>>
    ) {
        val newItems = tableSortService.invoke(lastSortOrder, index, articleStates.value)
        if (newItems != null) {
            articleStates.value = newItems
        }
    }

    fun clickHeaderColumn(index: Int) {
        val lastSortOrder = if (lastSorted.first == index) lastSorted.second else false
        lastSorted = index to lastSortOrder.not()

        sort(lastSortOrder, index, tableData)
    }

    fun setCursorOnHeader() {
        headerCursorOn.value = true
    }

    fun setCursorOffHeader() {
        headerCursorOn.value = false
    }

    fun onCursorOnHeader() = headerCursorOn.value

}