/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import { Eureka } from 'eureka-js-client';

import os from 'os';


const hostname = process.env.HOSTNAME || os.hostname();

const eureka = new Eureka({
  instance: {
    app: 'card-service', // Application name
    hostName: 'card-service',
    ipAddr: hostname,
    port: {
      '$': 4001,
      '@enabled': true,
    },
    vipAddress: 'card-service',
    secureVipAddress: 'card-service',
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
  },
});

export const startEureka = () => {
  eureka.start((err) => {
    if (err) {
      console.error('Eureka registration failed:', err);
    } else {
      console.log('Registered with Eureka as nodejs-test-service');
    }
  });
};
