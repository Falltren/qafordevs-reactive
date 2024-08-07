package com.fallt.qafordevs_reactive.controller;

import com.fallt.qafordevs_reactive.dto.DeveloperDto;
import com.fallt.qafordevs_reactive.entity.DeveloperEntity;
import com.fallt.qafordevs_reactive.exception.DeveloperNotFoundException;
import com.fallt.qafordevs_reactive.exception.DeveloperWithEmailAlreadyExistsException;
import com.fallt.qafordevs_reactive.service.DeveloperService;
import com.fallt.qafordevs_reactive.util.DataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ComponentScan("com.fallt.qafordevs_reactive.exception")
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = DeveloperControllerV1.class)
class DeveloperControllerV1Test {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DeveloperService developerService;

    @Test
    @DisplayName("Test create developer functionality")
    void givenDeveloperDto_whenCreateDeveloper_thenSuccessResponse() {
        //given
        DeveloperDto dto = DataUtils.getJohnDoeDtoTransient();
        DeveloperEntity entity = DataUtils.getJohnDoePersisted();
        BDDMockito.given(developerService.createDeveloper(any(DeveloperEntity.class)))
                .willReturn(Mono.just(entity));
        //when
        WebTestClient.ResponseSpec result = webTestClient.post()
                .uri("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), DeveloperDto.class)
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.firstName").isEqualTo("John")
                .jsonPath("$.lastName").isEqualTo("Doe")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Test create developer with duplicate email functionality")
    void givenDeveloperDtoWithDuplicateEmail_whenCreateDeveloper_thenExceptionIsThrown() {
        //given
        DeveloperDto dto = DataUtils.getJohnDoeDtoTransient();
        BDDMockito.given(developerService.createDeveloper(any(DeveloperEntity.class)))
                .willThrow(new DeveloperWithEmailAlreadyExistsException("Developer with defined email already exists", "DEVELOPER_DUPLICATE_EMAIL"));
        //when
        WebTestClient.ResponseSpec result = webTestClient.post()
                .uri("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), DeveloperDto.class)
                .exchange();
        //then
        result.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo("Developer with defined email already exists")
                .jsonPath("$.errorCode").isEqualTo("DEVELOPER_DUPLICATE_EMAIL");
    }

    @Test
    @DisplayName("Test update developer functionality")
    void givenDeveloperDto_whenUpdateDeveloper_thenSuccessResponse() {
        //given
        DeveloperDto dto = DataUtils.getJohnDoeDtoPersisted();
        DeveloperEntity entity = DataUtils.getJohnDoePersisted();
        BDDMockito.given(developerService.updateDeveloper(any(DeveloperEntity.class)))
                .willReturn(Mono.just(entity));
        //when
        WebTestClient.ResponseSpec result = webTestClient.put()
                .uri("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), DeveloperDto.class)
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.firstName").isEqualTo("John")
                .jsonPath("$.lastName").isEqualTo("Doe")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Test update developer with incorrect id functionality")
    void givenDtoWithIncorrectId_whenUpdateDeveloper_thenExceptionIsThrown() {
        //given
        DeveloperDto dto = DataUtils.getJohnDoeDtoPersisted();
        BDDMockito.given(developerService.updateDeveloper(any(DeveloperEntity.class)))
                .willThrow(new DeveloperNotFoundException("Developer not found", "DEVELOPER_NOT_FOUND"));
        //when
        WebTestClient.ResponseSpec result = webTestClient.put()
                .uri("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), DeveloperDto.class)
                .exchange();
        //then
        result.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo("Developer not found")
                .jsonPath("$.errorCode").isEqualTo("DEVELOPER_NOT_FOUND");
    }

    @Test
    @DisplayName("Test get all developers functionality")
    void givenThreeDevelopers_whenGetAll_thenDevelopersAreReturned() {
        //given
        DeveloperEntity developer1 = DataUtils.getJohnDoePersisted();
        DeveloperEntity developer2 = DataUtils.getMikeSmithPersisted();
        DeveloperEntity developer3 = DataUtils.getFrankJonesPersisted();
        BDDMockito.given(developerService.getAllDevelopers())
                .willReturn(Flux.just(developer1, developer2, developer3));
        //when
        WebTestClient.ResponseSpec result = webTestClient.get()
                .uri("/api/v1/developers")
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    void givenId_whenGetById_thenDeveloperIsReturned() {
        //given
        DeveloperEntity developer = DataUtils.getJohnDoePersisted();
        BDDMockito.given(developerService.getById(anyInt()))
                .willReturn(Mono.just(developer));
        //when
        WebTestClient.ResponseSpec result = webTestClient.get()
                .uri("/api/v1/developers/" + developer.getId())
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(developer.getId())
                .jsonPath("$.firstName").isEqualTo(developer.getFirstName())
                .jsonPath("$.lastName").isEqualTo(developer.getLastName())
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Test get developer by incorrect id functionality")
    void givenIncorrectId_whenGetById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerService.getById(anyInt()))
                .willThrow(new DeveloperNotFoundException("Developer not found", "DEVELOPER_NOT_FOUND"));
        //when
        WebTestClient.ResponseSpec result = webTestClient.get()
                .uri("/api/v1/developers/1")
                .exchange();
        //then
        result.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo("Developer not found")
                .jsonPath("$.errorCode").isEqualTo("DEVELOPER_NOT_FOUND");
    }

    @Test
    @DisplayName("Test soft delete functionality")
    void givenId_whenSoftDeleteById_thenSuccessResponse() {
        //given
        BDDMockito.given(developerService.softDeleteById(anyInt()))
                .willReturn(Mono.empty());
        //when
        WebTestClient.ResponseSpec result = webTestClient.delete()
                .uri("/api/v1/developers/1")
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println);
    }

    @Test
    @DisplayName("Test soft delete by incorrect id functionality")
    void givenIncorrectId_whenSoftDeleteById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerService.softDeleteById(anyInt()))
                .willThrow(new DeveloperNotFoundException("Developer not found", "DEVELOPER_NOT_FOUND"));
        //when
        WebTestClient.ResponseSpec result = webTestClient.delete()
                .uri("/api/v1/developers/1")
                .exchange();
        //then
        result.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo("Developer not found")
                .jsonPath("$.errorCode").isEqualTo("DEVELOPER_NOT_FOUND");
    }

    @Test
    @DisplayName("Test hard delete functionality")
    void givenId_whenHardDeleteById_thenSuccessResponse() {
        //given
        BDDMockito.given(developerService.hardDeleteById(anyInt()))
                .willReturn(Mono.empty());
        //when
        WebTestClient.ResponseSpec result = webTestClient.delete()
                .uri("/api/v1/developers/1?isHard=true")
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println);
    }

    @Test
    @DisplayName("Test hard delete by incorrect id functionality")
    void givenIncorrectId_whenHardDeleteById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerService.hardDeleteById(anyInt()))
                .willThrow(new DeveloperNotFoundException("Developer not found", "DEVELOPER_NOT_FOUND"));
        //when
        WebTestClient.ResponseSpec result = webTestClient.delete()
                .uri("/api/v1/developers/1?isHard=true")
                .exchange();
        //then
        result.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo("Developer not found")
                .jsonPath("$.errorCode").isEqualTo("DEVELOPER_NOT_FOUND");
    }
}
