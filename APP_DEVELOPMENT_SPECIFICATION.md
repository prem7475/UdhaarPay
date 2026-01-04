# UdhaarPay - Complete Banking & Payment App
## Comprehensive Development Specification

---

## PART 1: PROJECT ARCHITECTURE & SETUP

### 1.1 App Structure Overview
```
Package: com.udhaarpay.app

Main Sections:
├── Home/Dashboard
├── Payments (Send Money, Request Money, NFC Pay)
├── Tickets (Movies, Flights, Trains, Buses)
├── Investments (SIP, Mutual Funds, Demat, Bonds)
├── Insurance
├── Credit Cards (Apply, Manage, Add to UPI)
├── Bill Payments & Recharges
├── Bank Accounts (Add, Remove, Manage)
├── Debt/Money Lending (Track loans given/taken)
├── Profile (Edit, Settings)
└── Wallet Management
```

### 1.2 Database Structure (Room)
```
Entities to Create:
- UserProfile (name, email, phone, photo, kyc_status)
- BankAccount (account_number, ifsc_code, bank_name, account_type)
- CreditCard (card_number, card_type, expiry, balance, issuer)
- UPIPayment (transaction_id, amount, date, merchant, status)
- NFCTransaction (card_id, amount, date, merchant)
- Debt (person_name, amount, type[given/taken], date, category[food,travel,etc])
- Investment (broker_name, amount, type[sip/mutual/demat], date)
- Ticket (ticket_type[movie/flight/train/bus], booking_id, amount, date)
- Insurance (policy_id, type, premium, expiry)
```

### 1.3 Website Color Scheme (Get from your website)
**IMPORTANT**: Use colors from your UdhaarPay website, not the reference image colors.
- Primary Color: [Your website primary]
- Secondary Color: [Your website secondary]
- Accent Color: [Your website accent]
- Background: [Your website background]
- Text Colors: [Your website text colors]

---

## PART 2: DETAILED FEATURE SPECIFICATIONS WITH PROMPTS

---

# FEATURE 1: HOME DASHBOARD
## Prompt 1A: Create Dashboard Screen with Quick Actions

**What to do:**
Create a modern home dashboard showing:
- User greeting with profile image
- Wallet balance (mock data: ₹10,000)
- Quick action buttons (8 main sections)
- Recent transactions (if any)
- QR code for receiving payments

**Implementation Details:**
```
Screen: HomeScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/home/

Content:
- Header: User name + profile pic + wallet balance
- Grid: 8 quick action cards
  1. Send Money
  2. Scan & Pay (NFC)
  3. Tickets
  4. Invest
  5. Insurance
  6. Credit Cards
  7. Bill Payments
  8. My Accounts
- Section: Recent Transactions (empty initially)
- Bottom: Wallet details, QR code display
```

**Code Structure:**
```kotlin
@Composable
fun HomeScreen(navController: NavController) {
    // User data from ViewModel
    // Display wallet balance
    // Show 8 action cards in 2x4 grid
    // Each card navigates to respective feature
}

data class QuickAction(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)
```

---

## Prompt 1B: Create Wallet Management Screen

**What to do:**
Create wallet balance display with:
- Current balance
- Today's spending
- This month's spending
- Add/Withdraw from wallet
- View detailed wallet history

**Implementation Details:**
```
Screen: WalletScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/wallet/

Content:
- Large balance display: ₹10,000 (mock)
- Stats: Today's spending + Month's spending
- Buttons: Add Money, Withdraw, View History
- Transaction list (from Debt database)
- Categories breakdown (pie chart)
```

---

# FEATURE 2: PAYMENTS SYSTEM

## Prompt 2A: Send Money & Request Money Screen

**What to do:**
Create two-tab screen:
- TAB 1: Send Money (enter recipient, amount, message)
- TAB 2: Request Money (enter requester, amount, message)
Store as mock transaction in database.

**Implementation Details:**
```
Screen: SendMoneyScreen.kt + RequestMoneyScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/payments/

TAB 1 - Send Money:
- Recipient input (mock contact list)
- Amount input
- Message (optional)
- Payment method: Bank Transfer / UPI
- Add to recent contacts
- Show success dialog

TAB 2 - Request Money:
- Requester input
- Amount input
- Message (optional)
- Status: Pending/Received
- Reminder button
```

