/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import express from "express";
import bodyParser from "body-parser";
import cors from "cors";
import helmet from "helmet";
import morgan from "morgan";
import userRoutes from "./routes/user.route.js";
import { zipkinMiddleware } from './zipkin';

const app = express();

// Middleware
app.use(zipkinMiddleware);
app.use(helmet());
app.use(cors());
app.use(bodyParser.json());
app.use(morgan("dev"));

// Routes
app.use("/api/v1/users", userRoutes);

export default app;
