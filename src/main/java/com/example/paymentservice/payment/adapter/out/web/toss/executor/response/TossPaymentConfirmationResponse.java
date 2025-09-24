package com.example.paymentservice.payment.adapter.out.web.toss.executor.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TossPaymentConfirmationResponse {

  private String version;
  private String paymentKey;
  private String type;
  private String orderId;
  private String orderName;
  private String mId;
  private String currency;
  private String method;
  private Integer totalAmount;
  private int balanceAmount;
  private String status;
  private String requestedAt;
  private String approvedAt;
  private boolean useEscrow;
  private String lastTransactionKey;
  private int suppliedAmount;
  private int vat;
  private boolean cultureExpense;
  private int taxFreeAmount;
  private int taxExemptionAmount;
  private List<Cancel> cancels;
  private Card card;
  private VirtualAccount virtualAccount;
  private MobilePhone mobilePhone;
  private GiftCertificate giftCertificate;
  private Transfer transfer;
  private Receipt receipt;
  private Checkout checkout;
  private EasyPay easyPay;
  private String country;
  private TossFailureResponse failure;
  private CashReceipt cashReceipt;
  private List<CashReceipt> cashReceipts;
  private Discount discount;

  @Builder
  public TossPaymentConfirmationResponse(String approvedAt, int balanceAmount, List<Cancel> cancels,
      Card card, CashReceipt cashReceipt, List<CashReceipt> cashReceipts, Checkout checkout,
      String country, boolean cultureExpense, String currency, Discount discount, EasyPay easyPay,
      TossFailureResponse failure, GiftCertificate giftCertificate, String lastTransactionKey,
      String method, String mId, MobilePhone mobilePhone, String orderId, String orderName,
      String paymentKey, Receipt receipt, String requestedAt, String status, int suppliedAmount,
      int taxExemptionAmount, int taxFreeAmount, Integer totalAmount, Transfer transfer,
      String type,
      boolean useEscrow, int vat, String version, VirtualAccount virtualAccount) {
    this.approvedAt = approvedAt;
    this.balanceAmount = balanceAmount;
    this.cancels = cancels;
    this.card = card;
    this.cashReceipt = cashReceipt;
    this.cashReceipts = cashReceipts;
    this.checkout = checkout;
    this.country = country;
    this.cultureExpense = cultureExpense;
    this.currency = currency;
    this.discount = discount;
    this.easyPay = easyPay;
    this.failure = failure;
    this.giftCertificate = giftCertificate;
    this.lastTransactionKey = lastTransactionKey;
    this.method = method;
    this.mId = mId;
    this.mobilePhone = mobilePhone;
    this.orderId = orderId;
    this.orderName = orderName;
    this.paymentKey = paymentKey;
    this.receipt = receipt;
    this.requestedAt = requestedAt;
    this.status = status;
    this.suppliedAmount = suppliedAmount;
    this.taxExemptionAmount = taxExemptionAmount;
    this.taxFreeAmount = taxFreeAmount;
    this.totalAmount = totalAmount;
    this.transfer = transfer;
    this.type = type;
    this.useEscrow = useEscrow;
    this.vat = vat;
    this.version = version;
    this.virtualAccount = virtualAccount;
  }

  @Builder
  @Getter
  public static class Cancel {
    private Integer cancelAmount;
    private String cancelReason;
    private Integer taxFreeAmount;
    private Integer taxExemptionAmount;
    private Integer refundableAmount;
    private Integer easyPayDiscountAmount;
    private String canceledAt;
    private String transactionKey;
    private String receiptKey;
    private Boolean isPartialCancelable;
  }

  @Builder
  @Getter
  public static class Card {
    private Integer amount;
    private String issuerCode;
    private String acquirerCode;
    private String number;
    private Integer installmentPlanMonths;
    private String approveNo;
    private Boolean useCardPoint;
    private String cardType;
    private String ownerType;
    private String acquireStatus;
  }

  @Builder
  @Getter
  public static class VirtualAccount {
    private String accountType;
    private String accountNumber;
    private String bankCode;
    private String customerName;
    private String dueDate;
    private String refundStatus;
    private Boolean expired;
    private String settlementStatus;
    private RefundReceiveAccount refundReceiveAccount;
    private String secret;
  }

  @Builder
  @Getter
  public static class MobilePhone {
    private String customerMobilePhone;
    private String settlementStatus;
    private String receiptUrl;
  }

  @Builder
  @Getter
  public static class GiftCertificate {
    private String approveNo;
    private String settlemnetStatus;
  }

  @Builder
  @Getter
  public static class Transfer {
    private String bankCode;
    private String settlementStatus;
  }

  @Builder
  @Getter
  public static class Receipt {
    private String url;
  }

  @Builder
  @Getter
  public static class Checkout {
    private String url;
  }

  @Builder
  @Getter
  public static class EasyPay {
    private String provider;
    private Integer amount;
    private Integer discountAmount;
  }

  @Builder
  @Getter
  public static class TossFailureResponse {
    private String code;
    private String message;
  }

  @Builder
  @Getter
  public static class CashReceipt {
    private String type;
    private String receiptKey;
    private String issueNumber;
    private String receiptUrl;
    private Integer amount;
    private Integer taxFreeAmount;
  }

  @Builder
  @Getter
  public static class Discount {
    private Integer amount;
  }

  @Builder
  @Getter
  public static class RefundReceiveAccount {
    private String bankCode;
    private String accountNumber;
    private String holderName;
  }
}