**Database:**
```kotlin
// Insert into UPIPayment table
// recipient_upi, amount, date, message, status="pending"
```

---

## Prompt 2B: NFC Payment with Card Stack Animation

**What to do:**
Create NFC payment screen with:
- Card stack (3 cards visible, stacked behind each other)
- Click on card → animate cards in a line
- Top card shows: balance, last 4 digits, expiry
- NFC reader integration (mock)

**Implementation Details:**
```
Screen: NFCPaymentScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/payments/nfc/

Features:
- CardStackView: Animated card stack
  - Cards: From CreditCard database
  - Animation: Click → fan out in a line
  - Tap to select → moves to top
  - Shows selected card details
  
- Selected Card Display:
  - Balance: ₹5,000 (mock)
  - Last 4 digits: ••••5678
  - Expiry: 12/25
  - Issuer: RuPay
  
- NFC Reading (mock):
  - "Ready to Tap" UI
  - Amount input before tap
  - Success dialog with transaction details
  - Add to transaction history
```

**Animation Code Structure:**
```kotlin
@Composable
fun CardStack(
    cards: List<CreditCard>,
    onCardSelected: (CreditCard) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    // Stack layout when collapsed
    // Row layout when expanded (fan out)
    // Smooth transition animation
}
```

---

# FEATURE 3: TICKET BOOKING

## Prompt 3A: Movie & Event Booking

**What to do:**
Create movie booking screen showing:
- List of movies (mock data)
- Movie details (rating, duration, cast)
- Seat selection
- Booking confirmation
- Payment & receipt

**Implementation Details:**
```
Screen: MovieBookingScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/tickets/movies/

Flow:
1. Movie List
   - Movie name, poster (mock image), rating, language
   - Filter by city, genre, date
   
2. Show Times & Cinemas
   - List of cinemas nearby
   - Available shows for the day
   
3. Seat Selection
   - Cinema hall layout (mock)
   - Available/Booked/Selected seats
   - Price per seat
   
4. Payment
   - Amount display
   - Payment method selection
   - Apply coupon (optional)
   
5. Confirmation
   - Booking ID
   - Movie details, show time, seats
   - Receipt
   - Add to Ticket database

Mock Data:
- Movies: Pathaan, Oppenheimer, Gadar 2, etc.
- Cinemas: PVR, INOX, Cinepolis, etc.
```

**Database:**
```kotlin
// Ticket entity
data class Ticket(
    val ticketId: String,
    val ticketType: String = "movie",
    val movieName: String,
    val cinema: String,
    val date: String,
    val seats: String,
    val amount: Double,
    val status: String = "confirmed"
)
```

---

## Prompt 3B: Flight, Train & Bus Booking

**What to do:**
Create multi-tab ticket booking:
- TAB 1: Flight Booking (mock data)
- TAB 2: Train Booking (mock data)
- TAB 3: Bus Booking (mock data)

Each with mock in-app browser experience.

**Implementation Details:**
```
Screen: TicketBookingScreen.kt (with 3 tabs)
Location: app/src/main/java/com/example/udhaarpay/ui/screens/tickets/

TAB 1 - Flights (Mock Skyscanner):
- From/To airports
- Travel dates
- Passenger count
- List of flights with prices
- Booking & payment
- Ticket confirmation

TAB 2 - Trains (Mock IRCTC):
- From/To stations
- Travel date
- Class selection (AC, Sleeper, etc.)
- List of trains
- Seat selection
- Booking & payment
- e-Ticket display

TAB 3 - Buses (Mock RedBus):
- From/To cities
- Travel date
- Bus type
- List of buses
- Seat selection
- Booking & payment
- Bus details & route

Mock Data:
- Flights: IndiGo, Air India, SpiceJet
- Trains: Rajdhani, Shatabdi, Local
- Buses: RedBus, Ixigo, Goibibo
```

---

# FEATURE 4: INVESTMENTS

## Prompt 4A: SIP & Mutual Fund Investment

**What to do:**
Create investment screen showing:
- List of brokers (Zerodha, Grow, 5Paisa, etc.)
- SIP options with mock fund details
- Investment amount & frequency
- Confirmation & receipt

