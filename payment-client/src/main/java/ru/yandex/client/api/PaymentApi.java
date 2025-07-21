package ru.yandex.client.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.yandex.client.ApiClient;
import ru.yandex.client.model.BalanceResponse;
import ru.yandex.client.model.PaymentRequest;
import ru.yandex.client.model.PaymentResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-07-19T20:37:53.441780+04:00[Europe/Samara]", comments = "Generator version: 7.12.0")
public class PaymentApi {
    private ApiClient apiClient;

    public PaymentApi() {
        this(new ApiClient());
    }

    public PaymentApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    
    /**
     * Получение баланса на счёте
     * Возвращает текущий баланс пользователя. Используется для определения доступности оформления заказа.
     * <p><b>200</b> - Баланс успешно получен
     * <p><b>503</b> - Сервис недоступен
     * @return BalanceResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec balanceGetRequestCreation() throws WebClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<BalanceResponse> localVarReturnType = new ParameterizedTypeReference<BalanceResponse>() {};
        return apiClient.invokeAPI("/balance", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Получение баланса на счёте
     * Возвращает текущий баланс пользователя. Используется для определения доступности оформления заказа.
     * <p><b>200</b> - Баланс успешно получен
     * <p><b>503</b> - Сервис недоступен
     * @return BalanceResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<BalanceResponse> balanceGet() throws WebClientResponseException {
        ParameterizedTypeReference<BalanceResponse> localVarReturnType = new ParameterizedTypeReference<BalanceResponse>() {};
        return balanceGetRequestCreation().bodyToMono(localVarReturnType);
    }


    /**
     * Проведение платежа
     * Осуществляет вычитание суммы заказа из баланса. Возвращает результат платежа.
     * <p><b>200</b> - Платёж успешно выполнен
     * <p><b>400</b> - Недостаточно средств для оплаты
     * <p><b>503</b> - Сервис недоступен
     * @param paymentRequest The paymentRequest parameter
     * @return PaymentResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec paymentPostRequestCreation(PaymentRequest paymentRequest) throws WebClientResponseException {
        Object postBody = paymentRequest;
        // verify the required parameter 'paymentRequest' is set
        if (paymentRequest == null) {
            throw new WebClientResponseException("Missing the required parameter 'paymentRequest' when calling paymentPost", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<PaymentResponse> localVarReturnType = new ParameterizedTypeReference<PaymentResponse>() {};
        return apiClient.invokeAPI("/payment", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Проведение платежа
     * Осуществляет вычитание суммы заказа из баланса. Возвращает результат платежа.
     * <p><b>200</b> - Платёж успешно выполнен
     * <p><b>400</b> - Недостаточно средств для оплаты
     * <p><b>503</b> - Сервис недоступен
     * @param paymentRequest The paymentRequest parameter
     * @return PaymentResponse
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<PaymentResponse> paymentPost(PaymentRequest paymentRequest) throws WebClientResponseException {
        ParameterizedTypeReference<PaymentResponse> localVarReturnType = new ParameterizedTypeReference<PaymentResponse>() {};
        return paymentPostRequestCreation(paymentRequest).bodyToMono(localVarReturnType);
    }
}
