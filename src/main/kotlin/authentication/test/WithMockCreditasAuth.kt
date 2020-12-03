package authentication.test

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockCreditasSecurityContextFactory::class)
annotation class WithMockCreditasAuth(
    val userId: String = "123",
    val userType: String = "customer"
)
