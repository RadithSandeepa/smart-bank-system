import express, { Request, Response } from 'express';
import bodyParser from 'body-parser';
import { client } from './eureka-client';
import { zipkinMiddleware } from './zipkin-client';
import * as apiGateway from './api_g';
import * as serviceDiscovery from './d_discvoery';

const app = express();
const PORT = process.env.PORT || 3000;

// Add Zipkin middleware BEFORE other middleware
app.use(zipkinMiddleware);

// Middleware
app.use(bodyParser.json());

// Mock data store
const users: any[] = [];
const accounts: any[] = [];
const transactions: any[] = [];

// ===== Authentication Endpoints =====

// Register a new user
app.post('/api/auth/register', (req: Request, res: Response) => {
  const { username, password, email, fullName } = req.body;
  
  // Basic validation
  if (!username || !password || !email) {
    return res.status(400).json({ error: 'Missing required fields' });
  }
  
  // Check if user already exists
  if (users.find(u => u.username === username || u.email === email)) {
    return res.status(409).json({ error: 'User already exists' });
  }
  
  const newUser = {
    id: users.length + 1,
    username,
    password, // In a real app, this would be hashed
    email,
    fullName,
    createdAt: new Date()
  };
  
  users.push(newUser);
  
  // Don't return the password in response
  const { password: _, ...userResponse } = newUser;
  res.status(201).json(userResponse);
});

// Login
app.post('/api/auth/login', (req: Request, res: Response) => {
  const { username, password } = req.body;
  
  const user = users.find(u => u.username === username && u.password === password);
  if (!user) {
    return res.status(401).json({ error: 'Invalid credentials' });
  }
  
  // In a real app, generate and return JWT token
  res.json({
    message: 'Login successful',
    userId: user.id,
    token: 'sample-jwt-token'
  });
});

// ===== Account Management Endpoints =====

// Create a new account
app.post('/api/accounts', (req: Request, res: Response) => {
  const { userId, accountType, initialBalance } = req.body;
  
  // Validate user exists
  const user = users.find(u => u.id === userId);
  if (!user) {
    return res.status(404).json({ error: 'User not found' });
  }
  
  const newAccount = {
    id: accounts.length + 1,
    userId,
    accountType,
    balance: initialBalance || 0,
    accountNumber: `ACCT-${Math.floor(100000 + Math.random() * 900000)}`,
    createdAt: new Date(),
    status: 'active'
  };
  
  accounts.push(newAccount);
  res.status(201).json(newAccount);
});

// Get all accounts for a user
app.get('/api/users/:userId/accounts', (req: Request, res: Response) => {
  const userId = parseInt(req.params.userId);
  
  const userAccounts = accounts.filter(account => account.userId === userId);
  res.json(userAccounts);
});

// Get account details
app.get('/api/accounts/:accountId', (req: Request, res: Response) => {
  const accountId = parseInt(req.params.accountId);
  
  const account = accounts.find(a => a.id === accountId);
  if (!account) {
    return res.status(404).json({ error: 'Account not found' });
  }
  
  res.json(account);
});

// ===== Transaction Endpoints =====

// Deposit money
app.post('/api/transactions/deposit', (req: Request, res: Response) => {
  const { accountId, amount, description } = req.body;
  
  // Validation
  if (amount <= 0) {
    return res.status(400).json({ error: 'Amount must be positive' });
  }
  
  const account = accounts.find(a => a.id === accountId);
  if (!account) {
    return res.status(404).json({ error: 'Account not found' });
  }
  
  // Update account balance
  account.balance += amount;
  
  // Record transaction
  const transaction = {
    id: transactions.length + 1,
    accountId,
    type: 'DEPOSIT',
    amount,
    description,
    timestamp: new Date(),
    balance: account.balance
  };
  
  transactions.push(transaction);
  res.status(201).json({ transaction, updatedBalance: account.balance });
});

// Withdraw money
app.post('/api/transactions/withdraw', (req: Request, res: Response) => {
  const { accountId, amount, description } = req.body;
  
  // Validation
  if (amount <= 0) {
    return res.status(400).json({ error: 'Amount must be positive' });
  }
  
  const account = accounts.find(a => a.id === accountId);
  if (!account) {
    return res.status(404).json({ error: 'Account not found' });
  }
  
  if (account.balance < amount) {
    return res.status(400).json({ error: 'Insufficient balance' });
  }
  
  // Update account balance
  account.balance -= amount;
  
  // Record transaction
  const transaction = {
    id: transactions.length + 1,
    accountId,
    type: 'WITHDRAWAL',
    amount,
    description,
    timestamp: new Date(),
    balance: account.balance
  };
  
  transactions.push(transaction);
  res.status(201).json({ transaction, updatedBalance: account.balance });
});

