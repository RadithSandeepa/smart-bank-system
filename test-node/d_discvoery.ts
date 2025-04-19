import { client } from "./eureka-client";
import axios from 'axios';

// Define interfaces for type safety
interface EurekaInstance {
  hostName: string;
  port: {
    $: number;
  };
}

interface AccountBalanceResponse {
  accountId: string;
  balance: number;
  currency: string;
}

// Eureka client setup (similar to previous example)
client.start();

export async function getServiceUrl(serviceName: string): Promise<string> {
  const instances = client.getInstancesByAppId(serviceName) as EurekaInstance[];
  
  if (instances && instances.length > 0) {
    // Basic load balancing by randomly selecting an instance
    const instance = instances[Math.floor(Math.random() * instances.length)];
    const serviceUrl = `http://${instance.hostName}:${instance.port.$}`;
    return serviceUrl;
  } else {
    throw new Error(`No instances found for service: ${serviceName}`);
  }
}

export async function checkAccountBalance(accountId: string): Promise<AccountBalanceResponse> {
  try {
    const accountServiceUrl = await getServiceUrl('ACCOUNT-SERVICE');
    const response = await axios.get<AccountBalanceResponse>(`${accountServiceUrl}/api/v1/accounts/${accountId}`);
    return response.data;
  } catch (error) {
    console.error('Error checking account balance:', error instanceof Error ? error.message : String(error));
    throw error;
  }
}

export async function getServiceInstances(serviceName: string): Promise<EurekaInstance[]> {
  const instances = client.getInstancesByAppId(serviceName) as EurekaInstance[];
  return instances || [];
}