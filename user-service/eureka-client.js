const { Eureka } = require("eureka-js-client");
import os from 'os';


const hostname = process.env.HOSTNAME || os.hostname();

// Configure Eureka client
const client = new Eureka({
  instance: {
    app: "user-service",
    hostName: "user-service",
    ipAddr: hostname,
    port: {
      $: 5000,
      "@enabled": true,
    },
    vipAddress: "user-service",
    secureVipAddress: "user-service",
    status: "UP",
    homePageUrl: "http://localhost:5000",
    statusPageUrl: "http://localhost:5000/info",
    healthCheckUrl: "http://localhost:5000/health",
    dataCenterInfo: {
      "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
      name: "MyOwn",
    },
    metadata: {
      apiGatewayUrl: "http://localhost:8060",
      "management.port": "5000",
    },
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

// Start Eureka client
client.start((error) => {
  console.log(
    error
      ? `Eureka client failed to start: ${error}`
      : "Eureka client started successfully"
  );
});

// Handle graceful shutdown
process.on("SIGINT", () => {
  client.stop((error) => {
    console.log("Deregistered from Eureka");
    process.exit();
  });
});

module.exports = client;
