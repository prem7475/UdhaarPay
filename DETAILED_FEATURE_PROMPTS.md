# UdhaarPay Feature Development Prompts
## Use These Prompts with Gemini Android Studio Agent

---

## PROMPT SET 1: DATABASE & BASIC SETUP

### PROMPT 1.1: Create Room Database Entities
```
You are an Android development expert. I'm building a banking app called UdhaarPay.

Create the following Room database entities in Kotlin for Android:

1. UserProfile (Fields: userId, fullName, email, phone, dateOfBirth, gender, address, city, state, pincode, profilePhotoUrl, panNumber, aadhaarNumber, kycStatus, kycDate)

2. BankAccount (Fields: accountId, bankName, accountNumber, ifscCode, accountType, balance, nickname, addedDate)

3. CreditCard (Fields: cardId, cardNumber[last 4], cardType[RuPay/Visa/Mastercard], issuer, balance, limit, expiry, status, upiLinked)

4. UPIPayment (Fields: transactionId, senderUPI, recipientUPI, amount, date, message, status, type[sent/request])

5. Debt (Fields: debtId, personName, amount, type[given/taken], date, reason, status[pending/settled], settledDate, amountSettled)

6. Expense (Fields: expenseId, amount, category, subCategory, account[bank/card/wallet], accountName, description, date, month, receiptUrl)

7. Ticket (Fields: ticketId, ticketType[movie/flight/train/bus], movieName/destination, cinema/provider, date, seats, amount, status)

8. Investment (Fields: investmentId, brokerName, fundName, type[sip/mutual/demat], amount, frequency[monthly/quarterly], date, currentValue, returns)

9. Insurance (Fields: policyId, policyType, provider, premium, startDate, expiryDate, status, coverage)

Each entity should:
- Be a data class with @Entity annotation
- Have primary key (auto-generate ID)
- Include all fields as parameters
- Have appropriate data types (String, Double, Boolean, Long for dates)

Save all entities in: app/src/main/java/com/example/udhaarpay/data/local/entities/
Create separate .kt files for each entity.

Make sure entity names match exactly as listed above.
```

### PROMPT 1.2: Create Room Database DAOs
```
You are an Android Kotlin expert. I'm building an Android banking app.

Create Data Access Objects (DAOs) for the Room database. I need DAOs for:

1. UserProfileDao - Methods: insert, update, getById, getAll, delete
2. BankAccountDao - Methods: insert, update, delete, getAll, getById, getByBankName
3. CreditCardDao - Methods: insert, update, delete, getAll, getById, getByCardType
4. UPIPaymentDao - Methods: insert, delete, getAll, getByRecipient, getByDate, getByStatus
5. DebtDao - Methods: insert, update, delete, getAll, getByPersonName, getByType, getByStatus
6. ExpenseDao - Methods: insert, delete, getAll, getByCategory, getByDate, getByMonth, getByAccount
7. TicketDao - Methods: insert, delete, getAll, getByType, getByDate, getByStatus
8. InvestmentDao - Methods: insert, update, delete, getAll, getByBroker, getByType, getSummary
9. InsuranceDao - Methods: insert, update, delete, getAll, getByType, getByStatus, getByProvider

Each DAO should:
- Be an interface with @Dao annotation
- Have Flow return types for reactive updates (Flow<List<Entity>>)
- Suspend functions for database operations
- Proper @Query annotations with SQL
- @Insert, @Update, @Delete annotations where applicable

Save all DAOs in: app/src/main/java/com/example/udhaarpay/data/local/dao/
Create separate .kt files for each DAO.

Provide complete, ready-to-use Kotlin code.
```

### PROMPT 1.3: Create AppDatabase Class
```
You are an Android database expert using Room.

Create the main AppDatabase class that:

1. Extends RoomDatabase
2. Has @Database annotation with all 9 entities I created (UserProfile, BankAccount, CreditCard, UPIPayment, Debt, Expense, Ticket, Investment, Insurance)
3. Declares abstract functions for each DAO (userProfileDao(), bankAccountDao(), creditCardDao(), etc.)
4. Uses version = 1
5. Has a companion object with singleton pattern
6. Includes a function to create the database instance using databaseBuilder

The database should be created with:
- Context passed to it
- Database name: "udhaarpay_db"
- Fallback to destructive migration for testing
- PrePopulate with Hilt injection ready

Save in: app/src/main/java/com/example/udhaarpay/data/local/AppDatabase.kt

Provide complete, production-ready Kotlin code.
```

---

## PROMPT SET 2: HOME DASHBOARD

### PROMPT 2.1: Create Home Dashboard Screen
```
You are an Android Jetpack Compose expert. I'm building a banking app called UdhaarPay.

Create a HomeScreen composable with:

1. **Header Section**:
   - "Welcome, [UserName]" greeting
   - User profile picture (circular, clickable to navigate to profile)
   - Current time display

2. **Wallet Balance Card**:
   - Large balance display: ₹10,000 (mock data)
   - "Today's Spending: ₹245" subtitle
   - Gradient background using your app's primary color
   - Rounded corners, shadow

3. **Quick Action Grid** (2 columns, 4 rows = 8 buttons):
   - Send Money (icon: paperplane)
   - Scan & Pay (icon: nfc)
   - Tickets (icon: confirmation_number)
   - Invest (icon: trending_up)
   - Insurance (icon: health_and_safety)
   - Credit Cards (icon: credit_card)
   - Bill Payments (icon: receipt)
   - My Accounts (icon: account_balance)
   
   Each button should:
   - Show icon + title
   - Have consistent colors
   - Be clickable (navigate to respective screens)
   - Have hover animation

4. **Recent Transactions Section**:
   - "Recent Transactions" header
   - List of 3-4 recent transactions (mock data)
   - Transaction icon, name, amount, date
   - Clickable to view details

5. **QR Code Section**:
   - "Receive Money" header
   - QR code display (mock: use a sample image or generate random QR)
   - Copy UPI ID button

Use:
- Column/Row layouts
- Jetpack Compose Material 3
- LazyColumn for scrolling
- Color scheme from your website
- Proper spacing and padding
- State management with ViewModel

Function signature:
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
)

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/home/HomeScreen.kt
Provide complete Compose code.
```

