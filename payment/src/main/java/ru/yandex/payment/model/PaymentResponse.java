package ru.yandex.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * PaymentResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-19T20:28:07.083674400+04:00[Europe/Samara]", comments = "Generator version: 7.12.0")
public class PaymentResponse {

  private @Nullable Boolean success;

  private @Nullable Float remainingBalance;

  private @Nullable String error;

  public PaymentResponse success(Boolean success) {
    this.success = success;
    return this;
  }

  /**
   * Get success
   * @return success
   */
  
  @Schema(name = "success", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("success")
  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public PaymentResponse remainingBalance(Float remainingBalance) {
    this.remainingBalance = remainingBalance;
    return this;
  }

  /**
   * Get remainingBalance
   * @return remainingBalance
   */
  
  @Schema(name = "remainingBalance", example = "6.3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("remainingBalance")
  public Float getRemainingBalance() {
    return remainingBalance;
  }

  public void setRemainingBalance(Float remainingBalance) {
    this.remainingBalance = remainingBalance;
  }

  public PaymentResponse error(String error) {
    this.error = error;
    return this;
  }

  /**
   * Get error
   * @return error
   */
  
  @Schema(name = "error", example = "Недостаточно средств для оплаты", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("error")
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentResponse paymentResponse = (PaymentResponse) o;
    return Objects.equals(this.success, paymentResponse.success) &&
        Objects.equals(this.remainingBalance, paymentResponse.remainingBalance) &&
        Objects.equals(this.error, paymentResponse.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(success, remainingBalance, error);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentResponse {\n");
    sb.append("    success: ").append(toIndentedString(success)).append("\n");
    sb.append("    remainingBalance: ").append(toIndentedString(remainingBalance)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

