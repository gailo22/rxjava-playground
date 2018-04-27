package com.gailo22.springbootrxjava1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    @Bean
    public Docket api() {
//        List<Parameter> headerParameters = new ArrayList<>();
//        headerParameters.add(buildHeaderParameter("resourceOwnerID", "CustomerInfo Id", true));
//        headerParameters.add(buildHeaderParameter("branchId", "Branch Id", true));
//        headerParameters.add(buildHeaderParameter("caseId", "Case Id", false));
//        headerParameters.add(buildHeaderParameter("authorization", "Authorities separated by comma e.g. SSC,SC", false));

        return new Docket(DocumentationType.SWAGGER_2)
//                .globalOperationParameters(headerParameters)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gailo22"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Arrays.asList(new ApiKey("token", "Authorization", "header")))
                .apiInfo(apiInfo());
    }

    private Parameter buildHeaderParameter(String name, String description, boolean required) {
        return new ParameterBuilder()
                .name(name)
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .description(description)
                .required(required)
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "REST API",
                "Some custom description of API.",
                "API TOS",
                "Terms of service",
                new Contact("Haha MAN", "www.example.com", "myeaddress@company.com"),
                "License of API", "API license URL", Collections.emptyList());
    }
}
