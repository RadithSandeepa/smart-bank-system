/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import { Eureka } from 'eureka-js-client';

const eureka = new Eureka({
  instance: {
    app: 'card-service', // Application name
    hostName: 'localhost',
    ipAddr: '127.0.0.1',
    port: {
      '$': 4001,
      '@enabled': true,
    },
    vipAddress: 'nodejs-test-service',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
  },
  eureka: {
    host: 'localhost',
    port: 8761,
    servicePath: '/eureka/apps/',
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
