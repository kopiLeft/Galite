package org.kopi.galite.form.dsl

import org.kopi.galite.common.Action

/**
 * This class represents an action, ie a method to execute
 */
class FormAction<T>(protected var name: String?, method: () -> T) : Action<T>(method) {

}
