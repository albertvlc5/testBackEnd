package atwork.extension

import atwork.valueobject.Money

fun Double.toMoney(): Money = Money(this)
