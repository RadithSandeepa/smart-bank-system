/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import dotenv from 'dotenv';
import express from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';
import morgan from 'morgan';
import helmet from 'helmet';
import cors from 'cors';
import limiter from './middleware/rateLimit.js';

dotenv.config();

const app = express();

// Middlewares
app.use(helmet());
app.use(cors());
app.use(morgan('combined'));
app.use(limiter);

// Service URLs (can run locally or from Docker)
const SERVICES = {
  USER: process.env.USER_SERVICE_URL || 'http://localhost:3000',
  ACCOUNT: process.env.ACCOUNT_SERVICE_URL || 'http://localhost:3001',
  CARD: process.env.CARD_SERVICE_URL || 'http://localhost:3002',
  TRANSACTION: process.env.TRANSACTION_SERVICE_URL || 'http://localhost:3003'
};

// Auth middleware placeholder
app.use((req, res, next) => {
  const authHeader = req.headers.authorization;
  if (!authHeader?.startsWith('Bearer ')) {
    return res.status(401).json({ error: 'Unauthorized' });
  }
  // JWT validation logic (optional)
  next();
});

// Routes
app.use('/users', createProxyMiddleware({
  target: SERVICES.USER,
  changeOrigin: true,
  pathRewrite: { '^/users': '/' },
}));

app.use('/accounts', createProxyMiddleware({
  target: SERVICES.ACCOUNT,
  changeOrigin: true,
  pathRewrite: { '^/accounts': '/' },
}));

app.use('/cards', createProxyMiddleware({
  target: SERVICES.CARD,
  changeOrigin: true,
  pathRewrite: { '^/cards': '/' },
}));

app.use('/transactions', createProxyMiddleware({
  target: SERVICES.TRANSACTION,
  changeOrigin: true,
  pathRewrite: { '^/transactions': '/' },
}));

// Global error handler
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ error: 'Internal Server Error' });
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`API Gateway running on port ${PORT}`);
});
