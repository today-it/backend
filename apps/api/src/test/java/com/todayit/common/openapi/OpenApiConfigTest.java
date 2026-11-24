package com.todayit.common.openapi;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.OpenAPI;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.web.method.HandlerMethod;

@DisplayName("OpenAPI 설정 단위 테스트")
class OpenApiConfigTest {

  private final OpenApiConfig config = new OpenApiConfig();

  @Test
  @DisplayName("OpenAPI 빈에 Bearer 인증 스키마가 정상 등록된다")
  void configuresBearerAuthScheme() {
    // when
    OpenAPI openAPI = config.todayItOpenApi();

    // then
    assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey(OpenApiConfig.BEARER_AUTH);
  }

  @Test
  @DisplayName("선택적 인증이 선언된 메서드는 빈 보안 객체({})가 security 목록에 추가된다")
  void addsEmptySecurityItemForOptionalAuth() throws Exception {
    // given
    OperationCustomizer customizer = config.optionalSecurityCustomizer();
    io.swagger.v3.oas.models.Operation operation = new io.swagger.v3.oas.models.Operation();
    operation.addSecurityItem(
        new io.swagger.v3.oas.models.security.SecurityRequirement()
            .addList(OpenApiConfig.BEARER_AUTH));

    Method method = TestController.class.getMethod("optionalAuthMethod");
    HandlerMethod handlerMethod = new HandlerMethod(new TestController(), method);

    // when
    customizer.customize(operation, handlerMethod);

    // then
    assertThat(operation.getSecurity()).hasSize(2);
    boolean hasEmptyItem = operation.getSecurity().stream().anyMatch(Map::isEmpty);
    assertThat(hasEmptyItem).isTrue();
  }

  @Test
  @DisplayName("필수 인증만 선언된 메서드는 빈 보안 객체가 추가되지 않는다")
  void doesNotAddEmptySecurityItemForRequiredAuth() throws Exception {
    // given
    OperationCustomizer customizer = config.optionalSecurityCustomizer();
    io.swagger.v3.oas.models.Operation operation = new io.swagger.v3.oas.models.Operation();
    operation.addSecurityItem(
        new io.swagger.v3.oas.models.security.SecurityRequirement()
            .addList(OpenApiConfig.BEARER_AUTH));

    Method method = TestController.class.getMethod("requiredAuthMethod");
    HandlerMethod handlerMethod = new HandlerMethod(new TestController(), method);

    // when
    customizer.customize(operation, handlerMethod);

    // then
    assertThat(operation.getSecurity()).hasSize(1);
    boolean hasEmptyItem = operation.getSecurity().stream().anyMatch(Map::isEmpty);
    assertThat(hasEmptyItem).isFalse();
  }

  static class TestController {
    @Operation(
        summary = "선택적 인증 메서드",
        security = {
          @SecurityRequirement(name = ""),
          @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
        })
    public void optionalAuthMethod() {}

    @Operation(
        summary = "필수 인증 메서드",
        security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH))
    public void requiredAuthMethod() {}
  }
}
