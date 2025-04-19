import { Eureka } from 'eureka-js-client';
import os from 'os';


const hostname = process.env.HOSTNAME || os.hostname();

// Configure Eureka client
export const client = new Eureka({
    instance: {
        app: 'test-node',
        hostName: 'test-node',  // Use the container name
        ipAddr: hostname,       // This will get the container's IP
        // hostName: 'localhost',
        // ipAddr: '127.0.0.1',
        port: {
            '$': 3000,
            '@enabled': true,
        },
        vipAddress: 'test-node',
        secureVipAddress: 'test-node',
        status: 'UP',
        homePageUrl: 'http://localhost:3000',
        statusPageUrl: 'http://localhost:3000/info',
        healthCheckUrl: 'http://localhost:3000/health',
        dataCenterInfo: {
            '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
            name: 'MyOwn',
        },
        metadata: {
            'apiGatewayUrl': 'http://localhost:8060',
            'management.port': '3000'
        }
    },
    eureka: {
        host: 'discovery-service',
        port: 8761,
        servicePath: '/eureka/apps/',
        preferIpAddress: true,
        fetchRegistry: true,
        registerWithEureka: true,
        maxRetries: 3
    }
});

// Start Eureka client
client.start(error => {
    console.log(error ? `Eureka client failed to start: ${error}` : 'Eureka client started successfully');
});

// Handle graceful shutdown
process.on('SIGINT', () => {
    client.stop(error => {
        console.log('Deregistered from Eureka');
        process.exit();
    });
});