**Implementation Details:**
```
Screen: InvestmentScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/investments/

Brokers Available:
- Zerodha
- Grow
- 5Paisa
- Upstox
- Angel Broking
- ICICI Direct
- Kuvera
- ET Money

Features:
1. Broker Selection
   - List of brokers with fees
   - Demo account option
   
2. Fund Selection
   - Large Cap Funds
   - Mid Cap Funds
   - Small Cap Funds
   - Balanced Funds
   - Index Funds
   
3. SIP Details
   - Amount input (₹500 minimum)
   - Frequency (Monthly, Quarterly, Annual)
   - Duration
   - Estimated return (mock calculation)
   
4. Investment Confirmation
   - Fund details
   - Amount, frequency, duration
   - Expected return
   - Save to Investment database
   
5. Dashboard
   - Current investments
   - Total invested
   - Current value (mock growth)
   - Returns chart
```

**Database:**
```kotlin
data class Investment(
    val investmentId: String,
    val brokerName: String,
    val fundName: String,
    val type: String, // "sip" or "mutual" or "demat"
    val amount: Double,
    val frequency: String, // "monthly" or "quarterly"
    val date: String,
    val currentValue: Double,
    val returns: Double
)
```

---

## Prompt 4B: Demat Account & Bonds

**What to do:**
Create demat account opening screen:
- Select broker
- Account type
- Portfolio preview (mock)
- Holdings & transactions

**Implementation Details:**
```
Screen: DematScreen.kt + BondsScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/investments/

Demat Account:
1. Broker Selection
   - Top brokers (same list as above)
   
2. Account Types
   - Trading Account (Day trading)
   - Investment Account (Long term)
   - Demo Account (learning)
   
3. Account Details
   - Account number (mock: DP12345)
   - Holdings list
   - Portfolio value
   - Recent transactions
   
4. Mock Holdings
   - Reliance Industries: 10 shares @ ₹2500 = ₹25,000
   - TCS: 5 shares @ ₹3500 = ₹17,500
   - HDFC Bank: 8 shares @ ₹1500 = ₹12,000
   - Total Portfolio: ₹54,500

Bonds:
1. Government Bonds
   - 10-year bonds
   - 5-year bonds
   - Interest rate shown
   
2. Corporate Bonds
   - Top companies' bonds
   - Maturity dates
   - Return rates
   
3. Bond Purchase
   - Amount input
   - Duration
   - Interest frequency
   - Confirmation
```

---

# FEATURE 5: INSURANCE

## Prompt 5A: Insurance Policy Management

**What to do:**
Create insurance screen to:
- Apply for various insurance types
- View active policies
- Premium payment
- Policy details

**Implementation Details:**
```
Screen: InsuranceScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/insurance/

Insurance Types:
1. Life Insurance
   - Term Life
   - Whole Life
   - ULIPs
   - Premium input
   - Coverage amount
   
2. Health Insurance
   - Individual plans
   - Family plans
   - Premium calculation
   - Coverage details
   
3. Motor Insurance
   - 4-wheeler
   - 2-wheeler
   - Premium per vehicle
   - Add-ons (breakdowns, etc.)
   
4. Travel Insurance
   - Domestic
   - International
   - Duration selection
   - Coverage details
   
5. Property Insurance
   - Home
   - Contents
   - Premium calculation

Features:
- Policy list (active + expired)
- Premium payment reminders
- Claim filing (mock form)
- Policy document download (mock)
- Renewal options
```

**Database:**
```kotlin
data class Insurance(
    val policyId: String,
    val policyType: String,
    val provider: String,
    val premium: Double,
    val startDate: String,
    val expiryDate: String,
    val status: String,
    val coverage: Double
)
```

---

# FEATURE 6: CREDIT CARDS

## Prompt 6A: Credit Card Application & Management

**What to do:**
Create credit card screen to:
- Browse card options (RuPay, Visa, Mastercard)
- Apply for new card
- Manage existing cards
- Add to UPI

