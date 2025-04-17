/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import express from "express";
import dotenv from "dotenv";
import cors from "cors";
import { connectDB } from "./config/db";
import cardRoutes from "./routes/card.routes";

dotenv.config();
const app = express();

app.use(cors());
app.use(express.json());

connectDB();
app.use("/api/cards", cardRoutes);

const port = process.env.PORT || 4001;
app.listen(port, () => {
  console.log(`Card service running on port ${port}`);
});
