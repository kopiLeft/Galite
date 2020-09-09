package org.kopi.galite.base

import java.text.ChoiceFormat
import java.text.FieldPosition


/**
 * A customized choice format to support null test on objects.
 * The implementation transforms a boolean condition to a numeric
 * one in order to use the standard choice format implementation.
 *
 * In this conditions, we can pass any kind of object as an argument
 * to this extended choice format and for theses kind of objects, a null
 * test is performed and then transformed to {0, 1} numeric condition.
 * Knowing that 1 means that the object is not null and 0 means that the
 * object is null.
 *
 * @see .format
 */
open class ExtendedChoiceFormat : ChoiceFormat {
    // ----------------------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------------------
    constructor(newPattern: String?, hasNotNullMarker: Boolean) : super(newPattern) {
        this.hasNotNullMarker = hasNotNullMarker
    }

    constructor(limits: DoubleArray?, formats: Array<String?>?, hasNotNullMarker: Boolean) : super(limits, formats) {
        this.hasNotNullMarker = hasNotNullMarker
    }

    // ----------------------------------------------------------------------
    // IMPLEMENTATION
    // ----------------------------------------------------------------------
    /*
   * (non-Javadoc)
   * @see java.text.NumberFormat#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
   */
    override fun format(argument:Any, toAppendTo:StringBuffer, pos:FieldPosition):StringBuffer {
        // a null test is performed before :
        // false --> 0
        // true --> 1
        return if (argument is Boolean || hasNotNullMarker && !(argument is Number))
        {
            formatObject(argument, toAppendTo, pos)
        }
        else
        {
            // default behavior so number instances should pass here including fixed values
            super.format(argument, toAppendTo, pos)
        }
    }

    /**
     * Formats any kind of non numeric object. Only null test is performed and transformed to {0, 1} condition.
     * @param argument The argument to be formatted.
     * @param toAppendTo The resulting string buffer.
     * @param pos The field position.
     * @return The formatted string.
     */


    private fun formatObject(argument:Any, toAppendTo:StringBuffer, pos:FieldPosition):StringBuffer {
        return if (argument is Boolean)
        {
            super.format(toNumeric((argument )), toAppendTo, pos)
        }
        else
        {
            super.format(toNumeric(argument !== ExtendedMessageFormat.NULL_REPRESENTATION), toAppendTo, pos)
        }
    }


    /**
     * Transforms the given boolean value to a numeric value.
     * The translation is done in a way that 0 is equivalent to false
     * and 1 is equivalent to true.
     * @param value The boolean value.
     * @return The equivalent numeric value.
     */
    protected fun toNumeric(value: Boolean): Int {
        return if (value) 1 else 0
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    private val hasNotNullMarker: Boolean

    companion object {
        /**
         * Generated serial ID.
         */
        private const val serialVersionUID = -17531293681639232L
    }
}
