package wallet.api.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import wallet.api.infra.security.annotations.PublicRoute;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI walletAPIDocs() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wallet API")
                        .version("v1")
                        .description("Documentação da API"))
                .components(new Components().addSecuritySchemes("JavaInUseSecurityScheme", new SecurityScheme()
                        .name("JavaInUseSecurityScheme").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));


    }

    @Bean
    public OperationCustomizer securityCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            boolean isPublic = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), PublicRoute.class) != null
                    || AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), PublicRoute.class) != null;

            if (isPublic) {
                // Rota pública → não exige token
                operation.setSecurity(null);
                operation.addTagsItem("Public");
                String desc = operation.getDescription() != null ? operation.getDescription() : "";
                operation.setDescription("🔓 Público (não requer autenticação). " + desc);
            } else {
                // Rota privada → exige token
                operation.addSecurityItem(new SecurityRequirement().addList("JavaInUseSecurityScheme"));

                String desc = operation.getDescription() != null ? operation.getDescription() : "";
                operation.setDescription("🔒 Privado (requer Bearer Token). " + desc);
            }
            return operation;
        };
    }


}
