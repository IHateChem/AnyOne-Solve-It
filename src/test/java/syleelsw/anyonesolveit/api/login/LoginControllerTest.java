package syleelsw.anyonesolveit.api.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import syleelsw.anyonesolveit.config.SpringSecurityConfig;
import syleelsw.anyonesolveit.etc.JwtTokenProvider;
import syleelsw.anyonesolveit.service.login.LoginService;
import syleelsw.anyonesolveit.service.login.dto.LoginResponse;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@EnableAspectJAutoProxy @AutoConfigureWebMvc
@WebMvcTest(LoginController.class) @Slf4j
@Import(SpringSecurityConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
class LoginControllerTest {


    @MockBean
    private LoginService loginService;
    @Autowired
    private SpringSecurityConfig springSecurityConfig;

    @InjectMocks
    private LoginController loginController;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("login시도시 잘못된 Provider를 요청하면 400에러를 뱉는다. 제대로된 요청은 200을 뱉는다. ")
    @Test
    void test() throws Exception {
        // Mocking loginService.login() method response
        LoginResponse expectedResponse = LoginResponse.builder().isFirst(true).username("user").build();
        given(loginService.login(any())).willReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Create a LoginBody instance
        Map loginBody = Map.of("authCode", "12354", "provider", "oogle2");

        // Perform the POST request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginBody)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());



        // Create a LoginBody instance
        Map loginBody2 = Map.of("authCode", "12354", "provider", "GOOGLE");

        // Perform the POST request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginBody2)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        //given

        //when

        //then
    }

}