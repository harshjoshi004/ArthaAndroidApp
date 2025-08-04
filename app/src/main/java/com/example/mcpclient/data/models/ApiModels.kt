package com.example.mcpclient.data.models

import com.google.gson.annotations.SerializedName

data class JsonRpcResponse<T>(
    @SerializedName("jsonrpc") val jsonrpc: String,
    @SerializedName("id") val id: Int,
    @SerializedName("result") val result: T?
)

data class JsonRpcContent(
    @SerializedName("content") val content: List<ContentItem>
)

data class ContentItem(
    @SerializedName("type") val type: String,
    @SerializedName("text") val text: String
)

// Bank Transactions Models
data class BankTransactionsResponse(
    @SerializedName("schemaDescription") val schemaDescription: String,
    @SerializedName("bankTransactions") val bankTransactions: List<BankTransaction>
)

data class BankTransaction(
    @SerializedName("bank") val bank: String,
    @SerializedName("txns") val txns: List<List<Any>>
)

// Net Worth Models
data class NetWorthResponse(
    @SerializedName("netWorthResponse") val netWorthResponse: NetWorthData,
    @SerializedName("mfSchemeAnalytics") val mfSchemeAnalytics: MfSchemeAnalytics,
    @SerializedName("accountDetailsBulkResponse") val accountDetailsBulkResponse: AccountDetailsBulkResponse
)

data class NetWorthData(
    @SerializedName("assetValues") val assetValues: List<AssetValue>,
    @SerializedName("totalNetWorthValue") val totalNetWorthValue: CurrencyValue
)

data class AssetValue(
    @SerializedName("netWorthAttribute") val netWorthAttribute: String,
    @SerializedName("value") val value: CurrencyValue
)

data class CurrencyValue(
    @SerializedName("currencyCode") val currencyCode: String,
    @SerializedName("units") val units: Double
)

data class MfSchemeAnalytics(
    @SerializedName("schemeAnalytics") val schemeAnalytics: List<SchemeAnalytic>
)

data class SchemeAnalytic(
    @SerializedName("schemeDetail") val schemeDetail: SchemeDetail,
    @SerializedName("enrichedAnalytics") val enrichedAnalytics: EnrichedAnalytics
)

data class SchemeDetail(
    @SerializedName("amc") val amc: String,
    @SerializedName("nameData") val nameData: NameData,
    @SerializedName("planType") val planType: String,
    @SerializedName("investmentType") val investmentType: String,
    @SerializedName("optionType") val optionType: String,
    @SerializedName("nav") val nav: CurrencyValue,
    @SerializedName("assetClass") val assetClass: String,
    @SerializedName("isinNumber") val isinNumber: String,
    @SerializedName("categoryName") val categoryName: String
)

data class NameData(
    @SerializedName("longName") val longName: String
)

data class EnrichedAnalytics(
    @SerializedName("analytics") val analytics: Analytics
)

data class Analytics(
    @SerializedName("schemeDetails") val schemeDetails: SchemeDetails
)

data class SchemeDetails(
    @SerializedName("currentValue") val currentValue: CurrencyValue,
    @SerializedName("investedValue") val investedValue: CurrencyValue,
    @SerializedName("XIRR") val xirr: Double,
    @SerializedName("unrealisedReturns") val unrealisedReturns: CurrencyValue,
    @SerializedName("units") val units: Double
)

data class AccountDetailsBulkResponse(
    @SerializedName("accountDetailsMap") val accountDetailsMap: Map<String, AccountDetail>
)

data class AccountDetail(
    @SerializedName("accountDetails") val accountDetails: AccountInfo?,
    @SerializedName("mutualFundSummary") val mutualFundSummary: MutualFundSummary?,
    @SerializedName("epfSummary") val epfSummary: EpfSummary?,
    @SerializedName("npsSummary") val npsSummary: NpsSummary?,
    @SerializedName("equitySummary") val equitySummary: EquitySummary?,
    @SerializedName("depositSummary") val depositSummary: DepositSummary?,
    @SerializedName("creditCardSummary") val creditCardSummary: CreditCardSummary?
)

data class AccountInfo(
    @SerializedName("fipId") val fipId: String,
    @SerializedName("maskedAccountNumber") val maskedAccountNumber: String,
    @SerializedName("accInstrumentType") val accInstrumentType: String
)

data class MutualFundSummary(
    @SerializedName("currentValue") val currentValue: CurrencyValue,
    @SerializedName("holdingsInfo") val holdingsInfo: List<HoldingInfo>
)

data class HoldingInfo(
    @SerializedName("isin") val isin: String,
    @SerializedName("folioNumber") val folioNumber: String?,
    @SerializedName("issuerName") val issuerName: String?,
    @SerializedName("units") val units: Int?,
    @SerializedName("lastTradedPrice") val lastTradedPrice: CurrencyValue?
)

data class EpfSummary(
    @SerializedName("currentBalance") val currentBalance: CurrencyValue
)

data class NpsSummary(
    @SerializedName("accountId") val accountId: String,
    @SerializedName("currentValue") val currentValue: CurrencyValue
)

