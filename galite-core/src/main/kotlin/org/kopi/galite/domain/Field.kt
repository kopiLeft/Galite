package org.kopi.galite.domain

import org.kopi.galite.domain.exceptions.InvalidValueException

/**
 * A field represents a visual component that can hold values
 *
 * The type of the value and the range of possible values can be specified by the [domain]
 *
 * @param domain the field's domain
 */
class Field<T: Comparable<T>>(val domain: Domain<T>? = null)  {

    /** Field's label */
    var label: String = ""

    /**
     * Check that value passed to the field doesn't exceed the size of the field's domain
     *
     * @param value passed value
     * @return true if the domain is not defined or the value's length doesn't exceed the domain size,
     * and returns false otherwise.
     */
    fun checkLength(value: T): Boolean = when {
        domain == null -> true
        domain.length == null -> true
        else -> value.toString().length <= domain.length
    }

    fun checkValue(value: T): Boolean = when {
        domain == null -> true
        domain.check == null -> true
        domain.check!!.invoke(value) -> true
        else -> throw InvalidValueException(value, label)
    }
}