### PROMPT 2.2: Create Home ViewModel
```
You are an Android MVVM expert using Hilt.

Create HomeViewModel for HomeScreen with:

1. **State Variables** (using StateFlow):
   - userName: String
   - walletBalance: Double (initialize ₹10,000)
   - todaySpending: Double (initialize ₹245)
   - recentTransactions: List<Transaction>
   - userProfilePhoto: String (mock URL)

2. **Mock Data**:
   - userName = "John Doe"
   - 5 sample transactions with date, amount, type, status
   - Mock profile photo URL

3. **Functions**:
   - init {} block to load mock data
   - getRecentTransactions() - return last 3-4 transactions
   - refreshData() - simulate API call (mock with delay)

4. **Data Classes**:
   Create Transaction data class with:
   - id, name, amount, date, type[payment/request/received], icon, status

5. **Hilt Integration**:
   - @HiltViewModel annotation
   - Inject any repository if available
   - Provide empty constructor

Save in: app/src/main/java/com/example/udhaarpay/ui/viewmodel/HomeViewModel.kt
Provide complete Kotlin code with proper ViewModel structure.
```

---

## PROMPT SET 3: BANK ACCOUNT MANAGEMENT

### PROMPT 3.1: Create Bank Account Screen & Add Account
```
You are an Android Jetpack Compose expert.

Create BankAccountScreen composable with:

1. **Top Section**:
   - Back button (clickable)
   - "My Bank Accounts" title
   - Add Account button (+ icon)

2. **Accounts List**:
   - Show all bank accounts from database
   - Each account card displays:
     * Bank logo/icon
     * Bank name
     * Last 4 digits of account: ••••5678
     * Account type (Savings/Current)
     * Balance: ₹X,XXX
     * Edit and Delete buttons (swipe or buttons)

3. **Empty State**:
   - Show "No accounts yet" message with add button if no accounts

4. **Add Account Dialog**:
   When "Add Account" clicked:
   - Bank dropdown (list of 20+ Indian banks)
   - Account number input field
   - IFSC code input field
   - Account type dropdown (Savings/Current/Salary)
   - Nickname input (optional)
   - Save button (validates and saves to DB)
   - Cancel button

5. **Delete Confirmation**:
   - When delete clicked, show confirmation dialog
   - If confirmed, delete from database

Use:
- Compose Material 3
- StateFlow for state management
- Proper validation
- Database operations (Room)

Function signature:
@Composable
fun BankAccountScreen(
    navController: NavController,
    viewModel: BankAccountViewModel = hiltViewModel()
)

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/accounts/BankAccountScreen.kt
Provide complete, production-ready code.
```

### PROMPT 3.2: Create Bank Account ViewModel
```
You are an Android MVVM expert using Hilt and Room.

Create BankAccountViewModel with:

1. **State Variables** (StateFlow):
   - bankAccounts: StateFlow<List<BankAccount>>
   - isAddingAccount: StateFlow<Boolean>
   - errorMessage: StateFlow<String?>

2. **Mock Data - Bank List**:
   Create a function returning 25+ Indian banks:
   HDFC, ICICI, Axis, SBI, Kotak, IDBI, Federal, Yes Bank, RBL, Indusind, HSBC, Citibank, Aeon, SCB, and others.
   Return with bank codes for validation.

3. **Database Operations** (inject BankAccountRepository):
   - getAllBankAccounts() - from Room
   - addBankAccount(BankAccount) - insert into Room
   - deleteBankAccount(accountId) - delete from Room
   - updateBankAccount(BankAccount) - update in Room

4. **Validation Functions**:
   - validateAccountNumber(String) - check length
   - validateIFSCCode(String) - check format
   - validateAccountNickname(String)

5. **Initial State**:
   - Load accounts from database on init
   - Show mock accounts if DB is empty (3-4 sample accounts)

6. **Error Handling**:
   - Try-catch for database operations
   - Set errorMessage for UI to display

Use Hilt, ViewModel, StateFlow pattern properly.

Save in: app/src/main/java/com/example/udhaarpay/ui/viewmodel/BankAccountViewModel.kt
Provide complete code with proper error handling.
```

---

## PROMPT SET 4: PAYMENTS - SEND & REQUEST MONEY

### PROMPT 4.1: Create Send/Request Money Screen
```
You are an Android Jetpack Compose expert.

Create PaymentScreen with two tabs:

**TAB 1: Send Money**
- Recipient input field (autocomplete from contacts)
- Amount input field (with ₹ symbol)
- Message/Note input (optional)
- Payment method dropdown: Bank Transfer / UPI / Card / Wallet
- Send button (validates input, saves to DB, shows success dialog)
- Recent recipients list below (clickable to fill recipient)

**TAB 2: Request Money**
- Requester name input field
- Amount input field
- Message input
- Request button
- Pending requests list below
- Send reminder button for each pending request
- Mark as received option

Features for both:
- Input validation (amount > 0, name not empty)
- Success/Error dialogs
- Save to UPIPayment table in database
- Show transaction ID in success dialog
- Recent history at bottom

Use:
- Jetpack Compose with Tabs
- Material 3 components
- StateFlow for state management
- Database operations

Function signature:
@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel()
)

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/payments/PaymentScreen.kt
Provide complete Compose code.
```