**Implementation Details:**
```
Screen: CreditCardScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/cards/

Card Options (Mock):
- HDFC RuPay Card (₹2500 annual fee)
- ICICI RuPay Card (₹1500 annual fee)
- Axis RuPay Card (Free)
- Federal RuPay Card (Cashback)
- Kotak RuPay Card (Rewards)

Application Process:
1. Card Selection
   - Benefits & features
   - Annual fee
   - Rewards rate
   
2. Application Form
   - PAN number
   - Income
   - Existing cards
   - Employment details
   
3. Verification
   - Status: "Pending" → "Approved" (mock)
   - Card number generation
   - Delivery address
   
4. Card Management
   - List all cards
   - Card details: number, CVV, expiry
   - Limit & utilization
   - Transactions
   
5. Add to UPI
   - Link card to UPI ID
   - Set as primary payment method
   - UPI payments from card
```

**Database:**
```kotlin
data class CreditCard(
    val cardId: String,
    val cardNumber: String, // Last 4 digits shown
    val cardType: String, // "RuPay", "Visa", "Mastercard"
    val issuer: String,
    val balance: Double,
    val limit: Double,
    val expiry: String,
    val status: String,
    val upiLinked: Boolean
)
```

---

# FEATURE 7: BANK ACCOUNT MANAGEMENT

## Prompt 7A: Add & Remove Bank Accounts

**What to do:**
Create bank account management screen:
- List of all banks
- Add account with account number & IFSC
- Remove/delete accounts
- View account details

**Implementation Details:**
```
Screen: BankAccountScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/accounts/

Banks Available (Mock):
- HDFC Bank
- ICICI Bank
- Axis Bank
- SBI (State Bank of India)
- Kotak Mahindra Bank
- IDBI Bank
- Federal Bank
- Yes Bank
- RBL Bank
- Indusind Bank
- HSBC
- Citibank
- AEON Bank
- SCB Bank
- Plus all regional banks

Features:
1. Add Account
   - Select bank from dropdown
   - Account number input
   - IFSC code input
   - Account type (Savings/Current/Salary)
   - Nickname for account
   - Save to database
   
2. View Accounts
   - List all accounts
   - Bank name + last 4 digits
   - Account type
   - Display balance (mock)
   
3. Remove Account
   - Delete button
   - Confirmation dialog
   - Remove from database
   
4. Account Details
   - Full account info
   - Linked cards
   - Recent transactions
   - Payment history
```

**Database:**
```kotlin
data class BankAccount(
    val accountId: String,
    val bankName: String,
    val accountNumber: String,
    val ifscCode: String,
    val accountType: String,
    val balance: Double,
    val nickname: String,
    val addedDate: String
)
```

---

## Prompt 7B: Wallet Cash Account

**What to do:**
Create mock cash wallet:
- Add money to wallet
- Withdraw from wallet
- Spend from wallet
- View balance

**Implementation Details:**
```
Screen: CashWalletScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/accounts/

Features:
1. Wallet Balance
   - Display current balance (₹0 initially)
   - Add money button
   - Withdraw button
   
2. Add Money to Wallet
   - Amount input
   - Add to balance
   - Record as transaction
   
3. Withdraw from Wallet
   - Amount input
   - Check balance
   - Deduct from balance
   - Record as transaction
   
4. Wallet Transactions
   - List all add/remove transactions
   - Date and amount
   - Balance after each transaction
   
5. Wallet Account
   - Use wallet in Debt/Expense tracking
   - Can select "Cash Wallet" as payment method
   - Spending category tracking
```

---

# FEATURE 8: BILL PAYMENTS & RECHARGES

## Prompt 8A: Mobile Recharge & Data Plans

**What to do:**
Create mobile recharge screen:
- Enter phone number
- Auto-detect operator
- Show plans & offers
- Confirm payment

**Implementation Details:**
```
Screen: MobileRechargeScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/billpayments/recharge/

Features:
1. Mobile Number Entry
   - Phone number input
   - Auto-detect operator (Jio, Airtel, VI, BSNL)
   - Operator logo display
   
2. Recharge Plans (Mock from providers):
   Jio Plans:
   - ₹99 (2GB/day, 28 days)
   - ₹249 (2.5GB/day, 28 days)
   - ₹399 (3GB/day, 28 days)
   - ₹599 (4GB/day, 28 days)
   
   Airtel Plans:
   - ₹79 (2GB, 28 days)
   - ₹199 (3GB, 28 days)
   - ₹359 (4.5GB, 28 days)
   
   VI Plans:
   - ₹99 (2GB, 28 days)
   - ₹199 (3GB, 28 days)
   - ₹399 (4GB, 28 days)
   
3. Plan Selection
   - Show data, validity, price
   - Tap to select
   - See full details
   
4. Payment
   - Select payment method
   - Amount confirmation
   - Process payment (mock)
   
5. Confirmation
   - Recharge successful message
   - Reference number
   - Plan details
   - Save to transaction history
```

