/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import dotenv from "dotenv";
import connectDB from "./src/config/db.js";
import app from "./src/app.js";

dotenv.config();

// Connect to MongoDB
connectDB();

// Start server
const PORT = process.env.PORT || 4003;
const HOST = process.env.HOST || "http://localhost";

app.listen(PORT, () => {
  console.log(`Account service running on ${HOST}:${PORT}`);
});
