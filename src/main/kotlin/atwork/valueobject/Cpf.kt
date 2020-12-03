package atwork.valueobject

data class Cpf(val number: String) {
    override fun toString(): String = number
}