---

## Prompt 8B: Electricity & Utility Bill Payments

**What to do:**
Create bill payment screen for:
- Electricity bills
- Water bills
- Gas bills
- Internet bills
- etc.

**Implementation Details:**
```
Screen: BillPaymentScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/billpayments/

Bill Types:
1. Electricity
   - Consumer number input
   - Auto-fetch bill (mock)
   - Show due date & amount
   - Payment options
   
2. Water & Gas
   - Service provider selection
   - Account number
   - Bill details
   
3. Internet/Broadband
   - ISP selection (Airtel, Jio, ACT, etc.)
   - Account number
   - Plan & billing amount
   
4. Mobile Bill
   - Mobile number
   - Bill amount (auto-calculated)
   - Payment date

Features:
- Biller registration (save frequently used)
- Auto-bill fetch
- Payment scheduling (set reminder)
- Payment history
- Receipt download (mock)
- Save to transaction history
```

---

# FEATURE 9: DEBT & EXPENSE TRACKING

## Prompt 9A: Debt Management (Money Given/Taken)

**What to do:**
Create comprehensive debt tracking:
- Add debt (money given to someone)
- Add liability (money taken from someone)
- Track repayments
- Calculate totals

**Implementation Details:**
```
Screen: DebtScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/debt/

Two Sections:
1. Money I Gave (Debtors)
   - Add debt: Person name, amount, date, reason
   - Who owes me: ₹500 to Raj, ₹1000 to Priya
   - Mark as received (when they repay)
   - Edit/Delete debt entry
   
2. Money I Took (Creditors)
   - Add liability: Person name, amount, date, reason
   - I owe: ₹2000 to Mom, ₹500 to Brother
   - Mark as paid (when you repay)
   - Edit/Delete liability entry

Features:
- Add Entry Dialog
  - Person name
  - Amount
  - Date
  - Reason/Description
  - Category (optional)
  
- List View
  - Person name
  - Amount (highlighted if pending)
  - Date
  - Status (Pending/Settled)
  - Edit/Delete buttons
  
- Summary
  - Total I gave: ₹X
  - Total received: ₹Y
  - Net I gave: ₹(X-Y)
  - Total I owe: ₹A
  - Total paid: ₹B
  - Net I owe: ₹(A-B)
  
- Settlement
  - Mark as received/paid
  - Partial payment option
  - Payment date recording
  - Settlement history
```

**Database:**
```kotlin
data class Debt(
    val debtId: String,
    val personName: String,
    val amount: Double,
    val type: String, // "given" or "taken"
    val date: String,
    val reason: String,
    val status: String, // "pending" or "settled"
    val settledDate: String? = null,
    val amountSettled: Double = 0.0
)
```

---

## Prompt 9B: Expense Tracking with Categories & Accounts

**What to do:**
Create expense tracking:
- Record where money is spent
- Categorize expenses
- Track by payment account
- View spending analytics

**Implementation Details:**
```
Screen: ExpenseScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/debt/

Expense Categories:
- Transportation (Auto, Bus, Train, Taxi)
- Food (Restaurant, Groceries, Coffee)
- Travel (Flight, Hotel, Vacation)
- Entertainment (Movie, Games, Subscription)
- Shopping (Clothes, Electronics, Books)
- Utilities (Mobile, Internet, Electricity)
- Health (Medicine, Doctor, Gym)
- Education (Courses, Books, Tuition)
- Salary (Income)
- Other

Features:
1. Add Expense
   - Amount input
   - Category selection (dropdown)
   - Sub-category (if applicable)
   - Payment account selection
     * Bank account 1
     * Bank account 2
     * Credit card
     * Cash wallet
   - Description
   - Date & time
   - Receipt (optional)
   
2. Expense List
   - Show all expenses by date
   - Category icon + color
   - Amount & account
   - Swipe to delete/edit
   
3. Analytics
   - Monthly spending chart
   - Category breakdown (pie chart)
   - Average spending
   - Budget vs actual (if budget set)
   - Account-wise spending
   
4. Filtering
   - By date range
   - By category
   - By account
   - By amount range
   
5. Budget Setting
   - Set monthly budget
   - Alert when budget exceeded
   - Track vs budget
```