### PROMPT 4.2: Create Payment ViewModel
```
You are an Android MVVM expert.

Create PaymentViewModel with:

1. **State Variables**:
   - recipientName: StateFlow<String>
   - amount: StateFlow<String>
   - message: StateFlow<String>
   - paymentMethod: StateFlow<String>
   - recentRecipients: StateFlow<List<String>>
   - isLoading: StateFlow<Boolean>
   - successMessage: StateFlow<String?>
   - errorMessage: StateFlow<String?>

2. **Mock Recent Recipients**:
   - "Rajesh Kumar"
   - "Priya Singh"
   - "Amit Patel"
   - "Sarah Johnson"

3. **Mock UPI IDs**:
   - For each recipient, have mock UPI

4. **Functions**:
   - sendMoney(recipient, amount, message, method) - Insert to UPIPayment DB
   - requestMoney(requester, amount, message) - Insert to UPIPayment DB with type="request"
   - validateAmount(String) - check > 0
   - validateRecipient(String) - check not empty
   - getRecentRecipients() - return mock list
   - processPayment() - simulate payment (mock delay, then save to DB)

5. **Success Handling**:
   - Generate mock transaction ID
   - Show success dialog
   - Clear form fields
   - Add to recent recipients

Save in: app/src/main/java/com/example/udhaarpay/ui/viewmodel/PaymentViewModel.kt
Provide complete, production-ready code.
```

---

## PROMPT SET 5: NFC PAYMENT WITH CARD STACK ANIMATION

### PROMPT 5.1: Create NFC Payment Screen with Card Stack
```
You are an Android Jetpack Compose animation expert.

Create NFCPaymentScreen with:

1. **Top Section**:
   - "Tap to Pay" title
   - NFC status indicator (Ready/Scanning/Success)
   - Amount input field for payment amount

2. **Card Stack Animation** (Main Feature):
   - Show 3 credit cards stacked behind each other (initially)
   - When user clicks on any card:
     * Cards animate and spread out in a horizontal line
     * Cards are draggable/swippable
     * Click on a card in the line → it moves to front (becomes selected)
   - When card is selected:
     * Show card details:
       - Card number (last 4 digits): ••••5678
       - Cardholder name: JOHN DOE
       - Expiry date: 12/25
       - Available balance: ₹5,000
       - Card issuer: RuPay
     * This card becomes the payment card

3. **Payment Flow**:
   - Amount input at top
   - Select card from stack
   - "Ready to Tap" button appears
   - Show mock NFC indicator animation (pulsing circle)
   - After "tap": Show success dialog with:
     * Transaction ID
     * Amount debited
     * Merchant name (mock)
     * Time

4. **Card Data**:
   - Load from CreditCard database table
   - Show 2-3 sample cards if no cards in DB

Use:
- Jetpack Compose Animations (animate*, transition)
- Modifier.offset() for positioning
- Cards as composables with click handlers
- Color scheme from website

Function signature:
@Composable
fun NFCPaymentScreen(
    navController: NavController,
    viewModel: NFCPaymentViewModel = hiltViewModel()
)

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/payments/nfc/NFCPaymentScreen.kt
Provide complete code with animation logic.
```

### PROMPT 5.2: Create NFC Payment ViewModel
```
You are an Android MVVM expert.

Create NFCPaymentViewModel with:

1. **State Variables** (StateFlow):
   - creditCards: StateFlow<List<CreditCard>>
   - selectedCard: StateFlow<CreditCard?>
   - paymentAmount: StateFlow<String>
   - isCardExpanded: StateFlow<Boolean> (stack expanded = true)
   - nfcStatus: StateFlow<String> (Ready/Scanning/Processing/Success)
   - lastTransaction: StateFlow<NFCTransaction?>

2. **Mock Credit Cards** (if DB empty):
   - Card 1: Last 4 = 1234, Balance = ₹5,000, Expiry = 12/25, Type = RuPay
   - Card 2: Last 4 = 5678, Balance = ₹8,000, Expiry = 06/26, Type = RuPay
   - Card 3: Last 4 = 9012, Balance = ₹3,500, Expiry = 03/27, Type = RuPay

3. **Database Operations** (inject CreditCardRepository):
   - getAllCreditCards() - from Room
   - getCreditCardById(cardId) - single card

4. **Payment Functions**:
   - selectCard(CreditCard) - set as selectedCard
   - expandCardStack() - set isCardExpanded = true
   - collapseCardStack() - set isCardExpanded = false
   - processNFCPayment(amount) - simulate NFC transaction:
     * Validate amount
     * Check card balance
     * Create NFCTransaction record
     * Deduct from card balance
     * Generate transaction ID
     * Set nfcStatus = "Success"

5. **Mock Data**:
   - Generate random merchant names
   - Use current date/time for transactions
   - Mock transaction IDs: "TXN" + timestamp

Save in: app/src/main/java/com/example/udhaarpay/ui/viewmodel/NFCPaymentViewModel.kt
Provide complete, ready-to-use code.
```

---

