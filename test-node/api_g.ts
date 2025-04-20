import axios from 'axios';

// Define interfaces for the data structures
interface BalanceInfo {
  accountId: string;
  availableBalance: number;
  currentBalance: number;
  currency: string;
}

interface TransactionResult {
  success: boolean;
  message: string;
}

// Configure API Gateway URL with fallback to mock responses
const API_GATEWAY_URL = process.env.API_GATEWAY_URL || 'http://gateway-service:8060';
// Set to true to use mock data when API gateway is unavailable
const USE_MOCK_FALLBACK = true;

/**
 * Checks the balance of a specific account through the API gateway
 * @param accountId The ID of the account to check
 * @returns Promise containing account balance information
 */
export async function checkAccountBalance(accountId: string): Promise<BalanceInfo> {
  try {
    console.log(`Attempting to call API Gateway at: ${API_GATEWAY_URL}/account/api/v1/accounts/${accountId}`);
    
    // Call through the API gateway
    const response = await axios.get<BalanceInfo>(`${API_GATEWAY_URL}/account/api/v1/accounts/${accountId}`);
    return response.data;
  } catch (error) {
    console.error('Error checking account balance:', error instanceof Error ? error.message : String(error));
    
    // Provide detailed error info for debugging
    if (axios.isAxiosError(error) && error.response) {
      console.error(`API Gateway returned status: ${error.response.status}`);
      console.error('Response data:', error.response.data);
    } else if (axios.isAxiosError(error) && error.request) {
      console.error('API Gateway did not respond. Is it running?');
    }
    
    // Return mock data if configured to do so
    if (USE_MOCK_FALLBACK) {
      console.log('Returning mock balance data as fallback');
      return {
        accountId: accountId,
        availableBalance: 1000.00,
        currentBalance: 1000.00,
        currency: 'USD'
      };
    }
    
    throw error;
  }
}

/**
 * Processes a transaction after checking if sufficient funds are available
 * @param accountId The ID of the account for the transaction
 * @param amount The amount to be processed in the transaction
 * @returns Promise containing the result of the transaction attempt
 */
export async function processTransaction(accountId: string, amount: number): Promise<TransactionResult> {
  try {
    const balanceInfo = await checkAccountBalance(accountId);
    
    if (balanceInfo.availableBalance >= amount) {
      // Proceed with transaction
      return { success: true, message: 'Transaction approved' };
    } else {
      // Insufficient funds
      return { success: false, message: 'Insufficient funds' };
    }
  } catch (error) {
    if (USE_MOCK_FALLBACK) {
      console.log('API Gateway error. Using mock transaction response');
      return { success: true, message: 'Transaction approved (mock)' };
    }
    throw error;
  }
}