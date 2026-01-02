# UdhaarPay Build Fix Plan

## Priority 1: Critical Compilation Errors

### CommonComponents.kt
- [x] Fix @Composable invocation context issues in PremiumTopAppBar (line ~366)
- [x] Fix experimental material API warnings (lines ~363, 383)
- [x] Fix argument type mismatch issues (line ~373)
- [x] Remove unused KeyboardOptions import if not needed

### AnalyticsScreen.kt
- [x] Fix unresolved references: isLoading, error, monthlySpending, selectedMonth, clearError, loadAnalytics, totalSpent, totalReceived, selectMonth, category, weeklyData
- [x] Fix type inference issues for State delegates
- [x] Fix TextAlign unresolved reference
- [x] Fix No value passed for parameter issues

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

## Priority 2: Model-ViewModel Mismatches
- [ ] ProfileViewModel: Update to use user.name instead of user.fullName
- [ ] TransactionHistoryScreen: Fix TransactionType enum comparisons
- [ ] Add missing Transaction fields (recipientName) or update UI

## Priority 3: Missing DAO Methods
- [ ] TransactionRepository: Fix method calls to match DAO signatures
  - getAllTransactions() → getTransactionsFlow(userId)
  - saveTransaction() → insertTransaction()
  - Add getDebitTransactions(), getCreditTransactions(), deleteTransaction()

## Priority 4: Theme and Color Issues
- [ ] Add AccentLime to ui/theme/Color.kt
- [ ] Fix VectorIcon → Icon composable references

## Priority 5: Repository Suspend Context
- [ ] Add suspend wrappers or proper Flow management in TransactionRepository

## Priority 6: Legacy Code Cleanup
- [ ] Remove or migrate all .java Fragment files
- [ ] Consolidate duplicate adapters and components

## Build Configuration
- [ ] Suppress compileSdk warning in gradle.properties

## Progress Tracking
- [ ] Run build after each major fix to verify progress
- [ ] Update this TODO as fixes are completed