## PROMPT SET 6: TICKET BOOKING

### PROMPT 6.1: Create Movie & Event Booking Screen
```
You are an Android Jetpack Compose expert building a ticket booking system.

Create MovieBookingScreen composable with:

1. **Movie Selection**:
   - List of 5-6 movies (mock data):
     * Pathaan, Oppenheimer, Gadar 2, Barbie, Killers of the Flower Moon, Aquaman 2
   - Each movie shows:
     * Poster image (mock URL or colored rectangle)
     * Movie name
     * Rating (e.g., 8.5/10)
     * Language (English/Hindi)
     * Duration (e.g., 163 mins)
   - Clickable to select movie and proceed

2. **Cinema Selection** (after movie selected):
   - List of cinemas (mock):
     * PVR Cinemas
     * INOX
     * Cinepolis
     * Rajmandir
   - Each shows address and distance
   - Click to select cinema

3. **Show Time Selection** (after cinema selected):
   - Shows for today (mock data):
     * 9:30 AM, 1:00 PM, 4:30 PM, 8:00 PM, 11:30 PM
   - Click to select show time

4. **Seat Selection** (after show time selected):
   - Cinema hall grid (8 rows x 12 seats = mock)
   - Color code:
     * Gray = Available
     * Red = Booked
     * Green = Selected
   - Click seats to select/deselect
   - Show selected seats and total price

5. **Booking Confirmation**:
   - Show all details: Movie, Cinema, Show time, Seats, Total amount
   - Select payment method dropdown
   - "Book Now" button
   - On click: Save to Ticket table, show success dialog with:
     * Booking ID
     * E-ticket (mock QR code)
     * Confirmation details

Use Compose Material 3, proper state management.

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/tickets/MovieBookingScreen.kt
Provide complete code.
```

### PROMPT 6.2: Create Flight, Train & Bus Booking (3-Tab Screen)
```
You are an Android Jetpack Compose expert.

Create TicketBookingScreen with 3 tabs:

**TAB 1: Flight Booking (Mock Skyscanner)**
- From airport input (dropdown with major airports)
- To airport input
- Date picker
- Passengers count
- Show flights list with:
  * Airline name, departure/arrival time
  * Duration, stops, price
  * Click to book
- Seat selection after booking
- Final confirmation

**TAB 2: Train Booking (Mock IRCTC)**
- From station input
- To station input  
- Date picker
- Quota selection (General/Premium)
- Class selection (AC/Sleeper/General)
- Show trains list:
  * Train name, number
  * Departure/arrival, duration
  * Available seats, price
- Seat selection:
  * Show coach layout
  * Click seats to select
- Confirmation with ticket details

**TAB 3: Bus Booking (Mock RedBus)**
- From city input
- To city input
- Date picker
- Bus type filter (AC/Non-AC)
- Show buses list:
  * Bus operator name
  * Departure/arrival times
  * Route, duration
  * Seat availability, price
- Seat layout selection
- Confirmation

Mock Data (All tabs):
- Major routes pre-populated
- 3-5 options per route
- Realistic pricing
- All times reasonable

After booking:
- Save to Ticket table with ticketType = "flight"/"train"/"bus"
- Show confirmation with:
  * Booking ID
  * E-ticket download (mock)
  * SMS details

Use Compose Tabs, proper state management.

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/tickets/TicketBookingScreen.kt
Provide complete, production-ready code.
```

---

## PROMPT SET 7: INVESTMENTS

### PROMPT 7.1: Create SIP & Mutual Fund Investment Screen
```
You are an Android Jetpack Compose expert building investment features.

Create InvestmentScreen composable for SIP & Mutual Funds with:

1. **Broker Selection**:
   - List of 10 brokers (mock):
     * Zerodha, Grow, 5Paisa, Upstox, Angel Broking, ICICI Direct, Kuvera, ET Money, Paytm Money, Smallcase
   - Each shows:
     * Broker logo/icon
     * Broker name
     * Brokerage fee
     * Features highlights
   - Click to select and proceed

2. **Fund Selection** (after broker selected):
   - Fund categories:
     * Large Cap Funds (Nifty 50, Sensex)
     * Mid Cap Funds
     * Small Cap Funds
     * Balanced Funds
     * Index Funds
   - Each category shows 3-4 funds:
     * Fund name
     * Historical returns (mock: 15% YTD)
     * Expense ratio
     * NAV (Net Asset Value)
   - Click to select fund

3. **SIP Details** (after fund selected):
   - Amount input (₹500 minimum)
   - Frequency dropdown: Monthly / Quarterly / Annual
   - Duration input (years)
   - Investment start date picker
   - Show estimated return calculation (mock):
     * Total invested amount
     * Estimated value after X years
     * Expected return percentage
   - "Start SIP" button

4. **Confirmation & Receipt**:
   - Show all details:
     * Broker, Fund, Amount, Frequency, Duration
     * Expected return
   - "Confirm" button
   - Save to Investment table with:
     * type = "sip"
     * Calculate currentValue as initial amount (will grow in UI)
   - Show success dialog with receipt

5. **Investment Dashboard** (view existing investments):
   - List all investments from database
   - Each shows:
     * Fund name, amount, frequency
     * Current value, returns
     * Status (Active/Completed)
   - Click to view details or cancel SIP

Use Compose Material 3, Form handling, Proper state.

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/investments/InvestmentScreen.kt
Provide complete code.
```

