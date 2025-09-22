package com.example.paymentservice.payment.adapter.out.web.toss.executor.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TossPaymentConfirmationResponse {

  private String version;
  private String paymentKey;
  private String type;
  private String orderId;
  private String orderName;
  private String mId;
  private String currency;
  private String method;
  private int totalAmount;
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
  @Getter
  public class Cancel {
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
  public class Card {
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
  public class VirtualAccount {
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
  public class MobilePhone {
    private String customerMobilePhone;
    private String settlementStatus;
    private String receiptUrl;
  }

  @Builder
  @Getter
  public class GiftCertificate {
    private String approveNo;
    private String settlemnetStatus;
  }

  @Builder
  @Getter
  public class Transfer {
    private String bankCode;
    private String settlementStatus;
  }

  @Builder
  @Getter
  public class Receipt {
    private String url;
  }

  @Builder
  @Getter
  public class Checkout {
    private String url;
  }

  @Builder
  @Getter
  public class EasyPay {
    private String provider;
    private Integer amount;
    private Integer discountAmount;
  }

  @Builder
  @Getter
  public class TossFailureResponse {
    private String code;
    private String message;
  }

  @Builder
  @Getter
  public class CashReceipt {
    private String type;
    private String receiptKey;
    private String issueNumber;
    private String receiptUrl;
    private Integer amount;
    private Integer taxFreeAmount;
  }

  @Builder
  @Getter
  public class Discount {
    private Integer amount;
  }

  @Builder
  @Getter
  public class RefundReceiveAccount {
    private String bankCode;
    private String accountNumber;
    private String holderName;
  }
}