data class EquitySummary(
    @SerializedName("currentValue") val currentValue: CurrencyValue,
    @SerializedName("holdingsInfo") val holdingsInfo: List<HoldingInfo>
)

data class DepositSummary(
    @SerializedName("currentBalance") val currentBalance: CurrencyValue,
    @SerializedName("depositAccountType") val depositAccountType: String
)

data class CreditCardSummary(
    @SerializedName("currentBalance") val currentBalance: CurrencyValue,
    @SerializedName("creditLimit") val creditLimit: CurrencyValue
)

// EPF Details Models
data class EpfDetailsResponse(
    @SerializedName("uanAccounts") val uanAccounts: List<UanAccount>
)

data class UanAccount(
    @SerializedName("phoneNumber") val phoneNumber: Map<String, Any>,
    @SerializedName("rawDetails") val rawDetails: RawDetails
)

data class RawDetails(
    @SerializedName("est_details") val estDetails: List<EstDetail>,
    @SerializedName("overall_pf_balance") val overallPfBalance: OverallPfBalance
)

data class EstDetail(
    @SerializedName("est_name") val estName: String,
    @SerializedName("member_id") val memberId: String,
    @SerializedName("office") val office: String,
    @SerializedName("doj_epf") val dojEpf: String,
    @SerializedName("doe_epf") val doeEpf: String,
    @SerializedName("doe_eps") val doeEps: String,
    @SerializedName("pf_balance") val pfBalance: PfBalance
)

data class PfBalance(
    @SerializedName("net_balance") val netBalance: String,
    @SerializedName("employee_share") val employeeShare: ShareDetail,
    @SerializedName("employer_share") val employerShare: ShareDetail
)

data class ShareDetail(
    @SerializedName("credit") val credit: String,
    @SerializedName("balance") val balance: String
)

data class OverallPfBalance(
    @SerializedName("pension_balance") val pensionBalance: String,
    @SerializedName("current_pf_balance") val currentPfBalance: String,
    @SerializedName("employee_share_total") val employeeShareTotal: ShareDetail,
    @SerializedName("employer_share_total") val employerShareTotal: ShareDetail
)

// Credit Report Models
data class CreditReportResponse(
    @SerializedName("creditReports") val creditReports: List<CreditReport>
)

data class CreditReport(
    @SerializedName("creditReportData") val creditReportData: CreditReportData,
    @SerializedName("vendor") val vendor: String
)

data class CreditReportData(
    @SerializedName("userMessage") val userMessage: UserMessage,
    @SerializedName("creditProfileHeader") val creditProfileHeader: CreditProfileHeader,
    @SerializedName("currentApplication") val currentApplication: CurrentApplication,
    @SerializedName("creditAccount") val creditAccount: CreditAccount,
    @SerializedName("matchResult") val matchResult: MatchResult,
    @SerializedName("totalCapsSummary") val totalCapsSummary: TotalCapsSummary,
    @SerializedName("nonCreditCaps") val nonCreditCaps: NonCreditCaps,
    @SerializedName("score") val score: Score,
    @SerializedName("segment") val segment: Map<String, Any>,
    @SerializedName("caps") val caps: Caps
)

data class UserMessage(
    @SerializedName("userMessageText") val userMessageText: String
)

data class CreditProfileHeader(
    @SerializedName("reportDate") val reportDate: String,
    @SerializedName("reportTime") val reportTime: String
)

data class CurrentApplication(
    @SerializedName("currentApplicationDetails") val currentApplicationDetails: CurrentApplicationDetails
)

data class CurrentApplicationDetails(
    @SerializedName("enquiryReason") val enquiryReason: String,
    @SerializedName("amountFinanced") val amountFinanced: String,
    @SerializedName("durationOfAgreement") val durationOfAgreement: String,
    @SerializedName("currentApplicantDetails") val currentApplicantDetails: CurrentApplicantDetails
)

data class CurrentApplicantDetails(
    @SerializedName("dateOfBirthApplicant") val dateOfBirthApplicant: String
)

data class CreditAccount(
    @SerializedName("creditAccountSummary") val creditAccountSummary: CreditAccountSummary,
    @SerializedName("creditAccountDetails") val creditAccountDetails: List<CreditAccountDetail>
)

data class CreditAccountSummary(
    @SerializedName("account") val account: Account,
    @SerializedName("totalOutstandingBalance") val totalOutstandingBalance: TotalOutstandingBalance
)

data class Account(
    @SerializedName("creditAccountTotal") val creditAccountTotal: String,
    @SerializedName("creditAccountActive") val creditAccountActive: String,
    @SerializedName("creditAccountDefault") val creditAccountDefault: String,
    @SerializedName("creditAccountClosed") val creditAccountClosed: String,
    @SerializedName("cadSuitFiledCurrentBalance") val cadSuitFiledCurrentBalance: String
)

