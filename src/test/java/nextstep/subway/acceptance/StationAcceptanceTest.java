package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.model.StationEntitiesHelper.*;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("지하철역 관리 기능")
class StationAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철역 생성을 요청 하면
     * Then 지하철역 생성이 성공한다.
     */
    @DisplayName("지하철역 생성")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        assertThat(response.header(LOCATION)).isNotBlank();
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * Given 새로운 지하철역 생성을 요청 하고
     * When 지하철역 목록 조회를 요청 하면
     * Then 두 지하철역이 포함된 지하철역 목록을 응답받는다
     */
    @DisplayName("지하철역 목록 조회")
    @Test
    void getStations() {
        // given
        ExtractableResponse<Response> createResponse1 = 지하철역_생성_요청(강남역);

        // and
        ExtractableResponse<Response> createResponse2 = 지하철역_생성_요청(역삼역);

        // when
        ExtractableResponse<Response> response = 지하철역_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.jsonPath().getList("name")).contains(강남역, 역삼역);
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 생성한 지하철역 삭제를 요청 하면
     * Then 생성한 지하철역 삭제가 성공한다.
     */
    @DisplayName("지하철역 삭제")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청(강남역);

        // when
        ExtractableResponse<Response> response = 지하철역_삭제_요청(createResponse.header(LOCATION));

        // then
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 같은 이름으로 지하철역 생성을 요청 하면
     * Then 지하철역 생성이 실패한다.
     */
    @DisplayName("중복이름으로 지하철역 생성")
    @Test
    void createDuplicateStation()  {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청(강남역);

        // when
        ExtractableResponse<Response> failResponse = 지하철역_생성_요청(강남역);

        // then
        assertThat(failResponse.statusCode()).isEqualTo(BAD_REQUEST.value());
    }
}
