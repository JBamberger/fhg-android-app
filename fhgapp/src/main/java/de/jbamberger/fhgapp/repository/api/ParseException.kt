package de.jbamberger.fhgapp.repository.api

import java.io.IOException

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class ParseException : IOException {
    constructor() : super()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)
}
