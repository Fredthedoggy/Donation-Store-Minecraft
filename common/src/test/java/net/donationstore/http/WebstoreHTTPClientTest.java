package net.donationstore.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.donationstore.commands.*;
import net.donationstore.dto.CurrencyBalanceDTO;
import net.donationstore.dto.CurrencyCodeDTO;
import net.donationstore.dto.GatewayResponse;
import net.donationstore.dto.InformationDTO;
import net.donationstore.exception.WebstoreAPIException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class WebstoreHTTPClientTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Spy
    public WebstoreHTTPClient webstoreHTTPClient;

    private CommandFactory commandFactory;

    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        //webstoreHTTPClient = new WebstoreHTTPClient();

        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        objectMapper = new ObjectMapper();

        webstoreHTTPClient.setSecretKey("secretKey")
                .setWebstoreAPILocation("https://example.com");

        ReflectionTestUtils.setField(webstoreHTTPClient, "httpClient", httpClient);

        ReflectionTestUtils.setField(webstoreHTTPClient, "objectMapper", objectMapper);
    }

    @Test
    public void gettersSettersTest() {
        // then
        assertEquals("secretKey", webstoreHTTPClient.getSecretKey());
        assertEquals("https://example.com", webstoreHTTPClient.getWebstoreAPILocation());
    }

    @Test
    public void generateExceptionTest() {

    }

    @Test
    public void getAPIUrlTest() throws Exception {
        // when
        URI uri = webstoreHTTPClient.getAPIUrl("information");

        // then
        assertEquals(uri.getAuthority(), "example.com");
        assertEquals(uri.getHost(), "example.com");
        assertEquals(uri.getPath(), "/information");
    }

    @Test
    public void connectCommandTest() throws Exception {
        // given
        doReturn("{\"webstore\": {\"currency\": \"EUR\", \"id\": 1, \"name\": \"Example Store\"}, \"server\": {\"ip\": \"127.0.0.1\", \"id\": 1, \"name\": \"Hello World\"}}").when(webstoreHTTPClient).sendHttpRequest(any(HttpClient.class), any(HttpRequest.class));
        ConnectCommand connect = new ConnectCommand();
        connect.validate(new String[]{"connect", "secretKey", "https://example.com"});

        // when
        GatewayResponse gatewayResponse = webstoreHTTPClient.get(connect, "information");
        InformationDTO informationDTO = (InformationDTO) gatewayResponse.getBody();

        // then
        assertEquals(informationDTO.webstore.get("currency"), "EUR");
        assertEquals(informationDTO.webstore.get("id"), 1);
        assertEquals(informationDTO.webstore.get("name"), "Example Store");
        assertEquals(informationDTO.server.get("ip"), "127.0.0.1");
        assertEquals(informationDTO.server.get("name"), "Hello World");
    }

    @Test
    public void getCurrencyBalancesTest() throws Exception {
        // given
        doReturn("{\"username\": \"MCxJB\", \"uuid\": \"28408e37-5b7d-4c6d-b723-b7a845418dcd\", \"balances\": {\"EUR\": \"1.00\"}}").when(webstoreHTTPClient).sendHttpRequest(any(HttpClient.class), any(HttpRequest.class));
        GetCurrencyBalancesCommand getCurrencyBalancesCommand = new GetCurrencyBalancesCommand();
        getCurrencyBalancesCommand.validate(new String[]{"balance", "secretKey", "https://example.com", "28408e375b7d4c6db723b7a845418dcd"});

        // when
        GatewayResponse gatewayResponse = webstoreHTTPClient.post(getCurrencyBalancesCommand, "currency/balances");
        CurrencyBalanceDTO currencyBalanceDTO = (CurrencyBalanceDTO) gatewayResponse.getBody();

        // then
        assertEquals("MCxJB", currencyBalanceDTO.username);
        assertEquals(UUID.fromString("28408e37-5b7d-4c6d-b723-b7a845418dcd"), currencyBalanceDTO.uuid);
        assertEquals("1.00", currencyBalanceDTO.balances.get("EUR"));
    }

    @Test
    public void getCurrencyCodeTest() throws Exception {
        // given
        doReturn("{\"code\": \"D3CRWAZ47A\", \"uuid\": \"28408e37-5b7d-4c6d-b723-b7a845418dcd\"}").when(webstoreHTTPClient).sendHttpRequest(any(HttpClient.class), any(HttpRequest.class));
        GetCurrencyCodeCommand getCurrencyCodeCommand = new GetCurrencyCodeCommand();
        getCurrencyCodeCommand.validate(new String[]{"code", "secretKey", "https://example.com", "28408e375b7d4c6db723b7a845418dcd"});

        // when
        GatewayResponse gatewayResponse = webstoreHTTPClient.post(getCurrencyCodeCommand, "currency/code/generate");
        CurrencyCodeDTO currencyCodeDTO = (CurrencyCodeDTO) gatewayResponse.getBody();

        // then
        assertEquals("D3CRWAZ47A", currencyCodeDTO.code);
        assertEquals(UUID.fromString("28408e37-5b7d-4c6d-b723-b7a845418dcd"), currencyCodeDTO.uuid);
    }

    @Test
    public void giveCurrencyCodeTest() throws Exception {
        // given
        // Come back to when the API is more standardised, because now it needs a bit of work
    }

    @Test
    public void postRequestTest() {

    }

    @Test
    public void ioExceptionTest() {

    }

    @Test
    public void interruptedExceptionTest() {

    }

    @Test
    public void uriSyntaxExceptionTest() {

    }
}