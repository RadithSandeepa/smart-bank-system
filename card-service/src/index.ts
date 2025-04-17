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

dotenv.config();
const app = express();

// Middlewares
app.use(cors());
app.use(express.json());

// Connect MongoDB
connectDB();

// Register routes
app.use("/api/cards", cardRoutes);
app.use("/api/cardsystem", healthRoutes);

// Setup Swagger UI at /api-docs
setupSwagger(app);

// Start server
const port = process.env.PORT || 4001;
app.listen(port, () => {
  console.log(`Card service running on port ${port}`);
  console.log(`Swagger docs at http://localhost:${port}/api-docs`);
});
