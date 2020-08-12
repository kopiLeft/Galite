package org.kopi.galite.domain

/**
 * A field represents a visual component that can hold values
 *
 * The type of the value and the range of possible values can be specified by the [domain]
 *
 * @param domain the field's domain
 */
class Field<T: Comparable<T>>(val domain: Domain<T>? = null)  {
    var label: String = ""

    /**
     * Check that value passed to the field doesn't exceed the size
     * of the field's domain
     *
     * @param value passed value
     * @return true if the domain is not defined so that the field has a primitive type or the
     * value's size doesn't exceed the domain size, and returns false otherwise.
     */
    fun checkLength(value: T): Boolean {
        if(domain?.length == null) {
            return true
        }

        if(value.toString().length > domain.length) {
            return false
        }

        return true
    }
}