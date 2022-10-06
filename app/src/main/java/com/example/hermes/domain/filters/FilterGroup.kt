package com.example.hermes.domain.filters

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * Класс для операций над набором фильтров.
 * @param filters список фильтров.
 */
@Parcelize
class FilterGroup<V>(
    val filters: MutableList<out Filter<V>>
) : Parcelable {

    fun setEnable(enable: Boolean) {
        filters.forEach {
            it.enable = enable
            it.filterGroup.setEnable(enable)
        }
    }

    fun getFiltersByEnable(enable: Boolean): List<Filter<V>> {
        return filters.filter { it.enable == enable }
    }

    inline fun <reified T> getFiltersByType(): List<T> {
        val list: MutableList<T> = mutableListOf()
        filters.forEach { if (it is T) list.add(it) }
        return list
    }

    inline fun <reified T> getFiltersByConditionType(): List<Filter<V>> {
        val list: MutableList<Filter<V>> = mutableListOf()
        filters.forEach { if (it.condition is T) list.add(it) }
        return list
    }

    fun getFiltersByItem(item: V): List<Filter<V>> {
        val list: MutableList<Filter<V>> = mutableListOf()
        filters.forEach { if (it.condition.filter(item)) list.add(it) }
        return list
    }

    fun filter(item: V): Boolean {
        val filtersEnabled: List<Filter<V>> = getFiltersByEnable(true)
        if (filtersEnabled.isEmpty()) return true

        val completedAnd = filtersEnabled
            .filter { it.mode == Filter.Mode.AND }
            .find { !it.filter(item) } == null

        val completedOr = filtersEnabled
            .filter { it.mode == Filter.Mode.OR }
            .takeIf { it.isNotEmpty() }
            ?.let { filters -> filters.find { it.filter(item) } != null } ?: true

        return completedAnd && completedOr
    }
}