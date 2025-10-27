# Expense Split Application - How to Run

## Quick Start Instructions

### 1. Start the Application
The application is already running! Access it at: **http://localhost:8080**

### 2. Using the Web Interface

#### Step 1: Create Users
1. Click on **"Users"** in the navigation menu
2. Fill in the form:
   - **Name**: Enter full name
   - **Email**: Enter email address
   - **Contact**: Enter phone number
3. Click **"Create User"**
4. Repeat for all group members

#### Step 2: Create a Group
1. Click on **"Groups"** in the navigation menu
2. Enter a group name (e.g., "Weekend Shopping")
3. Click **"Create Group"**

#### Step 3: Add Members to Group
1. On the Groups page, find your created group
2. Click **"Add Member"** button
3. Select a user from the dropdown
4. Click **"Add Member"** to confirm
5. Repeat for all members

#### Step 4: Add Expenses
1. Click on **"View Details"** for your group
2. Click **"Add Expense"** button
3. Fill in the expense form:
   - **Description**: What was purchased (e.g., "Groceries")
   - **Amount**: Total cost (e.g., 1000.00)
   - **Paid By**: Who paid the bill
   - **Split Among**: Select all participants
4. Click **"Add Expense"**

#### Step 5: View Balances
1. On the group details page, click **"View Balances"**
2. See individual balances for each member
3. View settlement suggestions

### 3. Example: Split 1000 Rupees Among 3 Members

**Scenario**: Three friends go shopping and spend ₹1000
- **Member 1**: John Doe
- **Member 2**: Jane Smith  
- **Member 3**: Bob Wilson

**Steps**:
1. Create all 3 users
2. Create group "Shopping Trip"
3. Add all 3 members to the group
4. Add expense:
   - Description: "Shopping"
   - Amount: 1000.00
   - Paid By: John Doe
   - Split Among: All 3 members
5. Check balances - it will show:
   - John Doe: +₹333.33 (paid more than his share)
   - Jane Smith: -₹333.33 (owes money)
   - Bob Wilson: -₹333.33 (owes money)

### 4. Access Points

- **Web Application**: http://localhost:8080

### 5. Stop the Application

Press `Ctrl+C` in the terminal or run:
```bash
pkill -f "spring-boot:run"
```

## How It Works

The application automatically:
- **Calculates equal splits**: Total amount ÷ Number of participants
- **Tracks payments**: Shows who paid what
- **Computes balances**: Shows who owes whom
- **Suggests settlements**: Provides clear instructions for settling up

## Troubleshooting

If the application is not running:
```bash
cd /home/aravind/JavaFinal
mvn spring-boot:run
```

Wait for the message: `Started ExpenseSplitApplication in X.XXX seconds`


