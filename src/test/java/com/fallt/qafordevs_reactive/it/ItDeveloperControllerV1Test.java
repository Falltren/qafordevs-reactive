package com.fallt.qafordevs_reactive.it;

import com.fallt.qafordevs_reactive.config.PostgreTestcontainerConfig;
import com.fallt.qafordevs_reactive.dto.DeveloperDto;
import com.fallt.qafordevs_reactive.entity.DeveloperEntity;
import com.fallt.qafordevs_reactive.repository.DeveloperRepository;
import com.fallt.qafordevs_reactive.util.DataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(PostgreTestcontainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ItDeveloperControllerV1Test {

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        developerRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Test create developer functionality")
    void givenDeveloperDto_whenCreateDeveloper_thenSuccessResponse() {
        //given
        DeveloperDto dto = DataUtils.getJohnDoeDtoTransient();
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
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.firstName").isEqualTo("John")
                .jsonPath("$.lastName").isEqualTo("Doe")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Test create developer with duplicate email functionality")
    void givenDeveloperDtoWithDuplicateEmail_whenCreateDeveloper_thenExceptionIsThrown() {
        //given
        String duplicateEmail = "duplicate@gmail.com";
        DeveloperDto dto = DataUtils.getJohnDoeDtoTransient();
        DeveloperEntity entity = DataUtils.getJohnDoeTransient();
        dto.setEmail(duplicateEmail);
        entity.setEmail(duplicateEmail);
        developerRepository.save(entity).block();
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
        String updatedEmail = "updatedEmail@gmail.com";
        DeveloperEntity entity = DataUtils.getJohnDoeTransient();
        developerRepository.save(entity).block();
        DeveloperDto dto = DataUtils.getJohnDoeDtoPersisted();
        dto.setEmail(updatedEmail);
        dto.setId(entity.getId());
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
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.firstName").isEqualTo("John")
                .jsonPath("$.lastName").isEqualTo("Doe")
                .jsonPath("$.email").isEqualTo(updatedEmail)
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Test update developer with incorrect id functionality")
    void givenDtoWithIncorrectId_whenUpdateDeveloper_thenExceptionIsThrown() {
        //given
        DeveloperDto dto = DataUtils.getJohnDoeDtoPersisted();
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
        DeveloperEntity developer1 = DataUtils.getJohnDoeTransient();
        DeveloperEntity developer2 = DataUtils.getMikeSmithTransient();
        DeveloperEntity developer3 = DataUtils.getFrankJonesTransient();
        List<DeveloperEntity> list = List.of(developer1, developer2, developer3);
        developerRepository.saveAll(list).blockLast();
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
        DeveloperEntity developer = DataUtils.getJohnDoeTransient();
        developerRepository.save(developer).block();
        //when
        WebTestClient.ResponseSpec result = webTestClient.get()
                .uri("/api/v1/developers/" + developer.getId())
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.firstName").isEqualTo(developer.getFirstName())
                .jsonPath("$.lastName").isEqualTo(developer.getLastName())
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Test get developer by incorrect id functionality")
    void givenIncorrectId_whenGetById_thenExceptionIsThrown() {
        //given
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
        DeveloperEntity entity = DataUtils.getJohnDoeTransient();
        developerRepository.save(entity).block();
        //when
        WebTestClient.ResponseSpec result = webTestClient.delete()
                .uri("/api/v1/developers/" + entity.getId())
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
        DeveloperEntity entity = DataUtils.getJohnDoeTransient();
        developerRepository.save(entity).block();
        //when
        WebTestClient.ResponseSpec result = webTestClient.delete()
                .uri("/api/v1/developers/" + entity.getId() + "?isHard=true")
                .exchange();
        //then
        DeveloperEntity obtainedDeveloper = developerRepository.findById(entity.getId()).block();
        assertThat(obtainedDeveloper).isNull();
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println);
    }

    @Test
    @DisplayName("Test hard delete by incorrect id functionality")
    void givenIncorrectId_whenHardDeleteById_thenExceptionIsThrown() {
        //given
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
