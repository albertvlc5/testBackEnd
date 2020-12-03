package atwork.valueobject

import java.util.UUID

data class UserInfo(val id: UUID, val name: String?, val customerId: UUID?, val email: String?, val cpf: Cpf)