### PROMPT 7.2: Create Demat & Bonds Screen
```
You are an Android Jetpack Compose expert.

Create DematScreen composable with:

1. **Demat Account Selection**:
   - List of 8 brokers (Zerodha, Grow, 5Paisa, etc.)
   - Account type dropdown:
     * Trading Account (day trading)
     * Investment Account (long-term)
     * Demo Account (learning)
   - Click to open demat account

2. **Account Details** (after account opened):
   - Display account number: DP12345 (mock generated)
   - Account status: Active
   - Holdings list showing:
     * Stock symbol, quantity, price per share, total value
   - Mock holdings:
     * Reliance: 10 shares @ ₹2500 = ₹25,000
     * TCS: 5 shares @ ₹3500 = ₹17,500
     * HDFC Bank: 8 shares @ ₹1500 = ₹12,000
     * Infosys: 3 shares @ ₹2000 = ₹6,000
   - Portfolio value: ₹60,500
   - Recent transactions list

3. **Bonds Screen** (separate tab or section):
   - Government Bonds:
     * 10-year bonds, 5-year bonds
     * Interest rate (e.g., 6.2% for 10-year)
     * Minimum investment: ₹1000
   - Corporate Bonds:
     * Company name, maturity, interest rate
     * Top companies: TCS, Infosys, L&T, ICICI Bank
   - Buy Bond button:
     * Amount input
     * Confirm purchase
     * Save to Investment table with type = "bond"

4. **Tabs or Bottom Navigation**:
   - Tab 1: Active Demat Accounts
   - Tab 2: Government Bonds
   - Tab 3: Corporate Bonds
   - Tab 4: My Holdings
   - Tab 5: Transaction History

Use Compose Material 3, Proper state management.

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/investments/DematScreen.kt
Provide complete code.
```

---

## PROMPT SET 8: BILL PAYMENTS & RECHARGES

### PROMPT 8.1: Create Mobile Recharge Screen
```
You are an Android Jetpack Compose expert.

Create MobileRechargeScreen composable with:

1. **Phone Number Input**:
   - Input field for phone number
   - Auto-detect operator based on first 2 digits:
     * Jio (9, 7, 8)
     * Airtel (9, 8, 7)
     * VI (9, 8, 7)
     * BSNL (9, 8, 7)
   - Show operator logo and name below input

2. **Plan Selection**:
   - Grid of plans for detected operator:
   
   JIO:
   - ₹99: 2GB/day, 28 days
   - ₹249: 2.5GB/day, 28 days
   - ₹399: 3GB/day, 28 days
   - ₹599: 4GB/day, 28 days
   - ₹999: 4.5GB/day, 56 days
   
   AIRTEL:
   - ₹79: 2GB, 28 days
   - ₹199: 3GB, 28 days
   - ₹359: 4.5GB, 28 days
   - ₹599: 5GB, 56 days
   
   VI:
   - ₹99: 2GB, 28 days
   - ₹199: 3GB, 28 days
   - ₹399: 4GB, 28 days
   - ₹599: 6GB, 56 days

   - Each plan card shows:
     * Price in large text
     * Data amount
     * Validity days
     * Click to select
   - Selected plan highlighted

3. **Special Offers** (optional row):
   - Bank cashback offers
   - Special discounts

4. **Payment**:
   - Selected plan amount displayed
   - Payment method dropdown (Bank/Card/Wallet)
   - "Recharge Now" button

5. **Confirmation**:
   - After click, show dialog:
     * Phone number
     * Plan details
     * Amount
     * "Confirm" button
   - On confirm:
     * Save to Expense table as recharge
     * Show success dialog:
       - "Recharge Successful"
       - Reference ID
       - Message: "Balance will reflect in 15 minutes"

Use Compose Material 3, proper state handling.

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/billpayments/MobileRechargeScreen.kt
Provide complete code.
```

### PROMPT 8.2: Create Electricity & Utility Bill Payments Screen
```
You are an Android Jetpack Compose expert.

Create BillPaymentScreen composable with multiple bill types:

**Available Bill Types**:
1. Electricity - Consumer number input
2. Water - Service account number
3. Gas - Consumer ID
4. Internet/Broadband - Account number
5. Mobile Postpaid - Mobile number

**Screen Flow**:

1. **Bill Type Selection**:
   - Horizontal scrollable buttons or dropdown
   - Icons for each bill type

2. **Account Entry** (after bill type selected):
   - Input field for consumer/account number
   - "Fetch Bill" button
   - Or "Search" functionality

3. **Bill Details** (after fetching or selecting):
   - Biller name
   - Amount due
   - Due date (show if overdue: red)
   - Previous balance
   - Current consumption
   - Payment options

   Mock Bill Data:
   - Electricity: ₹2,500, Due: 15th Jan (red if past)
   - Water: ₹800, Due: 25th Jan
   - Internet: ₹999, Due: 30th Jan
   - Gas: ₹1,200, Due: 20th Jan
   - Mobile: ₹599, Due: 28th Jan

4. **Payment Confirmation**:
   - Amount to pay
   - Payment method dropdown
   - Due date reminder
   - "Pay Now" button

5. **Success Dialog**:
   - "Payment Successful"
   - Transaction ID
   - Receipt option (mock)
   - Save to Expense table as bill_payment

6. **Payment History** (lower section):
   - "Recent Bill Payments"
   - List of last 5 bill payments
   - Date, biller, amount, status

**Features**:
- Biller registration (save frequently used)
- Auto-refetch latest bill
- Set payment reminders
- Full bill details view

Use Compose Material 3, Proper state management, Loading indicators.

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/billpayments/BillPaymentScreen.kt
Provide complete code.
```

