/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import CircuitBreaker from 'opossum';

const breakerOptions = {
  timeout: 3000,
  errorThresholdPercentage: 50,
  resetTimeout: 30000,
};

const callUserService = async () => {
  // user service call logic here
};

const userServiceBreaker = new CircuitBreaker(callUserService, breakerOptions);

export { userServiceBreaker };
