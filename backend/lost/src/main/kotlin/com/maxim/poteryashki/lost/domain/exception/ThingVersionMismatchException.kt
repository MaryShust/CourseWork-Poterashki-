package com.maxim.poteryashki.lost.domain.exception

/**
 * Ошибка связанная с конфликтом версий
 */
class ThingVersionMismatchException(
    val passedVersion: Long?,
    val actualVersion: Long?
) : RuntimeException("Version mismatch. Passed: $passedVersion, actual: $actualVersion") {
}