---

## PROMPT SET 9: DEBT & EXPENSE TRACKING

### PROMPT 9.1: Create Debt Management Screen (Money Given/Taken)
```
You are an Android Jetpack Compose expert.

Create DebtScreen composable with two sections:

**SECTION 1: Money I Gave (Debtors)**
- Header: "₹X People Owe Me"
- List of debts:
  * Person name
  * Amount (in green)
  * Date given
  * Status: "Pending" / "Received"
  * Buttons: Edit, Mark Received, Delete

**SECTION 2: Money I Took (Creditors)**
- Header: "₹Y I Owe to People"
- List of liabilities:
  * Person name
  * Amount (in red)
  * Date taken
  * Status: "Pending" / "Paid"
  * Buttons: Edit, Mark Paid, Delete

**Add New Debt Dialog**:
- Person name input field
- Amount input field
- Type dropdown: "I gave" / "I took"
- Date picker
- Reason/Description input (optional)
- Category dropdown (optional):
  * Food, Travel, Entertainment, Shopping, Other
- "Add" button
- "Cancel" button

**Edit Debt Dialog** (same as Add but with pre-filled values)

**Mark as Received/Paid**:
- Click button → Dialog:
  * Show remaining amount
  * Input for partial payment (if any)
  * Date of settlement
  * "Mark as Received/Paid" button

**Summary Section** (at top):
- "Money I Gave": ₹X (amount received: ₹Y, net: ₹X-Y)
- "Money I Owe": ₹A (amount paid: ₹B, net: ₹A-B)

**Filtering** (optional):
- Tab: All / Pending / Settled
- Or filter buttons

Mock Data (if empty):
- John: ₹500 (Pending)
- Priya: ₹1000 (Pending)
- Mom: ₹2000 (I owe - Pending)
- Brother: ₹500 (I owe - Paid)

Database Operations:
- Save to Debt table
- Query by type and status
- Update status on settlement

Use Compose Material 3, proper state, animations for additions/deletions.

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/debt/DebtScreen.kt
Provide complete code.
```

### PROMPT 9.2: Create Expense Tracking Screen with Categories & Analytics
```
You are an Android Jetpack Compose expert.

Create ExpenseScreen composable with:

**1. TOP SECTION - Monthly Summary**:
- Current month: "January 2026"
- Total spending: "₹X,XXX"
- Budget (if set): "Budget: ₹5000 | Spent: ₹3,500 | Remaining: ₹1,500"
- Bar showing budget utilization

**2. ADD EXPENSE BUTTON**:
- FAB or button to add new expense
- "Add Expense" dialog when clicked

**ADD EXPENSE DIALOG**:
- Amount input field
- Category dropdown:
  * Transportation (Auto, Bus, Train, Taxi)
  * Food (Restaurant, Groceries, Coffee)
  * Travel (Flight, Hotel, Vacation)
  * Entertainment (Movie, Games, Subscription)
  * Shopping (Clothes, Electronics, Books)
  * Utilities (Mobile, Internet, Electricity)
  * Health (Medicine, Doctor, Gym)
  * Education (Courses, Books, Tuition)
  * Salary (Income - positive)
  * Other
- Sub-category dropdown (if available)
- Account selection:
  * Bank Account 1
  * Bank Account 2
  * Credit Card
  * Cash Wallet
- Description input
- Date & time picker
- Receipt image upload (optional - mock)
- "Save" button

**3. MAIN VIEW - Expense List**:
- Grouped by date (Today, Yesterday, This Week, etc.)
- Each expense shows:
  * Category icon (colored)
  * Description/merchant name
  * Amount
  * Account used
  * Time
  * Swipe to delete or edit

**4. ANALYTICS TAB** (bottom):
- 3 analytics views (tabs or toggles):

  **View 1: Category Breakdown**:
  - Pie chart showing expense %
  - Legend with categories and amounts
  - Click category to filter expenses
  
  **View 2: Monthly Trend**:
  - Bar chart: Last 6 months spending
  - Show average
  
  **View 3: Account Wise**:
  - Breakdown by payment account (Bank, Card, Cash)
  - Percentage and amounts

**5. FILTERING**:
- Date range picker
- Category filter
- Account filter
- Search by description

**6. BUDGET SETTING** (settings icon):
- Set monthly budget
- Budget alerts (optional)

Mock Data:
- 10-15 sample expenses across categories
- Various amounts and dates
- Different accounts

Database:
- Save to Expense table
- Query by date, category, account
- Calculate totals

Use Compose Material 3, Charts (if available), proper state management, animations.

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/debt/ExpenseScreen.kt
Provide complete code.
```

---

## PROMPT SET 10: PROFILE & SETTINGS

