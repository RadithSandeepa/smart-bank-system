/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import { Eureka } from 'eureka-js-client';
import os from 'os';

const hostname = process.env.HOSTNAME || os.hostname();

const eureka = new Eureka({
  instance: {
    app: 'transactions-service',
    hostName: 'transaction-service',
    ipAddr: hostname,
    port: {
      '$': 3000,
      '@enabled': true,
    },
    vipAddress: 'transactions-service',
    statusPageUrl: 'http://localhost:3000/info',
    healthCheckUrl: 'http://localhost:3000/health',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
  },
  eureka: {
    host: 'discovery-service',
    port: 8761,
    servicePath: '/eureka/apps/',
    preferIpAddress: true,
    fetchRegistry: true,
    registerWithEureka: true,
    maxRetries: 3,
  },
});

export default eureka;
