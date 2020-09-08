package org.kopi.galite.base
import java.io.Serializable
/**
 * {@code UComponent} is the top-level interface that is and must be implemented
 * by all kopi visual components.
 */
interface UComponent:Serializable {
    /**
     * Tests whether the component is enabled or not. A user can not interact
     * with disabled components. Disabled components are rendered in a style
     * that indicates the status, usually in gray color. Children of a disabled
     * component are also disabled. Components are enabled by default.
     * @see #setEnabled(boolean)
     */
    /**
     * Enables or disables the component. The user can not interact disabled
     * components, which are shown with a style that indicates the status,
     * usually shaded in light gray color. Components are enabled by default.
     * @param enabled a boolean value specifying if the component should be enabled or not.
     * @see #isEnabled()
     */
    var isEnabled:Boolean
    /**
     * Tests the <i>visibility</i> property of the component.
     *
     * <p>
     * Visible components are drawn in the user interface, while invisible ones
     * are not.
     * </p>
     *
     * <p>
     * A component is visible only if all its parents are also visible. This is
     * not checked by this method though, so even if this method returns true,
     * the component can be hidden from the user because a parent is set to
     * invisible.
     * </p>
     *
     * @return <code>true</code> if the component has been set to be visible in
     * the user interface, <code>false</code> if not
     * @see #setVisible(boolean)
     */
    /**
     * Sets the visibility of the component.
     *
     * <p>
     * Visible components are drawn in the user interface, while invisible ones
     * are not.
     * </p>
     *
     * <p>
     * A component is visible only if all of its parents are also visible. If a
     * component is explicitly set to be invisible, changes in the visibility of
     * its parents will not change the visibility of the component.
     * </p>
     *
     * @param visible the boolean value specifying if the component should be
     * visible after the call or not.
     * @see #isVisible()
     */
    var isVisible:Boolean
}