### PROMPT 10.1: Create User Profile Screen
```
You are an Android Jetpack Compose expert.

Create ProfileScreen composable with:

**1. PROFILE HEADER**:
- Profile picture (circular, 120dp)
- "Edit Picture" button (camera icon)
- User name
- Member since date (e.g., "Member since Jan 2024")

**2. EDITABLE PERSONAL INFORMATION**:
- Full Name (editable field)
- Email (editable field)
- Phone (editable field)
- Date of Birth (date picker)
- Gender (dropdown: Male/Female/Other)
- "Save Changes" button

**3. ADDRESS SECTION**:
- Street Address (editable)
- City (editable)
- State (dropdown)
- Pincode (editable)
- "Edit Address" or inline editing

**4. KYC & VERIFICATION**:
- "KYC Status" badge:
  * Not Verified (red)
  * Verified (green)
- PAN Number (show last 4 digits, edit button)
- Aadhaar (masked: ••••••••1234, no edit)
- Verification date (if verified)
- "Update KYC" button

**5. SECURITY SECTION**:
- "Change Password" button
- "Two-Factor Authentication" toggle
- "Biometric Login" toggle
- "Active Sessions" - show current device
- "Logout" button

**6. APP SETTINGS**:
- Language selection (English/Hindi/etc.)
- Currency preference (INR/etc.)
- Theme selection (Light/Dark/Auto)
- Notification preferences toggle

**7. HELP & SUPPORT**:
- "FAQ" button
- "Contact Support" button
- "Report Issue" button
- "App Version": v1.0.0

**8. ACCOUNT ACTIONS**:
- "Export Data" button
- "Delete Account" button (confirmation required)
- "Privacy Policy" button
- "Terms & Conditions" button

**Edit Features**:
- In-place editing with Save/Cancel buttons
- Validation for each field
- Success toast on save
- Load from UserProfile database table

Mock Data:
- Name: John Doe
- Email: john@example.com
- Phone: +91 9876543210
- DOB: 15-05-1995
- Address: 123 Main St, Mumbai, Maharashtra 400001
- PAN: ABCD1234E
- KYC: Verified on 10-Jan-2024

Use Compose Material 3, proper state management, photo picker integration.

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/profile/ProfileScreen.kt
Provide complete code.
```

### PROMPT 10.2: Create Credit Card Application Screen
```
You are an Android Jetpack Compose expert.

Create CreditCardScreen composable with:

**1. TABS** (3 tabs):

**TAB 1: Browse Cards**
- List of 5-6 RuPay card options:
  * HDFC RuPay (Annual fee: ₹2500)
  * ICICI RuPay (Annual fee: ₹1500)
  * Axis RuPay (Free)
  * Federal RuPay (Cashback offer)
  * Kotak RuPay (Rewards)
- Each card shows:
  * Card image (mock)
  * Card name
  * Key benefits (3-4 bullet points)
  * Annual fee
  * "Apply Now" button

**TAB 2: Apply for Card**
- Form (after clicking Apply):
  * Full name (pre-filled)
  * Email (pre-filled)
  * Phone (pre-filled)
  * Pan number (editable)
  * Annual income dropdown (₹2-5L, ₹5-10L, ₹10L+)
  * Employment status (Employed/Self-employed)
  * Existing cards (number input)
  * Select card to apply (pre-selected)
  * Terms checkbox
  * "Apply" button
- Show success: "Application Submitted! Status: Under Review"

**TAB 3: My Cards**
- List all approved cards:
  * Card image
  * Card name
  * Card number (last 4): ••••5678
  * Expiry date: 12/25
  * Card limit: ₹1,00,000
  * Current limit used: ₹35,000 (progress bar)
  * Quick action buttons:
    - View Transactions
    - Set Payment Method
    - Add to UPI (toggle)
    - Lock/Unlock
    - More options

**Card Details Screen** (when card clicked):
- Full card details
- Transaction history (last 5 transactions)
- Statement download (mock PDF)
- Limit increase option
- Report issue button

**Add to UPI Feature**:
- Toggle to link card to UPI
- If toggled ON:
  * Show UPI ID
  * Set as primary payment
  * Confirmation dialog
- If toggled OFF:
  * Unlink from UPI
  * Choose another payment method as primary

Mock Data:
- 2 approved cards in "My Cards"
- 1 pending application in "Apply"
- Transaction history with merchants

Database:
- Save to CreditCard table
- Load from database

Use Compose Material 3, State management, Proper validation.

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/cards/CreditCardScreen.kt
Provide complete code.
```

---

## PROMPT SET 11: INSURANCE

### PROMPT 11.1: Create Insurance Management Screen
```
You are an Android Jetpack Compose expert.

Create InsuranceScreen composable with:

**1. TABS** (2 tabs):

**TAB 1: Browse & Apply**
- Insurance type selection (horizontal scroll or dropdown):
  * Life Insurance
  * Health Insurance
  * Motor Insurance
  * Travel Insurance
  * Property Insurance

**Life Insurance Options**:
- Term Life (₹25/month for ₹10L coverage)
- Whole Life (₹75/month for ₹10L coverage)
- ULIPs (Variable premiums)
Each shows:
  * Type name
  * Premium per month
  * Coverage amount
  * Features (3-4 bullet points)
  * "Apply Now" button

**Health Insurance**:
- Individual plans (₹299-999/month)
- Family plans (₹499-1999/month)
- Senior citizens (₹399-1299/month)
Features shown for each

**Motor Insurance**:
- 4-wheeler plans
- 2-wheeler plans
- Premium based on vehicle (mock)
- Add-ons checkbox:
  * Breakdown coverage
  * Roadside assistance
  * etc.

**Travel Insurance**:
- Domestic (₹99-299)
- International (₹499-999)
- Duration selection (1 day - 365 days)

**Property Insurance**:
- Home insurance
- Contents insurance
- Premiums by coverage amount

**Application Form** (after selecting insurance):
- Personal info (auto-fill from profile)
- Insurance type (pre-selected)
- Coverage amount
- Premium frequency (Monthly/Annual)
- Start date
- Additional details (varies by type)
- "Submit Application" button

**TAB 2: My Policies**
- Status badges:
  * Active (green)
  * Pending (yellow)
  * Expired (gray)
  * Claimed (blue)

For each policy show:
- Policy ID
- Insurance type
- Provider name
- Premium amount & frequency
- Start date & expiry date
- Status badge
- "View Details" button

**Policy Details Screen**:
- Full policy information
- Coverage details
- Benefits list
- Premium payment history
- Renew button (if expiring soon)
- Claim filing button:
  * Form to submit claim
  * Upload documents (mock)
  * Claim status tracking
- Download policy document (mock PDF)

Mock Data:
- 2-3 active policies
- 1 expired policy
- 1 pending application
- Various insurance types

Database:
- Save to Insurance table
- Update status on actions

Use Compose Material 3, Status badges, Proper state management.

Save in: app/src/main/java/com/example/udhaarpay/ui/screens/insurance/InsuranceScreen.kt
Provide complete code.
```

