/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import express from "express";
import dotenv from "dotenv";
import cors from "cors";
import { connectDB } from "./config/db";
import cardRoutes from "./routes/card.routes";
import healthRoutes from "./routes/health.route";
import { setupSwagger } from "./swagger";
import { errorHandler } from "./middleware/error.middleware";
import helmet from "helmet";
import morgan from "morgan";
import { startEureka } from './eureka';
import { zipkinMiddleware } from './zipkin';

dotenv.config();
const app = express();

// Middlewares
app.use(zipkinMiddleware); // Zipkin middleware for tracing
app.use(cors());
app.use(express.json());
app.use(helmet()); // Security middleware
app.use(morgan("dev")); // Logging middleware

// Connect MongoDB
connectDB();

// Register routes
app.use("/api/cards", cardRoutes);
app.use("/api/card-service", healthRoutes);

// Setup Swagger UI at /api-docs
setupSwagger(app);

// Global error handler 
app.use(errorHandler);

// Start server
const port = process.env.PORT || 4001;
app.listen(port, () => {
  console.log(`Card service running on port ${port}`);
  console.log(`Swagger docs at http://localhost:${port}/api-docs`);
});

// Start Eureka service registration for service discovery
startEureka();
