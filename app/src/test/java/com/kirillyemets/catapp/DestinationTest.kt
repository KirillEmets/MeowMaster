package com.kirillyemets.catapp

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.kirillyemets.catapp.ui.navigation.Destination
import org.junit.Assert.assertEquals
import org.junit.Test

class DestinationTest {
    @Test
    fun noArgumentDestinationAndRouteAreRight() {
        val testDestination = object : Destination("Test") {
            fun route() = routeWithArgs()
        }

        assertEquals("Test", testDestination.destination)
        assertEquals("Test", testDestination.route())
    }

    @Test
    fun singleArgumentNotNullDestinationAndRouteAreRight() {
        val testDestination = object : Destination(
            name = "Test1",
            args = listOf(navArgument("age") { type = NavType.IntType })
        ) {
            fun route(age: Int) = this.routeWithArgs("age" to age)
        }

        assertEquals("Test1/{age}", testDestination.destination)
        assertEquals("Test1/14", testDestination.route(14))
    }

    @Test
    fun singleArgumentNullableDestinationAndRouteAreRight() {
        val testDestination = object : Destination(
            name = "Test2",
            args = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            fun route(name: String?) = this.routeWithArgs("name" to name)
        }

        assertEquals("Test2?name={name}", testDestination.destination)
        assertEquals("Test2?name=Kyryl", testDestination.route("Kyryl"))
        assertEquals("Test2", testDestination.route(null))
    }

    @Test
    fun twoArgumentsNullableSecondDestinationAndRouteAreRight() {
        val testDestination = object : Destination(
            name = "Test",
            args = listOf(
                navArgument("age") {
                    type = NavType.IntType
                },
                navArgument("name") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            fun route(age: Int, name: String? = null) =
                this.routeWithArgs("age" to age, "name" to name)
        }

        assertEquals("Test?age={age}&name={name}", testDestination.destination)
        assertEquals("Test?age=21&name=Kyryl", testDestination.route(21, "Kyryl"))
        assertEquals("Test?age=21", testDestination.route(21))
    }

    @Test
    fun twoArgumentsNullableFirstDestinationAndRouteAreRight() {
        val testDestination = object : Destination(
            name = "Test",
            args = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("age") {
                    type = NavType.IntType
                }
            )
        ) {
            fun route(age: Int, name: String? = null) =
                this.routeWithArgs("age" to age, "name" to name)
        }

        assertEquals("Test?name={name}&age={age}", testDestination.destination)
        assertEquals("Test?name=Kyryl&age=21", testDestination.route(21, "Kyryl"))
        assertEquals("Test?age=21", testDestination.route(21))
    }

    @Test
    fun threeArgumentsNullableDestinationAndRouteAreRight() {
        val testDestination = object : Destination(
            name = "Test",
            args = listOf(
                navArgument("surname") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("name") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("status") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            fun route(surname: String?, name: String? = null, status: String?) =
                this.routeWithArgs("surname" to surname, "name" to name, "status" to status)
        }

        assertEquals(
            "Test?surname={surname}&name={name}&status={status}",
            testDestination.destination
        )
        assertEquals(
            "Test?surname=Yemets&name=Kyryl&status=online",
            testDestination.route("Yemets", "Kyryl", "online")
        )
        assertEquals(
            "Test?name=Kyryl&status=online",
            testDestination.route(null, "Kyryl", "online")
        )
        assertEquals(
            "Test?surname=Yemets&status=online",
            testDestination.route("Yemets", null, "online")
        )
        assertEquals(
            "Test?surname=Yemets&name=Kyryl",
            testDestination.route("Yemets", "Kyryl", null)
        )
        assertEquals("Test?status=online", testDestination.route(null, null, "online"))
        assertEquals("Test?surname=Yemets", testDestination.route("Yemets", null, null))
        assertEquals("Test?name=Kyryl", testDestination.route(null, "Kyryl", null))
        assertEquals("Test", testDestination.route(null, null, null))
    }
}