// Transfer money between accounts
app.post('/api/transactions/transfer', (req: Request, res: Response) => {
  const { fromAccountId, toAccountId, amount, description } = req.body;
  
  // Validation
  if (amount <= 0) {
    return res.status(400).json({ error: 'Amount must be positive' });
  }
  
  const fromAccount = accounts.find(a => a.id === fromAccountId);
  const toAccount = accounts.find(a => a.id === toAccountId);
  
  if (!fromAccount || !toAccount) {
    return res.status(404).json({ error: 'One or both accounts not found' });
  }
  
  if (fromAccount.balance < amount) {
    return res.status(400).json({ error: 'Insufficient balance' });
  }
  
  // Update account balances
  fromAccount.balance -= amount;
  toAccount.balance += amount;
  
  // Record transactions
  const withdrawalTx = {
    id: transactions.length + 1,
    accountId: fromAccountId,
    type: 'TRANSFER_OUT',
    targetAccountId: toAccountId,
    amount,
    description,
    timestamp: new Date(),
    balance: fromAccount.balance
  };
  
  const depositTx = {
    id: transactions.length + 2,
    accountId: toAccountId,
    type: 'TRANSFER_IN',
    sourceAccountId: fromAccountId,
    amount,
    description,
    timestamp: new Date(),
    balance: toAccount.balance
  };
  
  transactions.push(withdrawalTx, depositTx);
  
  res.status(201).json({
    transfer: {
      from: withdrawalTx,
      to: depositTx
    },
    fromAccountBalance: fromAccount.balance,
    toAccountBalance: toAccount.balance
  });
});

// Get transaction history for an account
app.get('/api/accounts/:accountId/transactions', (req: Request, res: Response) => {
  const accountId = parseInt(req.params.accountId);
  
  // Optional query parameters for filtering
  const { type, startDate, endDate } = req.query;
  
  let accountTransactions = transactions.filter(tx => tx.accountId === accountId);
  
  // Apply filters if provided
  if (type) {
    accountTransactions = accountTransactions.filter(tx => tx.type === type);
  }
  
  if (startDate) {
    const start = new Date(startDate as string);
    accountTransactions = accountTransactions.filter(tx => new Date(tx.timestamp) >= start);
  }
  
  if (endDate) {
    const end = new Date(endDate as string);
    accountTransactions = accountTransactions.filter(tx => new Date(tx.timestamp) <= end);
  }
  
  // Sort by timestamp descending (newest first)
  accountTransactions.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
  
  res.json(accountTransactions);
});

// ===== Experimental API Gateway Routes =====
app.get('/api/gateway/balance/:accountId', async (req: Request, res: Response) => {
  try {
    const accountId = req.params.accountId;
    const balanceInfo = await apiGateway.checkAccountBalance(accountId);
    res.json(balanceInfo);
  } catch (error) {
    console.error('API Gateway Error:', error);
    res.status(500).json({ 
      error: 'Failed to retrieve account balance via API Gateway', 
      message: error instanceof Error ? error.message : String(error) 
    });
  }
});

app.post('/api/gateway/transaction', async (req: Request, res: Response) => {
  try {
    const { accountId, amount } = req.body;
    
    if (!accountId || amount === undefined) {
      return res.status(400).json({ error: 'Missing required fields: accountId and amount' });
    }
    
    const result = await apiGateway.processTransaction(accountId, amount);
    res.json(result);
  } catch (error) {
    console.error('API Gateway Error:', error);
    res.status(500).json({ 
      error: 'Failed to process transaction via API Gateway', 
      message: error instanceof Error ? error.message : String(error) 
    });
  }
});

// ===== Experimental Service Discovery Routes =====
app.get('/api/discovery/balance/:accountId', async (req: Request, res: Response) => {
  try {
    const accountId = req.params.accountId;
    const balanceInfo = await serviceDiscovery.checkAccountBalance(accountId);
    res.json(balanceInfo);
  } catch (error) {
    console.error('Service Discovery Error:', error);
    res.status(500).json({ 
      error: 'Failed to retrieve account balance via Service Discovery', 
      message: error instanceof Error ? error.message : String(error) 
    });
  }
});

app.get('/api/discovery/services/:serviceName', async (req: Request, res: Response) => {
  try {
    const serviceName = req.params.serviceName;
    const instances = await serviceDiscovery.getServiceInstances(serviceName);
    res.json({
      serviceName,
      instanceCount: instances.length,
      instances
    });
  } catch (error) {
    console.error('Service Discovery Error:', error);
    res.status(500).json({ 
      error: `Failed to get instances for service: ${req.params.serviceName}`,
      message: error instanceof Error ? error.message : String(error) 
    });
  }
});

app.get('/api/discovery/url/:serviceName', async (req: Request, res: Response) => {
  try {
    const serviceName = req.params.serviceName;
    const url = await serviceDiscovery.getServiceUrl(serviceName);
    res.json({
      serviceName,
      url
    });
  } catch (error) {
    console.error('Service Discovery Error:', error);
    res.status(500).json({ 
      error: `Failed to get URL for service: ${req.params.serviceName}`,
      message: error instanceof Error ? error.message : String(error) 
    });
  }
});

app.get('/health', (req, res) => {
    res.json({ status: 'UP' });
});

app.get('/info', (req, res) => {
    res.json({ status: 'UP' });
});

// Add your actual service endpoints
app.get('/api/example', (req, res) => {
    res.json({ message: 'Hello from Node.js service!' });
});

// Start the server
app.listen(PORT, () => {
    console.log(`Node.js service running on port ${PORT}`);
    console.log(`Connected to Zipkin at ${process.env.ZIPKIN_URL || 'http://localhost:9411'}`);
    
    // Start the Eureka client after the server is running
    client.start();
    
    // Handle shutdown gracefully
    process.on('SIGINT', () => {
      client.stop();
      process.exit();
    });
  });

export default app;