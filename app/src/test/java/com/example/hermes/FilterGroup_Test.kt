package com.example.hermes

import com.example.hermes.domain.filters.Filter
import com.example.hermes.domain.filters.FilterGroup
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.junit.Assert.*

class FilterGroup_Test {
    private lateinit var filters: MutableList<Filter<Any>>
    private lateinit var filter1: Filter<Any>
    private lateinit var filter2: Filter<Any>
    private lateinit var filter3: Filter<Any>
    private lateinit var item: Any

    @Before
    fun init() {
        filter1 = mock(Filter::class.java) as Filter<Any>
        filter2 = mock(Filter::class.java) as Filter<Any>
        filter3 = mock(Filter::class.java) as Filter<Any>
        filters = mutableListOf(filter1, filter2, filter3)
        item = Any()

        `when`(filter1.enable).thenReturn(true)
        `when`(filter2.enable).thenReturn(true)
        `when`(filter3.enable).thenReturn(true)

        `when`(filter1.mode).thenReturn(Filter.Mode.AND)
        `when`(filter2.mode).thenReturn(Filter.Mode.AND)
        `when`(filter3.mode).thenReturn(Filter.Mode.AND)
    }

    @Test
    fun getFiltersByEnable() {
        val filterGroup = FilterGroup(filters)

        `when`(filter1.enable).thenReturn(false)
        `when`(filter2.enable).thenReturn(true)
        `when`(filter3.enable).thenReturn(true)

        val filtersEnabled = filterGroup.getFiltersByEnable(true)
        assertTrue(filtersEnabled.contains(filter2))
        assertTrue(filtersEnabled.contains(filter3))
        assertEquals(2, filtersEnabled.size)
    }

    @Test
    fun filter_Disabled() {
        val filterGroup = FilterGroup(filters)

        `when`(filter1.enable).thenReturn(false)
        `when`(filter2.enable).thenReturn(true)
        `when`(filter3.enable).thenReturn(true)

        `when`(filter1.filter(item)).thenReturn(false)
        `when`(filter2.filter(item)).thenReturn(true)
        `when`(filter3.filter(item)).thenReturn(true)

        assertTrue(filterGroup.filter(item))
    }

    @Test
    fun filter_ModeAnd_Completed() {
        val filterGroup = FilterGroup(filters)

        `when`(filter1.mode).thenReturn(Filter.Mode.AND)
        `when`(filter2.mode).thenReturn(Filter.Mode.AND)
        `when`(filter3.mode).thenReturn(Filter.Mode.AND)

        `when`(filter1.filter(item)).thenReturn(true)
        `when`(filter2.filter(item)).thenReturn(true)
        `when`(filter3.filter(item)).thenReturn(true)

        assertTrue(filterGroup.filter(item))
    }

    @Test
    fun filter_ModeAnd_OneNotCompleted() {
        val filterGroup = FilterGroup(filters)

        `when`(filter1.mode).thenReturn(Filter.Mode.AND)
        `when`(filter2.mode).thenReturn(Filter.Mode.AND)
        `when`(filter3.mode).thenReturn(Filter.Mode.AND)

        `when`(filter1.filter(item)).thenReturn(false)
        `when`(filter2.filter(item)).thenReturn(true)
        `when`(filter3.filter(item)).thenReturn(true)

        assertFalse(filterGroup.filter(item))
    }

    @Test
    fun filter_ModeOr_Completed() {
        val filterGroup = FilterGroup(filters)

        `when`(filter1.mode).thenReturn(Filter.Mode.OR)
        `when`(filter2.mode).thenReturn(Filter.Mode.OR)
        `when`(filter3.mode).thenReturn(Filter.Mode.OR)

        `when`(filter1.filter(item)).thenReturn(true)
        `when`(filter2.filter(item)).thenReturn(true)
        `when`(filter3.filter(item)).thenReturn(true)

        assertTrue(filterGroup.filter(item))
    }

    @Test
    fun filter_ModeOr_OneNotCompleted() {
        val filterGroup = FilterGroup(filters)

        `when`(filter1.mode).thenReturn(Filter.Mode.OR)
        `when`(filter2.mode).thenReturn(Filter.Mode.OR)
        `when`(filter3.mode).thenReturn(Filter.Mode.OR)

        `when`(filter1.filter(item)).thenReturn(false)
        `when`(filter2.filter(item)).thenReturn(true)
        `when`(filter3.filter(item)).thenReturn(true)

        assertTrue(filterGroup.filter(item))
    }
}