data class TotalOutstandingBalance(
    @SerializedName("outstandingBalanceSecured") val outstandingBalanceSecured: String,
    @SerializedName("outstandingBalanceSecuredPercentage") val outstandingBalanceSecuredPercentage: String,
    @SerializedName("outstandingBalanceUnSecured") val outstandingBalanceUnSecured: String,
    @SerializedName("outstandingBalanceUnSecuredPercentage") val outstandingBalanceUnSecuredPercentage: String,
    @SerializedName("outstandingBalanceAll") val outstandingBalanceAll: String
)

data class CreditAccountDetail(
    @SerializedName("subscriberName") val subscriberName: String,
    @SerializedName("portfolioType") val portfolioType: String,
    @SerializedName("accountType") val accountType: String,
    @SerializedName("openDate") val openDate: String,
    @SerializedName("highestCreditOrOriginalLoanAmount") val highestCreditOrOriginalLoanAmount: String,
    @SerializedName("accountStatus") val accountStatus: String,
    @SerializedName("paymentRating") val paymentRating: String,
    @SerializedName("paymentHistoryProfile") val paymentHistoryProfile: String,
    @SerializedName("currentBalance") val currentBalance: String,
    @SerializedName("amountPastDue") val amountPastDue: String,
    @SerializedName("dateReported") val dateReported: String,
    @SerializedName("occupationCode") val occupationCode: String,
    @SerializedName("rateOfInterest") val rateOfInterest: String?,
    @SerializedName("repaymentTenure") val repaymentTenure: String,
    @SerializedName("dateOfAddition") val dateOfAddition: String,
    @SerializedName("currencyCode") val currencyCode: String,
    @SerializedName("accountHolderTypeCode") val accountHolderTypeCode: String,
    @SerializedName("creditLimitAmount") val creditLimitAmount: String?
)

data class MatchResult(
    @SerializedName("exactMatch") val exactMatch: String
)

data class TotalCapsSummary(
    @SerializedName("totalCapsLast7Days") val totalCapsLast7Days: String,
    @SerializedName("totalCapsLast30Days") val totalCapsLast30Days: String,
    @SerializedName("totalCapsLast90Days") val totalCapsLast90Days: String,
    @SerializedName("totalCapsLast180Days") val totalCapsLast180Days: String
)

data class NonCreditCaps(
    @SerializedName("nonCreditCapsSummary") val nonCreditCapsSummary: NonCreditCapsSummary,
    @SerializedName("capsApplicationDetailsArray") val capsApplicationDetailsArray: List<CapsApplicationDetail>
)

data class NonCreditCapsSummary(
    @SerializedName("nonCreditCapsLast7Days") val nonCreditCapsLast7Days: String,
    @SerializedName("nonCreditCapsLast30Days") val nonCreditCapsLast30Days: String,
    @SerializedName("nonCreditCapsLast90Days") val nonCreditCapsLast90Days: String,
    @SerializedName("nonCreditCapsLast180Days") val nonCreditCapsLast180Days: String
)

data class CapsApplicationDetail(
    @SerializedName("SubscriberName") val subscriberName: String,
    @SerializedName("FinancePurpose") val financePurpose: String,
    @SerializedName("capsApplicantDetails") val capsApplicantDetails: Map<String, Any>,
    @SerializedName("capsOtherDetails") val capsOtherDetails: Map<String, Any>,
    @SerializedName("capsApplicantAddressDetails") val capsApplicantAddressDetails: Map<String, Any>,
    @SerializedName("capsApplicantAdditionalAddressDetails") val capsApplicantAdditionalAddressDetails: Map<String, Any>,
    @SerializedName("DateOfRequest") val dateOfRequest: String?,
    @SerializedName("EnquiryReason") val enquiryReason: String?
)

data class Score(
    @SerializedName("bureauScore") val bureauScore: String,
    @SerializedName("bureauScoreConfidenceLevel") val bureauScoreConfidenceLevel: String
)

data class Caps(
    @SerializedName("capsSummary") val capsSummary: CapsSummary,
    @SerializedName("capsApplicationDetailsArray") val capsApplicationDetailsArray: List<CapsApplicationDetail>
)

data class CapsSummary(
    @SerializedName("capsLast7Days") val capsLast7Days: String,
    @SerializedName("capsLast30Days") val capsLast30Days: String,
    @SerializedName("capsLast90Days") val capsLast90Days: String,
    @SerializedName("capsLast180Days") val capsLast180Days: String
)

// MF Transactions Models
data class MfTransactionsResponse(
    @SerializedName("mfTransactions") val mfTransactions: List<MfTransaction>,
    @SerializedName("schemaDescription") val schemaDescription: String
)

data class MfTransaction(
    @SerializedName("isin") val isin: String,
    @SerializedName("schemeName") val schemeName: String,
    @SerializedName("folioId") val folioId: String,
    @SerializedName("txns") val txns: List<List<Any>>
)

// Stock Transactions Models
data class StockTransactionsResponse(
    @SerializedName("schemaDescription") val schemaDescription: String,
    @SerializedName("stockTransactions") val stockTransactions: List<StockTransaction>
)

data class StockTransaction(
    @SerializedName("isin") val isin: String,
    @SerializedName("txns") val txns: List<List<Any>>
)
