package com.todayit.common.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;

/** Today-it OpenAPI 문서의 공통 정보를 구성합니다. */
@Configuration
public class OpenApiConfig {
  public static final String BEARER_AUTH = "bearerAuth";

  /**
   * API 문서 정보와 JWT 인증 방식을 구성합니다.
   *
   * @return Today-it OpenAPI 설정
   */
  @Bean
  public OpenAPI todayItOpenApi() {
    return new OpenAPI()
        .info(new Info().title("Today-it API").description("Today-it 백엔드 API 명세").version("v1"))
        .components(
            new Components()
                .addSecuritySchemes(
                    BEARER_AUTH,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }

  /**
   * 빈 이름의 보안 요구사항이 선언된 선택적 인증 엔드포인트에 빈 보안 객체({})를 추가합니다.
   *
   * @return 선택적 인증 처리 OperationCustomizer
   */
  @Bean
  public OperationCustomizer optionalSecurityCustomizer() {
    return (operation, handlerMethod) -> {
      Operation opAnnotation =
          AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), Operation.class);
      if (opAnnotation != null) {
        boolean hasEmptySecurity =
            Arrays.stream(opAnnotation.security())
                .anyMatch(sec -> sec.name() == null || sec.name().isBlank());
        if (hasEmptySecurity) {
          if (operation.getSecurity() == null) {
            operation.setSecurity(new ArrayList<>());
          }
          boolean alreadyHasEmpty = operation.getSecurity().stream().anyMatch(Map::isEmpty);
          if (!alreadyHasEmpty) {
            operation.addSecurityItem(new SecurityRequirement());
          }
        }
      }
      return operation;
    };
  }
}
