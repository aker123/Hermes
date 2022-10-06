package com.example.hermes.domain.filters

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


/**
 * Класс фильтр.
 * @param V тип данных объектов фильтрации.
 * @param condition условие фильтра.
 * @param name наименование.
 * @param enable активность.
 * @param mode режим объединения в наборе.
 * @param filterGroup вложенные фильтры.
 */
@Parcelize
open class Filter<V>(
    val condition:@RawValue Condition<V>,
    open val name: String,
    open var enable: Boolean,
    open val mode: Mode,
    open val filterGroup: FilterGroup<V> = FilterGroup(mutableListOf())
) : Parcelable {

    /**
     * Условие фильтра
     */
    interface Condition<V> {
        /**
         * @return true, если [item] соответствует условию фильтра.
         */
        fun filter(item: V): Boolean
    }

    /**
     * Режимы объединения в наборе.
     */
    enum class Mode {
        /**
         * Фильтр объединяется в наборе через логическое "ИЛИ".
         */
        OR,

        /**
         * Фильтр объединяется в наборе через логическое "И".
         */
        AND
    }

    /**
     * @return true, если [item] соответствует условиям вложенных фильтров.
     */
    open fun filterGroup(item: V): Boolean {
        return filterGroup.filter(item)
    }

    /**
     * @return true, если:
     * фильтр включен.
     * [item] соответствует условию фильтра.
     * [item] соответствует условиям вложенных фильтров.
     */
    fun filter(item: V): Boolean {
        return enable && condition.filter(item) && filterGroup(item)
    }
}