**Database:**
```kotlin
data class Expense(
    val expenseId: String,
    val amount: Double,
    val category: String,
    val subCategory: String? = null,
    val account: String, // "bank", "credit_card", "cash_wallet"
    val accountName: String,
    val description: String,
    val date: String,
    val month: String,
    val receiptUrl: String? = null
)
```

---

# FEATURE 10: PROFILE & SETTINGS

## Prompt 10A: User Profile Management

**What to do:**
Create profile management screen:
- Edit name, email, phone
- Add/change profile picture
- Edit personal details
- View KYC status

**Implementation Details:**
```
Screen: ProfileScreen.kt
Location: app/src/main/java/com/example/udhaarpay/ui/screens/profile/

Sections:
1. Profile Picture
   - Current photo (default if none)
   - Change photo button
   - Camera/Gallery selection
   - Photo crop option
   
2. Personal Information
   - Full name (editable)
   - Email (editable)
   - Phone number (editable)
   - Date of birth (editable)
   - Gender selection
   - Save button
   
3. Address
   - Street address
   - City
   - State
   - Pincode
   - Edit button
   
4. KYC Details
   - PAN number (view/edit)
   - Aadhaar number (masked)
   - Verification status
   - Verification date
   
5. Security
   - Change password button
   - Enable biometric login
   - Two-factor authentication toggle
   - Active sessions
   
6. Settings
   - Language selection
   - Currency preference
   - Notification preferences
   - Theme (Light/Dark)
   
7. Help & Support
   - FAQ
   - Contact support
   - Report issue
   - App version
   
8. Account Management
   - Delete account (confirmation)
   - Export data
   - Privacy policy
   - Terms & conditions
```

**Database:**
```kotlin
data class UserProfile(
    val userId: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val dateOfBirth: String,
    val gender: String,
    val address: String,
    val city: String,
    val state: String,
    val pincode: String,
    val profilePhotoUrl: String,
    val panNumber: String,
    val aadhaarNumber: String,
    val kycStatus: String,
    val kycDate: String
)
```

---

# FEATURE 11: NAVIGATION & OVERALL STRUCTURE

## Prompt 11A: Bottom Navigation & Screen Navigation

**What to do:**
Create bottom navigation bar with 5 main sections:
- Home
- Payments
- Tickets
- Invest
- Account

**Implementation Details:**
```
Bottom Navigation Items:
1. Home → HomeScreen
   - Dashboard
   - Quick actions
   - Recent transactions
   
2. Payments → PaymentScreen (with sub-tabs)
   - Send Money
   - Request Money
   - NFC Pay
   - Bill Payments
   - Recharges
   
3. Tickets → TicketScreen (with sub-tabs)
   - Movies
   - Flights
   - Trains
   - Buses
   - My Bookings
   
4. Invest → InvestmentScreen (with sub-tabs)
   - SIP & Mutual Funds
   - Demat & Bonds
   - Insurance
   - My Investments
   
5. Account → AccountScreen (with sub-items)
   - Bank Accounts
   - Credit Cards
   - Debt Management
   - Expense Tracking
   - Profile
   - Settings

Navigation Flow:
- Bottom nav provides main navigation
- Each tab has internal navigation
- Use NavController with Navigation Compose
- Back button handling
- Deep linking support
```

---

## Prompt 11B: Onboarding & Authentication

**What to do:**
Create onboarding flow:
- Welcome screen
- Login/Signup (mock)
- Profile setup
- Terms acceptance
- Dashboard redirect

