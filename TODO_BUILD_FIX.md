# UdhaarPay Build Fix TODO List

## Critical Compilation Errors (Priority 1)

### CommonComponents.kt
- [ ] Fix Unresolved reference 'KeyboardOptions' (line 20, 141)
- [ ] Fix experimental material API warnings (lines 363, 383)
- [ ] Fix @Composable invocation context issues (line 366)
- [ ] Fix argument type mismatch issues (line 373)

### AnalyticsScreen.kt
- [ ] Fix unresolved references: isLoading, error, monthlySpending, selectedMonth, clearError, loadAnalytics, totalSpent, totalReceived, selectMonth, category, weeklyData
- [ ] Fix type inference issues for State delegates
- [ ] Fix TextAlign unresolved reference
- [ ] Fix No value passed for parameter issues

### Service Screens (Multiple files)
- [ ] Fix ImageVector vs Composable function argument mismatches in:
  - BroadbandRechargeScreen.kt
  - BusBookingScreen.kt
  - DTHRechargeScreen.kt
  - ElectricityBillScreen.kt
  - FlightBookingScreen.kt
  - GasCylinderScreen.kt
  - HotelBookingScreen.kt
  - InsuranceProductsScreen.kt
  - LoanBookingScreen.kt
  - MovieTicketsScreen.kt
  - MutualFundsScreen.kt
  - TrainBookingScreen.kt
  - WaterBillScreen.kt

### Other Screens
- [ ] Fix NFCPaymentScreen.kt: Unresolved reference 'clip'
- [ ] Fix OffersScreen.kt: @Composable invocation and border issues
- [ ] Fix ServicesScreen.kt: filterChipColors and border issues
- [ ] Fix TransactionHistoryScreen.kt: Unresolved reference 'border'

### Legacy Fragment Issues
- [ ] Fix ScanPayFragment.kt: Unresolved references to 'common', 'Barcode', 'loadPaymentFragment'

## Model-ViewModel Mismatches (Priority 2)
- [ ] ProfileViewModel: Update to use user.name instead of user.fullName
- [ ] TransactionHistoryScreen: Fix TransactionType enum comparisons
- [ ] Add missing Transaction fields (recipientName) or update UI

## Missing DAO Methods (Priority 3)
- [ ] TransactionRepository: Fix method calls to match DAO signatures
  - getAllTransactions() → getTransactionsFlow(userId)
  - saveTransaction() → insertTransaction()
  - Add getDebitTransactions(), getCreditTransactions(), deleteTransaction()

## Theme and Color Issues (Priority 4)
- [ ] Add AccentLime to ui/theme/Color.kt
- [ ] Fix VectorIcon → Icon composable references

## Repository Suspend Context (Priority 5)
- [ ] Add suspend wrappers or proper Flow management in TransactionRepository

## Legacy Code Cleanup (Priority 6)
- [ ] Remove or migrate all .java Fragment files
- [ ] Consolidate duplicate adapters and components

## Build Configuration
- [ ] Suppress compileSdk warning in gradle.properties

## Progress Tracking
- [ ] Run build after each major fix to verify progress
- [ ] Update this TODO as fixes are completed