---

## FINAL PROMPTS: INTEGRATION & NAVIGATION

### PROMPT 12.1: Create Bottom Navigation & Main Navigation Structure
```
You are an Android Jetpack Compose expert.

Create MainNavigationScreen composable with:

**1. BOTTOM NAVIGATION BAR** (5 items):
- Home (home icon)
- Payments (send icon)
- Tickets (confirmation_number icon)
- Invest (trending_up icon)
- Account (person icon)

Each item navigates to respective screen.

**2. NAVIGATION STRUCTURE**:

Home →
  - HomeScreen
  - Opens via bottom nav

Payments →
  - PaymentScreen (Send/Request tabs)
  - NFCPaymentScreen (from Send tab)
  - BillPaymentScreen
  - MobileRechargeScreen
  - Each with back navigation

Tickets →
  - MovieBookingScreen
  - TicketBookingScreen (3 tabs)
  - MyTicketsScreen (show past bookings from Ticket table)

Invest →
  - InvestmentScreen (SIP/Mutual)
  - DematScreen (Demat/Bonds)
  - InsuranceScreen
  - MyInvestmentsScreen (show all investments)

Account →
  - AccountMainScreen with sub-items:
    * BankAccountScreen
    * CreditCardScreen
    * DebtScreen
    * ExpenseScreen
    * ProfileScreen
  - Each with proper back navigation

**3. DEEP LINKING**:
- Support navigation by routes
- Example routes: "home", "payments/send", "tickets/movie", etc.

**4. BACK BUTTON HANDLING**:
- Proper handling in each screen
- Show back button in nested screens
- Confirmation if leaving with unsaved data

**5. STATE PRESERVATION**:
- Maintain state when switching tabs
- Restore scroll position
- Save form inputs temporarily

Use Jetpack Compose Navigation, BottomAppBar, proper NavController setup.

Save in: app/src/main/java/com/example/udhaarpay/ui/navigation/MainNavigation.kt
Provide complete code.
```

### PROMPT 12.2: Create Mock Data Provider & Repository Base
```
You are an Android Kotlin expert.

Create a MockDataProvider object and base Repository classes:

**1. MockDataProvider** (Singleton Object):
- Functions for each entity type:
  * getMockUsers() - 1 user
  * getMockBanks() - List of 25+ banks with codes
  * getMockBankAccounts() - 3 sample accounts
  * getMockCreditCards() - 3 sample cards
  * getMockMovies() - 5-6 movies
  * getMockFlights() - 10 flights
  * getMockTrains() - 8 trains
  * getMockBuses() - 10 buses
  * getMockBrokers() - 10 investment brokers
  * getMockFunds() - 20 mutual funds
  * getMockExpenses() - 15 expenses
  * getMockDebts() - 5 debts
  * etc.

Each mock object should have:
- Realistic data
- Proper formatting
- Appropriate values
- Varied dates/amounts

**2. Base Repository Classes**:

UserRepository interface:
- fun getUser(): Flow<UserProfile>
- fun updateUser(user: UserProfile)
- fun saveUser(user: UserProfile)

BankAccountRepository interface:
- fun getAllAccounts(): Flow<List<BankAccount>>
- fun getAccount(id: String): Flow<BankAccount>
- fun addAccount(account: BankAccount)
- fun updateAccount(account: BankAccount)
- fun deleteAccount(id: String)

(Similar for all other entities)

**3. Implementation**: 
- Use Room DAOs injected via Hilt
- Provide empty/default implementations
- Add proper error handling

Save MockDataProvider in: 
  app/src/main/java/com/example/udhaarpay/data/mock/MockDataProvider.kt

Save Repository interfaces in:
  app/src/main/java/com/example/udhaarpay/data/repository/

Provide complete Kotlin code.
```

---

# IMPLEMENTATION ORDER CHECKLIST

- [ ] Database & Setup (Prompts 1.1-1.3)
- [ ] Home Dashboard (Prompts 2.1-2.2)
- [ ] Bank Accounts (Prompts 3.1-3.2)
- [ ] Payments (Prompts 4.1-4.2)
- [ ] NFC Payment (Prompts 5.1-5.2)
- [ ] Tickets (Prompts 6.1-6.2)
- [ ] Investments (Prompts 7.1-7.2)
- [ ] Recharges & Bills (Prompts 8.1-8.2)
- [ ] Debt & Expenses (Prompts 9.1-9.2)
- [ ] Profile (Prompts 10.1-10.2)
- [ ] Insurance (Prompt 11.1)
- [ ] Navigation (Prompts 12.1-12.2)
- [ ] Testing & Polish

Each prompt is designed to be implemented independently and combined into a complete app.

---

END OF PROMPTS