**Implementation Details:**
```
Screens Sequence:
1. SplashScreen (2 seconds)
   - Logo
   - App name
   - Loading animation
   
2. OnboardingScreen (if first time)
   - Benefits carousel
   - Sign up now button
   
3. LoginScreen
   - Phone/Email input
   - Password input
   - "Forgot password?" link
   - OTP verification (mock - no real OTP)
   - Login button
   - Sign up link
   
4. SignupScreen
   - Full name input
   - Email input
   - Phone input
   - Password input
   - Confirm password
   - Terms checkbox
   - Create account button
   
5. ProfileSetupScreen (after signup)
   - Profile photo
   - Date of birth
   - Address
   - PAN (optional at signup)
   - Continue button
   
6. HomeScreen
   - Main dashboard shown

Authentication (Mock - No Real Auth):
- Save user data locally
- Check SharedPreferences for "isLoggedIn"
- Mock login validation (any password accepted)
- Remember login state
```

---

# DEVELOPMENT ORDER & PRIORITIES

## Phase 1: Core Setup (Week 1)
1. ✓ MyApplication & MainActivity (already done)
2. Create database entities & Room setup
3. Create ViewModels for each feature
4. Setup navigation structure
5. Create color theme from website

## Phase 2: Essential Features (Week 2-3)
1. Home Dashboard
2. Bank Account Management
3. Profile & Settings
4. Basic Payments (Send/Request Money)

## Phase 3: Payment Systems (Week 3-4)
1. NFC Payment with card stack
2. Credit Card management
3. Bill Payments & Recharges

## Phase 4: Booking & Investments (Week 4-5)
1. Ticket Booking (Movies, Flights, Trains, Buses)
2. SIP & Mutual Fund investments
3. Demat & Bonds

## Phase 5: Supporting Features (Week 5-6)
1. Insurance management
2. Debt & Expense tracking
3. Analytics & Reports

## Phase 6: Polish & Optimization (Week 6-7)
1. Error handling
2. Loading states
3. Offline support
4. Testing & bug fixes

---

# KEY IMPLEMENTATION NOTES

## 1. No Real APIs Required
- All data is mock/local
- User doesn't need actual OTP
- No real payment processing
- Everything stored in Room database
- Images use mock/placeholder URLs

## 2. Color Scheme
Replace these with your website colors:
```kotlin
// Example - Replace with your actual colors
val PrimaryColor = Color(0xFF2563EB) // Your primary
val SecondaryColor = Color(0xFF7C3AED) // Your secondary
val AccentColor = Color(0xFF059669) // Your accent
val BackgroundColor = Color(0xFFF9FAFB) // Your background
val TextPrimary = Color(0xFF111827) // Your text
val TextSecondary = Color(0xFF6B7280) // Your secondary text
```

## 3. Mock Data Strategy
```kotlin
// Create mock repositories
object MockDataProvider {
    fun getMockUsers() = listOf(...)
    fun getMockBanks() = listOf(...)
    fun getMockPlans() = listOf(...)
    fun getMockMovies() = listOf(...)
    // etc.
}

// Use in ViewModels instead of API calls
```

## 4. Database Setup
```kotlin
// In AppDatabase.kt
@Database(
    entities = [
        UserProfile::class,
        BankAccount::class,
        CreditCard::class,
        UPIPayment::class,
        Debt::class,
        Expense::class,
        Ticket::class,
        Investment::class,
        Insurance::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun bankAccountDao(): BankAccountDao
    // ... other DAOs
}
```

## 5. State Management
```kotlin
// Use Hilt + ViewModel + StateFlow
@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()
    
    // Implement payment logic
}
```

---

# TESTING CHECKLIST

Before submitting each feature:
- [ ] All UI elements render correctly
- [ ] No crashes on screen transitions
- [ ] Data saves to database correctly
- [ ] All buttons & inputs work
- [ ] Navigation is smooth
- [ ] Colors match website theme
- [ ] Text is readable
- [ ] Images load correctly
- [ ] Mock data displays properly
- [ ] Delete/Edit operations work
- [ ] Filter & search functions work
- [ ] No memory leaks
- [ ] Touch responsiveness is good
- [ ] No ANR (Application Not Responding)

---

# NEXT STEPS

1. Confirm website colors to use
2. Create color theme file
3. Set up Room database structure
4. Create mock data provider
5. Start with Feature 1: Home Dashboard
6. Follow prompts one by one
7. Test each feature before moving to next

---

End of Specification Document
