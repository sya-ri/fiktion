package example

import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderTest {
    @Test
    fun paidOrdersCanFocusOnlyOnRelevantValues() {
        val order = fake<Order>(seed = 123) {
            Order::status generates OrderStatus.Paid
        }

        assertEquals(OrderStatus.Paid, order.status)
        assertEquals(order, fake<Order>(seed = 123) { Order::status generates OrderStatus.Paid })
    }
}

data class Order(
    val id: String,
    val customer: Customer,
    val status: OrderStatus,
)

data class Customer(
    val id: String,
    val email: String,
)

enum class OrderStatus {
    Draft,
    Paid,
    Shipped,
}
