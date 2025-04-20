/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import express from "express";
import bodyParser from "body-parser";
import cors from "express-cors";
import helmet from "helmet";
import morgan from "morgan";
import sequelize from "./config/db.js";
import transactionRoutes from "./routes/transaction.route.js";
import eureka from './eureka.js';
import { zipkinMiddleware } from './zipkin';

// Initialize express app
const app = express();

// Apply middleware
app.use(zipkinMiddleware); // Zipkin tracing middleware
app.use(helmet()); // Security headers
app.use(cors({
  allowedOrigins: ['localhost:*', process.env.FRONTEND_URL || '*']
}));
app.use(bodyParser.json());
app.use(morgan('dev')); // Logging

// API documentation route
app.get("/", (req, res) => {
  res.json({
    message: "Welcome to the Transactions API",
    endpoints: {
      transactions: {
        GET: "/transactions - Get all transactions with optional filters",
        POST: "/transactions - Create a new transaction",
        GET_ONE: "/transactions/:transaction_id - Get a transaction by ID",
        STATS: "/transactions/stats - Get transaction statistics"
      }
    }
  });
});

// API routes
app.use("/transactions", transactionRoutes);

// Error handling middleware
app.use((err, req, res, next) => {
  console.error("Unhandled error:", err);
  res.status(500).json({
    error: "Server error",
    message: process.env.NODE_ENV === 'production' ? 'An unexpected error occurred' : err.message
  });
});

// Not found middleware
app.use((req, res) => {
  res.status(404).json({ error: "Not found", message: `Route ${req.url} not found` });
});

// Sync database and start server
const PORT = process.env.PORT || 3000;

const startServer = async () => {
  try {
    // Sync database (with { force: true } to recreate tables - use with caution!)
    await sequelize.sync();
    console.log("Database synchronized successfully");
    
    // Start server
    app.listen(PORT, () => {
      console.log(`Server running on port ${PORT}`);
      console.log(`Environment: ${process.env.NODE_ENV || 'development'}`);

      eureka.start((err) => {
        if (err) {
          console.error("Eureka registration failed:", err);
        } else {
          console.log("Registered with Eureka as transactions-service");
        }
      });
    });
  } catch (error) {
    console.error("Failed to start server:", error);
    process.exit(1);
  }
};

startServer();

export default app;