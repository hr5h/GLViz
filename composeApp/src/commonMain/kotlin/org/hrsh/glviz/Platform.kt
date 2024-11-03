package org.hrsh.glviz

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform